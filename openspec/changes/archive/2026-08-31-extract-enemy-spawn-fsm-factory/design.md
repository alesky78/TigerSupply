## Context

See proposal.md - Why. Today the enemy-spawn FSM is assembled inline in
`EnemyManager.initComponents()`: it constructs five `State*` instances, builds an
`engine.statemachine.TransitionTable<EnemySpawnContext>`, wires the graph, and sets the initial
state. State names and event names are `String` constants in `GameResources`; the `Event` instances
are partly `private final static` fields inside individual states and partly inline `new Event(...)`.
A subset of the event names (`hordeTimed`, `hordeClearable`, `bossSpawned`) is also the vocabulary
that the level XML uses and that `HordeSpawner`/`EnemySpawnContext` match against at runtime.

Constraints:
- The engine `State`/`StateMachine`/`TransitionTable` contracts stay generic and game-agnostic.
- Runtime behavior must be byte-for-byte equivalent: same states, events, transitions, initial state.
- `AbstractState` is extended only by the five enemy-spawn states (verified), so its contract can be
  adjusted with a contained blast radius.

## Goals / Non-Goals

**Goals:**
- One class (`EnemySpawnStateMachineFactory`) is the single source of truth for the FSM: state
  instances + their names, the shared event singletons + event-name constants, the transition graph,
  and the initial state — readable top-to-bottom without opening any `State*` class.
- Reduce each `State*` class to pure decision logic (which event to emit).
- Remove dead constants from `GameResources`.

**Non-Goals:**
- No change to the horde-sequencing or time-gated-delay behavior (owned by the unchanged
  `enemy-spawn-lifecycle` spec).
- No renaming of the state/event string *values* (`"awaitingTimer"`, `"hordeTimed"`, ...) — the XML
  contract and the FSM keys stay identical.
- No generalization of the engine state-machine framework beyond the small `AbstractState` change.

## Decisions

### D1 — The factory returns a fully-wired `StateMachine`, not a bare `TransitionTable`

`EnemySpawnStateMachineFactory.build(EnemySpawnContext ctx)` returns a ready
`StateMachine<EnemySpawnContext>` with transition table, context, and initial state already set.

Rationale: the class already owns the state instances and the initial state; handing back only a
`TransitionTable` would push initial-state selection back into `EnemyManager` and re-fragment the
very thing we are centralizing. The `TransitionTable` still exists — as a private build step inside
the factory — so it remains fully visible in one place, satisfying the "studiable table" goal.

Alternative considered: return `TransitionTable` (or a `{table, initialState}` holder). Rejected
because it leaks wiring back to the caller and makes the class name (`...StateMachineFactory`) lie.

### D2 — Name injection via a constructor on `AbstractState` (Option B)

`AbstractState<C>` gains a `private final String stateName`, a `protected AbstractState(String)`
constructor, and a concrete `getStateName()`. Each `State*` calls `super("<name>")` and drops its own
`getStateName()`. The factory passes every name literal at construction time:

```
State<Ctx> awaitingTimer      = new StateAwaitingTimer("awaitingTimer");
State<Ctx> awaitingClear      = new StateAwaitingClear("awaitingClear");
State<Ctx> spawningHorde      = new StateSpawningHorde("spawningHorde");
State<Ctx> awaitingBossDefeat = new StateAwaitingBossDefeat("awaitingBossDefeat");
State<Ctx> levelCleared       = new StateLevelCleared("levelCleared");
```

Rationale: the user wants every name visible by opening a single class. With injection, the state
classes no longer carry identity, and the factory shows class + name + edges together.

Alternative considered (Option A, relocation): keep `getStateName()` in each state, returning a
constant relocated to the factory. Rejected per the user's explicit preference for names living in
the factory. A non-invasive variant (per-state name field, engine untouched) was also rejected as
more boilerplate for the same result.

Trade-off: this edits an engine-level class. Mitigated by the contained blast radius (five
subclasses, all updated in this change).

### D3 — Event singletons and event-name constants both live in the factory

The factory declares:
- `public static final String EVENT_PENDING/READY/HORDE_CLEARABLE/HORDE_TIMED/BOSS_SPAWNED/BOSS_DEFEATED`
  — the canonical event-name vocabulary, also used as transition keys and for XML matching.
- `public static final Event PENDING/READY/DEFEATED` — the immutable singletons that states emit
  directly.

States reference these singletons statically (e.g. `return EnemySpawnStateMachineFactory.PENDING;`),
replacing today's `private final static Event ...` fields and inline `new Event(...)`.

Note the asymmetry: `SpawningHorde`'s outcome events (`hordeTimed`/`hordeClearable`/`bossSpawned`)
are built at runtime by `HordeSpawner` from the XML-declared name (`new Event(desc.getName())`), so
they are *not* singletons — only their *name* constants live in the factory. This is intentional:
those events are data-driven, the state-emitted ones are fixed.

Rationale: keeps all event identity in one file while respecting that some events are authored in XML.

### D4 — `HordeSpawner`/`EnemySpawnContext` depend on the factory for `EVENT_HORDE_TIMED`

Moving the event constants out of `GameResources` means these two classes now import
`EVENT_HORDE_TIMED` from `EnemySpawnStateMachineFactory`. They live in the same
`game.scene.statemachine` package, so this improves cohesion rather than adding cross-package
coupling.

### D5 — Class name avoids the engine `TransitionTable` collision

`EnemySpawnStateMachineFactory` (in `game.scene.statemachine`) is a distinct simple name from
`engine.statemachine.TransitionTable`, avoiding the sibling-package name collision the repo
conventions warn about, and honestly describing what it produces.

### D6 — Remove dead `GAME_STATE_*` / `GAME_EVENT_*` constants

These seven constants in `GameResources` are unused as identifiers and as string values (verified).
They describe a scene-manager FSM that no longer exists in code. Removing them is safe and included
here as an adjacent cleanup.

## Risks / Trade-offs

- **[Engine contract change to `AbstractState`]** → All five subclasses are updated in the same
  change; no other subclass exists. Compilation catches any miss.
- **[Static reference from states to factory `Event` singletons creates an apparent state->factory
  dependency]** → Acceptable: they are immutable `static final` fields initialized at class load;
  no runtime cycle, and the states already depended on `GameResources` for the same values.
- **[Raw string names passed at construction are typo-prone]** → All literals sit next to each other
  in the factory and next to the transition graph, so a wrong name is easy to spot in review; values
  are unchanged from today so a diff confirms parity.
- **[Behavior regression]** → Mitigated by keeping every state/event value identical and by a manual
  run of level-1 through boss defeat after the change (no automated tests exist).

## Migration Plan

Single atomic refactor; no data migration. Rollback is a straight revert. Manual smoke test: launch
the game, clear level-1's timed and clearable hordes, spawn and defeat the boss, confirm the level
reaches the cleared/final state as before.
