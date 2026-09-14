## Context

See proposal.md - Why. Two existing pieces frame the approach:

- `AbstractEntity.updateEntity` delegates motion **entirely** to
  `updateAlgorithm.updateLogic(position, speed, deltaSeconds)`; it does not integrate `Speed` into
  `Position` itself. `UpdateAlgoritmGoToPoint` already relies on this: it keeps its own internal
  per-axis speeds and mutates `Position` directly, ignoring the entity's `Speed`.
- `Enemy.scanTargetInRange` fires each ready weapon with `weapons[i].fire(target)`, where `target` is
  the **live player `Entity`** held by the enemy. So a weapon's `doFire(Entity target)` receives a
  live entity reference, and `UpdateAlgoritmFollowSprite` shows the pattern for holding it (via the
  `ALGPRO_SPRITE` property) and reading its current position each frame.

Constraints: engine module, JDK-only, no new dependencies; the algorithm is configured through
`DynaProperties` keys (`StaticResources.ALGPRO_*`) and constructed through
`UpdateAlgorithmFactory` / `UpdateAlgorithmFactoryWrapper`. Screen coordinates are `+x` right, `+y`
down, and `Position.angle` is clockwise-positive degrees — consistent with `atan2(dy, dx)` in screen
space.

## Goals / Non-Goals

**Goals:**
- One reusable engine strategy that steers a constant-speed entity toward a live target at a bounded
  turn rate, frame-rate independent.
- Reuse the existing rocket visuals (`ENEMY_SHOT_ROCKET` sprite + `EnemyRocket` smoke trail) so the
  seeker looks like the `DoubleRocketLauncher` rockets.
- A single-shot enemy weapon wired onto `EnemyShield` in place of `StandardShot`.

**Non-Goals:**
- Rotating the rocket sprite to face its heading (`Position.angle`): out of scope; the current rockets
  do not rotate either, so parity is kept.
- A composite/sequence algorithm, acceleration, or proximity-fuse detonation.
- Speccing per-enemy weapon loadouts as a capability (there is no such spec today; the loadout swap is
  game content).

## Decisions

### Decision 1: The algorithm owns its velocity and ignores the entity `Speed`

`UpdateAlgorithmHoming` keeps internal state — a `headingDeg`, a constant `speed` magnitude, a
`maxTurnDegPerSec`, and the target `Entity` — and mutates `Position` directly, mirroring
`UpdateAlgoritmGoToPoint`. Each frame:

```
desired = atan2(targetY - posY, targetX - posX)          // degrees, screen space
diff    = normalize(desired - headingDeg) in [-180, 180]
step    = clamp(diff, -maxTurnDegPerSec*dt, +maxTurnDegPerSec*dt)
headingDeg += step
vx = speed * cos(headingDeg);  vy = speed * sin(headingDeg)
posX += vx * dt;  posY += vy * dt
```

**Why over steering the entity `Speed` in place:** if the algorithm rotated the entity's `Speed`
vector instead, `EnemyRocket.updateEntity` — which nudges `speedX` toward `maxSpeed` every frame —
would fight the steering. By owning the velocity and ignoring `Speed`, that per-frame nudge becomes
**inert** (nothing reads `Speed` for motion), so `EnemyRocket` can be reused as-is for its sprite and
smoke trail. Alternative rejected: a dedicated `BaseEntity` shot that re-implements the smoke trail —
more code, no visual gain.

### Decision 2: Initial heading derived from the launch `Speed`

On the first `updateLogic`, seed `headingDeg` from the entity's initial `Speed`
(`atan2(speedY, speedX)`) so the rocket leaves the muzzle in its launch direction and then curves. If
the launch `Speed` is zero, seed the heading straight at the target. This keeps the launch visually
continuous with the muzzle position instead of snapping to face the player on frame one.

### Decision 3: Constant speed + bounded turn rate, fully `dt`-scaled

Both the turn (`maxTurnDegPerSec * dt`) and the translation (`speed * dt`) are integrated over
`deltaSeconds`, so the behavior is frame-rate independent — unlike `UpdateAlgoritmGoToPointIncreasingSpeed`,
whose per-frame acceleration is FPS-dependent. Constant speed (no acceleration) is what makes a
homing missile dodgeable and is the classic seeker model.

