## 1. Baseline

- [x] 1.1 Build and smoke-launch the current game (JDK 17: set `JAVA_HOME` to Temurin 17, `mvn -q -DskipTests clean package`, run `launcher/target/tigersupply.jar`) and record baseline behavior: a full level plays, `waitTime`/`waitKill` waves spawn on cadence, and killing the boss advances the level. Verify: game runs with no errors.

## 2. Engine FSM contract (generic, final states, on-enter, context)

> The engine reshape is breaking; it will not compile until the game consumer is migrated in section 3. Land sections 2 and 3 together before building.

- [x] 2.1 Make `State` generic over a context `C`: replace the successor-fetching contract with `Event process(C context)`, add `boolean isFinal()` (default `false`) and `void onEnter(C context)` (default no-op), keep `getStateName()`. Verify: interface compiles in isolation.
- [x] 2.2 Reduce `AbstractState` to the shared `try/catch` wrapping around `process(context)` (no transition-table lookup inside the state). Verify: compiles.
- [x] 2.3 Add a single concrete `TransitionTable<C>` to the engine (map keyed on `(stateName, eventName)` -> target `State<C>`) with `add(from, event, to)`, `selfLoop(state, event)`, and a `next(state, event)` lookup that raises the existing unsupported-event and unsupported-state errors on a miss; drop the `TransitionManager` interface. Finality lives on `State.isFinal()`, not the table. Verify: a small in-code table resolves declared pairs and throws on undeclared ones (temporary `main`/scratch or a JUnit test, then removed/kept).
- [x] 2.4 Update `StateMachine`/`StateMachineImpl` to hold the shared context, run the current state once per tick, look up and adopt the next state, invoke `onEnter(context)` only when the state actually changes, no-op while the current state is final, and expose `isInFinalState()`. Verify: compiles; ticking a final state makes no transition.

## 3. Migrate the enemy machine to the new contract

- [x] 3.1 Rename `EnemyBuilderDataModel` to `EnemySpawnContext` and use it as the machine's context `C`; remove its `bossKilled()` delegate. Verify: compiles.
- [x] 3.2 Make the four existing states stateless and context-driven: `StateWaitTime` resets the timer in `onEnter` (delete the `init` flag) and reads `elapsedTime` from the context; `StateWaitKill`, `StateGenerateHorde`, `StateKillBoss` take the context as a parameter. `StateKillBoss` emits the boss-killed event on empty scene and no longer writes any side effect. Verify: compiles.
- [x] 3.3 Add the final state `StateBossKilled` (`isFinal()` returns `true`) using a distinct state constant value in `GameResources` (not colliding with `EVENT_BOSS_KILLED`). Verify: compiles.
- [x] 3.4 In `EnemyManager.initComponents()`, build each state once, populate the `TransitionTable<C>` declaratively (the ~9-edge enemy graph, with `killBoss --bossKilled--> bossKilled`, where `bossKilled` is a final state via `StateBossKilled.isFinal()`), and set the machine's context. Delete `EnemyTransitionManager` and `StateAbstract`. Verify: compiles; the declared graph matches the documented edges.
- [x] 3.5 Change `EnemyManager.isBossDeath()` to return `stateMachine.isInFinalState()`, and remove `HordeSequencer.bossKilled` / `markBossAsKilled()` / `isBossDead()`. Verify: compiles; no remaining references to the removed members.

## 4. Verification

- [x] 4.1 Build the full reactor on JDK 17 (`mvn -q -DskipTests clean package`) and confirm the engine module still compiles standalone with no reference to any game type. Verify: build is green.
- [x] 4.2 Smoke-launch `launcher/target/tigersupply.jar` and play a full level: multiple `waitTime`/`waitKill` waves spawn on the same cadence as the baseline, and killing the boss triggers `doNextLevel()`. Verify: behavior matches the 1.1 baseline with no errors.
- [x] 4.3 Grep to confirm the removals: no references to `EnemyTransitionManager`, `StateAbstract`, `markBossAsKilled`, or a `bossKilled` flag on `HordeSequencer` remain. Verify: searches return no hits.
