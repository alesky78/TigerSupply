## Why

Level music is currently hardcoded in the scene-flow layer: `SceneFlowController.doNextLevel()`
imperatively starts `mainTheme` before every level, and the level content has no say over its own
soundtrack. Now that a step can run an ordered list of `LevelAction`s, music start/stop belongs in
the level definition alongside the waves it accompanies — turning the multi-action step model into a
real, visible feature and enabling per-level moments like cutting the main theme before the boss.

## What Changes

- Add two data-driven level actions, `playMusic` and `stopMusic`, resolved by `LevelActionFactory`
  and executed as ordinary step actions (fire-and-forget, commanding the audio subsystem).
  - `playMusic` reads `alias` and `loop` from the action's XML attributes and starts that music
    track.
  - `stopMusic` reads `alias` and stops that music track (stopping all music when `alias` is absent).
- Drive the level-1 soundtrack from `level-1.xml` instead of code:
  - a first step starts `mainTheme` (looping);
  - immediately before the boss, three steps stop `mainTheme`, wait, start the boss theme
    (reusing `mainTheme` for now until a dedicated boss track exists), wait, then spawn the boss.
- Remove the hardcoded `playMusic("mainTheme", true)` from `SceneFlowController.doNextLevel()`.
- Leave the level-exit `stopAllAudio()` in `LevelScene` untouched: it covers both player death and
  level-clear (neither of which corresponds to a step that runs) and also stops effects, so it stays
  a scene-flow concern.

## Capabilities

### New Capabilities

<!-- none -->

### Modified Capabilities

- `enemy-spawn-lifecycle`: add a requirement that music playback is controllable through step
  actions (`playMusic` / `stopMusic`) so a level's soundtrack is authored in the level definition
  rather than hardcoded, consistent with the existing "a step performs an ordered list of actions"
  model.

## Impact

- **Code (game module)**: new `PlayMusicAction` and `StopMusicAction` (`game.scene.action`); two
  registrations in `LevelActionFactory`; removal of the `playMusic` call in
  `SceneFlowController.doNextLevel()`.
- **Content (resources)**: `level-1.xml` gains a music-start step and the pre-boss stop/start steps.
- **Audio**: uses the existing `AudioManager` singleton (`playMusic` / `stopMusic`) directly; no new
  asset required for now (boss theme reuses `mainTheme`). A future real boss track only needs a new
  catalog alias and an alias swap in the XML.
- **Not affected**: the engine state machine, the completion-event vocabulary, and the level-exit
  audio stop in `LevelScene`.
