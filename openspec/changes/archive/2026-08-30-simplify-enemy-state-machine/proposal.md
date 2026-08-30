## Why

The enemy-spawn state machine spreads generic FSM plumbing across the game module: a ~55-line
hand-written `(state,event)->state` if/else (`EnemyTransitionManager`), a per-state
`setDataModel(...)` injection base (`StateAbstract`), the `new StateXxx(); setDataModel(...)` ritual
repeated on every transition, a self-looping terminal state annotated with a confusing comment, and
a "boss dead" fact duplicated between the machine and a boolean flag on `HordeSequencer`. None of
this is game-specific — it is framework machinery leaking into the game. Centralizing it in the
engine shrinks the game code and turns the engine's FSM into a genuinely reusable asset.

## What Changes

- **Engine FSM becomes generic and reusable**: the state machine carries a typed context `C`,
  threads it to each state, performs the transition lookup itself, and exposes the current outcome.
- **Declarative transition table in the engine**: a generic table (`add` / `selfLoop` / `markFinal`)
  replaces the hand-written if/else. The game declares its transitions in a handful of readable lines.
- **Final-state semantics in the engine**: a state can be marked final; when the machine reaches a
  final state it halts (ticks become no-ops) and `isInFinalState()` reports it.
- **`onEnter(context)` lifecycle hook in the engine**: invoked when the machine transitions into a
  state, so states can reset per-entry data without per-instance flags.
- **States are built once and reused** as stateless strategies instead of re-instantiated per edge.
- **New terminal `StateBossKilled`** replaces the self-looping `StateKillBoss` hack; reaching it is
  the single source of truth for "boss dead / level won".
- **BREAKING** (engine-internal only): the `State` / `AbstractState` / `TransitionManager` contract
  changes shape. The engine FSM has exactly one consumer (the enemy machine), which is migrated in
  the same change.
- **Removals in the game**: `EnemyTransitionManager`, `StateAbstract`, the `HordeSequencer` boss-dead
  flag (`bossKilled` / `markBossAsKilled()` / `isBossDead()`), `EnemyBuilderDataModel.bossKilled()`,
  and the `StateWaitTime.init` flag.
- **Out of scope** (unchanged): observable gameplay (same hordes, timing, boss-ends-level),
  `SceneFlowController` and the `LevelScene` player-death poll, the dead XML `time` attribute, and
  the `String`-based state/event constants in `GameResources`.

## Capabilities

### New Capabilities
- `engine-state-machine`: the engine's reusable finite-state-machine framework — typed context,
  one-transition-per-tick execution, a declarative transition table, final-state halting, and an
  `onEnter` lifecycle hook — with no reference to any concrete game type.

### Modified Capabilities
<!-- None: observable game behavior is unchanged (pure refactor on the game side); the enemy
     spawn sequence, timings, and level-end remain identical, so no game-module requirement changes. -->

## Impact

- **Engine** (`it.spaghettisource.tigersupply.engine.statemachine`): `StateMachine` /
  `StateMachineImpl`, `State`, `AbstractState`, `TransitionManager`, plus a new generic transition
  table type. New `State` capabilities: typed context parameter, `isFinal()`, `onEnter(context)`.
- **Game** (`it.spaghettisource.tigersupply.game.scene.statemachine` + `game.entity.EnemyManager`,
  `game.builder.HordeSequencer`): delete `EnemyTransitionManager` and `StateAbstract`; add
  `StateBossKilled`; make the four existing states stateless; `EnemyManager` builds the states once,
  populates the table, and reports `isBossDeath()` via `stateMachine.isInFinalState()`.
- **No new runtime dependencies**; pure JDK. No changes to level XML, catalogs, or packaging.
- **Docs**: the `documentation/subsystems/enemy-spawn-lifecycle/` pages will need follow-up updates
  (tracked separately from this change's code tasks).
