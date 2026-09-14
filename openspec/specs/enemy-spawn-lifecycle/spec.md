# enemy-spawn-lifecycle Specification

## Purpose

Defines how the level's scripted steps are sequenced during play — in particular how each step runs
its ordered actions and how long the game waits between two time-gated steps, driven by the level
definition and validated when the level loads.

## Requirements

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

### Requirement: Music playback is controllable through step actions

A level's music SHALL be controllable from the level definition through step actions, so that a
level's soundtrack is authored alongside its waves rather than fixed in code. The level definition
SHALL be able to declare an action that starts a named music track (optionally looping) and an
action that stops a named music track. Such an action SHALL take effect once when its step runs, in
declaration order with the step's other actions, and SHALL command the audio subsystem in a
fire-and-forget manner consistent with every other step action; the step's completion event SHALL
remain independent of the music action.

Starting a music track that is already playing SHALL have no cumulative effect (the track is not
layered on itself). Stopping a track that is not playing SHALL be a no-op. A stop action that names
no specific track SHALL stop all music.

#### Scenario: A step starts a music track

- **WHEN** a step whose actions include a start-music action for a track becomes active
- **THEN** that music track begins playing as part of that step

#### Scenario: A looping track keeps playing across later steps

- **WHEN** a start-music action requests looping playback
- **THEN** the track continues playing while subsequent steps run, until it is stopped

#### Scenario: A step stops a music track

- **WHEN** a step whose actions include a stop-music action for the currently playing track becomes active
- **THEN** that music track stops as part of that step

#### Scenario: Music start and spawn coexist in the boss step

- **WHEN** the boss is preceded by a step that stops the main track and a step that starts the boss track
- **THEN** the main track stops, the boss track starts after the declared waits, and the boss is then spawned

#### Scenario: Restarting an already-stopped track is harmless

- **WHEN** a start-music action targets a track that is not currently playing
- **THEN** the track starts playing and no error occurs

### Requirement: Per-level music start is authored in the level definition

The music that plays during a level SHALL be started by the level definition rather than by the
scene-flow code that transitions into the level. Entering a level SHALL NOT itself start a music
track; instead the level's own steps SHALL declare when its music begins. Stopping all audio when
leaving a level (on player death or level completion) remains a scene-flow responsibility and is
outside this requirement.

#### Scenario: Level music comes from the level definition

- **WHEN** a level begins
- **THEN** its music starts only because one of its steps declares a start-music action, not because the transition into the level started it

#### Scenario: Leaving the level still stops audio

- **WHEN** the player dies or the level is completed
- **THEN** audio is stopped by the scene-flow layer, independent of any step action

### Requirement: Dialogue is controllable through step actions

A level's scripted dialogue SHALL be startable from the level definition through a step action, so a
briefing or character moment is authored alongside the level's waves rather than fixed in code. The
level definition SHALL be able to declare an action that starts a named dialogue script. Such an
action SHALL take effect once when its step runs, in declaration order with the step's other actions,
and SHALL command the dialogue subsystem in a fire-and-forget manner consistent with every other step
action; the step's completion event SHALL remain independent of the dialogue action.

#### Scenario: A step starts a dialogue

- **WHEN** a step whose actions include a dialogue-start action for a named script becomes active
- **THEN** that dialogue begins as part of that step

#### Scenario: Dialogue action is fire-and-forget like other actions

- **WHEN** a dialogue-start action runs among the other actions of a step
- **THEN** it takes effect once, in its declaration order, and the ongoing dialogue is owned by the dialogue subsystem rather than by the step

### Requirement: A step can wait until its dialogue is dismissed

The completion vocabulary that decides how the game waits before the next step SHALL include a
dialogue-gated wait. A step whose completion event is dialogue-gated SHALL hold the level on that step
until the dialogue subsystem reports the dialogue has been dismissed, and only then SHALL the next
step begin. This extends the existing closed completion vocabulary (time-gated, screen-clear-gated,
boss-spawned) with a dialogue-gated option; as with the others, one completion value maps to exactly
one dedicated wait.

#### Scenario: Dialogue-gated step waits for dismissal

- **WHEN** a step's completion event is dialogue-gated and its dialogue-start action has run
- **THEN** the next step begins only after the dialogue has been dismissed

#### Scenario: The action stays suspended for the whole wait

- **WHEN** the level is waiting on a dialogue-gated step
- **THEN** the gameplay action remains suspended until the dialogue is dismissed

#### Scenario: Dialogue-gated wait ignores any declared time

- **WHEN** a dialogue-gated step also declares a wait duration
- **THEN** the declared duration has no effect and the step still waits until the dialogue is dismissed
