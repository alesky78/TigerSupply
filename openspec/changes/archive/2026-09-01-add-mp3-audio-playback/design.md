## Context

See proposal.md - Why. Today `AudioPlayer.play()` wraps the in-memory `byte[]` of an audio file in a
`ByteArrayInputStream` and hands it to `AudioSystem.getAudioInputStream()`. The JDK's
`javax.sound.sampled` ships format readers only for WAV/AU/AIFF, so MP3 currently raises
`UnsupportedAudioFileException`. Looping is implemented by `mark()`/`reset()` on the decoded stream,
sized from `getFrameLength()`.

## Goals / Non-Goals

**Goals:**
- Play WAV and MP3 through a single, unchanged playback path.
- Make looping work for both encodings without relying on stream `mark()`/`reset()`.

**Non-Goals:**
- No per-format player class or player factory. The SPI already dispatches by content.
- No change to `FileAudioLoader`, `AudioRepository`, `AudioManager`, or the `byte[]` in-memory model.
- No new audio kinds and no change to the MUSIC/FX semantics.
- No support for encodings beyond WAV and MP3 in this change.

## Decisions

### Decision: Use the JavaZoom MP3SPI provider (SPI), not a direct decoder

Add `com.googlecode.soundlibs:mp3spi` (which pulls `jlayer` and `tritonus-share` transitively) to
`engine`. MP3SPI registers `javax.sound.sampled` SPI providers, so
`AudioSystem.getAudioInputStream()` transparently decodes MP3 to PCM. `AudioPlayer` keeps a single
code path for all formats.

- **Alternative — JLayer used directly** (`javazoom.jl.player.Player`): rejected. It bypasses
  `javax.sound.sampled`, forcing a second, format-specific playback path — exactly the duplication a
  per-format factory would create.
- **Alternative — OGG/Vorbis via `vorbisspi`** (more permissive licence): viable and would also
  shrink files, but the requested outcome is MP3. Recorded as a fallback if the LGPL dependency is
  later deemed unacceptable.
- **Alternative — keep WAV on `mark/reset` and branch by format**: rejected as over-engineering; the
  recreate-from-bytes loop below serves both encodings with less code.

This is a deliberate, documented exception to the project's "zero external runtime dependencies"
convention: the JDK cannot decode MP3 without a third-party decoder.

### Decision: Explicitly convert non-PCM streams to signed PCM before opening the line

`AudioSystem.getAudioInputStream(bytes)` returns an MP3 stream still in `MPEG` encoding; a
`SourceDataLine` cannot be opened on it (`IllegalArgumentException: No line matching ... MPEG...`).
`AudioPlayer.getAudioInputStream()` therefore returns already-`PCM_SIGNED` streams (WAV) unchanged
and, for every other encoding (MPEG, ALAW/ULAW), builds a 16-bit `PCM_SIGNED` target derived from
the source sample rate and channel count and lets the SPI provider perform the conversion. This
replaces the previous ALAW/ULAW-only conversion, which left MP3 undecoded.

### Decision: Rewind loops by recreating the stream from the source bytes

Replace the `mark()`/`reset()` rewind with recreating the `AudioInputStream` from the original
`byte[]` at the start of each loop iteration:

```
while (!forceStop) {
    ais = getAudioInputStream(new ByteArrayInputStream(data));
    sendStreamToMixer(ais, line);
    ais.close();
}
```

The source bytes are already held in memory, so recreation is cheap. This works identically for WAV
and MP3, and removes the dead `streamLengthInBytes > Integer.MAX_VALUE` check (it compares an `int`
to `Integer.MAX_VALUE`, so it can never be true) and the reliance on `getFrameLength()` (often
`NOT_SPECIFIED` for SPI-decoded MP3).

## Risks / Trade-offs

- **New LGPL runtime dependency in `engine`** → Mitigation: confined to the audio decode path,
  documented as an explicit policy exception; `vorbisspi` (OGG) recorded as a more permissive
  fallback.
- **MP3 loop seam may be briefly audible** (gapless looping is not guaranteed because each iteration
  reopens the stream) → Mitigation: acceptable for arcade background music; revisit only if a seam is
  noticeable in practice.
- **MP3 decode adds CPU cost vs. raw PCM** → Mitigation: negligible for this game's audio; decoding
  runs on the existing per-sound playback thread.
