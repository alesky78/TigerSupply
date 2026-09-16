## Why

TigerSupply's enemy arsenal is either sprite-based (rockets, plasma) or a static beam
(`LightningBoltLaser`). There is no travelling energy projectile with a self-drawn, code-rendered
look and a fading "comet" trail. We want a new enemy weapon whose projectile — a spherical energy
ball with darkening edges that leaves a trail of fading square pixels — is rendered procedurally with
`Graphics2D` (like `LightningBolt`), not from image assets, and a dedicated enemy that carries it.

## What Changes

- Add a procedurally-rendered enemy projectile `EnergyBall`: a travelling sphere drawn with a
  `RadialGradientPaint` (bright core to a darkening edge) that advances straight to the left and, on
  a timer, emits square trail particles — mirroring how `EnemyRocket` emits `Smoke`.
- Add a procedurally-rendered trail effect `EnergyTrailParticle`: a small square (`fillRect`) whose
  alpha decays over a short lifetime so it fades out, then removes itself. Trail particles live in
  the `effectManager` group (visual-only, never collision-tested), exactly like existing effects.
- Add a new enemy weapon `EnergyBallCannon` (`AbstractWeapon<Enemy>`) that launches one `EnergyBall`
  per shot, following the `DoubleRocketLauncher` pattern (creates the projectile, wires its
  `effectManager` from the owner, registers it with the owner's `shotManager`).
- Add a new factory method `EntityFactoryWrapper.newEnemyShotEnergyBall(...)` that builds the
  `EnergyBall` with a leftward speed, the default movement algorithm, and a size (so the shot manager
  collides it against the player like other projectiles).
- Add a new enemy `EnemyEnergyShooter` (`Enemy` subclass) that equips a single `EnergyBallCannon`,
  and register it as a level prototype plus a spawn step in `level-1.xml`.

## Capabilities

### New Capabilities
- `energy-ball-weapon`: an enemy weapon whose code-rendered energy-ball projectile travels across the
  playfield, collides with the player like other shots, disappears when it leaves the screen, and
  trails fading square particles; delivered by a dedicated enemy spawned from the level script.

### Modified Capabilities
<!-- None. This is an additive game-content capability; it introduces no requirement changes to
     existing specs (movement uses the existing default strategy; module boundaries are unchanged). -->

## Impact

- **Game code (new)**: `game.entity.projectile.EnergyBall`, `game.entity.effect.EnergyTrailParticle`,
  `game.weapon.enemy.EnergyBallCannon`, `game.entity.enemy.EnemyEnergyShooter`.
- **Game code (modified)**: `game.utils.EntityFactoryWrapper` gains `newEnemyShotEnergyBall(...)`.
- **Content / level**: `game/src/main/resources/level/level-1.xml` gains one `<enemyPrototype>`
  (class `EnemyEnergyShooter`, an existing image alias for the enemy body) and one `<spawnHorde>`
  step that evokes it. The projectile and trail need **no** image/audio assets — they are code-drawn.
- **Dependencies**: none added (JDK/AWT only, consistent with the existing rendering approach).
- **Collision model**: the `EnergyBall` goes into the `shotManager` (collision-tested, removed on
  hit via the default `BaseEntity` reaction and pruned when off-screen by `EntityGroupScreenBound`);
  trail particles go into the `effectManager` (never collision-tested).
