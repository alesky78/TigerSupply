## Context

See proposal.md — Why. Today `ExplosionParticle` is already a self-contained `BaseEntity` that
moves, fades, and self-removes; the effect group renders and updates each particle. What is missing
is an object for the *burst*: the loop that spawns N particles lives in `Enemy`/`Player`/`EnemyBoss`,
and the boss additionally hand-times a periodic burst with its own `explosionCounter`. The engine
already exposes a position-copying `UpdateAlgorithm` (used by `CopyPosition`/`FollowSprite`), so an
entity that follows an owner is an established pattern.

## Goals / Non-Goals

**Goals:**
- Make an explosion a single entity that the sandbox can spawn and observe in full.
- Keep particle rendering and z-order identical to today by emitting into the shared effect group.
- Remove the scattered `particle*` burst fields from game entities in favour of two profiles.
- Fold the boss's hand-timed burst into a timed, owner-following emitter.

**Non-Goals:**
- Changing `ExplosionParticle`'s own motion/fade/removal behaviour.
- Changing `ParticleColorScheme`.
- Making explosions data-driven from XML (possible future hook, not now).

## Decisions

### Emitter that spawns into the shared effect group (not a self-contained particle system)

`Explosion extends BaseEntity` is invisible (`renderEntity` is a no-op) and on update spawns
`ExplosionParticle`s into the effect `EntityGroupScreenBound` via `addRequest`, exactly as the
current loops do. Alternative considered: an `Explosion` that owns, updates, and renders its own
particle list. Rejected because it changes rendering to a single draw call and complicates z-order
against other effects; emitting into the group preserves current visuals and reuses existing update.

Trade-off: the emitter must be given the effect manager (as `EnergeticShield` already is in the
sandbox), so a "full explosion" sandbox case wires `m.effect()`.

### One entity covers one-shot and timed emission

Fields: `ExplosionProfile profile`, `float duration`, `float interval`, `int perEmit`.
- `duration <= 0` → one-shot: emit `perEmit` particles once, set `remove = true`.
- `duration > 0` → accumulate elapsed/interval, emit each interval, remove when duration elapses.

This lets the same class serve death/hit bursts and the boss agony effect. Alternative: two separate
classes (Burst vs Emitter). Rejected as needless duplication — the timed path degenerates cleanly to
one-shot.

### Following boss emitter reuses the engine position-copy pattern

For the boss agony effect the emitter is attached to the boss and copies its position each update,
and jitters each emission origin within the owner's size (`Size.getHalfWidth/Height`). This
reproduces today's behaviour (which recomputes origin from the boss's current position every 2s) but
without the boss owning an `explosionCounter`. The boss starts one following `Explosion` at the
`life>=15 → life<15` transition instead of re-emitting every frame-tick window.

Alternative considered: a static emitter fixed at spawn point. Rejected — the boss moves and the
bursts would lag behind it.

### Two `ExplosionProfile` fields per entity (chosen: option 2a)

`Enemy`/`Player` hold `hitProfile` and `deathProfile` (each: scheme, count, maxSize, maxSpeed,
maxLifetime), replacing the seven `particle*`/`particleDeath*` fields. `collided()` picks
`hitProfile` while alive and `deathProfile` on death. Presets come from an `ExplosionProfileFactory`
in the existing Factory style. Jitter/size and follow behaviour are properties of the emitter, not
the profile, so the profile stays pure particle configuration.

Alternatives considered: (2b) keep the seven fields and only pack them into a profile at spawn
time — rejected, leaves the ridundancy; (X) no fields, profiles chosen by per-type methods —
rejected by the user in favour of explicit fields.

## Risks / Trade-offs

- [Boss visual timing changes slightly — a continuous following emitter instead of a 2s re-trigger]
  → Tune `interval`/`perEmit` of the boss agony profile to match the current cadence during review.
- [Emitter needs the effect manager wired everywhere it is used] → Provide an
  `EntityFactoryWrapper` factory that takes the effect manager, and cover it with a sandbox case so
  a missing wiring is caught immediately.
- [Following emitter holding an owner reference could outlive the owner] → Bind the emitter's
  lifetime to its `duration`; it self-removes regardless of owner state.
