## 1. Engine contract

- [x] 1.1 Add a `private final String stateName`, a `protected AbstractState(String stateName)`
      constructor, and a concrete `getStateName()` to
      `engine.statemachine.AbstractState`; verify the engine module still compiles
      (`mvn -q -pl engine compile`).

## 2. Central factory

- [x] 2.1 Create `game.scene.statemachine.EnemySpawnStateMachineFactory` declaring the event-name
      `String` constants (`EVENT_PENDING`, `EVENT_READY`, `EVENT_HORDE_CLEARABLE`,
      `EVENT_HORDE_TIMED`, `EVENT_BOSS_SPAWNED`, `EVENT_BOSS_DEFEATED`) and the state-name `String`
      constants, with values identical to today's `GameResources` values; verify by diffing the
      literals against the current `GameResources`.
- [x] 2.2 Add the shared `public static final Event PENDING`, `READY`, `DEFEATED` singletons to the
      factory; verify they wrap the matching `EVENT_*` name constants.
- [x] 2.3 Add `public static StateMachine<EnemySpawnContext> build(EnemySpawnContext ctx)` that
      constructs the five states with injected names, builds the `TransitionTable` with the exact
      edges from the current `EnemyManager.initComponents()`, sets the table/context, sets
      `awaitingTimer` as the initial state, and returns the machine; verify the edge list matches the
      current wiring one-for-one.

## 3. State classes

- [x] 3.1 Update `StateAwaitingTimer` to take a name via `super(name)`, drop `getStateName()`, and
      return `EnemySpawnStateMachineFactory.READY`/`.PENDING`; keep its `onEnter` reset logic.
- [x] 3.2 Update `StateAwaitingClear` to take a name via `super(name)`, drop `getStateName()`, and
      return the factory `READY`/`PENDING` singletons.
- [x] 3.3 Update `StateSpawningHorde` to take a name via `super(name)` and drop `getStateName()`
      (its event still comes from `context.spawnNextHorde()`).
- [x] 3.4 Update `StateAwaitingBossDefeat` to take a name via `super(name)`, drop `getStateName()`
      and its `private final static Event` fields, and return the factory `DEFEATED`/`PENDING`.
- [x] 3.5 Update `StateLevelCleared` to take a name via `super(name)`, drop `getStateName()` and its
      `private final static Event PENDING`, keep `isFinal()==true`, and return the factory `PENDING`;
      verify the game module compiles after 3.1-3.5 (`mvn -q -pl game -am compile`).

## 4. Wiring and consumers

- [x] 4.1 Replace the state-machine construction block in `EnemyManager.initComponents()` with a
      single `stateMachine = EnemySpawnStateMachineFactory.build(spawnContext);` call, removing the
      now-unused state/table imports; verify the method no longer references `TransitionTable` or the
      `State*` classes directly.
- [x] 4.2 Point `EnemySpawnContext` and `HordeSpawner` at
      `EnemySpawnStateMachineFactory.EVENT_HORDE_TIMED` instead of `GameResources.EVENT_HORDE_TIMED`;
      verify no remaining reference to `GameResources.EVENT_*` exists in either file.

## 5. GameResources cleanup

- [x] 5.1 Remove the enemy-spawn `STATE_*` and `EVENT_*` constants from `GameResources` now that the
      factory owns them; verify a workspace search finds no remaining reference to the removed
      `GameResources` symbols.
- [x] 5.2 Remove the dead `GAME_STATE_*` and `GAME_EVENT_*` constants from `GameResources`; verify a
      workspace search confirms zero references to these identifiers and their string values.

## 6. Docs and verification

- [x] 6.1 Update the enemy-spawn-lifecycle subsystem docs (the pages showing the wiring inside
      `EnemyManager`) to point at `EnemySpawnStateMachineFactory`; verify the code snippet reflects
      the new single-call wiring.
- [x] 6.2 Full reactor build passes (`mvn -q clean install`).
- [x] 6.3 Manual smoke test: launch level-1, clear the timed and clearable hordes, spawn and defeat
      the boss, and confirm the machine reaches the level-cleared final state exactly as before.
