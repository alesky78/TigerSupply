## 1. Velocity

- [x] 1.1 In `ExplosionParticle`'s constructor, replace the entire `sinX`/`sinY` `if/else` velocity
  cascade with the polar form (`angle = 2*PI*random()`, `r = random()*maxSpeed`,
  `speedX = cos(angle)*r`, `speedY = sin(angle)*r`); remove the now-dead helper locals. Verify
  `mvn -pl game compile` succeeds and no branch can leave a zero-length velocity vector.

## 2. Lifetime and fade

- [x] 2.1 Rename `spriteTimeDuration`/`spriteCounter` to `lifeTime`/`lifeCounter` and floor the
  randomized `lifeTime` to a small positive minimum and `size` to at least `1`. Verify it compiles.
- [x] 2.2 Replace the `increaseForLoop`/`context.getPeriodSeconds()` fade with elapsed-time based
  `colorAlteration = 1 - (lifeCounter / lifeTime)` clamped to `[0,1]`, and delete the unused
  `increaseForLoop` field. Verify a particle at half its lifetime shows ~half intensity regardless
  of frame interval and that self-removal at end of life still occurs.

## 3. Housekeeping

- [x] 3.1 Replace the silent `catch (Exception e) {}` around `UpdateAlgorithmFactoryWrapper.newDefault()`
  with `e.printStackTrace()` (matching `EnergyTrailParticle`) and mark `PI2` `final`. Verify it
  compiles.

## 4. Verification

- [x] 4.1 Run `mvn -pl game compile` and launch the game (`mvn -pl launcher exec:java`), destroy an
  enemy and the player, and confirm explosion bursts spread uniformly in all directions, fade and
  shrink smoothly, and no particle is stuck or invisible.
