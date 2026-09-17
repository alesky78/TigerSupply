## Context

`Player` creates a persistent `PlayerEngine` effect that follows the player's `Position` and is
never removed for leaving the playfield. However, `Player.updateEntity` currently also holds the
exhaust timer and creates `Smoke` directly. The engine's visual responsibility is therefore split
between two entities.

`PlayerRocket` and `EnemyRocket` provide the local precedent: each entity owns its smoke timer,
creates its smoke at its own rear edge, and queues it into the shared effect group.

## Goals / Non-Goals

**Goals:**
- Make `PlayerEngine` the sole owner of player-engine smoke emission.
- Keep `Player` as the owner of keyboard state and player movement.
- Preserve every current smoke rule: emission is conditional on `right`, uses the engine rear
  position, and remains possible during entry/respawn when `right` is held.
- Preserve the existing persistent, player-following engine sprite.

**Non-Goals:**
- Change the controls, player movement, smoke sprite, smoke lifetime, or smoke cadence.
- Generalize exhaust emission into a shared engine-module abstraction.
- Alter rocket smoke ownership or unrelated effect emitters.

## Decisions

### Player sends an explicit thrust command

`PlayerEngine` gains a boolean thrust state and a public command such as
`setThrustActive(boolean)`. `Player` remains the sole interpreter of keyboard input and passes its
current `right` state to the engine during its update. The command is set every frame so an engine
created after input has already begun immediately receives the current state.

The engine does not infer thrust from its copied `Position` or player speed: position has no
directional intent, and the established gameplay rule is specifically tied to the `right` key.

### PlayerEngine owns all exhaust-emission mechanics

`PlayerEngine` holds the smoke counter and the effect-group reference. During
`updateEntity(deltaSeconds)`, after its normal movement and animation update, it advances the
counter and, when thrust is active, computes its own rear emission point, creates `Smoke` through
`EntityFactoryWrapper`, and calls `effectManager.addRequest(...)`.

The existing counter expression and nozzle offset are moved unchanged. This preserves the observed
cadence and z-order rather than attempting to normalize timing as part of this ownership refactor.

### Player creates and wires a typed engine once

Replace the `engineCreated` flag with a `PlayerEngine` field. When the engine is first created,
`Player` supplies the shared effect manager through an engine setter, queues the engine in that
manager, and keeps the returned reference to communicate thrust state. The existing factory can
continue to construct the sprite, position-copying algorithm, and immortal `PlayerEngine`; it does
not need to own group wiring.

## Risks / Trade-offs

- A missing effect manager would fail only when the engine emits smoke. The one-time construction
  path must wire the manager before the engine is queued.
- Calling the thrust command every player update creates a small dependency from input state to the
  engine, but it is intentional: the player communicates domain intent while the engine owns visual
  execution.
- Keeping the current delta-relative cadence preserves behaviour but leaves its legacy timing
  characteristic unchanged; cadence improvement is deliberately outside this change.

## Migration Plan

1. Add thrust and effect-manager state plus smoke emission to `PlayerEngine`.
2. Retain the created engine in `Player`, wire it once, and pass the current right-thrust state.
3. Remove player-owned smoke imports, counter, positioning, construction, and submission code.
4. Compile the game module, then launch the game or sandbox to confirm right-thrust smoke and the
   persistent engine visual.

## Open Questions

None. Smoke during entry/respawn remains conditional on `right`, matching the existing behaviour.