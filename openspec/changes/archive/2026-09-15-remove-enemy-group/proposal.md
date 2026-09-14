## Why

`EnemyGroup` no longer earns its keep. Since the horde-sequencing FSM was hoisted out into
`LevelDirector`, the class does nothing but hold the live enemies — a job the generic engine
container `EntityGroupScreenBound<Enemy>` already does — plus one leftover second pass that calls
`Enemy.scanTargetInRange()` on each enemy. That single behavior belongs to the enemy itself and can
move into `Enemy.updateEntity()`, next to where the enemy already ticks its weapons, letting the
game-specific subclass disappear entirely.

## What Changes

- Move the per-enemy target scan into the enemy: `Enemy.updateEntity(float)` calls
  `scanTargetInRange()` itself, right after its weapon-update loop, so an enemy owns its own
  "decide whether to fire" step instead of depending on an external group pass.
- **Remove** `game.entity.EnemyGroup` — its `updateEntity()` override becomes redundant and its
  `reset()` method is dead code with no callers anywhere in the repo.
- Switch `LevelScene`'s enemy-container field and instantiation from `EnemyGroup` to the generic
  `EntityGroupScreenBound<Enemy>`; the collision detectors, the render enumeration, and the
  per-frame tick already use only base-class API.
- No new observable gameplay: enemies keep engaging the player exactly as before. Two sub-frame
  timing side effects are accepted and documented in design.md.

## Capabilities

### New Capabilities

None. This is a structural refactor that introduces no new observable behavior — enemies engage the
player as they do today.

### Modified Capabilities

None. No spec documents per-enemy fire control, and this change alters no requirement or scenario
(the `enemy-spawn-lifecycle` spec is about level sequencing, not per-enemy firing). The change sets
`skip_specs: true` in its `.openspec.yaml` rather than inventing a delta to satisfy validation.

## Impact

- **Removed code**: `game.entity.EnemyGroup` (class deleted, including the dead `reset()`).
- **Modified code**: `game.entity.Enemy` (`updateEntity` now calls `scanTargetInRange`) and
  `game.scene.LevelScene` (field type + instantiation switch to `EntityGroupScreenBound<Enemy>`).
- **Unaffected**: `LevelDirector`, `DirectorContext`, `SpawnHordeAction`, and `Enemy.enemyManager`
  already type the enemy container as `EntityGroupScreenBound<Enemy>`; collision detection and
  rendering use only base-group APIs.
- **Behavioral deltas** (both sub-frame, non-observable): a just-removed enemy may fire one final
  shot, and a freshly spawned enemy engages from the next frame instead of its spawn frame. See
  design.md.
- **No** XML changes, **no** dependency changes, **no** engine changes.
- **Docs**: the `level-director-sequencing` subsystem pages that name `EnemyGroup` need updating to
  reflect its removal.
