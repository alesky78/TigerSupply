## Why

The `game` module's `it.spaghettisource.tigersupply.game.entity` package is a single flat package
holding 18 unrelated classes — the player, every enemy variant, projectiles, and visual effects all
sit side by side. As the roster grows this flat list makes it hard to see which entities belong to
which gameplay role. Grouping them into role-based sub-packages makes ownership and intent visible
from the package name.

## What Changes

- Introduce four sub-packages under `game.entity` and move the existing classes into them by role:
  - `game.entity.player` — `Player`, `PlayerEngine`
  - `game.entity.enemy` — `Enemy`, `EnemyStandard`, `EnemyShield`, `EnemyShoterRocket`, `EnemyBoss`,
    `EnemyBackGround`, `Asteroid`, `EnergeticShield`
  - `game.entity.projectile` — `PlayerRocket`, `PlayerBomb`, `EnemyRocket`, `LightningBolt`
  - `game.entity.effect` — `Effect`, `Smoke`, `ExplosionParticle`
- Keep `BaseEntity` in the root `game.entity` package as the shared base type.
- Update all Java imports and `Class` references across the `game` module that name the moved types.
- Update the fully-qualified `class="..."` strings in `game/src/main/resources/level/level-1.xml`
  for the seven enemy prototypes now living under `game.entity.enemy`.
- No class is renamed and no behavior changes — this is a pure packaging refactor. Existing public
  identifiers (including known typos such as `EnemyShoterRocket`) are preserved deliberately.

## Capabilities

### New Capabilities
<!-- None. This is a pure packaging refactor with no spec-level behavior change. -->

### Modified Capabilities
<!-- None. The game-module capability fixes the module namespace to
     it.spaghettisource.tigersupply.game, which is unchanged: the new sub-packages remain under it.
     No requirement changes, so skip_specs is set in .openspec.yaml. -->

## Impact

- **Code**: 18 entity classes move package; ~20 `game`-module files that import them (scenes,
  controllers, weapons, UI, `EntityFactoryWrapper`) update their imports and `.class` references.
- **Resources**: `level/level-1.xml` — seven `enemyPrototype` `class="..."` attributes updated to the
  new `game.entity.enemy` package (reflection resolves these at runtime, so a miss fails at load time,
  not compile time).
- **Dependencies**: none added or changed. Engine module untouched (it holds no reference into the
  game). No behavior, API surface, or resource-loading contract changes.
