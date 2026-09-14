## Why

TigerSupply plays as an uninterrupted wave shooter with no way to pause the action for a scripted
story beat. The defining flavour of 90s arcade shmups is the radio-communication window — a character
portrait, a speaker name, and text that types out one letter at a time — used to deliver briefings
and character moments mid-mission. Adding an authorable radio-dialogue system lets a level tell its
story at chosen moments without hard-coding it, reusing the level's existing data-driven step model.

## What Changes

- Introduce a radio communication window that appears at scripted moments: black background, thin
  high-contrast white border, character portrait on the left, speaker name and white bitmap-font
  text on the right.
- Reveal the message text progressively, one character at a time (radio/terminal effect), and show a
  blinking cursor/arrow once a message is fully revealed to signal the player can proceed.
- Suspend the gameplay action while a dialogue is on screen — player, enemies, shots and collisions
  freeze while the dialogue animates and the level keeps sequencing; the scene keeps rendering the
  frozen playfield beneath the window.
- Advance the dialogue with the **existing fire key**: a first press reveals the whole current
  message instantly, the next press moves to the following message, and dismissing the last message
  resumes the action. No new key is introduced.
- Model a message as a speaker name + a portrait (with per-situation expressions such as calm,
  angry, focused) + its text pre-split into explicit lines (no automatic word-wrap). Changing speaker
  immediately swaps the portrait and the displayed name.
- Add a `showDialog` step action that starts a named dialogue script, and a new completion event that
  holds the level on that step until the dialogue is dismissed — extending the level-director
  sequencing (this **modifies** `enemy-spawn-lifecycle`).
- Author dialogue content in the level definition as reusable named scripts (window geometry + reveal
  speed + the ordered messages), referenced by name from the `showDialog` action — mirroring how
  enemy and algorithm prototypes are already referenced.
- Register new portrait image assets in the image catalog; reuse the existing bitmap font.

## Capabilities

### New Capabilities
- `radio-dialogue`: the in-level radio communication window — its appearance, the progressive
  character-by-character text reveal, the portrait/speaker presentation and speaker swaps, the
  player-driven advancement, the suspension of the gameplay action while it is shown, and the
  authorable dialogue-script content model referenced by name from the level definition.

### Modified Capabilities
- `enemy-spawn-lifecycle`: adds a step action that starts a dialogue and a completion event that
  waits until the dialogue is dismissed before the next step, extending the step-action and
  completion vocabularies — consistent with how the music start/stop actions were added.

## Impact

- **New game code** under `game.scene.dialog` (the durative dialogue manager + the message-box
  rendering widget), new parser definitions under `game.scene.builder.definition`, a new `showDialog`
  action under `game.scene.action`, and a new wait state under `game.scene.statemachine`. The
  dialogue subsystem is kept separate from the `game.ui` UI-scene framework.
- **`LevelScene`** gains a local gameplay-suspend gate (distinct from the engine's global
  `GameContext` pause) and routes input to the dialogue while it is active; **`DirectorContext`**
  exposes the dialogue manager so the `showDialog` action can command it.
- **Level XML** gains a `<scripts>` section (alongside `<enemiesPrototype>`/`<algorithmsPrototype>`)
  and can declare a `showDialog` step with a `dialogClosed` completion; the SAX loader and
  `LevelDataRepository` parse and serve scripts by name.
- **Resources**: new portrait images added to the image catalog; the existing bitmap font is reused.
- **No engine module changes** and **no new runtime dependencies**. The playfield stays fixed at
  1360x660, so window coordinates are authored per script (a shared default is left as future work).
