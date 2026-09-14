## 1. Baseline and assets

- [x] 1.1 Confirm a clean baseline: with `JAVA_HOME` on Temurin 17, run `mvn -q -DskipTests clean package` and verify it is green before any change.
- [x] 1.2 Add the character portrait images under `game/src/main/resources/image/` and register one alias per (speaker, expression) in `image-catalog.txt` (e.g. `s portrait_falcon_calm image/portrait-falcon-calm.png`); verify each alias loads by launching the game (image repository preloads without error).

## 2. Parser data model (scripts)

- [x] 2.1 Add `WindowDefinition`, `MessageDefinition`, and `ScriptDefinition` POJOs in `game.scene.builder.definition` (window = posX/posY/width/height/portraitWidth/charDelay; message = speaker/portrait/`List<String>` lines; script = name + window + `List<MessageDefinition>`); verify the module compiles.
- [x] 2.2 Extend `EnemyDataBuilderSaxXml` to parse the new `<scripts>/<script name>/<window .../>/<messages>/<message speaker portrait>/<line>` nesting into `ScriptDefinition`s; verify by loading a level containing one script and asserting the parsed script has the expected messages and lines (temporary debug print or unit test).
- [x] 2.3 Add `getScriptByName(...)` (and the backing storage) to `LevelDataRepository`; verify a defined name returns its script and an undefined name returns null.

## 3. Dialogue runtime subsystem (game.scene.dialog)

- [x] 3.1 Implement `MessageBox` in `game.scene.dialog` that draws the window (black fill, thin white border), the portrait scaled to `portraitWidth` on the left, the speaker name and the revealed lines in the bitmap font on the right, and a blinking cursor/arrow when the message is fully revealed; verify by rendering a fixed sample message and visually confirming the layout on launch.
- [x] 3.2 Implement `DialogManager` in `game.scene.dialog`: `start(ScriptDefinition)`, `update(delta)` advancing the reveal count `floor(elapsed/charDelay)` and the blink timer, `advance()` with the two-stage behavior (reveal-all while typing, else next message, else finish), plus `isActive()` and `isFinished()`; verify the revealed count grows over time and that `advance()` moves reveal → next → finished as specified (isolated unit test or debug harness).

## 4. Level-director integration

- [x] 4.1 Add `get/setDialogManager(...)` to `DirectorContext`; verify the module compiles.
- [x] 4.2 Implement `ShowDialogAction` (`LevelAction`) that reads the `script` property, resolves it via `context.getLevelData().getScriptByName(...)`, and calls `dialogManager.start(...)`; register `"showDialog"` in `LevelActionFactory`; verify the factory instantiates it and executing it starts a dialogue.
- [x] 4.3 Add `EVENT_DIALOG_CLOSED = "dialogClosed"` / `STATE_AWAITING_DIALOG = "awaitingDialog"` and a `StateAwaitingDialog` (mirrors `StateAwaitingClear`, polling `dialogManager.isFinished()`) to `LevelDirectorStateMachineFactory`, wiring `executingStep --dialogClosed--> awaitingDialog`, `awaitingDialog` PENDING self-loop, and `awaitingDialog --ready--> executingStep`; verify the transition graph builds and a `dialogClosed` step routes to `awaitingDialog`.
- [x] 4.4 In `LevelDirector.init()`, validate that every `showDialog` step references a script defined in `<scripts>`, failing level load with the offending script name (mirroring the time-gated fail-fast); verify that a step referencing an unknown script name aborts loading with that name in the error.

## 5. Scene wiring (suspend, input, render)

- [x] 5.1 In `LevelScene`, create the `DialogManager`, set it on `DirectorContext`, and restructure `update(delta)` so that (still respecting global `isPaused()/isStop()`) the dialogue and the director always tick while the gameplay subsystems (`magageGameFlow`, player, enemies, effects, shots, collisions) are gated behind `!dialogManager.isActive()`; verify player and enemies freeze while a dialogue is active and resume after it is dismissed.
- [x] 5.2 In `LevelScene`, track `fireHeld` from the key stream, deliver a single `dialogManager.advance()` on the fresh fire-key press while a dialogue is active, swallow gameplay key-presses during a dialogue, and always forward `keyReleased` to the player; verify that holding fire advances by at most one message and that no movement key stays stuck after the dialogue closes.
- [x] 5.3 Draw the dialogue overlay last in `LevelScene.internalRender`; verify the window renders on top of the frozen playfield during a dialogue.

## 6. Content and end-to-end verification

- [x] 6.1 Add a `<scripts>` block and a `showDialog` + `dialogClosed` step to `level-1.xml`; verify the dialogue triggers at that point during play.
- [x] 6.2 End-to-end smoke on the packaged uber-jar (JDK 17/21): reach the dialogue step and confirm the action suspends, the text types out one character at a time, the blinking indicator appears, the fire key completes/advances/dismisses as specified, and the action and level sequencing resume after the last message; also confirm the unknown-script fail-fast by temporarily referencing a missing script name.

## 7. Documentation (optional)

- [x] 7.1 (Optional) Update the `level-director-sequencing` subsystem docs to add the `showDialog` action and the `dialogClosed`/`awaitingDialog` completion to the vocabulary and recipes; verify the referenced files and any mermaid diagrams remain valid.
