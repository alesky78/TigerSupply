## 1. Dependency

- [x] 1.1 Add `com.googlecode.soundlibs:mp3spi` (version 1.9.5.4) to `engine/pom.xml` and verify
  `jlayer` and `tritonus-share` resolve transitively via `mvn -pl engine dependency:tree`
- [x] 1.2 Verify the engine module still builds with the new dependency via `mvn -pl engine compile`

## 2. Playback path (AudioPlayer)

- [x] 2.1 Refactor the looping branch of `AudioPlayer.play()` to recreate the `AudioInputStream` from
  the source `byte[]` each iteration (via `getAudioInputStream(new ByteArrayInputStream(data))`)
  instead of `mark()`/`reset()`; verify a looping WAV track still restarts and keeps playing
- [x] 2.2 Remove the now-dead `streamLengthInBytes > Integer.MAX_VALUE` check and the
  `getFrameLength()`-based sizing; verify `AudioPlayer` compiles and looping WAV playback is unchanged
- [x] 2.3 Verify a looping MP3 track restarts from the beginning and continues until `forceStop`

## 3. End-to-end verification

- [x] 3.1 Add one MP3 asset to the resources and a catalog entry for it (alias + path, same format as
  existing entries), then verify it plays in-game (as music and/or effect) alongside existing WAV
  audio, with independent volume/mute/stop behavior
- [x] 3.2 Run `openspec validate add-mp3-audio-playback --strict` and confirm the change passes
