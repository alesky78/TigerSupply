## 1. Trail effect

- [x] 1.1 Add `it.spaghettisource.tigersupply.game.entity.effect.EnergyTrailParticle extends BaseEntity`:
  a small square drawn with `fillRect`, an alpha/colour that decays each frame from a per-particle
  lifetime counter, near-zero motion, and `remove = true` once the lifetime elapses (modelled on
  `ExplosionParticle`). Verify it compiles and, when added to the effect group, renders as a fading
  square that disappears.

## 2. Energy-ball projectile

- [x] 2.1 Add `it.spaghettisource.tigersupply.game.entity.projectile.EnergyBall extends BaseEntity`
  with an injected `effectManager` (via `setEffectManager`, like `EnemyRocket`), a `trailCounter`, and
  an overridden `renderEntity` that paints a `RadialGradientPaint` sphere with a bright core and a
  darkening outer stop (no image asset). Verify it compiles.
- [x] 2.2 In `EnergyBall.updateEntity`, call `super.updateEntity` (advance via the default algorithm)
  and, past a small emission threshold, spawn an `EnergyTrailParticle` at the ball's position into the
  `effectManager` — mirroring `EnemyRocket`'s smoke emission. Verify the ball leaves a trail of fading
  squares as it moves.

## 3. Factory wiring

- [x] 3.1 Add `EntityFactoryWrapper.newEnemyShotEnergyBall(...)` that builds the `EnergyBall` at the
  shot position with a leftward speed, the default `UpdateAlgorithm`, and a `Size` (so the shot
  manager collides it), setting `Z_SHOT`. Verify it returns a positioned, sized `EnergyBall`.

## 4. Weapon

- [x] 4.1 Add `it.spaghettisource.tigersupply.game.weapon.enemy.EnergyBallCannon extends AbstractWeapon<Enemy>`
  (modelled on `DoubleRocketLauncher`): set `reloadingTime`; `doReload()` empty; `doFire(target)`
  creates the ball via the new factory, calls `ball.setEffectManager(owner.getEffectManager())` and
  `owner.getShotManager().addRequest(ball)`; `targetInRange(target)` true when the player is on the
  enemy's left. Verify it compiles and fires one ball per cadence.

## 5. Dedicated enemy

- [x] 5.1 Add `it.spaghettisource.tigersupply.game.entity.enemy.EnemyEnergyShooter extends Enemy`
  (modelled on `EnemyShoterRocket`): set `life`, the explosion-particle fields, and
  `weapons[0] = new EnergyBallCannon()` with `setOwner(this)`. Verify it compiles.

## 6. Level content

- [x] 6.1 In `game/src/main/resources/level/level-1.xml`, add one `<enemyPrototype type="imageSingleSprite"
  class="...EnemyEnergyShooter">` (existing image alias, speed, scale) and one `<spawnHorde>` step that
  evokes it at the right edge. Verify the XML parses on level load.

## 7. Integration verification

- [x] 7.1 Build the reactor (`mvn -q -pl game -am compile`) and run the game; reach the new spawn step
  and confirm: the energy-shooter enemy appears, fires code-drawn energy balls that advance left,
  each ball trails fading square pixels, a ball hitting the player is consumed, and a ball leaving the
  screen is removed.
