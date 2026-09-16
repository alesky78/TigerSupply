## Context

See proposal.md - Why. `ExplosionParticle` is a single, self-contained procedural effect
(`BaseEntity` subclass, no sprite, no collision) whose constructor builds a burst velocity and whose
`updateEntity`/`renderEntity` drive a fade-and-shrink lifecycle. The sibling `EnergyTrailParticle` in
the same package already implements the clean version of the same lifetime pattern and is the
reference for consistency. `Speed` accepts `float` components, so no precision workaround is needed.
The public constructor signature and the `EntityFactoryWrapper.newExplosionParticleFire/Energetic`
factories are unchanged, so no caller is touched.

## Goals / Non-Goals

**Goals:**
- Correct, uniform burst direction with no degenerate particles.
- Framerate-independent fade tied to elapsed lifetime.
- Guard against zero-valued random lifetime/size.
- Bring naming, exception handling, and constants in line with `EnergyTrailParticle`.

**Non-Goals:**
- No shared `AbstractFadingParticle` base class (deferred follow-up noted in the proposal).
- No change to the fade *style* (size shrink + color-channel drop, alpha fixed at 1) — kept as-is to
  preserve the existing look; only the fade *driver* changes.
- No constructor/factory signature or resource-catalog changes.

## Decisions

- **Polar velocity over the clamp cascade.** Replace the ~40-line `if/else` block with
  `angle = 2*PI*random(); r = random()*maxSpeed; speedX = cos(angle)*r; speedY = sin(angle)*r`.
  This yields a uniform direction and a magnitude `<= maxSpeed`, and structurally eliminates the
  `sinX == 0` branch that left `speedX = 0` / set `speedY` twice. Alternative considered: patch only
  the buggy `else` branch — rejected because the surrounding distribution is still incoherent (X and
  Y from unrelated angles), so a full replacement is both simpler and correct. Using `r = random()*maxSpeed`
  (rather than a fixed `maxSpeed`) preserves the existing spread of particle speeds within the burst.

- **Elapsed-time fade.** Compute `colorAlteration = 1 - (lifeCounter / lifeTime)`, clamped to
  `[0,1]`, instead of decrementing by a per-frame `increaseForLoop` derived from
  `context.getPeriodSeconds()`. This removes the fixed-period assumption and the `increaseForLoop`
  field entirely. Alternative: keep the decrement but recompute it from `deltaSeconds` each frame —
  rejected as more code for the same result.

- **Floor lifetime and size.** Apply a small positive minimum (e.g. `Math.max(epsilon, random*max)`
  for lifetime and `Math.max(1, random*maxSize)` for size) so a `Math.random()` of `0` cannot cause a
  divide-by-zero or an invisible particle. Keeps the randomized feel while removing the footgun.

- **Housekeeping to match the sibling.** Rename `spriteTimeDuration`/`spriteCounter` to
  `lifeTime`/`lifeCounter` (no sprite is involved), replace the silent `catch (Exception e) {}` with
  `e.printStackTrace()` (repo convention, matching `EnergyTrailParticle`), and mark `PI2` `final`.

## Risks / Trade-offs

- [Direction distribution changes slightly, so the visual burst differs from today's biased pattern]
  → Intended improvement; verified by eye at runtime. No gameplay/collision impact since particles
  never collide.
- [Speed magnitude semantics change from the old clamp to `random()*maxSpeed`] → Still bounded by
  `maxSpeed` as the spec requires; the burst stays within the same envelope callers already pass.
- [Behavior is visual and untestable by the existing (empty) test suite] → Validate with
  `mvn -pl game compile` and a manual run; the spec scenarios document the expected observable
  behavior for future tests.
