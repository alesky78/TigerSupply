## Context

See [proposal.md](proposal.md) for motivation. The relevant current state:

- The enemy-spawn FSM is generic in the engine (`engine.statemachine`), but the concrete states,
  the context (`EnemySpawnContext`), and the wiring live in `game` and are enemy-only. The single
  action state `StateSpawningHorde` hard-wires "spawn these enemies".
- The FSM is owned by `EnemyManager` and its context reaches only `HordeSpawner`, so the machine can
  physically touch nothing but enemies.
- The level XML pairs, per horde, *what to spawn* with a `<generateEvent>` that declares *how the
  horde completes* (`hordeTimed`/`hordeClearable`/`bossSpawned`). The completion vocabulary already
  routes the FSM; the action is the only thing hard-wired.
- The "base" in classic-shmup style is faked today as non-colliding `EnemyBackGround` enemies,
  because there is no other seam a level script can drive.

## Goals / Non-Goals

**Goals:**

- Separate *what a step does* (an ordered list of pluggable actions) from *how a step completes*
  (a closed completion-event vocabulary that routes the FSM).
- Provide a single `LevelDirector` that owns the FSM and can command multiple subsystems through a
  broadened context, so future actions (base motion, background motion, audio) have a home.
- Keep the change confined to the `game` module; leave the engine `statemachine` package untouched.

**Non-Goals (design-level boundaries):**

- No concrete colliding base entity/manager is built here. This change only establishes the action
  seam that a future base-motion action will use. See proposal.md - What Changes.
- No change to the engine `Event` type or the `onEnter` contract: the `timed` wait keeps travelling
  through the shared context (`waitTime`), exactly as today. The alternative (payload on `Event`
  delivered to `onEnter`) stays deferred — captured in the subsystem note
  `documentation/subsystems/enemy-spawn-lifecycle/migliorie-payload-eventi.md`.
- The faked `EnemyBackGround` decorations are not removed or migrated.

## Decisions

### D1 — Generalize the action state into `StateExecutingStep`

A step carries an ordered list of actions plus one completion event. `StateExecutingStep.internalProcess`
runs each action then returns the step's completion `Event`. The FSM graph keeps its exact shape;
only the action node changes.

```
awaitingTimer --ready--> [ executeStep ] --timed-->       awaitingTimer
awaitingClear --ready--> [ executeStep ] --cleared-->     awaitingClear
                         [ executeStep ] --bossSpawned--> awaitingBossDefeat
awaitingBossDefeat --bossDefeated--> levelCleared (final)
```

- **Alternatives considered.** (a) A separate parallel timeline for the base — rejected: two
  timelines to keep in sync, and "when *this* wave spawns, halt the base" needs fragile
  cross-references. (b) Actions attached to FSM transitions (Mealy flavor) — rejected: bigger,
  touches the engine, and is harder to author than an explicit per-step action list.

### D2 — Actions are pluggable Commands, fire-and-forget

`LevelAction` is a Command (`execute(DirectorContext)`), instantiated by fully-qualified class name
via a `LevelActionFactory`, mirroring the existing `EntityFactory` / `UpdateAlgorithmFactory`
reflection pattern. Actions are imperative and complete within the tick; any durative behavior lives
in the subsystem the action commands (e.g. a future base manager keeps moving on its own after a
`setBaseMotion` action tells it to).

- **Rationale.** Keeps actions trivial and the step atomic, and preserves the FSM's one-transition-
  per-tick semantics (identical to how `StateSpawningHorde` runs once and immediately emits its
  event today).
- **Alternative considered.** Durative actions that block a step until finished — rejected: would
  require an "action in progress" concept and complicate the FSM for no current need.

### D3 — Asymmetry: actions open, completion closed

Actions are the open extension point (any FQN). The completion vocabulary is a small, closed set —
`timed`, `cleared`, `bossSpawned` (plus the internal `bossDefeated`) — because each value maps to a
dedicated wait state. Making completion open would multiply FSM states.

### D4 — Hoist the FSM into a `LevelDirector`

Move the machine out of `EnemyManager` into a `game.scene.director.LevelDirector`, ticked once per
frame by `LevelScene`. Broaden `EnemySpawnContext` into `DirectorContext`, which can reach several
subsystems (enemy manager today; base manager, background, audio later).

