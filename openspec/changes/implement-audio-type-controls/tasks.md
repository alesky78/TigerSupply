## 1. Convert `AudioType` to an enum

- [x] 1.1 Replace the `int`-constant class `AudioType` with `public enum AudioType { MUSIC, FX }`
  (keep the `@author` tag) and verify `engine` still compiles except for the not-yet-updated
  `int` references.

## 2. Per-kind state and API in `AudioManager`

- [x] 2.1 Replace `threadFX` / `threadMUSIC` with per-kind state: a `Map<String, AudioPlayerThread>`
  for music (keyed by alias) and a thread-safe `List<AudioPlayerThread>` for FX, each kind also
  holding a `float` volume (default 1.0) and a `boolean` muted (default false); verify `AudioManager`
  compiles.
- [x] 2.2 Change the private `play(...)` to take `AudioType` and the alias, snapshot the kind's
  current volume/muted, pass the effective volume (0.0 when muted, clamped otherwise) into
  `AudioPlayerThread`, and register the playback: for MUSIC, **no-op if the alias is already
  playing** else add it to the map; for FX, add to the list; verify `playMusic` layers nothing when
  the same alias is already playing and `playFx` allows overlaps.
- [x] 2.3 Add public `setVolume(AudioType, float)` (clamped to `[0,1]`) and
  `setMuted(AudioType, boolean)`; verify a manual/unit check that an out-of-range volume is stored
  as the nearest bound.
- [x] 2.4 Add public `stopMusic(String alias)` (stops one track), `stopMusic()` / `stopFx()` (stop a
  whole kind), and update `stopAllAudio()` to stop both kinds; verify stopping one of two music
  aliases leaves the other playing.

## 3. Thread lifecycle and volume in the player

- [x] 3.1 Add a completion callback to `AudioPlayerThread`, change its `audioType` field/constructor
  to `AudioType`, thread the effective volume through, and invoke the callback in a `finally` in
  `run()`; verify the manager removes the finished playback — music by alias (freeing it for replay),
  FX from the list.
- [x] 3.2 Apply the effective volume in `AudioPlayer.play(...)` after `line.open(...)` using
  `FloatControl.Type.MASTER_GAIN` (linear→dB), falling back to `VOLUME`, and skipping gracefully
  when neither control `isControlSupported`; verify no exception is thrown on an unsupported line.

## 4. Update call sites and validate

- [x] 4.1 Update the only in-repo references to the old `AudioType.MUSIC`/`AudioType.FX` `int`
  constants (inside `AudioManager`) and confirm the `game` call sites
  (`SceneFlowController`, `Enemy`, `RocketLauncher`, `SynusoidalGun`) need no changes; verify a full
  `mvn -q -pl engine,game -am compile` succeeds.
- [ ] 4.2 Run the game (`mvn -pl launcher exec:java`) and verify: music loops, FX play and can
  overlap, requesting an already-playing music alias does not layer it, lowering music volume keeps
  FX audible, `stopMusic(alias)` stops one of two music tracks while the other continues, and
  repeated FX do not grow the tracked collections unbounded.
- [x] 4.3 Run `openspec validate implement-audio-type-controls --strict` and confirm it passes.
