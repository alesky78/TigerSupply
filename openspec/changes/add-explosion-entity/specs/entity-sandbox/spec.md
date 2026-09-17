## MODIFIED Requirements

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