```
LevelScene
  |
  +-- LevelDirector          -- owns FSM + DirectorContext; tick 1x/frame
  |       ctx --> { enemyManager, [background], [baseManager future], ... }
  +-- EnemyManager           -- enemy entities only
  +-- background (decor)      -- engine.background.*, unchanged
```

- **Rationale.** A step can command more than enemies only if the context can reach more than
  enemies. Hoisting also honors the engine/game separation the subsystem doc defends.
- **Alternative considered.** Keep the FSM inside `EnemyManager` — rejected: the context could not
  reach the base without `EnemyManager` depending on unrelated subsystems.

### D5 — Full XML restructure, no backward compatibility

`<hordes>/<horde>` + `<generateEvent>` become `<steps>/<step>` with an inner `<actions>` list and a
trailing `<completionEvent>` (placed after the actions so the file reads in execution order: run
actions, then emit the event). `<enemy>`, `<enemiesPrototype>`, `<algorithmsPrototype>` are
unchanged; enemies now live inside `<action type="spawnHorde">`.

```xml
<step>
  <actions>
    <action type="spawnHorde">
      <enemy enemyPrototype="standard-2" posX="1350" posY="150" posZ="20" algorithmPrototype="default" />
    </action>
    <action type="setBaseMotion" state="halt" />
  </actions>
  <completionEvent name="timed" time="1.0" />
</step>
```

- **Naming rationale.** `completionEvent` chosen over `onComplete` and `generateEvent`: the value
  literally becomes the engine `Event` name that routes the FSM, so "event" must stay in the name;
  `onComplete` wrongly implies a code handler and breaks the file's `name=`-as-identifier convention;
  a noun element maps cleanly to a `CompletionEvent` model type (the rename of `GenerateEvent`). The
  temporal reading ("do, then complete") comes from placing the element after `<actions>`, not from
  the name. `cleared` chosen over `clearable` for the screen-clear completion value.
- **Alternative considered.** Keep `<horde>` as sugar for a single-`spawnHorde` step — rejected: the
  step/actions model is the whole point; a hybrid schema would muddy it.

### D6 — Coherent rename

`Horde`→`Step`, `GenerateEvent`→`CompletionEvent`, `HordeSpawner` decomposed (spawn logic →
`SpawnHordeAction`; sequencing → `LevelDirector`), `EnemySpawnContext`→`DirectorContext`,
`EnemySpawnStateMachineFactory`→`LevelDirectorStateMachineFactory`,
`StateSpawningHorde`→`StateExecutingStep`, event names `hordeTimed`/`hordeClearable`→`timed`/`cleared`.

## Risks / Trade-offs

- Generalizing infrastructure for a single current call-site (only `SpawnHordeAction` exists at
  first) → Mitigation: the base-motion and background actions are the near-term consumers that
  justify it, and the pattern reuses the existing reflection factory, so the incremental cost is low.
- Large mechanical migration of `level-1.xml` (every `<horde>` → `<step>`) → risk of transcription
  errors → Mitigation: migrate mechanically and smoke-test the level end-to-end after the change.
- Retaining the `waitTime` side-channel for the `timed` payload keeps a known implicit coupling → the
  step model makes it slightly more visible, but D-Non-Goal keeps the engine untouched; revisit via
  the deferred payload note if more parametric completion events appear.
- Hoisting the director into the scene changes the per-frame update order (director tick vs. manager
  updates) → Mitigation: preserve today's ordering (tick the director, then update managers) when
  wiring `LevelScene`.
- Rename ripple into the level XML and class hierarchy (a hazard the repo instructions call out) →
  Mitigation: this change deliberately owns that rename as its explicit scope, in one pass.

## Migration Plan

- Introduce the new director/action classes and the new XML schema, migrate `level-1.xml`, then
  remove the retired `Horde`/`GenerateEvent`/`HordeSpawner`/`StateSpawningHorde` code paths.
- Rollback is a plain revert: the game is a single-process offline app with no persisted state or
  saved levels beyond `level-1.xml`.

## Open Questions

These are safely deferrable — they do not affect this change's specs, approach, or task breakdown,
because the concrete base is out of scope here:

- The base's collision model (indestructible wall vs. destructible turrets, or both in one manager)
  is decided when the base entity/manager is actually built.
- Whether base motion is a simple enum mode or its own engine-FSM instance is likewise deferred to
  the base change.
