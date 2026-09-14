## Context

See [proposal.md](proposal.md) for motivation. The design works within these existing facts:

- **Level sequencing** is a `LevelDirector` that owns a generic engine state machine and is ticked
  once per frame from `LevelScene.update(delta)`. A step runs an ordered list of fire-and-forget
  `LevelAction`s, then emits a **completion event** from a **closed vocabulary** (`timed`, `cleared`,
  `bossSpawned`), each mapping to one dedicated wait state. See the `enemy-spawn-lifecycle` spec.
- **Actions are the open extension point**, resolved by name through `LevelActionFactory`
  (`spawnHorde`, `playMusic`, `stopMusic`). A durative action commands a subsystem exposed on
  `DirectorContext` (e.g. `spawnHorde` → `EnemyGroup`, `playMusic` → `AudioManager`).
- **`LevelScene.update()`** today gates *all* gameplay behind `!context.isPaused() && !context.isStop()`
  — including `levelDirector.tick(...)`.
- **Level XML** references reusable prototypes by name (`<enemiesPrototype>`, `<algorithmsPrototype>`
  at the bottom of the file); the SAX loader validates time-gated steps at load and fails fast.
- **Input**: `LevelScene.keyPressed/keyReleased` forward to `Player`; fire is `VK_SPACE`, a *held*
  input (`shotRequest` true while down) that fires continuously.
- The playfield is fixed at 1360x660; the bitmap fonts `techno`/`comodore64` already exist.

## Goals / Non-Goals

**Goals:**

- Add the radio dialogue as a *new action + new completion + new durative subsystem*, reusing the
  established "actions open, completion closed" model rather than bolting on a parallel mechanism.
- Keep the dialogue subsystem self-contained in `game.scene.dialog.*`, separate from the `game.ui`
  UI-scene framework; no engine-module changes.
- Freeze the gameplay action during a dialogue **without** breaking the director's ability to detect
  dismissal.

**Non-Goals:**

- No shared/default window geometry — coordinates are authored per script (future work).
- No automatic word-wrap — authored lines are displayed verbatim.
- No branching/choices, no voice audio, no localization file; a script is a linear message list.
- No new input binding; reuse the fire control.

## Decisions

### 1. Suspend the action with a local gate, not the engine's global pause

A dialogue must freeze the player/enemies/shots while its own animation and the director keep
running (the director must poll for dismissal). The engine's `GameContext.isPaused()` cannot be used:
`LevelScene.update()` gates `levelDirector.tick(...)` behind it, so pausing globally would freeze the
very state machine that has to detect the dialogue ending — a deadlock.

`LevelScene.update()` is restructured so that, when not globally paused/stopped, it **always** ticks
the dialogue subsystem and the director, and gates only the gameplay subsystems behind
`!dialogManager.isActive()`:

```
update(delta):
  if (isStop() || isPaused()) return;         // engine global pause still respected
  dialogManager.update(delta);                // typewriter + blink; always alive
  levelDirector.tick(delta);                  // must run so awaitingDialog can see dismissal
  if (!dialogManager.isActive()):
      magageGameFlow();                        // player-death / level-clear checks
      player, enemies, effects, shots, collisions ... update
  background.updateBackground(delta);          // ambient; keeps scrolling (see Decision 7)
```

*Alternatives considered:* (a) global pause — deadlocks the director; (b) make the `showDialog`
action itself block step advancement — hides the wait in an action and contradicts the "completion
closed" model. The local gate keeps the mechanism explicit and symmetric with the other waits.

### 2. New completion event `dialogClosed` + wait state `awaitingDialog`

Extend the closed completion vocabulary with one entry, symmetric to `cleared`/`awaitingClear`:

```
executingStep  --dialogClosed-->  awaitingDialog
awaitingDialog --pending (self-loop)  until dialogManager.isFinished()
awaitingDialog --ready-->  executingStep
```

`LevelDirectorStateMachineFactory` gains `EVENT_DIALOG_CLOSED = "dialogClosed"` and
`STATE_AWAITING_DIALOG = "awaitingDialog"`, two `table` edges, and a new `StateAwaitingDialog` that
mirrors `StateAwaitingClear` but polls `context.getDialogManager().isFinished()`. The event, like
`timed`/`cleared`/`bossSpawned`, is built at runtime from the step's completion name (no shared
singleton needed).

*Alternative considered:* reuse `cleared` — wrong meaning (no enemies involved) and would conflate
two waits. A dedicated completion honors "one completion value maps to one dedicated wait".

### 3. `showDialog` action commands a durative `DialogManager` via `DirectorContext`

`ShowDialogAction` (registered `"showDialog"` in `LevelActionFactory`) is fire-and-forget: it reads
`script="..."`, resolves the `ScriptDefinition` from `DirectorContext.getLevelData().getScriptByName(...)`,
and calls `dialogManager.start(scriptDefinition)`. `DirectorContext` exposes
`get/setDialogManager()`, mirroring how `EnemyGroup`/`AudioManager` are commanded. `LevelScene` owns
the `DialogManager`, sets it on the context, updates it, renders it, and feeds it input.

### 4. XML: a `<scripts>` prototype section (Strada C)

