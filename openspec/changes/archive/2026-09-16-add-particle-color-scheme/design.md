## Context

See proposal.md - Why. Two `BaseEntity` fade particles in `game.entity.effect` decide color
differently: `ExplosionParticle.renderEntity` branches on `int TYPE_FIRE/TYPE_ENERGETIC`, and
`EnergyTrailParticle.renderEntity` uses a cyan literal. Both already fade a single `colorAlteration`
from `1 -> 0`; the only real difference is the `colorAlteration -> Color` mapping and whether the fade
lives in the alpha channel. The engine already uses the Strategy pattern for behavior variation
(`UpdateAlgorithm`), and the game favors type-safe constants over loose ints.

## Goals / Non-Goals

**Goals:**
- One reusable, type-safe color source keyed by particle purpose, consumed by both particles.
- "New particle look = one new constant" extensibility.
- Preserve the current visuals exactly (color-only change).

**Non-Goals:**
- No change to particle shape, size, shrink behavior, or lifecycle.
- No unifying of the fade *mechanism* (explosion stays shrink-based, trail stays alpha-based).
- No `AbstractFadingParticle` base extraction (separate deferred follow-up).
- No engine-level color abstraction; palettes are game content and stay in `game.entity.effect`.

## Decisions

- **Enum carrying `colorAt(float t)` over a functional interface or a static switch.** An enum maps
  1:1 onto the existing `TYPE_*` ints, is self-documenting and discoverable, and matches the codebase's
  type-safe-constant style. Each constant overrides `colorAt` to return its `Color`. Alternatives:
  a `FadeColor` functional interface with lambdas (more flexible but less discoverable in a
  conventional-OOP codebase), or a `ParticleColors.forType(int,float)` switch (keeps the untyped int
  we are trying to retire). Rejected both.

- **Each constant owns its RGBA policy, including alpha.** `FIRE = Color(1,t,0,1)` and
  `ENERGETIC = Color(t,t,1,1)` return opaque colors (their fade stays the shrinking oval);
  `ENERGY_TRAIL = Color(.4f,.9f,1f,t)` carries the fade in alpha. This lets one `colorAt(t)` capture
  both the palette and the fade-style difference, so the two render methods collapse to
  `dbg.setColor(scheme.colorAt(colorAlteration))` without changing either look.

- **Placement in `game.entity.effect`.** The palettes are TigerSupply content, not engine framework,
  so the enum lives beside the particles. If cross-game reuse were ever needed, an engine-level
  `FadeColorScheme` interface implemented by this enum is the escalation path — out of scope now.

- **Field swap, factories absorb the mapping.** `ExplosionParticle`'s `int type` field becomes a
  `ParticleColorScheme scheme`; `EnergyTrailParticle` gains a `scheme` field. The
  `EntityFactoryWrapper` methods select the scheme (`newExplosionParticleFire -> FIRE`,
  `newExplosionParticleEnergetic -> ENERGETIC`, `newEnergyTrailParticle -> ENERGY_TRAIL`), keeping
  their public signatures so no caller changes.

## Risks / Trade-offs

- [Constructor signature of the two particle classes changes (int/none -> scheme)] → Contained: only
  `EntityFactoryWrapper` constructs them, and its own method signatures stay the same, so the ripple
  stops at the factory.
- [A future scheme could accidentally alter an existing effect's alpha policy] → The spec pins the
  opaque-vs-alpha behavior per scheme; verify the three current schemes reproduce today's colors by
  eye at runtime.
- [Change is visual and untestable by the current empty suite] → Validate via `mvn -pl game compile`
  and a manual run comparing explosion and energy-ball trail against current behavior.
