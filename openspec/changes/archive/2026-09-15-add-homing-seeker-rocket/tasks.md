## 1. Engine: homing movement strategy

- [x] 1.1 Add `ALGPRO_SPEED` (`"speed"`) and `ALGPRO_TURN_RATE` (`"turnrate"`) to
  `engine.utils.StaticResources`; verify the module compiles and the two constants are referenced by
  the new algorithm/factory in the tasks below.
- [x] 1.2 Add `UpdateAlgorithmHoming extends AbstractUpdateAlgorithm` in `engine.entity.logic`:
  `init` reads `ALGPRO_SPRITE` (live target `Entity`), `ALGPRO_SPEED`, `ALGPRO_TURN_RATE`;
  `updateLogic` seeds `headingDeg` from the launch `Speed` on the first frame (or aims at the target
  if the launch speed is zero), rotates the heading toward the target's current position clamped to
  `maxTurnDegPerSec * deltaSeconds`, then integrates `Position` by `speed * deltaSeconds`. Verify by
  a temporary main/unit check that a stationary target is reached (converges) and that per-frame turn
  never exceeds `maxTurnDegPerSec * dt`.
- [x] 1.3 Guard the target read in `updateLogic`: when the target is null/absent, keep the current
  heading (fly straight) instead of throwing. Verify by exercising `updateLogic` with a null target
  and confirming no exception and straight-line motion.
- [x] 1.4 Add `UpdateAlgorithmFactoryWrapper.newHoming(Entity target, float speed, float maxTurnDegPerSec)`
  that packs the three keys into `DynaProperties` and calls the factory. Verify it returns a
  configured `UpdateAlgorithmHoming` whose `updateLogic` steers toward the given target.
- [x] 1.5 Bound the homing to a configured duration via `ALGPRO_SEEK_TIME`: after it elapses, stop
  steering and keep the heading (fly straight); thread `seekSeconds` through `newHoming(...)` and
  `newEnemyShotSeekerRocket(...)` (~3 s). Verify a dodged seeker flies straight after the duration and
  is removed once it leaves the screen.

## 2. Game: seeker rocket weapon and wiring

- [x] 2.1 Add `EntityFactoryWrapper.newEnemyShotSeekerRocket(Position shotPosition, Entity target)`:
  uses the `ENEMY_SHOT_ROCKET` sprite, builds the algorithm via `UpdateAlgorithmFactoryWrapper.newHoming(target, ~200, ~90..120)`
  passing the **live** `target` (not a snapshot `Position`), and returns an `EnemyRocket` created
  through `EntityFactory`. Verify the returned rocket tracks the target's live position across frames
  (not the launch-time position).
- [x] 2.2 Add `it.spaghettisource.tigersupply.game.weapon.enemy.SeekerRocketLauncher extends AbstractWeapon<Enemy>`:
  `reloadingTime ~= 2..2.5`; `doFire(target)` creates one seeker rocket at the owner position, sets
  the effect manager (smoke), and adds it to the owner's shot manager; `targetInRange` keeps the
  `target.getXposition() < owner.getXposition()` convention. Verify a single rocket is enqueued per
  fire.
- [x] 2.3 Change `EnemyShield.weapons[0]` from `new StandardShot()` to `new SeekerRocketLauncher()`
  (remove the `StandardShot` import if now unused). Verify `EnemyShield` compiles and constructs its
  single weapon slot with the seeker launcher.

## 3. Build and in-game verification

- [x] 3.1 Build both modules with Maven (`mvn -q -pl engine,game -am install` or full reactor) and
  verify compilation succeeds with no new warnings introduced by the added types.
- [x] 3.2 Launch the game, reach a horde containing the shield enemy, and verify the shield enemy
  fires a single rocket that visibly **curves** toward the moving player and **hits** a stationary
  player, with the smoke trail present (parity with `DoubleRocketLauncher` visuals); and that a rocket
  the player dodges stops homing after the seek time, flies straight, and leaves the screen (no
  immortal orbiting).
