# engine-movement-algorithms Specification

## Purpose

Defines the engine's reusable, data-driven entity movement strategies that a spawned entity delegates
its per-frame motion to. This delta introduces the smooth-path strategy: following a smooth curve
through a set of ordered waypoints at a constant, frame-rate-independent speed.

## Requirements

### Requirement: Smooth curved path through ordered waypoints

The smooth-path movement strategy SHALL move the entity along a smooth curve that passes through each
configured waypoint in the given order, starting at the first waypoint and ending at the last. The
curve SHALL be continuous through the interior waypoints (no visible corners) while still passing
through every waypoint, including the first and the last.

#### Scenario: The path passes through every waypoint in order

- **WHEN** the strategy is configured with an ordered list of two or more waypoints
- **THEN** the entity travels from the first waypoint through each subsequent waypoint in order until
  it reaches the last one
- **AND** the trajectory between consecutive waypoints is a smooth curve rather than a straight
  segment with sharp corners at the waypoints

### Requirement: Frame-rate-independent constant speed

The smooth-path strategy SHALL advance the entity along the curve at a constant travel speed derived
from the magnitude of the entity's reference speed, integrated over the elapsed time of each frame, so
that the distance covered per unit of real time is independent of the frame rate.

#### Scenario: Same elapsed time yields the same progress regardless of frame rate

- **WHEN** the entity is driven by the smooth-path strategy for a fixed amount of real time
- **THEN** the distance travelled along the curve is the same whether that time elapsed over many
  short frames or few long frames

#### Scenario: Travel speed matches the configured reference speed

- **WHEN** the entity's reference speed has a given magnitude
- **THEN** the entity advances along the curve at that magnitude in pixels per second

### Requirement: Stop at the end of the path

When the entity reaches the end of the smooth path, the strategy SHALL stop moving the entity and keep
it at the final waypoint.

#### Scenario: The entity halts after the last waypoint

- **WHEN** the entity has reached the last configured waypoint
- **THEN** the strategy leaves the entity at that position on every subsequent frame
