## Purpose

Defines an enemy weapon whose fire-ball projectile is rendered entirely in code (no image assets)
using the game's fire explosion palette, travels across the playfield leaving a fading square-pixel
trail, and behaves as a standard enemy shot.

## Requirements

### Requirement: Code-rendered fire-ball projectile

The fire-ball projectile SHALL be rendered procedurally at draw time (no image sprite) as a spherical
ball whose colour is drawn from the fire explosion palette: a bright hot core that darkens toward red
at its edge. The projectile SHALL advance horizontally toward the player's side of the playfield.

#### Scenario: Ball is drawn without an image asset

- **WHEN** a fire-ball projectile is on screen
- **THEN** it is drawn as a filled sphere whose colour is brightest at the centre and darkens toward
  a red edge, using only code-generated graphics from the fire explosion palette and no image asset

#### Scenario: Ball advances across the playfield

- **WHEN** a fire-ball projectile exists and time advances
- **THEN** its position moves horizontally toward the player's side each frame

### Requirement: Fading square-pixel fire trail

While the fire ball advances it SHALL leave a comet-like trail of square particles behind it, coloured
from the fire explosion palette. Each trail particle SHALL be a small square that fades out over a
short lifetime and then be removed. Trail particles SHALL be visual-only and SHALL NOT participate in
collision detection.

#### Scenario: Trail is emitted as the ball advances

- **WHEN** the fire ball advances over successive frames
- **THEN** square trail particles coloured from the fire palette are released along the path travelled

#### Scenario: Trail particles fade and disappear

- **WHEN** a trail particle has been emitted
- **THEN** it becomes progressively more transparent over its lifetime and is removed once its
  lifetime elapses

#### Scenario: Trail does not affect gameplay collisions

- **WHEN** a trail particle overlaps the player or any other entity
- **THEN** no collision is registered and no damage is dealt by the trail particle

### Requirement: Fire ball behaves as an enemy shot

The fire ball SHALL behave like other enemy projectiles for gameplay purposes: it SHALL be
collision-tested against the player, SHALL be removed when it hits the player, and SHALL be removed
once it leaves the visible screen.

#### Scenario: Ball hits the player

- **WHEN** a fire ball collides with the player
- **THEN** the collision is registered and the fire ball is removed

#### Scenario: Ball leaves the screen

- **WHEN** a fire ball travels beyond the visible screen bounds
- **THEN** it is removed from play

### Requirement: Fire-ball cannon weapon

A weapon SHALL carry the fire ball and launch one fire-ball projectile toward the player when the
firing condition is met. This weapon SHALL be equippable by an enemy exactly as the energy-ball
cannon is, differing only in that it fires a fire ball rather than an energy ball.

#### Scenario: Weapon fires a fire ball at the player

- **WHEN** an enemy equipped with the fire-ball cannon is active and the player is within the
  weapon's firing condition
- **THEN** the weapon launches a fire-ball projectile toward the player
