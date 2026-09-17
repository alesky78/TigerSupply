# explosion-entity Specification

## Purpose
Defines the behavior of the explosion emitter: a self-contained effect entity that produces a burst
of explosion particles from a configuration profile, either as a single instantaneous burst or as a
timed emitter that can follow a moving owner, and then removes itself.

## Requirements

### Requirement: Profile-driven explosion burst

An explosion SHALL be produced from an explosion profile that specifies its particle colour scheme,
particle count, and the maximum size, speed, and lifetime of its particles. Spawning an explosion
SHALL emit that number of particles at the explosion's origin, each particle honouring the profile's
maxima.

#### Scenario: Burst emits the configured particle count

- **WHEN** an explosion is spawned from a profile with a given particle count
- **THEN** that number of explosion particles appear at the explosion's origin, each using the
  profile's colour scheme and respecting its size, speed, and lifetime maxima

### Requirement: One-shot burst self-removes

An explosion configured as a one-shot SHALL emit its full burst once and then flag itself for
removal, without emitting again.

#### Scenario: One-shot emits once then is removed

- **WHEN** a one-shot explosion has emitted its burst
- **THEN** it reports that it can be removed and emits no further particles

### Requirement: Timed emitter over a duration

An explosion configured with a positive duration SHALL emit repeatedly at a fixed interval until the
duration elapses, then flag itself for removal. Emission timing SHALL be based on elapsed time,
independently of the frame rate.

#### Scenario: Timed emitter emits at its interval

- **WHEN** a timed explosion runs for several intervals
- **THEN** it emits a burst at each interval and stops once the total duration has elapsed,
  regardless of the actual frame interval

### Requirement: Following emitter tracks its owner

A timed explosion MAY be attached to an owning entity so that its emission origin tracks the owner's
position as the owner moves, and the origin of each emission SHALL be jittered within the owner's
size so successive bursts appear across the owner's body rather than at a single fixed point.

#### Scenario: Emitter follows a moving owner

- **WHEN** a following timed explosion is attached to an owner that moves
- **THEN** subsequent bursts originate from the owner's current position, scattered within the
  owner's extent

### Requirement: Explosion does not collide

An explosion emitter SHALL NOT participate in collision detection; it only spawns particles and
manages its own lifetime.

#### Scenario: Emitter is inert to collisions

- **WHEN** an explosion emitter overlaps other entities
- **THEN** no collision is registered for the emitter itself