### Decision 4: Config keys and factory surface

Add `StaticResources.ALGPRO_SPEED` (`"speed"`), `ALGPRO_TURN_RATE` (`"turnrate"`) and
`ALGPRO_SEEK_TIME` (`"seektime"`); reuse the existing `ALGPRO_SPRITE` (`"sprite"`) for the live
target entity. Add
`UpdateAlgorithmFactoryWrapper.newHoming(Entity target, float speed, float maxTurnDegPerSec, float seekSeconds)`. The
new class is named with the correct spelling `UpdateAlgorithmHoming` (the missing-`h` `UpdateAlgoritm…`
spelling is a historical typo kept only on the four legacy classes; new engine algorithms use the
correct prefix).

### Decision 5: Weapon and enemy wiring

`SeekerRocketLauncher extends AbstractWeapon<Enemy>` fires a **single** rocket in `doFire(target)`
via a new `EntityFactoryWrapper.newEnemyShotSeekerRocket(shotPosition, target)`, sets the effect
manager (for smoke), and adds it to the owner's shot manager — the same shape as
`DoubleRocketLauncher` but one rocket and homing. `targetInRange` keeps the existing convention
(`target.getXposition() < owner.getXposition()`). `EnemyShield` sets `weapons[0]` to a
`SeekerRocketLauncher` instead of a `StandardShot`. Starting tuning: `speed ~= 200` px/s,
`maxTurnDegPerSec ~= 90–120`, `seekSeconds ~= 3` s, `reloadingTime ~= 2–2.5` s.

### Decision 6: Seeking is time-bounded, then the missile flies straight

Unbounded homing at a dodgeable turn rate makes the missile **orbit** the player forever: it never
travels in a straight line, so it never leaves the screen and the out-of-screen shot cleanup never
removes it, cluttering the playfield with immortal rockets. After `seekSeconds` of homing the
algorithm stops steering and keeps its current heading; a straight line at constant speed always
exits a finite screen, so the existing screen-bound manager removes it. This reuses the same
straight-flight path as the absent-target guard (task 1.3). Alternative rejected: a hard lifetime
with a self-destruct explosion — more code, and it would touch `EnemyRocket`, which is shared with
the non-homing `DoubleRocketLauncher` rocket.

## Risks / Trade-offs

- Live target reference becomes stale (player removed / between respawns) → guard the target read in
  `updateLogic`; if the target is null/absent, keep the current heading (fly straight) rather than
  throwing.
- Turn rate too high makes the rocket undodgeable, too low makes it harmless → the two knobs (`speed`,
  `maxTurnDegPerSec`) are exposed for tuning; start conservative and adjust in playtest.
- Reusing `EnemyRocket` leaves its `speedX` acceleration as dead state for a seeker (never read for
  motion) → acceptable for this change; a later cleanup could split a dedicated seeker entity if the
  coupling becomes confusing.
- Unbounded homing orbits a dodging target forever and never leaves the screen, so the out-of-screen
  cleanup never removes it → bound the seeking to `seekSeconds`, after which the missile flies straight
  and exits a finite screen (Decision 6).
- Angle sign/`atan2` convention must match screen space (`+y` down, clockwise-positive) → validated
  against `Position`'s documented angle convention; unit-checkable via the spec scenarios.

## Migration Plan

1. Engine: add `ALGPRO_SPEED` / `ALGPRO_TURN_RATE` / `ALGPRO_SEEK_TIME`; add `UpdateAlgorithmHoming`;
   add `UpdateAlgorithmFactoryWrapper.newHoming(...)`.
2. Game: add `EntityFactoryWrapper.newEnemyShotSeekerRocket(...)`; add `SeekerRocketLauncher`; change
   `EnemyShield.weapons[0]` to the new weapon.
3. Build both modules with Maven; launch and verify the shield enemy fires a curving rocket that
   tracks the player and that a stationary player is hit.

Rollback: revert the change; no persisted state or external system is involved.
