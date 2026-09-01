## RENAMED Requirements

- FROM: `### Requirement: Time-gated hordes honor the authored delay`
- TO: `### Requirement: Time-gated steps honor the authored delay`

## MODIFIED Requirements

### Requirement: Time-gated steps honor the authored delay

A step whose completion event is a time-gated wait SHALL delay the next step by the number of seconds
declared for that step in the level definition, rather than a fixed built-in delay. The delay SHALL
be expressed in seconds as a fractional value, on the same time base as the game's frame update, so
that a level author changing the declared time changes the observed pause between steps.

#### Scenario: Authored delay controls the pause between waves

- **WHEN** a time-gated step declares a wait of N seconds and its actions have run
- **THEN** the next step begins only after N seconds of play have elapsed

#### Scenario: Different delays produce different pauses

- **WHEN** two time-gated steps declare different wait durations
- **THEN** the pause before each following step matches that step's own declared duration

#### Scenario: Fractional delays are honored

- **WHEN** a time-gated step declares a fractional wait such as 0.5 seconds
- **THEN** the next step begins after that fractional duration rather than a rounded or fixed delay

### Requirement: Missing or invalid time-gated delay fails fast

When the level definition is loaded, a time-gated step whose declared wait is absent or not a valid
number SHALL cause level loading to fail with an error that identifies the offending step. The game
SHALL NOT fall back to a default delay for a time-gated step, so that every time-gated wait in the
level definition is explicit and human-readable.

#### Scenario: Absent delay is rejected at load time

- **WHEN** a level defines a time-gated step with no declared wait
- **THEN** loading the level fails with an error identifying the offending step
- **AND** the level does not start

#### Scenario: Unparseable delay is rejected at load time

- **WHEN** a level defines a time-gated step whose declared wait is not a valid number
- **THEN** loading the level fails with an error identifying the offending step
- **AND** the level does not start

#### Scenario: A declared delay on a non-time-gated horde is ignored

- **WHEN** a level declares a wait value on a step whose completion is not time-gated
- **THEN** loading the level succeeds and the declared value has no effect on sequencing

## ADDED Requirements

### Requirement: A step performs an ordered list of actions then emits a completion event

A level SHALL be sequenced as an ordered series of steps. Each step SHALL declare an ordered list of
one or more actions and exactly one completion event. When a step becomes active, the game SHALL
perform each of the step's actions once, in declaration order, and then emit the step's completion
event. The completion event SHALL determine how the game waits before the next step and SHALL be
independent of which actions the step performed. Actions SHALL take effect immediately when the step
runs; any ongoing behavior an action starts is owned by the subsystem it commands, not by the step.

#### Scenario: A step's actions all take effect when the step runs

- **WHEN** a step with several actions becomes active
- **THEN** each of its actions takes effect once, in the order it is declared

#### Scenario: Heterogeneous actions in one step

- **WHEN** a step declares both a wave-spawning action and an action that commands another subsystem, such as halting a moving structure
- **THEN** both take effect as part of that single step

#### Scenario: Completion event is independent of the actions

- **WHEN** two steps perform different actions but declare the same completion event
- **THEN** the game waits for the next step in the same way for both

#### Scenario: Completion vocabulary selects how the game waits

- **WHEN** a step's completion event is time-gated, screen-clear-gated, or boss-spawned
- **THEN** the game respectively waits the declared duration, waits until the screen is cleared, or waits for the boss to be defeated before ending the level

#### Scenario: Introducing the boss is an ordinary action

- **WHEN** the step that introduces the boss becomes active
- **THEN** the boss is introduced by one of the step's actions and the step's completion event is the boss-spawned wait
