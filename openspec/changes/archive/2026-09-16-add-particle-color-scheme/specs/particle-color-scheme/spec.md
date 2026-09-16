## Purpose

Provides a reusable, type-safe way to map a procedural particle's purpose and its fade progress to a
render color, so that different particle effects share one extensible color source instead of
hard-coding their own palettes.

## ADDED Requirements

### Requirement: Named, extensible color schemes

The system SHALL provide a set of named particle color schemes, each of which maps a fade-progress
value in the range `[0,1]` (full intensity to fully faded) to a render color. Adding a new particle
appearance SHALL require only introducing a new named scheme, without modifying the particles that
consume schemes.

#### Scenario: Scheme produces a color for a progress value

- **WHEN** a color scheme is asked for the color at a given fade-progress value in `[0,1]`
- **THEN** it returns a fully specified render color (red, green, blue, and alpha) for that value

#### Scenario: A new appearance is added without touching consumers

- **WHEN** a new particle appearance is introduced as a new named scheme
- **THEN** existing particle types can adopt it without changes to their rendering logic beyond
  selecting the new scheme

### Requirement: Particle color is scheme-driven

Each procedural fade particle SHALL derive its render color solely from its assigned color scheme
evaluated at its current fade progress. A particle MUST NOT branch on an untyped identifier or embed a
literal color to decide its appearance.

#### Scenario: Explosion particle colors from its scheme

- **WHEN** an explosion particle is created with the fire or the energetic scheme
- **THEN** its rendered color at each frame is the scheme's color for the particle's current fade
  progress, and no other appearance branch is used

#### Scenario: Energy-trail particle colors from its scheme

- **WHEN** an energy-trail particle is created
- **THEN** its rendered color is produced by its assigned scheme rather than a hard-coded literal

### Requirement: Fade style is preserved per scheme

Each color scheme SHALL encode how fade progress is expressed across the color channels, including
whether the fade is carried by the alpha channel. Existing effects MUST keep their current fade
appearance: the fire and energetic schemes keep a fully opaque color (their fade is expressed by the
particle shrinking), and the energy-trail scheme fades its alpha to zero.

#### Scenario: Opaque explosion schemes

- **WHEN** the fire or energetic scheme is evaluated at any fade progress
- **THEN** the returned color is fully opaque

#### Scenario: Alpha-fading trail scheme

- **WHEN** the energy-trail scheme is evaluated as fade progress goes from full to zero
- **THEN** the returned color's alpha goes from fully opaque to fully transparent
