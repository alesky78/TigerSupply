## ADDED Requirements

### Requirement: Target-seeking motion at a bounded turn rate

The target-seeking movement strategy SHALL move the entity along a heading that it rotates toward the
current position of a live target entity on every frame, turning by no more than a configured maximum
turn rate expressed in degrees per second. Because the turn rate is bounded, the entity SHALL converge
on a target that holds still and SHALL NOT reverse instantly toward a target that moves — it overshoots
and curves back rather than oscillating in place around the target.

#### Scenario: Converges on a stationary target

- **WHEN** the entity is driven by the seeking strategy toward a target that does not move
- **THEN** the entity's heading turns toward the target on each frame
- **AND** the entity reaches, or passes through, the target's position rather than settling short of it
  or oscillating around it

#### Scenario: The turn is capped by the maximum turn rate

- **WHEN** reaching the target would require turning by more than the maximum turn rate allows in a
  single frame
- **THEN** the entity turns by at most the maximum turn rate for that frame
- **AND** it therefore cannot pivot instantly and may overshoot a moving target before curving back
  toward it

#### Scenario: Steers toward the live target, not a launch-time snapshot

- **WHEN** the target moves to a new position after the seeking entity has been created
- **THEN** the entity steers toward the target's current position
- **AND** not toward the position the target held at the moment the entity was created

### Requirement: Constant, frame-rate-independent travel speed while seeking

The target-seeking strategy SHALL advance the entity at a constant travel speed integrated over the
elapsed time of each frame, so that the distance covered per unit of real time is independent of the
frame rate and the entity's speed neither grows nor decays while it seeks; only the heading changes.

#### Scenario: Same elapsed time yields the same progress regardless of frame rate

- **WHEN** the entity is driven by the seeking strategy for a fixed amount of real time
- **THEN** the distance travelled is the same whether that time elapsed over many short frames or few
  long frames

#### Scenario: Travel speed stays constant while seeking

- **WHEN** the entity is seeking a target over successive frames
- **THEN** the magnitude of its travel speed remains the configured constant on every frame, while its
  heading rotates toward the target

### Requirement: Time-bounded seeking

The target-seeking strategy SHALL home for a configured bounded duration; once that duration has
elapsed it SHALL stop turning its heading and continue in a straight line at its constant speed, so
that a target that is never reached does not keep the entity on screen indefinitely.

#### Scenario: The entity flies straight after the seek duration

- **WHEN** the configured seek duration has elapsed since the entity started seeking
- **THEN** the entity stops rotating its heading toward the target
- **AND** it continues in a straight line at the constant travel speed

#### Scenario: A dodged seeker eventually leaves a bounded play area

- **WHEN** the target keeps evading the seeker until the seek duration has elapsed
- **THEN** the seeker thereafter travels in a straight line
- **AND** it therefore exits any finite play area instead of orbiting the target indefinitely
