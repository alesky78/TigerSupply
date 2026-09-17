## Why

Explosions are not objects: they are imperative loops living inside `Player`, `Enemy`, and
`EnemyBoss` (methods like `createExplosionParticleFire` that spawn N `ExplosionParticle` into the
effect group). The burst configuration is scattered across seven per-instance fields
(`particleNum`, `particleMaxSize`, `particleDeathMaxSize`, `particleMaxSpeed`,
`particleDeathMaxSpeed`, `particleMaxLifeTime`, `particleDeathMaxLifeTime`), and the boss even
re-implements a hand-timed periodic burst with its own counter. Because the whole burst is a method
of another entity, the sandbox can only spawn a single `ExplosionParticle` — the full explosion
cannot be studied in isolation.

Turning the explosion into an entity makes the burst a first-class, self-contained thing that the
sandbox can drop in and observe, and removes the duplicated particle bookkeeping from the game
entities.

## What Changes

- Introduce an `Explosion` emitter entity (in `game.entity.effect`) that, given an
  `ExplosionProfile`, emits `ExplosionParticle`s into the effect group. It supports both a one-shot
  burst (emit N, then self-remove) and a timed emitter (emit at a fixed interval for a duration).
- The timed emitter can **follow its owner**: for the boss agony effect it copies the owner's
  position (reusing the engine's position-following capability) and jitters the emission origin
  within the owner's size.
- Introduce `ExplosionProfile` (scheme, particle count, max size, max speed, max lifetime) and an
  `ExplosionProfileFactory` of named presets, mirroring the existing Factory/`ParticleColorScheme`
  style.
- **BREAKING (internal):** Remove the seven `particle*` burst fields from `Enemy`/`Player`/
  `EnemyBoss`. Each entity instead holds two `ExplosionProfile` fields — `hitProfile` and
  `deathProfile` — and `collided()` selects between them. The hand-timed boss burst
  (`explosionCounter` / `addRandomExplosion`) is replaced by a following timed `Explosion`.
- The entity-sandbox catalog gains "full explosion" effect cases (hit / death / boss agony) wired to
  the effect manager, replacing the single-particle-only view of explosions.

## Capabilities

### New Capabilities
- `explosion-entity`: the emitter entity that produces an explosion burst from a profile — one-shot
  and timed/following emission, origin jitter, and self-removal — plus the `ExplosionProfile`
  configuration and its presets.

### Modified Capabilities
- `entity-sandbox`: the catalog SHALL offer explosion cases that produce a complete burst (not just a
  single particle) so an explosion can be studied in isolation.

## Impact

- New code: `game.entity.effect.Explosion`, `game.entity.effect.ExplosionProfile`,
  `game.utils` (or effect package) `ExplosionProfileFactory`; new `EntityFactoryWrapper` factory
  method(s) for explosions.
- Modified code: `Enemy`, `EnemyBoss`, `Player` (remove `particle*` fields, add `hitProfile`/
  `deathProfile`, rewrite the create/`collided`/boss-agony paths); `SandboxCatalog.buildEffects`.
- `ExplosionParticle` and `ParticleColorScheme` are unchanged.
- No new runtime dependencies.
