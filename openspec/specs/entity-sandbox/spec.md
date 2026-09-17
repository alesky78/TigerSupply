# entity-sandbox Specification

## Purpose

The entity-sandbox is a standalone developer tool for exercising any single concrete game entity in
isolation - selecting it from a catalog, running it in a minimal live environment, and observing its
behaviour and appearance - without playing through the game flow.

## Requirements

### Requirement: The sandbox launches as a standalone deliverable

The sandbox SHALL be startable through its own entry point, independently of the game, and SHALL NOT
be reachable from the shipped game (no in-game key, menu, or flag exposes it).

#### Scenario: Sandbox started on its own

- **WHEN** the sandbox entry point is launched
- **THEN** a game window opens showing the sandbox, not the game's Presentation scene

#### Scenario: Shipped game does not expose the sandbox

- **WHEN** the game is launched and played through its normal flow
- **THEN** no input, menu entry, or flag reaches the sandbox

### Requirement: The sandbox boots the shared asset repositories

The sandbox SHALL initialise the same image, font, audio, sprite, and entity infrastructure the game
relies on, so that entities render and behave as they do in the game.

#### Scenario: Assets available in the sandbox

- **WHEN** the sandbox has started
- **THEN** entities that use catalogued images, fonts, or procedural rendering appear correctly

### Requirement: A menu lists every testable entity and launches one

The sandbox SHALL present a menu that lists all catalogued entities, grouped by family (projectile,
enemy, effect), and SHALL let the user move a selection cursor and launch the highlighted entity.
The menu SHALL use a two-pane master-detail layout: a family pane listing the families, and an item
pane listing the entities of the currently opened family. The item pane SHALL scroll independently
so that a family of any length remains fully reachable within the fixed window, and SHALL show a
position indicator of the highlighted item within the family (for example `3/11`). Every catalogued
entity SHALL be selectable and launchable regardless of how many entities the catalog contains.

The effect family SHALL include explosion cases that launch a complete explosion burst — not merely
a single explosion particle — so that an explosion can be observed and studied in isolation. These
cases SHALL cover at least a one-shot burst and a timed following burst, wired to the sandbox effect
manager so the emitted particles render.

#### Scenario: Browsing the catalog

- **WHEN** the menu is shown
- **THEN** the families are listed in the family pane and the entities of the opened family are
  listed in the item pane under their family

#### Scenario: Opening a family and moving between panes

- **WHEN** the user opens the highlighted family
- **THEN** focus moves to the item pane, the opened family stays highlighted in the family pane, and
  the user can move back to the family pane to choose another family

#### Scenario: Every entity remains reachable as the catalog grows

- **WHEN** a family contains more entities than fit in the visible item pane
- **THEN** the item pane scrolls as the cursor moves so that every entity in that family can be
  highlighted and launched, and the position indicator reflects the highlighted item

#### Scenario: Selecting and launching an entity

- **WHEN** the user moves the cursor to an entry in the item pane and confirms the selection
- **THEN** the sandbox switches to running that entity in isolation

#### Scenario: Launching a full explosion from the effect family

- **WHEN** the user launches an explosion case from the effect family
- **THEN** the complete burst of particles is emitted and animates, rather than a single particle

### Requirement: The catalog covers all three entity families

The catalog SHALL enumerate the concrete testable entities of the projectile, enemy, and effect
families, and each entry SHALL declare its family and how the entity is built and wired to the
managers it needs.

#### Scenario: An entity from each family can be launched

- **WHEN** the user launches a projectile, an enemy, and an effect from the catalog in turn
- **THEN** each runs correctly wired to the groups and collaborators it requires

#### Scenario: A firing enemy's shots are shown

- **WHEN** an enemy that fires a weapon is launched
- **THEN** the projectiles it fires appear and are updated within the sandbox

### Requirement: A single entity runs in isolation with a debug overlay

When an entity is launched, the sandbox SHALL update and render only that entity (plus anything it
spawns) each frame, and SHALL display debug information about it such as its position, speed, and
bounding size.

#### Scenario: Running entity is observable

- **WHEN** an entity is running in the sandbox
- **THEN** the entity is animated each frame and its debug information is displayed

#### Scenario: Entity that leaves the playfield is renewed

- **WHEN** the running entity travels off the visible area
- **THEN** the sandbox makes it observable again (for example by respawning it)

### Requirement: A movable target lets motion behaviours be tested

The sandbox SHALL provide a target that the user can move around the playfield, and entities that
aim at, home toward, or follow a target SHALL use this target so their reaction can be observed.

#### Scenario: Target is moved by the user

- **WHEN** the user issues the directional move inputs in the test view
- **THEN** the target moves up, down, left, and right accordingly

#### Scenario: A homing entity reacts to the target

- **WHEN** a target-seeking entity is running and the user moves the target
- **THEN** the entity's motion changes in response to the target's new position

### Requirement: The user can control the running entity and return to the menu

While an entity is running the sandbox SHALL let the user respawn it, pause and single-step the
simulation, and return to the menu to choose another entity without restarting the application.

#### Scenario: Returning to the menu

- **WHEN** the user requests to leave the running entity
- **THEN** the sandbox shows the menu again with the catalog

#### Scenario: Pausing and stepping

- **WHEN** the user pauses the simulation and then requests a single step
- **THEN** the simulation advances by one frame and pauses again

### Requirement: The sandbox is isolated from the game and launcher code

The sandbox SHALL live in its own module and package root, SHALL depend only on the existing public
game and engine APIs, and SHALL NOT require any change to the engine, game, or launcher modules.

#### Scenario: No sandbox code in game or launcher packages

- **WHEN** the sandbox is added
- **THEN** no class under the game or launcher package roots references or contains sandbox code
