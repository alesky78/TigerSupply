## 1. Baseline

- [x] 1.1 Build the reactor with JDK 17 (`$env:JAVA_HOME=...Temurin-jdk-17...; mvn -q -DskipTests clean package`) and confirm it is green before renaming, establishing the baseline.

## 2. Rename FSM state & event identifiers (Java)

- [x] 2.1 In `game.utils.GameResources`, rename the eleven `STATE_*` / `EVENT_*` constants — both the Java identifier and the string value — per the proposal mapping (`STATE_GENERATE_HORDE`→`STATE_SPAWNING_HORDE "spawningHorde"`, `STATE_WAIT_TIME`→`STATE_AWAITING_TIMER "awaitingTimer"`, `STATE_WAIT_KILL`→`STATE_AWAITING_CLEAR "awaitingClear"`, `STATE_KILL_BOSS`→`STATE_AWAITING_BOSS_DEFEAT "awaitingBossDefeat"`, `STATE_BOSS_KILLED`→`STATE_LEVEL_CLEARED "levelCleared"`, `EVENT_WAIT`→`EVENT_PENDING "pending"`, `EVENT_NEW_HORDE`→`EVENT_READY "ready"`, `EVENT_WAIT_TIME`→`EVENT_HORDE_TIMED "hordeTimed"`, `EVENT_WAIT_KILL`→`EVENT_HORDE_CLEARABLE "hordeClearable"`, `EVENT_BOSS_GENERATED`→`EVENT_BOSS_SPAWNED "bossSpawned"`, `EVENT_BOSS_KILLED`→`EVENT_BOSS_DEFEATED "bossDefeated"`); verify no value is shared between a state and an event.
- [x] 2.2 Rename the five state files/classes in `game.scene.statemachine` (`StateGenerateHorde`→`StateSpawningHorde`, `StateWaitTime`→`StateAwaitingTimer`, `StateWaitKill`→`StateAwaitingClear`, `StateKillBoss`→`StateAwaitingBossDefeat`, `StateBossKilled`→`StateLevelCleared`) and repoint their `getStateName()` / `Event` references to the renamed constants; verify each class references only new constant names.
- [x] 2.3 Update `game.entity.EnemyManager.initComponents` — imports, the five state instances, every `TransitionTable` `add`/`selfLoop` edge, and the ASCII transition comment — to the renamed classes and constants; verify the declared edges match the proposal graph.
- [x] 2.4 Update the string-comparing consumers `game.scene.statemachine.HordeSpawner` (`validateWaitTimeHordes`, `createHordeEvent`) and `game.scene.statemachine.EnemySpawnContext` (`spawnNextHorde`) to compare against the renamed `EVENT_HORDE_TIMED` constant; verify no literal old token remains in these files.
- [x] 2.5 Compile the game module against the current engine (`mvn -q -DskipTests -pl game -am compile`) and confirm it builds clean with the renamed identifiers.

## 3. Update level data

- [x] 3.1 In `game/src/main/resources/level/level-1.xml`, re-spell every `<generateEvent name="...">` token (`waitTime`→`hordeTimed`, `waitKill`→`hordeClearable`, `bossGenerated`→`bossSpawned`), leaving `time` attributes untouched; verify a grep for the three old tokens in the file returns zero matches.

## 4. Update documentation (Italian subsystem docs)

- [x] 4.1 Update `documentation/subsystems/enemy-spawn-lifecycle/index.md` — the state/event name tables, prose, the `STATE_BOSS_KILLED` vs `EVENT_BOSS_KILLED` note, and the Mermaid state/wiring diagrams — to the new names; verify no old name remains.
- [x] 4.2 Update `documentation/subsystems/enemy-spawn-lifecycle/sequenziamento-horde.md` (state names, transition table snippet, initial/final state prose, Mermaid) to the new names.
- [x] 4.3 Update `documentation/subsystems/enemy-spawn-lifecycle/caricamento-dati-livello.md` (`StateGenerateHorde` reference, `waitTime`/`waitKill` event mentions, `validateWaitTimeHordes` narrative) to the new names.
- [x] 4.4 Update `documentation/subsystems/enemy-spawn-lifecycle/aggiungere-nuovi-elementi.md` (the `<generateEvent>` example tokens and the event-name legend) to the new tokens.
- [x] 4.5 Re-validate every Mermaid diagram touched in 4.1–4.4 renders without error.

## 5. Verify end-to-end

- [x] 5.1 Build the full reactor with JDK 17 (`mvn -q -DskipTests clean package`) and confirm it is green.
- [x] 5.2 Smoke-launch `launcher/target/tigersupply.jar` and confirm the level plays through to the terminal state (`bossSpawned` → `levelCleared`) with no state-machine errors.
- [x] 5.3 Grep the whole repo (`.java`, `.xml`, docs) for the old identifiers and string values (`STATE_GENERATE_HORDE`, `STATE_WAIT_TIME`, `STATE_WAIT_KILL`, `STATE_KILL_BOSS`, `STATE_BOSS_KILLED`, `EVENT_WAIT`, `EVENT_NEW_HORDE`, `EVENT_WAIT_TIME`, `EVENT_WAIT_KILL`, `EVENT_BOSS_GENERATED`, `EVENT_BOSS_KILLED`, and the old string tokens) and confirm zero matches remain outside the archived proposal/design of this change.
