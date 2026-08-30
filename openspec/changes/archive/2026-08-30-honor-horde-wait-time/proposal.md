## Why

The `time` attribute of a `waitTime` horde event in the level XML is parsed but never used:
`StateWaitTime` hard-codes a 1-second delay between hordes. Any authored `time` value is silently
ignored, so level designers cannot control the pacing between waves. This was a known shortcut left
in place when the engine was refactored.

## What Changes

- The per-horde `waitTime` delay SHALL be driven by the level XML `time` attribute instead of the
  hard-coded 1-second value in `StateWaitTime`.
- The parsed `time` value SHALL flow from the horde definition into the enemy-spawn state machine's
  shared context, where `StateWaitTime` reads it.
- The spawn timer SHALL use a single floating-point second unit consistent with the rest of the
  framework: `elapsedTime` and the delay comparison move from `double` to `float`, matching the
  `float` frame delta the engine already propagates.
- A `waitTime` horde event with a missing or unparseable `time` SHALL fail fast at level-load time
  with a clear error, forcing an explicit, human-readable level XML. **BREAKING** for any level XML
  that omits `time` on a `waitTime` event.
- The level XML (`level-1.xml`) SHALL be updated: every bare `waitTime` event gets an explicit
  `time="1"` (preserving current pacing), and the meaningless `time` attribute is removed from
  `waitKill` events.

## Capabilities

### New Capabilities
- `enemy-spawn-lifecycle`: how the horde spawn state machine sequences waves, including the
  inter-wave wait-time delay driven by the level XML and its load-time validation.

### Modified Capabilities

_None._

## Impact

- Game code: `EnemySpawnContext` (timer type + new wait-time field), `StateWaitTime` (read the
  configured delay), `HordeSpawner` (parse `time`, inject it into the context, fail fast on a
  missing/invalid `waitTime` time).
- Content: `game/src/main/resources/level/level-1.xml` (27 bare `waitTime` events populated with
  `time="1"`; `time` removed from `waitKill` events).
- Behavior: level XML that omits `time` on a `waitTime` event no longer loads (fail-fast).
- No engine, launcher, or public-API changes; no new dependencies.
