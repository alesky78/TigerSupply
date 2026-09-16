## Why

`ExplosionParticle` builds its burst velocity with a convoluted, buggy routine: the X and Y
components are drawn from two unrelated random angles (so the emitted direction is neither uniform
nor of the intended magnitude), and the `sinX == 0` branch sets `speedY` twice while never setting
`speedX`, producing a degenerate particle. The lifetime fade also assumes every frame equals the
fixed engine period rather than using the real elapsed time, and a zero-valued random lifetime or
size can divide by zero or spawn an invisible particle. These are contained defects in a single,
frequently-spawned visual effect, so fixing them cleanly is low risk and improves the explosion look.

## What Changes

- Replace the velocity cascade with the standard polar form (`angle = 2*PI*random`,
  `speedX = cos(angle)*r`, `speedY = sin(angle)*r`), giving a uniform random direction and removing
  the degenerate `sinX == 0` branch bug.
- Drive the fade from elapsed lifetime (`colorAlteration = 1 - lifeCounter / lifeTime`) so it is
  framerate-independent instead of tied to the fixed engine period.
- Floor the randomized lifetime and size to small positive minimums so a `Math.random()` of `0`
  cannot cause a divide-by-zero or an invisible, slot-wasting particle.
- Align housekeeping with the sibling `EnergyTrailParticle`: rename the misnamed `sprite*` fields to
  `life*`, replace the silent empty `catch` with `e.printStackTrace()`, and make `PI2` `final`.
- Out of scope (deferred follow-up): extracting a shared `AbstractFadingParticle` base for
  `ExplosionParticle` and `EnergyTrailParticle`. Noted here but intentionally not part of this change.

## Capabilities

### New Capabilities
- `explosion-particle`: the procedural fire/energetic explosion particle — its randomized burst
  direction, time-based fade-and-shrink lifecycle, and self-removal.

### Modified Capabilities
<!-- None: no existing spec covers this effect. -->

## Impact

- Code: `game/src/main/java/it/spaghettisource/tigersupply/game/entity/effect/ExplosionParticle.java`
  only. No public constructor signature or factory (`EntityFactoryWrapper`) change, so all callers
  (`Enemy`, `EnemyBoss`, `Player`, `EnergeticShield`, `GameOverScene`) are unaffected.
- Behavior: explosion bursts become visually more uniform and correct; no API, dependency, or
  resource-catalog changes.
