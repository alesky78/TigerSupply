## Why

The two procedural fade particles pick their render color in incompatible, hard-coded ways:
`ExplosionParticle` branches on an untyped `int` (`TYPE_FIRE`/`TYPE_ENERGETIC`) inside `renderEntity`,
while `EnergyTrailParticle` hard-codes a cyan literal. There is no shared, discoverable way to say
"this particle uses that palette," so every new particle purpose means another ad-hoc color branch or
literal. A single reusable, type-safe color scheme keyed by purpose removes the duplication and makes
adding a new particle look a one-line change.

## What Changes

- Introduce a `ParticleColorScheme` enum in `game.entity.effect` where each constant maps fade
  progress `t` (the `colorAlteration`, `1 -> 0`) to an AWT `Color`: `FIRE` (`Color(1,t,0,1)`),
  `ENERGETIC` (`Color(t,t,1,1)`), `ENERGY_TRAIL` (`Color(.4f,.9f,1f,t)`).
- `ExplosionParticle` holds a `ParticleColorScheme` instead of the `int type`; its render becomes
  `dbg.setColor(scheme.colorAt(colorAlteration))`. The `TYPE_FIRE`/`TYPE_ENERGETIC` constants and the
  `if(type==...)` branch are removed.
- `EnergyTrailParticle` holds a `ParticleColorScheme` (defaulting to `ENERGY_TRAIL`) and renders the
  same way, replacing the hard-coded cyan literal.
- Each scheme decides how it uses `t` across RGBA, so the existing fade *style* is preserved exactly:
  `FIRE`/`ENERGETIC` keep `alpha = 1` (their fade stays the shrinking oval), `ENERGY_TRAIL` keeps the
  alpha fade. This change is color-only — particle shape, size/shrink behavior, and lifecycle are
  untouched.
- Out of scope: unifying the fade *mechanism* (making the explosion fade via alpha too), and the
  deferred `AbstractFadingParticle` base-class extraction. Both remain possible follow-ups.

## Capabilities

### New Capabilities
- `particle-color-scheme`: the reusable, type-safe mapping from a particle's purpose and its fade
  progress to a render color, shared by the explosion and energy-trail particles and extensible by
  adding a new scheme.

### Modified Capabilities
<!-- None: the existing explosion-particle spec has no color requirement; particle color behavior is
     newly captured by the particle-color-scheme capability. -->

## Impact

- Code (all in `game/src/main/java/.../game/entity/effect/`): new `ParticleColorScheme.java`;
  `ExplosionParticle` and `EnergyTrailParticle` updated to hold and use a scheme.
- `game.utils.EntityFactoryWrapper`: `newExplosionParticleFire/Energetic` pass the matching scheme in
  place of the `int` type; `newEnergyTrailParticle` passes `ENERGY_TRAIL`. Factory signatures are
  unchanged, so callers (`Enemy`, `Player`, `GameOverScene`, `EnergyBall`) are unaffected.
- No engine, dependency, or resource-catalog changes; no observable change to the current visuals.
