## 1. Level model and completion vocabulary

- [x] 1.1 Rename `GenerateEvent` to `CompletionEvent` (keep `name` + optional `time`) and update all references; verify the `game` module compiles and grep shows no remaining `GenerateEvent` references.
- [x] 1.2 Rename the completion-event name constants to `timed`/`cleared`/`bossSpawned`/`bossDefeated` (dropping the `horde` prefix) in the FSM factory; verify compilation and that grep finds no `hordeTimed`/`hordeClearable` identifiers.
- [x] 1.3 Rename `Horde` to `Step`, giving it an ordered list of action definitions plus exactly one `CompletionEvent`; verify it compiles and a `Step` exposes both an ordered action list and a single completion event.
- [x] 1.4 Introduce an action-definition type carrying an action `type` and its data (nested enemies for `spawnHorde`, a properties bag for other actions); verify a `Step` can hold two heterogeneous action definitions and compiles.

## 2. Action abstraction (Command)

- [x] 2.1 Add the `LevelAction` interface with `execute(DirectorContext)` in `game`; verify the module compiles.
- [x] 2.2 Add `LevelActionFactory` that instantiates a `LevelAction` from an action definition by fully-qualified class name via reflection (mirroring `EntityFactory`/`UpdateAlgorithmFactory`); verify a lookup of the `spawnHorde` type returns a `SpawnHordeAction` instance.
- [x] 2.3 Implement `SpawnHordeAction` by extracting today's enemy-instantiation logic out of `HordeSpawner`; verify that executing it against a context registers the declared enemies with the enemy manager (observable in a smoke run).

## 3. Director, context, and state machine

- [x] 3.1 Rename `EnemySpawnContext` to `DirectorContext` and broaden it to reach the enemy manager, leaving a documented seam for future subsystems; verify it compiles and a `timed` completion still sets `waitTime` through the existing side-channel.
- [x] 3.2 Rename `StateSpawningHorde` to `StateExecutingStep` so its `internalProcess` runs each action of the current step in declaration order via the factory, then returns the step's completion `Event`; verify a step with two actions runs both once and emits the completion event.
- [x] 3.3 Rename `EnemySpawnStateMachineFactory` to `LevelDirectorStateMachineFactory`, keeping the transition-graph shape with the renamed states/events; verify the graph covers every reachable `(state, event)` pair (no `StateMachineUnsupportedEvent` at runtime).
- [x] 3.4 Add `LevelDirector` owning the state machine and `DirectorContext`, exposing `tick()` and `isLevelCleared()`; verify it compiles.

## 4. Level loading (builder and repository)

- [x] 4.1 Update `EnemyDataBuilderSaxXml` to parse `<steps>/<step>/<actions>/<action>` plus a trailing `<completionEvent>` into `Step`/action-definitions/`CompletionEvent`; verify parsing the migrated `level-1.xml` yields the expected step count with correct per-step actions and completion data.
- [x] 4.2 Update `LevelDataRepository` to store steps (renamed from hordes) while leaving enemy/algorithm prototype lookups unchanged; verify it compiles and returns steps in declaration order.
- [x] 4.3 Preserve fail-fast validation for `timed` steps whose `time` is missing or unparseable, naming the offending step by index; verify a `timed` step without a valid `time` fails level loading with an error that identifies the step.

## 5. Scene wiring

- [x] 5.1 Remove the state machine from `EnemyManager` so it manages enemy entities only; verify it compiles and no longer references the FSM.
- [x] 5.2 Wire `LevelDirector` into `LevelScene`, ticked once per frame before the managers update (preserving today's order), and switch level-end detection to `LevelDirector.isLevelCleared()`; verify a smoke run advances through waves and ends the level on boss defeat.

## 6. XML migration

- [x] 6.1 Migrate `level-1.xml` to the `steps`/`actions`/`completionEvent` schema (each old `<horde>` becomes a `<step>` with one `spawnHorde` action; `hordeTimed`->`timed`, `hordeClearable`->`cleared`, `bossSpawned` unchanged), leaving `<enemiesPrototype>`/`<algorithmsPrototype>` intact; verify the level loads without error.

## 7. Build and smoke verification

- [x] 7.1 Run `mvn -DskipTests clean package` and confirm a green reactor build across `engine`, `game`, and `launcher`.
- [x] 7.2 Smoke-launch `launcher/target/tigersupply.jar`: waves spawn in order, `timed` and `cleared` gating behave as before, and defeating the boss ends the level, with no console errors.
