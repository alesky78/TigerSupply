## Context

The existing `EnergyBall` (in `game.entity.projectile`) is a code-rendered enemy shot: it advances via
the default/homing algorithm, draws itself with a `RadialGradientPaint` (white → blue → dark blue),
and emits `EnergyTrailParticle` squares coloured by `ParticleColorScheme.ENERGY_TRAIL` (cyan,
alpha-fading). It is launched by `EnergyBallCannon` and built by
`EntityFactoryWrapper.newEnemyShotEnergyBall(...)`. See proposal.md — Why.

`ParticleColorScheme` already encodes the game's explosion palettes: `FIRE` (red→yellow, opaque),
`ENERGETIC` (white→blue, opaque), and `ENERGY_TRAIL` (cyan, alpha-fading). The energy ball's own
sphere colours are a hard-coded gradient, not sourced from a scheme.

## Goals / Non-Goals

**Goals:**
- Add a `FireBall` projectile mechanically identical to `EnergyBall` but rendered with the fire
  explosion palette (hot core → red edge) and a fire-coloured trail.
- Add a `FireBallCannon` weapon identical to `EnergyBallCannon` except it fires a `FireBall`.
- Reuse the existing explosion palette / trail particle infrastructure.

**Non-Goals:**
- No change to `EnergyBall`, `EnergyBallCannon`, or existing enemies.
- Not wiring a new enemy or level XML to fire the cannon in this change (can follow later, mirroring
  `EnemyEnergyShooter`).

## Decisions

- **`FireBall` as a sibling class of `EnergyBall`, not a subclass.** `EnergyBall` hard-codes its trail
  emission and gradient colours in `private` members with no extension seam, so subclassing would
  require overriding `renderEntity`/`updateEntity` wholesale. A parallel `FireBall extends BaseEntity`
  (a near-copy differing only in the gradient colours and the trail scheme) is clearer and matches the
  existing convention of one concrete class per projectile. Alternative considered: refactor
  `EnergyBall` to accept a colour source and reuse it — rejected as scope creep against the "identical
  but recoloured" request; can be revisited if a third ball is added.
- **Sphere gradient uses the fire explosion palette.** Draw the `RadialGradientPaint` with hot colours
  matching `ParticleColorScheme.FIRE` (bright core → yellow/orange → dark red edge), so the ball reads
  as the explosion fire colour.
- **Trail colour source.** The trail must fade its alpha like the energy trail but use fire colours.
  `ParticleColorScheme.FIRE` is opaque, so add a new `FIRE_TRAIL` constant (fire hue with
  alpha-fading `t`) and pass it to `EnergyTrailParticle`. This keeps the "add a new appearance = add a
  scheme constant" contract of the particle-color-scheme capability and reuses `EnergyTrailParticle`
  unchanged. Alternative: reuse `ENERGY_TRAIL` — rejected because a cyan trail on a fire ball looks
  wrong.
- **Factory + weapon mirror the energy versions.** Add
  `EntityFactoryWrapper.newEnemyShotFireBall(context, shotPosition, target)` copied from
  `newEnemyShotEnergyBall` (same speed, size, homing algorithm, `Z_SHOT`) but constructing a
  `FireBall`; add `FireBallCannon extends AbstractWeapon<Enemy>` copied from `EnergyBallCannon` but
  calling the new factory method.

## Risks / Trade-offs

- [Copy-paste duplication between `EnergyBall`/`FireBall` and the two cannons/factory methods] →
  Acceptable and intentional per the "identical but recoloured" request; a shared abstraction can be
  extracted later if a third variant appears.
- [Adding `FIRE_TRAIL` grows the `ParticleColorScheme` enum] → Minimal, and it is exactly the
  extension path the enum was designed for.
