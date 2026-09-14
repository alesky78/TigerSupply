## Context

See proposal.md — Why. `EnemyGroup` extends the engine's generic `EntityGroupScreenBound<Enemy>`
and adds exactly two members: a `reset()` that clears the entity list (no callers anywhere in the
repo) and an `updateEntity(float)` override that runs the base update and then a **second loop**
calling `Enemy.scanTargetInRange()` on every live enemy. The concrete `EnemyGroup` type is named
only in `LevelScene`; every other collaborator (`LevelDirector`, `DirectorContext`,
`Enemy.enemyManager`, `SpawnHordeAction`) already refers to the base `EntityGroupScreenBound<Enemy>`.

The base `EntityGroup.updateEntity(float)` has a fixed per-frame order: **update every child → prune
children whose `shouldRemove(...)` is true** (dead via `canBeRemoved()`, or off-screen for the
screen-bound subclass) **→ flush queued spawn requests**. The current scan runs as a second pass
*after* that order, i.e. over the post-prune, post-flush set.

`Enemy.updateEntity(float)` already advances the enemy's motion (`super.updateEntity`) and then ticks
each weapon (`updateWeapon`). `scanTargetInRange()` reloads unloaded weapons and fires ready ones
whose target is in range; fired shots are queued into a separate `shotManager`, never into the enemy
group, so it cannot mutate the enemy collection being iterated. All four enemy subclasses that
override `updateEntity` (`EnemyBoss`, `EnemyShield`, `EnemyRocket`, `EnemyBackGround`) call
`super.updateEntity`, so the moved scan reaches every enemy unchanged.

Constraints:
- The engine stays game-agnostic: target scanning is game logic and must live in `game.entity.Enemy`,
  not be pushed into the reusable engine group.
- Preserve observable gameplay: enemies engage the player as they do today.

## Goals / Non-Goals

**Goals:**
- An enemy owns its fire-control decision inside its own update; no external per-group scan pass.
- Delete `EnemyGroup` so the enemy container is the plain engine `EntityGroupScreenBound<Enemy>`.
- Remove the dead `reset()` along with the class.

**Non-Goals:**
- No change to weapon range, reload, or fire-rate logic, nor to `scanTargetInRange()` itself.
- No change to how enemies are spawned or sequenced (owned by `LevelDirector`).
- No engine changes; the generic group is reused as-is.

## Decisions

### D1 — `scanTargetInRange()` is called unconditionally at the end of `Enemy.updateEntity()`

After `super.updateEntity()` and the weapon-update loop, `Enemy.updateEntity` calls
`scanTargetInRange()`. This keeps the natural per-enemy order (advance motion → tick weapons →
scan/fire) and puts the call exactly where the enemy already does its weapon bookkeeping.

Alternative considered: guard the call with `if(!canBeRemoved())` to suppress a final shot from a
just-dead enemy. Rejected as unnecessary complexity for a sub-frame, sub-perceptual effect that it
cannot fully address anyway — the off-screen case is invisible to the enemy (screen bounds are the
group's concern). Recorded as an accepted delta in D2 instead.

### D2 — Accept two sub-frame timing deltas rather than reproduce the two-pass ordering

Moving the scan out of the group's post-prune / post-flush second pass shifts *when* two edge-case
groups scan, by at most one frame:

```
                   CURRENT (group 2nd pass)              MOVED (inside Enemy.updateEntity)
  enemy pruned     not scanned (removed first)           scanned before removal -> may fire 1 last shot
  this frame
  enemy spawned    scanned same frame (post-flush)       not scanned until next frame -> engages 1 frame later
  this frame
```

Neither is observable in normal play (~16 ms at 60 fps; most enemies have life 0-1 and fire single
shots). The spawn-frame delta is arguably *more* correct: today a freshly flushed enemy can fire
before its own `updateWeapon` has ever run. No documented requirement covers either case, so no spec
changes (`skip_specs: true`).

Rationale: exactly reproducing the old ordering would require re-introducing a group-level pass or
leaking group concerns (pruning, screen bounds) into `Enemy` — the opposite of this change's goal.

### D3 — `LevelScene` uses the generic `EntityGroupScreenBound<Enemy>` directly

The `enemyManager` field, its instantiation, the three `CollisionDetector` wirings, and the render
enumeration (`getManagedEntities()`) already depend only on base-group API. Switching the declared
type from `EnemyGroup` to `EntityGroupScreenBound<Enemy>` removes the last reference to the subclass.

Rationale: no behavior depends on the subclass once the scan pass is gone. `EntityGroupScreenBound`
is already imported by `LevelScene`; only an `Enemy` import needs adding.

### D4 — `reset()` is deleted, not relocated

`EnemyGroup.reset()` (`entities.clear()`) has no callers (verified across the repo). It is removed
with the class rather than pushed down into the engine group.

Rationale: don't grow the reusable engine to preserve dead code. A deliberate clear-on-restart hook
can be added to the base group later if a real need appears.

## Risks / Trade-offs

- A dead/off-screen enemy firing one last shot (D2) → accepted; negligible. The D1 guard is a
  ready mitigation if it ever proves visible.
- Stale documentation: the `level-director-sequencing` subsystem pages name `EnemyGroup` → update
  them to name `EntityGroupScreenBound<Enemy>` and note that fire-control now lives in
  `Enemy.updateEntity` (covered in tasks).
