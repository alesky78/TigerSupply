## Context

See proposal.md — *Why*. The current audio subsystem
(`it.spaghettisource.tigersupply.engine.audio`) is:

- `AudioType` — a final class holding two `int` constants (`MUSIC = 0`, `FX = 1`).
- `AudioManager` — singleton with `playMusic` / `playFx`, a private `play(byte[], boolean, int)`
  that wraps the buffer in an `AudioPlayerThread`, appends it to `threadFX` **or** `threadMUSIC`,
  and starts a `Thread`. `stopAllAudio()` iterates both lists calling `stopPlayer()`.
- `AudioPlayerThread` — `Runnable` holding the `int audioType` and an `AudioPlayer`.
- `AudioPlayer` — opens a `SourceDataLine`, converts ALAW/ULAW → PCM, and writes the buffer to the
  mixer; supports looping and a cooperative `forceStop()`.

Constraints from the project: JDK-only (`javax.sound.sampled`), no logging framework
(`e.printStackTrace()` on error paths), singletons via `getInstance()`, keep the `@author` tag on
existing files. The type is currently inert — `AudioPlayer`'s own Javadoc says the type "has not
effect inside its code" — and finished threads are never removed from the two lists.

## Goals / Non-Goals

**Goals:**
- Make `AudioType` a type-safe `enum` that is the real seam between music and FX behavior:
  music is exclusive per track (addressed by alias), effects are polyphonic.
- Add per-kind volume + mute, per-track music stop (`stopMusic(alias)`) and per-kind stop to
  `AudioManager`, keeping existing call sites working.
- Reclaim `AudioPlayerThread`s (and free a finished music track's alias) when playback ends.

**Non-Goals:**
- Live volume changes to sounds already playing (volume/mute are snapshotted at start — see the
  spec scenario "Lowering music volume leaves effects unchanged", which only constrains
  subsequently started sounds).
- Addressing or stopping an individual sound-effect instance (effects are fire-and-forget; only
  music is individually addressable, and by alias — not by an opaque handle).
- A full mixer (per-sound volume, fades, panning, ducking), a settings-persistence layer, or any UI.
- Changing the audio catalog format or the loading pipeline.

## Decisions

### 1. `AudioType` becomes an `enum { MUSIC, FX }`

Replaces the `int` constants, giving compile-time safety and letting the manager key per-kind state
off it (`EnumMap`). `AudioPlayerThread`'s `int audioType` field/constructor parameter and
`AudioManager.play(...)`'s `int` parameter become `AudioType`.
*Alternative considered:* keep the `int` constants and just add behavior — rejected, it leaves the
weakly-typed API and invites invalid values.
*Trade-off:* **BREAKING** for the in-repo references to the old constants; all call sites are inside
this repo and are updated in the same change.

### 2. Per-kind state lives in `AudioManager`: music keyed by alias, FX as a list

Introduce per-kind state holding a linear `volume` (0.0–1.0, default 1.0) and a `muted` flag
(default false), plus the active playbacks tracked differently per kind to encode the behavioral
difference:
- **MUSIC** — a `Map<String, AudioPlayerThread>` keyed by alias. `playMusic(alias, loop)` starts a
  track only when the alias is absent from the map (otherwise **no-op**), guaranteeing at most one
  playback per track and no self-layering. `stopMusic(alias)` stops and removes that entry; two
  different aliases coexist as two map entries.
- **FX** — a list of `AudioPlayerThread` (polyphonic; the same alias may appear multiple times).

`playMusic` / `playFx` remain thin wrappers over `play(alias, loop, AudioType)`. New public methods:
`setVolume(AudioType, float)` (clamped to `[0,1]`), `setMuted(AudioType, boolean)`,
`stopMusic(String alias)`, plus `stopMusic()` / `stopFx()`; `stopAllAudio()` stays and now stops
both kinds.
*Alternative considered:* returning an opaque per-playback handle from `play*` so any single sound
could be stopped — rejected: the alias is already a natural identity for music, it prevents the
nonsensical self-layering of a track for free, and effects don't need individual stop. Avoiding the
`void`→handle return change also leaves the existing call sites untouched.
Volume is **clamped** to `[0,1]` (spec: "Volume outside the valid range is clamped").

### 3. Volume applied via `SourceDataLine` gain, snapshotted at start

`AudioManager.play(...)` reads the kind's current `volume`/`muted` and passes an effective volume
(0.0 when muted) into the `AudioPlayerThread` → `AudioPlayer.play(...)`. After `line.open(...)`,
`AudioPlayer` applies the volume by:
- preferring `FloatControl.Type.MASTER_GAIN` when supported: convert linear `v` to decibels
  (`20 * log10(v)`, with `v == 0` → the control's minimum), then clamp to the control's
  `[min,max]`;
- falling back to `FloatControl.Type.VOLUME` when master gain is unsupported;
- if neither control is supported, playing at the default level (best-effort, no error).

*Alternative considered:* scaling the raw PCM samples by hand — rejected as far more code (must
handle every sample size/encoding) for no user-visible benefit over the mixer's own gain control.

### 4. Threads reclaim themselves on completion via a callback

`AudioPlayerThread` gets a completion callback supplied by `AudioManager`. In `run()`, after
`player.play(...)` returns (in a `finally` so it also runs on exception), the thread invokes the
callback and the manager removes it — for music, the alias entry is removed from the map (freeing it
for replay); for FX, the thread is removed from the list. Because threads add/remove entries
concurrently with `stop*`/`play`, the music map and FX list are guarded for thread-safe access
(e.g. a synchronized map/list or a dedicated lock). A track already removed (finished) is simply
absent, so a later stop request cannot affect it (spec: "A reclaimed playback SHALL NOT be affected
a later stop request").

## Risks / Trade-offs

- **Mixer lacks a gain control on some platforms/formats** → detect with
  `line.isControlSupported(...)` and degrade gracefully to default volume; never throw. Volume
  control is best-effort, consistent with the arcade scope.
- **Concurrency between playback threads and manager calls** → confine shared mutation to the
  thread-safe per-kind collections and snapshot volume/mute at start, so no locks are held across
  the blocking `play(...)` call.
- **A music track finishing exactly as the same alias is (re)requested** → guard the music map so the
  `playMusic` "already present?" check and the reclaim's alias removal are mutually consistent; the
  worst case is that a just-finished track is seen as absent and correctly (re)started — never a
  self-layered duplicate.
- **`AudioPlayer` currently swallows the type** → the `AudioType` no longer flows into `AudioPlayer`
  (it stays in the manager/thread layer); `AudioPlayer` only receives the resolved effective volume,
  keeping it decoupled from the kind concept.
- **Breaking constant replacement** → grep confirms the only `AudioType.MUSIC` / `AudioType.FX`
  references are inside `AudioManager`; the game modules call `playMusic`/`playFx`, which are
  unchanged.
