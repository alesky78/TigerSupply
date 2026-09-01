# engine-audio-playback Specification

## Purpose

Defines how the engine plays background music and short sound effects, treating the two as
distinct audio kinds — music is exclusive per track (addressable by alias) while effects are
polyphonic — so callers can control their volume and stop them independently, while the engine
reclaims player resources once playback finishes.

## Requirements

### Requirement: Playback is categorized by audio kind

The audio subsystem SHALL classify every played sound as one of two kinds — background music or
sound effect — and SHALL keep the two kinds independently controllable. Requesting playback of a
sound SHALL associate it with exactly one kind for the whole duration of that playback.

#### Scenario: Playing music and an effect concurrently

- **WHEN** the caller starts a looping music track and then triggers a sound effect
- **THEN** both play simultaneously
- **AND** each is tracked under its own kind so it can later be controlled independently of the other

### Requirement: Music is exclusive per track; effects are polyphonic

The audio subsystem SHALL play at most one music track per alias at a time: starting a music track
whose alias is already playing SHALL have no effect (no second, layered playback). Two music tracks
with different aliases MAY play concurrently. Sound effects SHALL be polyphonic: the same effect
alias MAY have multiple overlapping playbacks.

#### Scenario: The same music track is never layered on itself

- **WHEN** a music track is already playing and the caller requests the same track (same alias) again
- **THEN** no second playback of that track starts
- **AND** the already-playing track continues uninterrupted

#### Scenario: Two different music tracks play together

- **WHEN** the caller starts one music track and then a different music track
- **THEN** both play concurrently

#### Scenario: The same effect can overlap itself

- **WHEN** the caller triggers the same sound effect twice in quick succession
- **THEN** two overlapping playbacks of that effect are heard

### Requirement: Independent per-kind volume and mute

The audio subsystem SHALL expose a volume level and a mute state that can be set independently for
each audio kind. A volume or mute change for one kind SHALL NOT affect the other kind. The
configured volume/mute for a kind SHALL apply to sounds of that kind that start after the change.

#### Scenario: Lowering music volume leaves effects unchanged

- **WHEN** the caller lowers the music volume (or mutes music)
- **THEN** subsequently started music plays at the reduced (or silent) level
- **AND** sound effects continue to play at their own configured level

#### Scenario: Volume outside the valid range is clamped

- **WHEN** the caller sets a volume for a kind to a value outside the supported range
- **THEN** the value is clamped to the nearest supported bound rather than raising an error

### Requirement: Independent stop by kind and by music track

The audio subsystem SHALL allow the caller to stop a single music track identified by its alias
without affecting other music tracks or effects, to stop all sounds of a single kind without
affecting the other kind, and to stop all sounds of every kind at once.

#### Scenario: Stopping one of two music tracks

- **WHEN** two different music tracks are playing and the caller stops one of them by its alias
- **THEN** that track stops
- **AND** the other music track keeps playing

#### Scenario: Stopping only music

- **WHEN** music and one or more sound effects are playing and the caller stops all music
- **THEN** the music stops
- **AND** the sound effects keep playing to their natural end

#### Scenario: Stopping all audio

- **WHEN** any sounds are playing and the caller stops all audio
- **THEN** every playing sound of every kind stops

### Requirement: Finished playbacks are reclaimed

The audio subsystem SHALL release the tracking resources for a sound once its playback ends — and a
finished music track SHALL free its alias so it can be started again — so the set of tracked
playbacks does not grow unbounded over a long session. A reclaimed playback SHALL NOT be affected by
a later stop request.

#### Scenario: Completed effects do not accumulate

- **WHEN** many non-looping sound effects are played and allowed to finish over time
- **THEN** the number of tracked playbacks does not keep growing after each effect completes

#### Scenario: A finished music track can be played again

- **WHEN** a non-looping music track finishes on its own and the caller later starts the same track again
- **THEN** the track plays again (its alias was freed when it finished)

### Requirement: Multiple audio encodings are supported

The audio subsystem SHALL play back sounds encoded as uncompressed WAV and as MP3 through the same
playback path, so that any sound (music or effect) MAY be provided in either encoding without the
caller choosing a different playback mechanism. Requesting playback of a sound SHALL behave the same
way — with respect to kind, volume, mute, looping, and stopping — regardless of its source encoding.

#### Scenario: Playing a WAV and an MP3 track

- **WHEN** the caller starts one music track supplied as WAV and another supplied as MP3
- **THEN** both play correctly through the same playback path
- **AND** each remains independently controllable by kind and by alias exactly as an equivalent
  WAV-only setup would be

#### Scenario: Looping an MP3 track

- **WHEN** a looping music track supplied as MP3 reaches its end
- **THEN** it restarts from the beginning and keeps playing until stopped
- **AND** the audible result is the same as looping the equivalent WAV track

#### Scenario: An unsupported encoding is rejected

- **WHEN** a sound is provided in an encoding the subsystem does not support
- **THEN** loading fails with a clear error identifying the offending resource
- **AND** supported WAV and MP3 sounds are unaffected
