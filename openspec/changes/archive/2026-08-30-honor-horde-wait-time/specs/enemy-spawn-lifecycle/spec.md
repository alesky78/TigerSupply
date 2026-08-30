## Purpose

Defines how the level's scripted hordes are sequenced during play — in particular how long the game
waits between two time-gated waves, driven by the level definition and validated when the level loads.

## ADDED Requirements

### Requirement: Time-gated hordes honor the authored delay

A horde whose completion event is a time-gated wait SHALL delay the next horde by the number of
seconds declared for that horde in the level definition, rather than a fixed built-in delay. The
delay SHALL be expressed in seconds as a fractional value, on the same time base as the game's frame
update, so that a level author changing the declared time changes the observed pause between waves.

#### Scenario: Authored delay controls the pause between waves

- **WHEN** a time-gated horde declares a wait of N seconds and its enemies have spawned
- **THEN** the next horde spawns only after N seconds of play have elapsed

#### Scenario: Different delays produce different pauses

- **WHEN** two time-gated hordes declare different wait durations
- **THEN** the pause before each following horde matches that horde's own declared duration

#### Scenario: Fractional delays are honored

- **WHEN** a time-gated horde declares a fractional wait such as 0.5 seconds
- **THEN** the next horde spawns after that fractional duration rather than a rounded or fixed delay

### Requirement: Missing or invalid time-gated delay fails fast

When the level definition is loaded, a time-gated horde whose declared wait is absent or not a valid
number SHALL cause level loading to fail with an error that identifies the offending horde. The game
SHALL NOT fall back to a default delay for a time-gated horde, so that every time-gated wait in the
level definition is explicit and human-readable.

#### Scenario: Absent delay is rejected at load time

- **WHEN** a level defines a time-gated horde with no declared wait
- **THEN** loading the level fails with an error identifying the offending horde
- **AND** the level does not start

#### Scenario: Unparseable delay is rejected at load time

- **WHEN** a level defines a time-gated horde whose declared wait is not a valid number
- **THEN** loading the level fails with an error identifying the offending horde
- **AND** the level does not start

#### Scenario: A declared delay on a non-time-gated horde is ignored

- **WHEN** a level declares a wait value on a horde whose completion is not time-gated
- **THEN** loading the level succeeds and the declared value has no effect on sequencing
