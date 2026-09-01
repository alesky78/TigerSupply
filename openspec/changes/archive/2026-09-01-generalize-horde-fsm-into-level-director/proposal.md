## Why

The enemy-spawn finite-state machine only knows one verb: spawn a horde of enemies. Its single
action state (`StateSpawningHorde`) hard-wires "instantiate these enemies", and the machine lives
inside `EnemyManager`, so it can only ever touch enemies. This blocks a whole class of choreography a
90s-style shmup needs — for example a foreground **base** structure (top/bottom of the screen, with
its own collision) that the level script must be able to halt or advance in lock-step with a wave of
enemies. Today the "base" is faked as non-colliding `EnemyBackGround` enemies precisely because there
is no other seam to drive it.

The fix is to separate *what a wave does* from *how a wave completes*: generalize the single
enemy-spawn action into an ordered list of pluggable **actions** owned by a **level director**, so the
same sequencing engine can command enemies, the base, the background, audio, and future subsystems.

## What Changes

- **Generalize the action state.** `StateSpawningHorde` becomes `StateExecutingStep`: it runs an
  ordered list of actions for the current step and then emits the step's completion event. The FSM
  graph keeps its shape (the wait states and their transitions are unchanged).
- **Introduce a pluggable action abstraction.** A new `LevelAction` (Command) type, instantiated by
  fully-qualified class name via a `LevelActionFactory`, consistent with the existing
  `EntityFactory` / `UpdateAlgorithmFactory` reflection pattern. Actions are **fire-and-forget**
  (imperative, one-tick side effects); any durative behavior lives in the subsystem the action
  commands. First concrete action: `SpawnHordeAction` (today's enemy-spawn logic). Other action
  types (background motion, base motion, audio cues) are the open extension point.
- **Separate completion from action.** Each step declares a **completion event** (closed vocabulary:
  `timed`, `cleared`, `bossSpawned`, `bossDefeated`) that routes the FSM to a wait state. `bossSpawned`
  stops being a horde type and becomes just a completion event; spawning the boss is an ordinary
  action.
- **Hoist the FSM out of `EnemyManager` into a `LevelDirector`.** The director owns the machine and a
  broadened `DirectorContext` that can reach multiple subsystems (enemy manager now; base manager,
  background, audio later). `EnemyManager` goes back to managing enemy entities only.
- **BREAKING — restructure the level XML.** `<hordes>/<horde>` + `<generateEvent>` are replaced by
  `<steps>/<step>` with an inner `<actions>` list and a trailing `<completionEvent>`. No
  backward-compatible `<horde>` alias is kept; `level-1.xml` is migrated. `<enemy>`,
  `<enemiesPrototype>`, and `<algorithmsPrototype>` are unchanged.
- **Coherent rename.** `Horde` -> `Step`, `GenerateEvent` -> `CompletionEvent`, `HordeSpawner`
  decomposed (spawn logic -> `SpawnHordeAction`, sequencing -> `LevelDirector`), `EnemySpawnContext`
  -> `DirectorContext`, `EnemySpawnStateMachineFactory` -> `LevelDirectorStateMachineFactory`,
  `StateSpawningHorde` -> `StateExecutingStep`, event names `hordeTimed`/`hordeClearable` -> `timed`/
  `cleared`.

Out of scope for this change: the concrete colliding base entity/manager is **not** built here (it is
a separate ad-hoc entity, driven later by a base-motion action); the existing faked `EnemyBackGround`
decorations stay as-is; the engine `statemachine` package and the `Event` payload side-channel are
**not** touched (the `timed` wait still travels via `waitTime` as today).

## Capabilities

### New Capabilities
<!-- none: the generalization broadens the existing capability rather than adding a new one -->

### Modified Capabilities
- `enemy-spawn-lifecycle`: sequencing broadens from "spawn hordes" to "execute an ordered list of
  step actions, then emit the step's completion event". The level definition is restructured to
  steps + actions + completion event; the completion-event vocabulary is renamed (`timed`/`cleared`/
  `bossSpawned`/`bossDefeated`) while the authored time-gated delay behavior is preserved.

## Impact

- **Game module (code).** `EnemyManager` (FSM hoisted out), new `game.scene.director.LevelDirector`
  + `DirectorContext`, `LevelDirectorStateMachineFactory`, `StateExecutingStep`, `LevelAction` +
  `LevelActionFactory` + `SpawnHordeAction`; `HordeSpawner` decomposed; `LevelScene` wiring (ticks the
  director; broadened context). Renames of `Horde`/`GenerateEvent`/`EnemySpawnContext` and the state
  classes.
- **Game module (resources).** `level/level-1.xml` fully migrated to the `steps`/`actions`/
  `completionEvent` schema; the SAX builder (`EnemyDataBuilderSaxXml`) and `LevelDataRepository`
  updated to the new element structure.
- **Engine module.** No change (the generic `statemachine` package and `Event` remain untouched).
- **Docs.** The `enemy-spawn-lifecycle` subsystem pages describe the new step/action/completion model
  and the director hoist once implemented.
