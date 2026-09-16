# explosion-particle Specification

## Purpose

Defines the behavior of the procedural explosion particle spawned in bursts when enemies, the
player, or bosses are hit or destroyed: how each particle picks its direction, how it fades and
shrinks over its lifetime, and when it removes itself.

## Requirements

### Requirement: Uniform random burst direction

Each explosion particle SHALL travel in a single, coherent direction chosen uniformly at random over
the full circle, at a speed no greater than the configured maximum. The horizontal and vertical
velocity components MUST derive from the same randomly chosen angle so that the resulting motion is a
genuine straight-line trajectory rather than an axis-biased or degenerate one.

#### Scenario: Direction is uniformly distributed

- **WHEN** a large number of explosion particles are spawned with the same maximum speed
- **THEN** their travel directions are spread over the whole circle with no axis bias, and every
  particle moves (no particle is left with a zero-length velocity vector)

#### Scenario: Speed respects the configured maximum

- **WHEN** a particle is spawned with a given maximum speed
- **THEN** the magnitude of its velocity vector does not exceed that maximum

### Requirement: Time-based fade and shrink

Each explosion particle SHALL fade and shrink from full intensity to nothing over its lifetime based
on elapsed time, independently of the frame rate. The fade progress MUST be a function of how much of
the particle's lifetime has elapsed, not of a fixed assumed frame duration.

#### Scenario: Fade tracks elapsed lifetime

- **WHEN** half of a particle's lifetime has elapsed
- **THEN** its fade/shrink intensity is approximately half of full, regardless of the actual frame
  interval

### Requirement: Self-removal at end of life

Each explosion particle SHALL flag itself for removal once its lifetime has fully elapsed, and MUST
NOT participate in collision detection while alive.

#### Scenario: Particle removed after lifetime

- **WHEN** a particle's elapsed time reaches or exceeds its lifetime
- **THEN** the particle reports that it can be removed

### Requirement: Robust randomized lifetime and size

Each explosion particle's randomized lifetime and size SHALL be clamped to a small positive minimum
so that a degenerate random draw cannot produce a particle with zero lifetime or zero size.

#### Scenario: Zero random draw is floored

- **WHEN** the randomized lifetime or size for a particle resolves to zero
- **THEN** the particle still has a strictly positive lifetime and a visible size, and no
  division-by-zero occurs while computing its fade
