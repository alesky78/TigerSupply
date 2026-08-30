## Context

See proposal.md — Why. The engine's FSM (`engine.statemachine`) has exactly one consumer today: the
enemy-spawn machine in the game module. The current `State` contract has each state ask the transition
manager for its own successor (`processState(txManager)`), each state carries its shared data via a
game-side `StateAbstract.setDataModel(...)` base, and every transition re-instantiates the target
state and re-injects the data model. "Boss dead" is represented twice: as the machine looping forever
in `StateKillBoss` and as a boolean flag on `HordeSequencer` read by `LevelScene`'s poll. Because the
framework has a single consumer, the `State`/`AbstractState`/`TransitionManager` contract can be
reshaped freely as long as the enemy machine is migrated in the same change.

## Goals / Non-Goals

**Goals:**
- Move the generic FSM plumbing (transition table, final-state halting, on-enter lifecycle, context
  threading) into the engine so it is reusable and the game side shrinks.
- Make "boss dead / level won" a single source of truth: the machine being in a final state.
- Preserve observable gameplay exactly (same hordes, timings, and level-end behavior).

**Non-Goals:**
- Folding the `LevelScene` player-death poll or `SceneFlowController` scene navigation into this FSM
  (they solve a broader, app-level concern with many unrelated triggers — verified: 5 call sites,
  mostly UI/bootstrap).
- Wiring the dead XML `time` attribute or converting `String` state/event constants to enums.
- Any change to level XML, catalogs, packaging, or runtime dependencies.

## Decisions

### D1 — Thread a typed context `C` through the machine; the machine does the lookup (Design Y)
The engine `State<C>` computes only an event from a supplied context (`Event process(C)`); the
machine holds the shared context and performs the table lookup itself. `StateAbstract` (game) is
deleted because there is nothing left to inject per state.
- **Alternative (Design X, rejected):** keep states asking the table (`processState(txManager)`) and
  keep `StateAbstract` holding the data model, injected once at build. Less code churn in the engine,
  but leaves framework plumbing (`StateAbstract`, state→table coupling) in the game and does not make
  the engine FSM genuinely reusable — contrary to the stated goal.

### D2 — Declarative transition table in the engine (a single `TransitionTable<C>` type)
Provide a single concrete generic type `TransitionTable<C>` populated by declaration
(`add(from, event, to)`, `selfLoop(state, event)`) with a `next(state, event)` lookup keyed on the
`(stateName, eventName)` pair that raises the existing distinct unsupported-event / unsupported-state
errors on a miss. Finality is not a table concern — it lives on the state (`State.isFinal()`, see D3),
so a final state simply has no outgoing entries. The `TransitionManager` interface is dropped (not
replaced by a generic implementation) and the game's `EnemyTransitionManager` (~55-line if/else) is
deleted; the enemy graph is declared in ~9 lines at the wiring site (`EnemyManager.initComponents()`).
- **Alternative (rejected) — keep a `TransitionManager` interface** with a generic map-based
  implementation (or interface + `TransitionTable` impl): retains an extension point for custom
  transition strategies, but that point has no consumer today (YAGNI) and "manager" names a behavior
  to write rather than a data structure to fill. A single `TransitionTable` type communicates the new
  declarative intent with the fewest types; the interface can be reintroduced if a computed-transition
  machine is ever needed.

### D3 — Final-state semantics in the engine (the "elegant" variant)
A state is declarable final; `event()` is a no-op while the current state is final, and the machine
exposes `isInFinalState()`. `EnemyManager.isBossDeath()` becomes `stateMachine.isInFinalState()`.
- **Alternative (rejected, "minimal"):** expose only `getCurrentStateName()` and compare the string in
  the game. Works, but pushes a framework concept (terminal state) back into game string-matching and
  keeps the self-looping state. The final-state concept is a standard, reusable FSM capability.

### D4 — Reuse state instances built once
States become stateless strategies constructed once at wiring time and referenced by the table, so the
`new StateXxx(); setDataModel(...)` ritual disappears. This is a direct consequence of D1+D2 (the table
returns target-state references).

### D5 — `onEnter(context)` lifecycle hook
The machine invokes `onEnter(context)` when a tick changes the current state to a different state (not
on self-transitions). `StateWaitTime` resets the elapsed-time counter in `onEnter` instead of via a
per-instance `init` flag, which would otherwise stick `true` on a reused singleton and break the
second `waitTime` wave.
- **Alternatives (rejected):** reset the timer inside the transition wiring (leaks state semantics
  into the table) or keep a manually-reset local timer in the state (re-introduces per-instance
  mutable state, defeating singleton reuse). `onEnter`/`onExit` is the conventional FSM hook; only
  `onEnter` is added now (no `onExit` until needed).

### D6 — New terminal `StateBossKilled`; remove the duplicate boss-dead flag
Split the overloaded `StateKillBoss`: it stays the "waiting for boss to die" state and, when the scene
empties, emits the boss-killed event to transition into the new final `StateBossKilled`. Reaching the
final state is the single source of truth, so `HordeSequencer.bossKilled` / `markBossAsKilled()` /
`isBossDead()` and `EnemySpawnContext.bossKilled()` are removed, along with the self-loop and its
comment. `StateKillBoss` no longer writes any side effect.

### D7 — Naming
- **Context type:** rename `EnemyBuilderDataModel` to `EnemySpawnContext`. The class is the machine's
  shared context `C`; the new name states what it *is* rather than how it was built.
- **Transition table type:** a single concrete `TransitionTable<C>` (see D2), no `TransitionManager`
  interface.

## Risks / Trade-offs

- **Reshaping the engine `State`/`AbstractState` contract and replacing the `TransitionManager`
  interface with `TransitionTable<C>` is breaking** → Mitigation: the framework has a single verified
  consumer (the enemy machine), migrated in the same change; a full build + smoke launch validates it.
- **Timer-reset regression when states are reused** (the `init`-flag trap) → Mitigation: D5's
  `onEnter` resets on every entry; verified by a level with multiple `waitTime` waves spawning on the
  expected cadence.
- **Behavior drift in horde sequencing/level-end** → Mitigation: the transition graph is preserved
  edge-for-edge (only the terminal self-loop becomes an explicit final node); smoke-test a full level
  through the boss to confirm `doNextLevel()` still fires.
- **Event/state string-namespace collision** (`EVENT_BOSS_KILLED` vs a new `STATE_BOSS_KILLED` would
  both be `"bossKilled"`) → Mitigation: the boss-killed event is engine-internal (absent from the XML,
  which uses only `bossGenerated`), so give the new final state a distinct constant value; lookups key
  on the pair regardless.
