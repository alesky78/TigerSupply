## ADDED Requirements

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
