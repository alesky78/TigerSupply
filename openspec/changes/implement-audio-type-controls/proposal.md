## Why

`AudioType.MUSIC` and `AudioType.FX` were introduced as a categorization seam but the distinction
was never actually implemented: the type is carried through `AudioManager` and `AudioPlayerThread`
but has no effect on playback, and the two per-type lists (`threadFX` / `threadMUSIC`) are treated
identically by the only consumer, `stopAllAudio()`. As a result there is no way to control music
and sound-effects independently (separate volume, separate stop), which a shmup normally needs
(e.g. lower the music while keeping FX, or stop the looping theme without cutting explosions).
On top of that, finished player threads are never removed from those lists, so they grow unbounded
for the whole session (a memory leak).

## What Changes

- `AudioType` SHALL become the seam that actually differentiates playback behavior between music
  and sound effects: **music is exclusive per track** (at most one playback per alias) while
  **sound effects are polyphonic** (the same effect may overlap itself).
- Music playback SHALL be addressable by its alias: `playMusic(alias, loop)` starts a track only
  when that alias is not already playing (otherwise it is a **no-op**, so the same track is never
  layered on itself), and a new `stopMusic(alias)` SHALL stop that single track. Two *different*
  music aliases MAY play concurrently.
- The audio subsystem SHALL support **independent per-type volume**: the caller can set a volume
  level (and a mute state) for `MUSIC` and for `FX` separately, applied to the corresponding
  playback lines.
- The audio subsystem SHALL support **independent stop**: stop a single music track by alias
  (`stopMusic(alias)`), all music, all effects (`stopFx()`), or everything (`stopAllAudio()`).
- Finished player threads SHALL be removed from their per-type collection when playback ends (and a
  finished music track SHALL free its alias), so the collections no longer grow unbounded over a
  session.
- `AudioType` SHALL be modeled as a type-safe Java `enum` (`MUSIC`, `FX`) instead of a class of
  `int` constants, and the now-meaningful type SHALL replace the `int audioType` fields/parameters
  in `AudioPlayerThread` and `AudioManager`. **BREAKING** for any code referencing the old
  `AudioType.MUSIC` / `AudioType.FX` `int` constants or the `int`-typed constructor of
  `AudioPlayerThread` (all such callers are inside this repository and are updated by this change).

## Capabilities

### New Capabilities
- `engine-audio-playback`: how the engine plays music and sound effects, distinguishing the two
  by `AudioType` (music exclusive per track, effects polyphonic), addressing music by alias
  (start/stop a single track), exposing per-type volume/mute and independent stop, and managing the
  lifecycle of player threads (including reclaiming finished ones and freeing a finished track's
  alias).

### Modified Capabilities

_None._

## Impact

- Engine code: `AudioType` (class of `int` constants → `enum`), `AudioManager` (music tracked by
  alias for exclusivity + `stopMusic(alias)`, FX tracked as a polyphonic list, per-type volume/mute
  state, stop methods, prune finished threads), `AudioPlayerThread` (typed `AudioType`, completion
  callback so the manager can reclaim it), `AudioPlayer` (apply a per-line gain via
  `javax.sound.sampled.FloatControl`).
- Game code: existing call sites (`SceneFlowController#playMusic`, `Enemy`, `RocketLauncher`,
  `SynusoidalGun` via `playFx`) keep working unchanged; new per-type volume/stop APIs are additive.
- Public API: additive methods on `AudioManager`; the `AudioType` `int` constants are replaced by
  enum constants (breaking only for the in-repo references listed above).
- No new dependencies (stays on `javax.sound.sampled` from the JDK); no launcher/build changes.
