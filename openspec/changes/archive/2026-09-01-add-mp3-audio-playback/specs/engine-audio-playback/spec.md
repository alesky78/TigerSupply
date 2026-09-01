## ADDED Requirements

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