Dialogue content lives in a new `<scripts>` block at the bottom of the level file, next to the other
prototype catalogs, and steps reference a script by name — the same reference-by-name pattern already
used three times:

```xml
<step>
  <actions><action type="showDialog" script="briefing-intro" /></actions>
  <completionEvent name="dialogClosed" />
</step>
...
<scripts>
  <script name="briefing-intro">
    <window posX="100" posY="450" width="1160" height="180"
            portraitWidth="160" charDelay="0.04" />
    <messages>
      <message speaker="Falcon Leader" portrait="portrait_falcon_calm">
        <line>Nemici individuati a ore undici.</line>
        <line>Prepararsi all'intercettazione...</line>
      </message>
    </messages>
  </script>
</scripts>
```

New parser POJOs live with the others in `game.scene.builder.definition`: `ScriptDefinition`
(name + `WindowDefinition` + `List<MessageDefinition>`), `MessageDefinition` (speaker, portrait,
`List<String>` lines), `WindowDefinition` (posX, posY, width, height, portraitWidth, charDelay).
`EnemyDataBuilderSaxXml` parses the new nesting; `LevelDataRepository` gains `getScriptByName(...)`.
`ActionDefinition` is **unchanged** — `showDialog` uses only the existing `script` property, with no
nested children.

*Alternatives considered:* (A) messages inline inside the action — would grow `ActionDefinition` and
clutter steps; (B) an external dialogue file — more infra than warranted now. Strada C keeps steps
clean, co-locates window + content, and matches the existing file shape.

### 5. Explicit authored lines, typewriter over a flat index

No word-wrap: each `<line>` is one display row. The typewriter reveals a running character count
`revealed = floor(elapsed / charDelay)` walked across the concatenated lines; a line draws
`min(remaining, line.length)` characters and passes the remainder down. When `revealed` reaches the
message's total character count, a blinking cursor/arrow is drawn. This keeps explicit lines and
progressive reveal fully compatible while the code stays trivial.

### 6. Edge-triggered advance, detected in `LevelScene`

Fire is a *held* input, but advancing must be *discrete* (one press = one advance) or a held key would
blow through every message. `LevelScene` tracks `fireHeld` from the continuous key stream (it sees
every event) and delivers a discrete `dialogManager.advance()` only on the up→down edge:

```
keyPressed(e):
  if (e == VK_SPACE):
     wasHeld = fireHeld; fireHeld = true;
     if (dialogManager.isActive()) { if (!wasHeld) dialogManager.advance(); return; }
  if (!dialogManager.isActive()) player.KeyboardPressed(e);   // else swallow gameplay keys
keyReleased(e):
  if (e == VK_SPACE) fireHeld = false;
  player.KeyboardReleased(e);   // always forward releases so movement/fire flags never stick
```

`advance()` encapsulates the two-stage behavior: reveal-all if still typing, else next message, else
finish. Because `fireHeld` is tracked across the whole scene, a key **held when the dialogue opens**
does not advance until released and pressed again (auto-repeat keeps `wasHeld` true), and always
forwarding `keyReleased` to the player prevents stuck movement after the dialogue closes.

### 7. `MessageBox` in `game.scene.dialog`, background keeps scrolling

The rendering widget (`MessageBox`: black rounded/plain rect, thin white border, portrait scaled to
`portraitWidth`, speaker name, revealed text, blink cursor) stays in `game.scene.dialog` for now —
game-local, deliberately not in `game.ui` (that framework is for UI-only scenes, not gameplay
overlays). The `DialogManager` holds timing/index/blink state and delegates drawing to `MessageBox`;
`LevelScene.internalRender` draws it last, on top of the frozen playfield. The ambient background is
left scrolling during a dialogue (it is not "the action"); gating it is a one-line change if a full
freeze is preferred later.

### 8. Fail-fast validation of script references at load

Mirroring the existing time-gated fail-fast, `LevelDirector.init()` validates that every `showDialog`
step references a script defined in `<scripts>`, failing level load with the offending name if not.

## Risks / Trade-offs

- **Director accidentally gated by the suspend** → deadlock. *Mitigation:* Decision 1 fixes the tick
  order explicitly (dialogue + director always tick; only gameplay is gated); covered by the
  `enemy-spawn-lifecycle` "waits until dismissed" scenarios.
- **Auto-repeat / key held at dialogue open skips a message.** *Mitigation:* edge detection on
  `fireHeld` tracked continuously in `LevelScene` (Decision 6).
- **Stuck movement keys after a dialogue** (releases not seen). *Mitigation:* always forward
  `keyReleased` to the player.
- **Fixed 1360x660 resolution** → window coordinates are hard-coded per script (like the existing
  background TODO). *Mitigation:* accepted; per-script params now, shared default later.
- **Authoring burden** (lines must be hand-fitted to the window). *Mitigation:* accepted trade-off for
  a much simpler renderer; stable because the resolution is fixed.
- **Simple-name collisions** (`Dialog`/`Message`) and the dead `game.entity.Entity` trap.
  *Mitigation:* confine everything to `game.scene.dialog.*` and the shared `definition` package.
- **No tests exist yet.** *Mitigation:* the `DialogManager` reveal/advance logic is a good first
  isolated unit test; optional in this change.
