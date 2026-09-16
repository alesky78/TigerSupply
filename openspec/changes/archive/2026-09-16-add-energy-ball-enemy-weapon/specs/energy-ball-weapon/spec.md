## Purpose

Defines an enemy weapon whose energy-ball projectile is rendered entirely in code (no image assets),
travels across the playfield leaving a fading square-pixel trail, and is delivered by a dedicated
enemy spawned from the level script.

## ADDED Requirements

### Requirement: Code-rendered energy-ball projectile

The energy-ball projectile SHALL be rendered procedurally at draw time (no image sprite) as a
spherical energy ball with a bright core that darkens toward its edge. The projectile SHALL advance
horizontally toward the player's side of the playfield.

#### Scenario: Ball is drawn without an image asset

- **WHEN** an energy-ball projectile is on screen
- **THEN** it is drawn as a filled sphere whose colour is brightest at the centre and darkens toward
  the edge, using only code-generated graphics and no image asset

#### Scenario: Ball advances across the playfield

- **WHEN** an energy-ball projectile exists and time advances
- **THEN** its position moves horizontally toward the player's side each frame

### Requirement: Fading square-pixel trail

While the energy ball advances it SHALL leave a comet-like trail of square particles behind it. Each
trail particle SHALL be a small square that fades out over a short lifetime and then be removed. Trail
particles SHALL be visual-only and SHALL NOT participate in collision detection.

#### Scenario: Trail is emitted as the ball advances

- **WHEN** the energy ball advances over successive frames
- **THEN** square trail particles are released along the path it has travelled

#### Scenario: Trail particles fade and disappear

- **WHEN** a trail particle has been emitted
- **THEN** it becomes progressively more transparent over its lifetime and is removed once its
  lifetime elapses

#### Scenario: Trail does not affect gameplay collisions

- **WHEN** a trail particle overlaps the player or any other entity
- **THEN** no collision is registered and no damage is dealt by the trail particle

### Requirement: Energy ball behaves as an enemy shot

The energy ball SHALL behave like other enemy projectiles for gameplay purposes: it SHALL be
collision-tested against the player, SHALL be removed when it hits the player, and SHALL be removed
once it leaves the visible screen.

#### Scenario: Ball hits the player

- **WHEN** an energy ball collides with the player
- **THEN** the collision is registered and the energy ball is removed

#### Scenario: Ball leaves the screen

- **WHEN** an energy ball travels beyond the visible screen bounds
- **THEN** it is removed from play

### Requirement: Dedicated energy-shooter enemy

A dedicated enemy type SHALL carry the energy-ball weapon and fire it at the player when the player is
within the weapon's firing condition. This enemy SHALL be spawnable from the level script like other
enemies.

#### Scenario: Enemy fires the energy ball at the player

- **WHEN** the energy-shooter enemy is active and the player is within its firing condition
- **THEN** the enemy launches an energy-ball projectile toward the player

#### Scenario: Enemy is spawned from the level script

- **WHEN** the level reaches the step that spawns the energy-shooter enemy
- **THEN** the enemy appears and behaves as defined by its level prototype
