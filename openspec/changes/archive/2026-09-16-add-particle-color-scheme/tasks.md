## 1. Color scheme

- [x] 1.1 Add `it.spaghettisource.tigersupply.game.entity.effect.ParticleColorScheme` enum with
  constants `FIRE`, `ENERGETIC`, `ENERGY_TRAIL`, each overriding `Color colorAt(float t)` to return
  `Color(1,t,0,1)`, `Color(t,t,1,1)`, and `Color(.4f,.9f,1f,t)` respectively. Verify `mvn -pl game
  compile` succeeds.

## 2. Consumers

- [x] 2.1 In `ExplosionParticle`, replace the `int type` field (and the `TYPE_FIRE`/`TYPE_ENERGETIC`
  constants) with a `ParticleColorScheme scheme` field set from the constructor, and change
  `renderEntity` to `dbg.setColor(scheme.colorAt(colorAlteration))`, removing the `if(type==...)`
  branch. Verify it compiles and the shrinking-oval fade is unchanged.
- [x] 2.2 In `EnergyTrailParticle`, add a `ParticleColorScheme scheme` field and change `renderEntity`
  to `dbg.setColor(scheme.colorAt(colorAlteration))`, removing the cyan literal. Verify it compiles.

## 3. Factory wiring

- [x] 3.1 In `EntityFactoryWrapper`, pass `ParticleColorScheme.FIRE`/`ENERGETIC` from
  `newExplosionParticleFire`/`newExplosionParticleEnergetic` and `ParticleColorScheme.ENERGY_TRAIL`
  from `newEnergyTrailParticle`, keeping all three method signatures unchanged. Verify `mvn -pl game
  compile` succeeds and callers (`Enemy`, `Player`, `GameOverScene`, `EnergyBall`) are untouched.

## 4. Verification

- [x] 4.1 Run `mvn -pl game compile`, then launch the game (`mvn -pl launcher exec:java`); destroy an
  enemy and the player and fire an energy ball, confirming explosion bursts (red->yellow and
  white->blue) and the cyan energy-ball trail look identical to before the change.
