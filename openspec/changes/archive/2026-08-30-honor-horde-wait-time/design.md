## Context

See proposal.md — Why. The enemy-spawn state machine ticks once per frame from
`EnemyManager.updateEntity(float deltaSeconds)`, which accumulates the frame delta into a shared
context (`EnemySpawnContext`) and runs the current state. `StateGenerateHorde` delegates to
`HordeSpawner.spawnNextHorde()`, which builds the horde's enemies and returns an `Event` created from
the horde's `generateEvent` name — dropping the parsed `time`. `StateWaitTime` then compares the
accumulated `elapsedTime` against a hard-coded `1`.

Two facts constrain the approach:

- The engine's entire time axis is `float` seconds (`Scene.update(float)`, `updateEntity(float)`,
  `getPeriodSeconds()`), but `EnemySpawnContext.elapsedTime` / `increaseElapsedTime(double)` are the
  lone `double` outliers, silently widening the `float` delta.
- `Event` (engine, game-agnostic) carries only a name, and neither `onEnter` nor `internalProcess`
  receives the triggering event — so a state cannot read the event that activated it.

## Goals / Non-Goals

**Goals:**
- Drive the inter-wave delay from the level XML `time` attribute.
- Keep the spawn timer on the same `float`-seconds base as the rest of the framework.
- Fail fast at load time when a `waitTime` horde has no valid `time`.

**Non-Goals:**
- No change to the engine `Event` type or the state-machine contract.
- No new validation rejecting a stray `time` on non-`waitTime` events (cleaned in XML instead).
- No change to `waitKill` / boss sequencing behavior.

## Decisions

**Carry the delay on the shared context, not on the `Event`.**
The wait duration is stored in `EnemySpawnContext` (a new `float waitTime` field) and read by
`StateWaitTime`. Chosen over adding a payload to `Event` because `Event` is game-agnostic engine code
and the triggering event is not delivered to the next state anyway; the context is already the
blackboard threaded to every state (it holds `elapsedTime`). `HordeSpawner` sets the context's
`waitTime` when it spawns a time-gated horde, symmetric with how `elapsedTime` already lives there.

**Uniform `float` seconds.**
`EnemySpawnContext.elapsedTime` and `increaseElapsedTime(...)` move from `double` to `float`, and the
new `waitTime` is `float`. Removes the lone `double` outlier and the silent widening at the
`EnemyManager → EnemySpawnContext` boundary. `float`'s ~7 significant digits are ample for a timer
that resets on every `onEnter`. Alternative (make everything `double`) was rejected: it would ripple
into the engine's `float` update signatures.

**Parse and validate at level load (fail-fast).**
`time` is parsed with `Float.parseFloat` and validated while loading the level (in `HordeSpawner`,
whose `loadLevelData()` already declares `throws Exception`). A `waitTime` horde with a
missing/blank/unparseable `time` raises an `Exception` naming the offending horde. Chosen over
lazy parsing inside `StateWaitTime` so authoring errors surface at startup, not mid-level. A stray
`time` on a non-`waitTime` horde is ignored, not rejected.

**Clean the level XML.**
`level-1.xml` gets `time="1"` on all 27 bare `waitTime` events (preserving the current 1-second
pacing) and the meaningless `time` removed from `waitKill` events.

## Risks / Trade-offs

- [Existing levels omitting `time` on `waitTime` stop loading] → In-repo `level-1.xml` is updated in
  this change; the fail-fast error names the offending horde so any external level is a quick fix.
- [`float` precision vs `double`] → Negligible: the timer resets each wait and counts a few seconds.
- [New capability spec `enemy-spawn-lifecycle` overlaps the existing subsystem doc] → Intentional;
  the spec captures the behavior contract, the subsystem doc remains the narrative reference.
