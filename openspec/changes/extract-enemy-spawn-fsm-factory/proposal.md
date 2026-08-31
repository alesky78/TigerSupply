## Why

The enemy-spawn finite-state machine's definition is scattered across four places — the
`STATE_*`/`EVENT_*` constants in `GameResources`, the transition-graph wiring in
`EnemyManager.initComponents()`, the per-state name lookups and `private final static Event`
fields inside each `State*` class, and the event-name vocabulary shared with the level XML. This
makes the machine hard to study or reason about as a whole. Centralizing the whole definition in one
class makes it comprehensible at a glance, with no change to runtime behavior.

## What Changes

- Introduce `EnemySpawnStateMachineFactory` in `game.scene.statemachine`, a single class that
  builds and returns a fully-wired `StateMachine<EnemySpawnContext>` — it owns the state instances,
  the shared `Event` singletons, the state-name and event-name constants, the transition graph, and
  the initial state, all in one readable place.
- Relocate the enemy-spawn `STATE_*` and `EVENT_*` string constants out of `GameResources` into the
  factory (they remain the canonical event vocabulary shared with the level XML contract).
- Relocate the shared `Event` singletons (today `private final static Event ...` in states and
  inline `new Event(...)`) into the factory as public singletons the states reference.
- Inject each state's name through its constructor (Option B): add a `stateName` field and
  protected constructor to the engine `AbstractState`, make `getStateName()` concrete there, and
  reduce each `State*` class to its decision logic only.
- Slim `EnemyManager.initComponents()` down to preparing the context/spawner and a single
  `EnemySpawnStateMachineFactory.build(spawnContext)` call.
- Point `HordeSpawner` and `EnemySpawnContext` at the relocated `EVENT_HORDE_TIMED` constant.
- **Remove** the dead `GAME_STATE_*` / `GAME_EVENT_*` constants from `GameResources` (verified
  unused as both identifiers and string values).
- Update the enemy-spawn-lifecycle subsystem documentation to reflect the new wiring location.
- No runtime behavior change: identical states, events, transitions and initial state.

## Capabilities

### New Capabilities

None. This is a pure internal refactor that introduces no new observable behavior.

### Modified Capabilities

None. The `enemy-spawn-lifecycle` spec is behavior-level (horde sequencing and time-gated delays)
and is unchanged by this refactor. The change sets `skip_specs: true` in its `.openspec.yaml`.

## Impact

- **New code**: `EnemySpawnStateMachineFactory` (`game.scene.statemachine`).
- **Modified code**: `EnemyManager`, the five `State*` classes, `HordeSpawner`, `EnemySpawnContext`,
  `GameResources`, and the engine `AbstractState`.
- **Engine contract**: `AbstractState` gains a protected constructor and a concrete
  `getStateName()`. Blast radius is contained — the five enemy-spawn states are its only subclasses.
- **Docs**: enemy-spawn-lifecycle subsystem pages that show the wiring inside `EnemyManager`.
- **No** XML changes, **no** dependency changes, **no** behavior change.
