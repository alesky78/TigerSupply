## Purpose

Provides an in-level radio-communication window in the style of 90s arcade shmups: at scripted
moments it suspends the action to show a character portrait and a speaker's message typed out one
character at a time, driven by authorable dialogue scripts in the level definition.

## ADDED Requirements

### Requirement: Radio dialogue window presentation

When a dialogue is shown, the game SHALL display a horizontal communication window with a fully black
background and a thin, high-contrast white border. The window SHALL place the current speaker's
portrait on its left side and, to the right of the portrait, the speaker's name together with the
message text in a white bitmap/pixel-style font. The window SHALL stay on screen for the whole
duration of the message.

#### Scenario: Window appears with the arcade radio layout

- **WHEN** a dialogue message is shown
- **THEN** a horizontal window with a black background and a thin white border appears with the portrait on the left and the speaker name and white text to its right

#### Scenario: Window stays for the whole message

- **WHEN** a message is being shown and has not yet been dismissed
- **THEN** the window remains visible and does not disappear between characters

### Requirement: Progressive text reveal

The message text SHALL NOT appear all at once. The game SHALL reveal the characters of the current
message one at a time, with a brief pause between successive characters, simulating a radio
transmission or terminal typing. Once the whole message is revealed, the game SHALL display a blinking
cursor or arrow indicating the player may proceed. The per-character reveal pace SHALL be taken from
the dialogue script.

#### Scenario: Characters appear one at a time

- **WHEN** a message begins showing
- **THEN** its characters become visible progressively over time rather than instantly

#### Scenario: Completion indicator after the last character

- **WHEN** the last character of a message has been revealed
- **THEN** a blinking cursor or arrow is shown to indicate the player can proceed

#### Scenario: Reveal pace comes from the script

- **WHEN** two scripts declare different reveal speeds
- **THEN** each message reveals its characters at its own declared pace

### Requirement: Portrait and speaker presentation

Each message SHALL be attributed to a named speaker and SHALL show that speaker's portrait — a
half-body manga/anime-style illustration whose expression can differ per situation (for example calm,
angry, focused, confident). When the speaker changes from one message to the next, the game SHALL
immediately replace both the portrait and the displayed name with those of the new speaker.

#### Scenario: Message shows its speaker's portrait and name

- **WHEN** a message authored for a given speaker and expression is shown
- **THEN** that speaker's name and the portrait for that expression are displayed

#### Scenario: Speaker change swaps portrait and name

- **WHEN** the next message is attributed to a different speaker
- **THEN** the portrait and name switch immediately to the new speaker's

### Requirement: Gameplay is suspended while a dialogue is shown

While a dialogue is on screen, the game SHALL suspend the gameplay action: the player ship, the
enemies, the shots and the collisions SHALL not advance. The dialogue's own animation and the level's
step sequencing SHALL continue so the dialogue can play and later be dismissed, and the scene SHALL
keep rendering the frozen playfield beneath the window. This suspension SHALL be independent of the
engine's global pause. When the dialogue is dismissed, the gameplay action SHALL resume.

#### Scenario: Action freezes under the window

- **WHEN** a dialogue is being shown
- **THEN** the player, enemies, shots and collisions do not move while the frozen playfield remains drawn behind the window

#### Scenario: Dialogue and sequencing stay alive

- **WHEN** a dialogue is being shown
- **THEN** the text keeps revealing and the level can detect the dialogue's dismissal

#### Scenario: Action resumes after dismissal

- **WHEN** the dialogue is dismissed
- **THEN** the player, enemies, shots and collisions resume advancing

### Requirement: Player advances the dialogue with the fire control

The player SHALL advance the dialogue using the same control used to fire the weapon, without
introducing a new key. Advancement SHALL be edge-triggered so that one physical press produces one
advancement: a press while the text is still revealing SHALL immediately reveal the whole current
message, and a press on a fully revealed message SHALL move to the next message. Dismissing the last
message SHALL finish the dialogue. Holding the control down SHALL NOT skip through multiple messages,
and a control already held when the dialogue opens SHALL NOT advance it until released and pressed
again.

#### Scenario: First press completes the current message

- **WHEN** the player presses the fire control while the text is still being revealed
- **THEN** the entire current message is revealed at once

#### Scenario: Next press advances to the following message

- **WHEN** the player presses the fire control on a fully revealed message that is not the last
- **THEN** the next message of the dialogue is shown

#### Scenario: Dismissing the last message finishes the dialogue

- **WHEN** the player presses the fire control on the last fully revealed message
- **THEN** the dialogue finishes and the action resumes

#### Scenario: Holding fire does not skip messages

- **WHEN** the player holds the fire control down
- **THEN** the dialogue advances by at most one step until the control is released and pressed again

### Requirement: Dialogue content is authored as reusable named scripts

Dialogue content SHALL be authored in the level definition as named scripts, each referenced by name
from the step action that starts it. A script SHALL declare its window geometry and its text reveal
speed, plus an ordered list of messages; each message SHALL carry its speaker name, its portrait
(including expression), and its text pre-split into explicit display lines. The system SHALL NOT
re-wrap message text — the authored lines SHALL be displayed as written. Referencing a script name
that is not defined in the level SHALL fail when the level loads.

#### Scenario: A step action references a script by name

- **WHEN** a step action names a dialogue script defined in the level
- **THEN** that script's messages are shown in the authored order

#### Scenario: Authored lines are shown as written

- **WHEN** a message declares several explicit lines
- **THEN** those lines are shown as authored without automatic re-wrapping

#### Scenario: Unknown script name fails at load time

- **WHEN** a step action references a script name that is not defined in the level
- **THEN** loading the level fails with an error identifying the missing script
- **AND** the level does not start
