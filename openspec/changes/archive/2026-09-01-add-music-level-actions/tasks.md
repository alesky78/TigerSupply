## 1. Music actions

- [x] 1.1 Add `PlayMusicAction` in `game.scene.action`: `init(...)` reads `alias` (required) and
  `loop` (boolean, default `true`) from the `ActionDefinition` property bag; `execute(...)` calls
  `AudioManager.getInstance().playMusic(alias, loop)`. Verify a missing `alias` fails with a clear
  error at execute time.
- [x] 1.2 Add `StopMusicAction` in `game.scene.action`: `init(...)` reads optional `alias`;
  `execute(...)` calls `AudioManager.getInstance().stopMusic(alias)` when present, else
  `stopMusic()`. Verify both branches compile and behave (alias vs no-alias).
- [x] 1.3 Register `"playMusic"` and `"stopMusic"` in `LevelActionFactory.TYPE_TO_CLASS`; verify an
  unknown type still throws and the two new types resolve.

## 2. Wire the level content

- [x] 2.1 Add a first step to `level-1.xml` that runs `playMusic` `mainTheme` `loop="true"` with
  `completionEvent timed 1`; verify the XML still parses (level loads without error).
- [x] 2.2 Insert the three pre-boss steps before the boss step in `level-1.xml`: `stopMusic`
  `mainTheme` (timed 1), `playMusic` `mainTheme` `loop="true"` (timed 2), leaving the existing boss
  `spawnHorde` step (bossSpawned) unchanged; verify the level loads.
- [x] 2.3 Remove the `AudioManager.getInstance().playMusic("mainTheme", true)` call from
  `SceneFlowController.doNextLevel()`; verify `LevelScene`'s exit `stopAllAudio()` is left untouched.

## 3. Verify end-to-end

- [x] 3.1 Build the reactor (`mvn -q -pl game -am compile`) and verify it succeeds.
- [x] 3.2 Run the game (`mvn -pl launcher exec:java`) and verify: music starts at level start, the
  main theme stops then restarts just before the boss with the authored pauses, the boss then
  appears, and audio stops on both level-clear and player death.
