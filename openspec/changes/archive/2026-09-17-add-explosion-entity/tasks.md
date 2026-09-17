## 1. Explosion profile

- [x] 1.1 Add `ExplosionProfile` in `game.entity.effect` holding `ParticleColorScheme scheme`,
  `int particleNum`, `int maxSize`, `int maxSpeed`, `float maxLifeTime` (immutable, with getters);
  verify `mvn -pl game compile` succeeds.
- [x] 1.2 Add `ExplosionProfileFactory` (Factory style, e.g. `getInstance()` or static presets)
  exposing named presets that reproduce today's values: standard hit/death (fire), player death,
  boss death, boss agony, and an energetic variant; verify it compiles and each preset returns the
  expected field values.

## 2. Explosion emitter entity

- [x] 2.1 Add `Explosion extends BaseEntity` in `game.entity.effect` with fields `ExplosionProfile
  profile`, `EntityGroupScreenBound<Entity> effectManager`, `float duration`, `float interval`,
  `int perEmit`; `renderEntity` is a no-op; verify it compiles.
- [x] 2.2 Implement one-shot emission (`duration <= 0`): on first `updateEntity` spawn `perEmit`
  `ExplosionParticle`s (via `EntityFactoryWrapper`) at the emitter position using the profile, then
  set `remove = true`; verify in the sandbox a one-shot case shows a full burst that disappears.
- [x] 2.3 Implement timed emission (`duration > 0`): accumulate elapsed/interval time
  (framerate-independent), emit `perEmit` particles each interval, set `remove = true` once the
  duration elapses; verify a timed sandbox case emits repeatedly then stops.
- [x] 2.4 Implement owner-following: when attached to an owner, copy the owner's position each update
  (reusing the engine position-copy `UpdateAlgorithm`) and jitter each emission origin within the
  owner's `Size` half-extents; verify bursts track a moving owner and scatter across its body.
- [x] 2.5 Ensure the emitter never collides (no collision participation / `canBeRemoved` tied only to
  lifetime); verify it does not register collisions in the sandbox.

## 3. Factory wiring

- [x] 3.1 Add `EntityFactoryWrapper` methods to build explosions: a one-shot burst
  (`newExplosion(profile, posX, posY, effectManager, context)`) and a following timed burst
  (`newFollowingExplosion(profile, owner, duration, interval, effectManager, context)`); verify they
  compile and return a wired `Explosion`.

## 4. Migrate game entities to profiles

- [x] 4.1 In `Enemy`, remove the seven `particle*`/`particleDeath*` fields and add
  `ExplosionProfile hitProfile` and `ExplosionProfile deathProfile`; rewrite
  `createdHitExplosionParticle`/`createdDeadExplosionParticle` (and the energetic hit path) to spawn
  a one-shot `Explosion` from the matching profile; verify `mvn -pl game compile` succeeds.
- [x] 4.2 Update `Enemy` subclasses and prototypes to assign `hitProfile`/`deathProfile` from
  `ExplosionProfileFactory` presets instead of the old fields; verify each enemy still explodes on
  hit and on death in-game.
- [x] 4.3 In `EnemyBoss`, remove the `explosionCounter`/`addRandomExplosion` hand-timed burst and,
  at the `life>=15 -> life<15` transition, start a single following timed `Explosion` (boss-agony
  preset) that follows the boss; verify the agonising boss emits ongoing explosions across its body.
- [x] 4.4 In `Player`, remove the `particle*` fields and its `createExplosionParticle*` methods,
  adding `deathProfile` (and hit profile if used) and spawning a one-shot `Explosion` on death;
  verify the player death explosion still appears.
- [x] 4.5 Update any remaining callers (`EnergeticShield`, `GameOverScene`) that relied on the
  removed methods/fields to use profiles or the factory; verify `mvn compile` succeeds for all
  modules.

## 5. Sandbox coverage

- [x] 5.1 In `SandboxCatalog.buildEffects`, replace/extend the single-particle explosion cases with
  full-explosion cases (one-shot hit, one-shot death, boss-agony following) wired to `m.effect()`;
  verify each case launches a complete animating burst in the sandbox.

## 6. Verification

- [x] 6.1 Run `mvn clean compile` for the reactor and confirm all modules build.
- [ ] 6.2 Launch the game and the sandbox and confirm enemy-hit, enemy-death, player-death, and
  boss-agony explosions match the prior look/cadence; run `openspec validate add-explosion-entity
  --type change` and confirm it passes.
