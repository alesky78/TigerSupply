## 1. Fire trail colour scheme

- [x] 1.1 Add a `FIRE_TRAIL` constant to
  `game.entity.effect.ParticleColorScheme` returning a fire-hued colour whose alpha fades with `t`
  (fire colour with `colorAt(t)` alpha = `t`). Verify `mvn -pl game compile` succeeds.

## 2. FireBall projectile

- [x] 2.1 Add `it.spaghettisource.tigersupply.game.entity.projectile.FireBall extends BaseEntity`,
  copied from `EnergyBall`: same trail-emission logic, `setEffectManager`, and `updateEntity`, but
  emitting trail particles with the fire scheme. Verify it compiles.
- [x] 2.2 In `FireBall.renderEntity`, draw the `RadialGradientPaint` with fire explosion colours
  (bright hot core → yellow/orange → dark red edge) instead of the blue energy gradient. Verify it
  compiles.
- [x] 2.3 Wire the trail emission to use `ParticleColorScheme.FIRE_TRAIL` (via a new factory method,
  see 3.2). Verify emitted trail particles render in fire colours.

## 3. Factory method

- [x] 3.1 Add `EntityFactoryWrapper.newEnemyShotFireBall(GameContext, Position, Entity target)`
  copied from `newEnemyShotEnergyBall` (same speed `-350,0`, size `25x25`, homing algorithm,
  `Z_SHOT`) but constructing a `FireBall`. Verify it returns a positioned, sized `FireBall`.
- [x] 3.2 Add `EntityFactoryWrapper.newFireTrailParticle(...)` (mirroring `newEnergyTrailParticle`)
  passing `ParticleColorScheme.FIRE_TRAIL` to `EnergyTrailParticle`, and use it from `FireBall`.
  Verify it compiles.

## 4. FireBallCannon weapon

- [x] 4.1 Add `it.spaghettisource.tigersupply.game.weapon.enemy.FireBallCannon extends
  AbstractWeapon<Enemy>`, copied from `EnergyBallCannon` (same `reloadingTime`, `targetInRange`), but
  `doFire` building the shot via `EntityFactoryWrapper.newEnemyShotFireBall(...)`, setting the effect
  manager, and adding it to the shot manager. Verify it compiles.

## 5. Build verification

- [x] 5.1 Run `mvn compile` at the reactor root and verify all modules compile with no errors.
