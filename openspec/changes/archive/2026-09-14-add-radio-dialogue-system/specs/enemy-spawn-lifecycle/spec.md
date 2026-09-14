## ADDED Requirements

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
