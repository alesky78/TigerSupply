## Context

See proposal.md - Why. The step model already runs an ordered list of `LevelAction`s per step, and
`LevelActionFactory` resolves an action's `type` to a class by reflection (only `spawnHorde` is
registered today). `ActionDefinition` already captures every non-`type` XML attribute of an
`<action>` into a `Map<String,String>` property bag, so a `playMusic`/`stopMusic` action can read
`alias`/`loop` from there without any parser change. `AudioManager` is a JDK-standard-library
singleton exposing `playMusic(alias, loop)`, `stopMusic(alias)`, and `stopMusic()`; music is
exclusive per alias (a second `playMusic` of the same alias is a no-op) and a stopped track
deregisters itself asynchronously via its finished-callback.

Today `SceneFlowController.doNextLevel()` calls `playMusic("mainTheme", true)` before building the
`LevelScene`, and `LevelScene.magageGameFlow()` calls `stopAllAudio()` on both player-death and
level-clear. There is currently exactly one level (`level-1.xml`).

## Goals / Non-Goals

**Goals:**
- Two new step actions (`playMusic`, `stopMusic`) reachable purely by editing the level XML.
- Move the level-1 soundtrack (start + pre-boss swap) into `level-1.xml`.
- Keep the engine state machine, the completion-event vocabulary, and `LevelScene`'s exit stop
  unchanged.

**Non-Goals:**
- No new audio asset: the boss theme reuses `mainTheme` until a real track exists.
- No change to how the level-exit `stopAllAudio()` works (it must cover death + effects, so it stays
  scene-flow, not a step action).
- No SAX/parser changes (the property bag already carries the attributes).
- No multi-level generalization beyond level-1; other levels would simply declare their own
  start-music step.

## Decisions

### Decision: The music actions reach `AudioManager` via its singleton, not via `DirectorContext`

`PlayMusicAction`/`StopMusicAction` call `AudioManager.getInstance()` directly in `execute(...)`.

- **Why**: `AudioManager` is a process-global singleton like `SpriteFactory`/`EntityFactory`, which
  `SpawnHordeAction` already calls directly. The subsystems threaded through `DirectorContext`
  (enemy/shot/effect managers) are per-scene objects constructed fresh each level; audio is not one
  of them. Routing audio through the context would imply a scene-scoped lifetime it does not have.
- **Alternative considered**: add `getAudioManager()` to `DirectorContext` (the index.md doc hints
  at "audio later"). Rejected for now to avoid implying scene-scoping and to keep the change small;
  it can be revisited if audio ever becomes scene-scoped.

### Decision: `loop` and `alias` come from the action property bag

`PlayMusicAction.init(...)` reads `alias` (required) and `loop` (parsed as a boolean, default
`true`) from `ActionDefinition.getProperty(...)`. `StopMusicAction.init(...)` reads optional `alias`;
when absent, `execute(...)` calls `stopMusic()` (all music).

- **Why**: mirrors how `SpawnHordeAction` reads its data from the already-parsed definition; needs no
  parser change.

### Decision: The pre-boss swap is three separate single-action steps

```
step  stopMusic  mainTheme            completionEvent timed 1
step  playMusic  mainTheme  loop      completionEvent timed 2   (boss theme == mainTheme for now)
step  spawnHorde boss                 completionEvent bossSpawned
```

- **Why**: each `timed` wait is a property of a step, so the "stop -> pause -> start -> pause ->
  spawn" cadence maps naturally onto three steps. It also keeps each step trivially readable. The
  boss step itself is left as the existing single-`spawnHorde` step.
- **Alternative considered**: fold `playMusic` into the boss step as a second action
  (`playMusic` + `spawnHorde` in one step). Rejected because the author wants a timed pause between
  starting the boss theme and the boss appearing, which a single step cannot express.

### Decision: Level-1 music start becomes step 0

A new first step runs `playMusic mainTheme loop` with `completionEvent timed 1`; the hardcoded
`playMusic` in `doNextLevel()` is removed.

- **Why**: makes the soundtrack part of the level content. Removing the code call avoids a redundant
  double-start (harmless due to per-alias idempotency, but sloppy).

## Risks / Trade-offs

- **Stop/replay race on the same alias**: `stopMusic(mainTheme)` signals the track to stop and it
  deregisters via its finished-callback; the following `playMusic(mainTheme)` could in theory be a
  no-op if the old entry has not been removed yet. → The two are separated by the stop step's 1s
  `timed` wait, which is far longer than the thread teardown, so in practice the replay always
  starts a fresh track. If a future track has a long tail, increase the wait.
- **Every level must now declare its own start-music step**: removing the code default means a level
  with no `playMusic` step is silent. → Acceptable and intended (music is now content); there is only
  one level today, and it declares the step.
- **Boss theme is a placeholder (`mainTheme`)**: the "swap" restarts the main theme rather than
  playing distinct boss music. → Cosmetic; swapping in a real `bossTheme` later is a one-line catalog
  add plus an alias change in the XML, with no code change.
- **Unknown action type must fail clearly**: `LevelActionFactory` already throws for an unregistered
  `type`, so a typo in the XML fails fast at load — no new handling needed.
