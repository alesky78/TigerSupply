## Why

The audio subsystem can only play uncompressed WAV files, which are large on disk and constrain
the amount and length of music/effects that can ship with the game. Adding MP3 support lets the
game use compressed audio (smaller files, longer tracks) while reusing the existing single
playback path.

## What Changes

- Add MP3 decoding to the engine by introducing the JavaZoom **MP3SPI** provider
  (`mp3spi` + transitive `jlayer` + `tritonus-share`) as an `engine` runtime dependency. This is a
  deliberate, documented exception to the project's "zero external runtime dependencies" convention,
  because the JDK's `javax.sound.sampled` cannot decode MP3 on its own.
- Keep **one** playback path for every format. `AudioSystem` + the SPI mechanism already dispatch by
  format from the file content, so no per-format player/factory is introduced.
- Replace the loop's `mark()`/`reset()` rewind (which is unsupported on SPI-decoded MP3 streams and
  relies on `getFrameLength()`) with a rewind that **recreates the `AudioInputStream` from the
  original bytes** each iteration. This works identically for WAV and MP3 and removes the dead
  `streamLengthInBytes > Integer.MAX_VALUE` check.

## Capabilities

### New Capabilities
<!-- none -->

### Modified Capabilities
- `engine-audio-playback`: the subsystem now supports multiple audio encodings (WAV and MP3), and
  looping playback is defined in terms of restarting a track from its source data rather than an
  in-place stream reset.

## Impact

- **Dependencies**: `engine/pom.xml` gains `com.googlecode.soundlibs:mp3spi` (pulls `jlayer` and
  `tritonus-share` transitively). Licence is LGPL — flagged as a deliberate policy exception.
- **Code**: `AudioPlayer.play()` loop refactored to recreate the stream per loop iteration.
- **Unchanged**: `RepositoryLoader`, the catalog format, `FileAudioLoader`, `AudioRepository`,
  `AudioManager`, and the `byte[]`-based in-memory representation of audio are not affected.
