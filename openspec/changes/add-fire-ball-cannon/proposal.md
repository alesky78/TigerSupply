## Why

The game has a single procedural code-rendered enemy projectile, the cool blue `EnergyBall` fired by
`EnergyBallCannon`. To vary the enemy arsenal we want a hot-coloured counterpart: a fire ball that is
mechanically identical but reuses the game's explosion (fire) palette so it reads as a burning
projectile rather than an energy one.

## What Changes

- Add a procedurally-rendered enemy projectile `FireBall`: mechanically identical to `EnergyBall`
  (advances across the playfield, emits a fading square-pixel trail, behaves as an enemy shot) but
  rendered with the fire explosion palette — a hot core (white/yellow) darkening to red at the edge —
  and a trail that reuses the fire explosion colours instead of the cyan energy trail.
- Add a new enemy weapon `FireBallCannon` (`AbstractWeapon<Enemy>`) identical to `EnergyBallCannon`
  except that it launches a `FireBall`.
- Add a factory method `EntityFactoryWrapper.newEnemyShotFireBall(...)` mirroring
  `newEnemyShotEnergyBall(...)`.
- Reuse the existing `ParticleColorScheme.FIRE` explosion palette (red→yellow) so the ball and its
  trail share the game's explosion colours; add a fire-oriented scheme only if the trail's
  alpha-fade requires one (see design).

## Capabilities

### New Capabilities
- `fire-ball-weapon`: An enemy weapon whose fire-ball projectile is code-rendered with the fire
  explosion palette, travels across the playfield leaving a fading square-pixel trail, and behaves
  as a standard enemy shot.

### Modified Capabilities
<!-- None. -->

## Impact

- **Game code (new)**: `game.entity.projectile.FireBall`, `game.weapon.enemy.FireBallCannon`.
- **Game code (modified)**: `game.utils.EntityFactoryWrapper` gains `newEnemyShotFireBall(...)`.
- **Reused**: `game.entity.effect.ParticleColorScheme` (FIRE / explosion palette),
  `game.entity.effect.EnergyTrailParticle` (trail particle), collision via `shotManager`.
- **No new runtime dependencies**; no changes to level XML unless an enemy is later wired to fire it.
