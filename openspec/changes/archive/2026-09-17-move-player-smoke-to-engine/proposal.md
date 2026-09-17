## Why

`Player` currently owns the `PlayerEngine` exhaust implementation: it decides when to emit smoke,
calculates the nozzle position, creates `Smoke`, and queues it in the effect group. This splits one
visual responsibility across the player and its engine, unlike the existing rocket entities that
own their own smoke emission.

## What Changes

- Move the smoke-emission timer, nozzle-position calculation, smoke creation, and effect-group
  submission from `Player` to `PlayerEngine`.
- Let `Player` retain keyboard input ownership and explicitly notify its engine when forward thrust
  is active or inactive.
- Preserve the current visual rule: smoke is emitted only while the player holds `right`, including
  while the entry or respawn animation is running when that key remains active.
- Preserve the persistent engine sprite, its player-position tracking, and its exemption from
  screen-bound removal.

## Capabilities

### New Capabilities

None.

### Modified Capabilities

None. This is an internal responsibility refactor that preserves existing gameplay and visual
behaviour.

## Impact

- Modified game code: `game.entity.player.Player`, `game.entity.player.PlayerEngine`, and the
  player-engine factory wiring in `EntityFactoryWrapper` as needed to provide the effect group.
- No changes to XML content, engine-module APIs, runtime dependencies, or gameplay controls.