## Why

Enemy rockets today are fired by `DoubleRocketLauncher` through
`EntityFactoryWrapper.newEnemyShotRocket`, which aims at a **frozen snapshot** of the player's
position taken at the instant of firing (`new Position(target.getXposition(), target.getYposition())`)
and then flies a fixed accelerating path; once launched it cannot correct its course. The only
strategy that tracks a live target, `UpdateAlgoritmFollowSprite`, uses fixed per-axis speeds and only
flips their sign, so it **oscillates** around the target and never converges — it cannot model a
seeking missile. There is no movement strategy that steers toward the live player and converges. We
want a true homing strategy and a new enemy weapon that uses it.

## What Changes

- Add a new engine movement strategy `UpdateAlgorithmHoming`: it keeps a constant-speed velocity and,
  every frame, rotates its heading toward the **live** target entity by at most a configurable maximum
  turn rate (degrees/second), integrating the position over `deltaSeconds` (frame-rate independent).
  It converges on a stationary target and overshoots-then-recurves on a dodging one — unlike the
  oscillating `UpdateAlgoritmFollowSprite`.
- Add the typed factory method `UpdateAlgorithmFactoryWrapper.newHoming(target, speed, maxTurnDegPerSec)`
  and the configuration keys `ALGPRO_SPEED` and `ALGPRO_TURN_RATE` in `StaticResources` (reusing the
  existing `ALGPRO_SPRITE` key for the live target entity).
- Add a new enemy weapon `it.spaghettisource.tigersupply.game.weapon.enemy.SeekerRocketLauncher` that
  fires a **single** homing rocket, reusing the existing `EnemyRocket` entity and the
  `ENEMY_SHOT_ROCKET` sprite (same visuals and smoke trail as `DoubleRocketLauncher`).
- Add the game factory method `EntityFactoryWrapper.newEnemyShotSeekerRocket(shotPosition, target)`
  that wires the homing strategy to the **live** target entity (not a snapshot `Position`).
- Wire `EnemyShield` to fire `SeekerRocketLauncher` in place of `StandardShot` (full replacement of
  its single weapon slot).

## Capabilities

### New Capabilities
<!-- The engine-movement-algorithms capability spec already exists; this change modifies it. -->

### Modified Capabilities
- `engine-movement-algorithms`: adds a target-seeking (homing) strategy that steers toward a live
  target entity at a bounded maximum turn rate while travelling at a constant, frame-rate-independent
  speed, so it converges on the target instead of oscillating around it.

## Impact

- **Engine code**: new `UpdateAlgorithmHoming` (package `engine.entity.logic`); new
  `newHoming(...)` in `UpdateAlgorithmFactoryWrapper`; new `ALGPRO_SPEED` / `ALGPRO_TURN_RATE`
  constants in `StaticResources`.
- **Game code**: new `SeekerRocketLauncher` weapon; new `newEnemyShotSeekerRocket(...)` in
  `EntityFactoryWrapper`; `EnemyShield.weapons[0]` changed from `StandardShot` to
  `SeekerRocketLauncher`.
- **Content / visuals**: reuses the `ENEMY_SHOT_ROCKET` sprite and the `EnemyRocket` entity (smoke
  trail); no new assets and no `level-1.xml` changes — the weapon is code-wired, like the other enemy
  weapons. Sprite rotation along the curve is explicitly out of scope for this change.
- **Dependencies**: none added (JDK-only).
- **Risk**: the homing strategy holds a live reference to the player entity (like
  `UpdateAlgoritmFollowSprite`); it needs a guard for when that target is absent.
