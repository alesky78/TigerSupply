## Context

See proposal.md — Why/What. The engine renders every entity through `Entity.renderEntity(Graphics2D)`
and moves it through an `UpdateAlgorithm`; a `BaseEntity` may instead override `renderEntity` to draw
itself procedurally (as `LightningBolt` and `ExplosionParticle` already do). Enemy shots are
registered with the owner's `shotManager` (`EntityGroupScreenBound<Entity>`, collision-tested against
the player and auto-pruned off-screen); purely visual effects go into the `effectManager` (same group
type, never collision-tested). Enemies are instantiated by reflection from `level-1.xml` prototypes
(`SpawnHordeAction` → `EntityFactory.createEntity(..., className)`), and each `Enemy` drives its
`Weapon[]` from `scanTargetInRange()`. This change stays entirely inside those seams.

## Goals / Non-Goals

**Goals:**
- A travelling, code-rendered energy-ball projectile with a darkening edge and a fading square-pixel
  trail, wired exactly like the existing `EnemyRocket` + `Smoke` pair (projectile in `shotManager`,
  trail in `effectManager`).
- A new enemy weapon and a dedicated enemy that carries it, reachable from `level-1.xml`.
- Zero new runtime dependencies and zero new image/audio assets.

**Non-Goals:**
- No new `UpdateAlgorithm` — the ball travels straight using the existing default strategy.
- No homing/steering or curved paths (straight-left only for this change).
- No new engine capability; all new types live in the game module.
- No sprite rotation, no new sound effects, no scoring/damage-value changes.

## Decisions

### Trail as separate particles in `effectManager` (Model B), not a self-contained history

The ball emits discrete `EnergyTrailParticle` entities on a timer into the `effectManager`, mirroring
`EnemyRocket.updateEntity()` which emits `Smoke` every ~0.03s. Chosen over a self-contained ring
buffer of past positions because it reuses the established effect pipeline, keeps the particles out of
collision entirely (the `effectManager` group is never collision-tested), and lets each particle own
its own fade/lifetime and removal — the same lifecycle `ExplosionParticle` already uses. Trade-off:
more short-lived entities than a single self-drawn trail, accepted because effect particles are
already produced in far larger numbers (explosions spawn dozens to hundreds).

### `EnergyBall extends BaseEntity`, modelled on `EnemyRocket`

The projectile carries `speed` + the default `UpdateAlgorithm` (so `super.updateEntity` moves it),
holds an injected `effectManager` (via a `setEffectManager` setter, exactly like `EnemyRocket`), and a
`trailCounter` accumulator that spawns particles past a threshold. It keeps the default `BaseEntity`
collision reaction (`remove = true` on hit) and the default `getEntityRectangle()` (so it needs a
`Size` — supplied by the factory) so the `shotManager` collides it against the player and
`EntityGroupScreenBound` prunes it off-screen. Rendering overrides `renderEntity` to paint a
`RadialGradientPaint` sphere (core→edge colours with a dark outer stop) — the `LightningBolt`
loading-ball technique, minus image assets.

### `EnergyTrailParticle extends BaseEntity`, modelled on `ExplosionParticle`

A small square drawn with `fillRect` and an alpha that decays each frame from a per-particle lifetime
counter; sets `remove = true` when the lifetime elapses. Kept near-stationary (little or no speed) so
the trail marks the path travelled. Uses `fillRect` (square) rather than `ExplosionParticle`'s
`fillOval`, which is why it is a new small class rather than a reuse of `ExplosionParticle`.

### `EnergyBallCannon extends AbstractWeapon<Enemy>`, modelled on `DoubleRocketLauncher`

`doFire(target)` builds the ball via `EntityFactoryWrapper.newEnemyShotEnergyBall(...)`, calls
`ball.setEffectManager(owner.getEffectManager())`, and `owner.getShotManager().addRequest(ball)`.
`doReload()` is empty and `reloadingTime` gives the cadence (no separate charge phase — "come il
rocket"). `targetInRange(target)` returns true when the player is on the enemy's left, matching the
other left-firing enemy weapons.

### New enemy + level wiring

`EnemyEnergyShooter extends Enemy` sets `life`, the explosion-particle fields, and
`weapons[0] = new EnergyBallCannon()` (then `setOwner(this)`), following `EnemyShoterRocket`. The
enemy body still uses an existing image sprite; only the weapon/projectile/trail are code-drawn. It is
registered in `level-1.xml` as one `<enemyPrototype type="imageSingleSprite" class="...EnemyEnergyShooter">`
(reusing an existing image alias) plus one `<spawnHorde>` step that evokes it at the right edge.

## Risks / Trade-offs

- [Trail particle count per ball] → keep the emission interval and particle lifetime small (comparable
  to `EnemyRocket`'s smoke cadence) so the `effectManager` population stays bounded; particles remove
  themselves on lifetime and are pruned off-screen by `EntityGroupScreenBound`.
- [Collision rectangle vs. drawn sphere] → the default AABB from `Size` is a square approximation of
  the round ball; acceptable and consistent with how other round shots are collided.
- [`effectManager`/`shotManager` must both be injected] → the factory + weapon wire them exactly as
  `DoubleRocketLauncher` does; a missing `effectManager` would NPE on the first trail emission, so the
  weapon sets it before `addRequest`, matching the rocket precedent.
- [Simple-name collisions] → place the new types in their conventional packages
  (`entity.projectile`, `entity.effect`, `weapon.enemy`, `entity.enemy`) and verify fully-qualified
  imports, per the project's naming-collision guidance.
