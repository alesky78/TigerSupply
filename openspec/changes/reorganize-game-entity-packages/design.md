## Context

See proposal.md - Why. The `game.entity` package is flat (18 classes). All entity classes extend
`game.entity.BaseEntity`, which extends the engine's `AbstractEntity`. Two mechanisms bind callers to
these types:

1. **Compile-time**: ~20 `game`-module files import the classes and/or pass `Xxx.class` literals to
   `EntityFactory.createEntity(...)` (e.g. `EntityFactoryWrapper`). These are caught by the compiler.
2. **Runtime reflection**: `level/level-1.xml` names seven enemy classes by fully-qualified string in
   `<enemyPrototype class="...">`. The level builder resolves them via `Class.forName`, so a stale
   string fails only when the level loads, not at compile time.

The engine module holds no reference into the game (per the `game-module` capability), so it is
untouched.

## Goals / Non-Goals

**Goals:**
- Group the 18 entity classes into role-based sub-packages under `game.entity`.
- Keep every type under the module namespace `it.spaghettisource.tigersupply.game` (no spec change).
- Preserve all class names, signatures, and runtime behavior — move only.

**Non-Goals:**
- No class/identifier renames (known typos like `EnemyShoterRocket` stay as-is).
- No changes to the engine module, weapon logic, or scene flow beyond import updates.
- No new sub-packages in `game.weapon`, `game.scene`, or other packages.

## Decisions

**Sub-package layout (variant B — by role, projectiles separated):**

```
game.entity
  |  BaseEntity            (shared base, stays in root)
  +-- player              Player, PlayerEngine
  +-- enemy               Enemy, EnemyStandard, EnemyShield, EnemyShoterRocket,
  |                       EnemyBoss, EnemyBackGround, Asteroid, EnergeticShield
  +-- projectile          PlayerRocket, PlayerBomb, EnemyRocket, LightningBolt
  +-- effect              Effect, Smoke, ExplosionParticle
```

- **`BaseEntity` stays in the root package.** It is the shared ancestor of every group; placing it in
  one child package would couple the others to that child. Alternative (a `base` sub-package) adds a
  package for a single class with no benefit.
- **`PlayerEngine` goes to `player`, not `effect`, despite extending `Effect`.** Its lifecycle is owned
  by the player ship (thruster). Role/ownership beats inheritance here; cross-package inheritance
  (`player.PlayerEngine extends effect.Effect`) is normal in Java. Alternative (put it in `effect` to
  match its parent) was rejected because it obscures ownership.
- **`LightningBolt` goes to `projectile`, not `effect`.** It is fired by an enemy weapon
  (`LightningBoltLaser`) as a damaging shot; it behaves as a projectile even though it reads like a
  visual effect. Grouping it with the other shots keeps the weapon→projectile mapping legible.
- **`EnergeticShield` goes to `enemy`, matching its `extends Enemy`.** It is modeled as an enemy in the
  type hierarchy; moving it elsewhere would split a class from its parent for a debatable semantic gain.
  Flagged as the one deliberately arguable placement.
- **Update the XML strings in the same change.** The seven `class="..."` values in `level-1.xml` are
  part of the move; leaving them stale would break level load at runtime.

## Risks / Trade-offs

- **Stale reflection string in `level-1.xml`** (only the seven enemy prototypes matter) → Grep the XML
  for `game.entity.` after the move and confirm each resolves to the new `game.entity.enemy.*` path;
  run the launcher and start level 1 to prove the level loads.
- **Missed import / `.class` reference in a `game` file** → Caught at compile time; a full
  `mvn -pl game -am compile` (and the reactor build) must pass before the change is considered done.
- **`EnergeticShield` placement is debatable** → Isolated, reversible decision; documented above so it
  can be revisited without affecting the rest of the move.
- **Simple-name collisions when re-importing** (e.g. `RocketLauncher` already exists twice under
  `game.weapon`) → The moved types keep unique simple names within `game.entity`, so no new collision
  is introduced; verify imports resolve to the intended `game.entity.*` type.

## Migration Plan

1. Create the four sub-packages and move each class, updating its `package` declaration.
2. Fix imports and `Xxx.class` references across the `game` module (IDE/compiler-driven).
3. Update the seven `class="..."` strings in `level/level-1.xml` to `game.entity.enemy.*`.
4. Build the reactor (`mvn compile`) and launch level 1 to confirm no runtime reflection failure.

Rollback is a straightforward revert (packaging-only, no data or API migration).
