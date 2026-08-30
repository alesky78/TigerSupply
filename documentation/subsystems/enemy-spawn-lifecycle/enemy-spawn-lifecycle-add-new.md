# Recipe — Add a New Horde (and optionally a new spawn state)

> **Related index**: [Enemy Spawn Lifecycle & State Machine](index.md)

## Table of Contents

1. [Goal](#1-goal)
2. [Prerequisites](#2-prerequisites)
3. [Part A — Add a new horde to a level (the common case)](#3-part-a--add-a-new-horde-to-a-level-the-common-case)
4. [Part B — Add a brand-new spawn state (advanced)](#4-part-b--add-a-brand-new-spawn-state-advanced)
5. [Verification](#5-verification)
6. [Checklist](#6-checklist)

---

## 1. Goal

Add another enemy wave to a level end to end, reusing the existing state machine — and, only if
you need a genuinely new gating condition between waves, add a new spawn state.

> **Design intent:** adding a wave should be **additive and isolated** — usually *just an XML
> edit*, no Java changes. You only touch Java when you introduce a new *kind* of transition.

---

## 2. Prerequisites

- The level file already exists (e.g. `game/src/main/resources/level/level-1.xml`) and is
  registered in `SceneFlowController.levelConfiguration`.
- The enemy and algorithm **prototypes** you want to reuse already exist in that XML (the
  `<enemyPrototypes>` / `<algorithmPrototypes>` sections). If you need a new enemy *type* or
  movement *algorithm*, create those first (a separate concern — a new `Enemy` subclass /
  `UpdateAlgorithm` subclass + a prototype entry), then come back here.
- The sprite alias referenced by the prototype exists in `image-catalog.txt`.

---

## 3. Part A — Add a new horde to a level (the common case)

A horde is one `<horde>` element inside the `<hordes>` list. Its **position in the list** is its
spawn order; its `<generateEvent name>` decides what happens *after* it spawns.

**Step A1 — add the `<horde>` element.** In the level XML, insert the new horde at the point in the
sequence where it should appear:

```xml
<horde>
    <!-- what to do AFTER this wave is on screen:
         waitTime  = pause ~1s then next wave
         waitKill  = wait until all these enemies are destroyed, then next wave
         bossGenerated = terminal: go to the boss-death state (use once, last) -->
    <generateEvent name="waitKill" />
    <enemy enemyPrototype="standard" posX="1350" posY="200" posZ="20" algorithmPrototype="default" />
    <enemy enemyPrototype="standard" posX="1350" posY="400" posZ="20" algorithmPrototype="default" />
</horde>
```

Rules:
- `enemyPrototype` / `algorithmPrototype` **must** match a `name` in the prototype sections.
- `posX/posY/posZ` are absolute pixels on the fixed **1360×660** playfield; `posZ` controls draw
  order (larger = drawn on top, via `EntityZComparator`).
- Choose `name`:
  - `waitTime` for a timed drip of waves,
  - `waitKill` for a "clear the screen" gate,
  - `bossGenerated` **only** for the final wave that should end the level.

> **The `time` attribute does nothing.** `StateWaitTime` always waits ~1 second. Do not rely on
> `time="…"`; if you need a different delay, that is a Java change (see the note in Part B).

**Step A2 — nothing else.** No Java edit is required: `HordeSequencer` consumes hordes by index and
already understands `waitTime` / `waitKill` / `bossGenerated`.

---

## 4. Part B — Add a brand-new spawn state (advanced)

Only needed for a **new gating condition** (e.g. "wait until N enemies remain", or "wait a
configurable delay"). Adding a state is four coordinated edits.

**Step B1 — declare the state name and its trigger event** in
[`GameResources`](../../../game/src/main/java/it/spaghettisource/tigersupply/game/utils/GameResources.java):

```java
public static final String STATE_WAIT_COUNT = "waitCount";
public static final String EVENT_WAIT_COUNT = "waitCount"; // event that enters the new state
```

**Step B2 — create the state class** under
`game/src/main/java/it/spaghettisource/tigersupply/game/scene/statemachine/`, extending
`StateAbstract`:

```java
public class StateWaitCount extends StateAbstract {
    public String getStateName() { return GameResources.STATE_WAIT_COUNT; }

    public Event internalProcess() throws Exception {
        // read the shared dataModel to decide; add a helper on EnemyBuilderDataModel if needed
        if (/* your condition */ true)
            return new Event(GameResources.EVENT_NEW_HORDE);
        return new Event(GameResources.EVENT_WAIT);
    }
}
```

- Return `EVENT_WAIT` to stay, `EVENT_NEW_HORDE` to move on to `StateGenerateHorde` (already
  wired), or a new event you then handle in Step B3.
- If the state needs new data (a counter, a configurable delay), add a method to
  [`EnemyBuilderDataModel`](../../../game/src/main/java/it/spaghettisource/tigersupply/game/scene/statemachine/EnemyBuilderDataModel.java)
  rather than reaching around it.

**Step B3 — wire the transitions** in
[`EnemyTxManager.findNextState`](../../../game/src/main/java/it/spaghettisource/tigersupply/game/scene/statemachine/EnemyTxManager.java):

```java
} else if (stateName.equals(GameResources.STATE_WAIT_COUNT)) {
    if (EventType.equals(GameResources.EVENT_WAIT)) {
        return state;                                  // stay
    } else if (EventType.equals(GameResources.EVENT_NEW_HORDE)) {
        newState = new StateGenerateHorde();
        newState.setDataModel(dataModel);
        return newState;
    } else {
        throw new StateMachineUnsupportedEvent("... state:" + stateName + " event:" + EventType);
    }
}
```

Also add a branch so `StateGenerateHorde` can transition **into** your state: under the
`STATE_GENERATE_HORDE` block, handle `EVENT_WAIT_COUNT` by returning a new `StateWaitCount`.

**Step B4 — reference it from a horde.** Set a horde's `<generateEvent name="waitCount" />` in the
level XML (this string is `EVENT_WAIT_COUNT`, matched in the `STATE_GENERATE_HORDE` block).

> **Follow the existing conventions, quirks included.** States are re-instantiated per transition
> (`new StateWaitCount()`), and remember every new state instance must get `setDataModel(dataModel)`.
> If you want a *configurable* wait time, this is where you would finally consume
> `GenerateEvent.time` — but that means threading it through the data model, since the XML value is
> currently ignored.

---

## 5. Verification

1. Build and run the game (`mvn -pl launcher exec:java`, or the launcher's runnable jar).
2. On startup, `HordeSequencer.loadLevelData` prints the parsed `LevelDataRepository` to stdout —
   confirm your new horde and its `generateEvent` appear in order.
3. Play the level and watch your wave spawn at the expected point:
   - `waitTime` waves should appear ~1s after the previous wave clears its gate;
   - `waitKill` waves should appear only after the previous wave is fully destroyed.
4. For a new state (Part B), temporarily add a `System.out.println` in `internalProcess()` (matching
   the codebase's `System.out`/`printStackTrace` convention) to confirm it is entered and emits the
   expected events; an unhandled `(state, event)` pair throws `StateMachineUnsupportedEvent`.

---

## 6. Checklist

**Part A (new horde):**
- [ ] `<horde>` inserted at the correct sequence position.
- [ ] `enemyPrototype` / `algorithmPrototype` names match existing prototypes.
- [ ] `posX/posY/posZ` valid for the 1360×660 playfield.
- [ ] `<generateEvent name>` is one of `waitTime` / `waitKill` / `bossGenerated`.
- [ ] Exactly one `bossGenerated` wave per level (the last).

**Part B (new state):**
- [ ] `STATE_*` and `EVENT_*` constants added to `GameResources`.
- [ ] New `StateAbstract` subclass created with `getStateName()` + `internalProcess()`.
- [ ] Any new shared data added via `EnemyBuilderDataModel`.
- [ ] `EnemyTxManager` handles both the transition **into** the new state (from
      `STATE_GENERATE_HORDE`) and **out of** it, with an unsupported-event fallback.
- [ ] Every new state instance gets `setDataModel(dataModel)`.
- [ ] A horde's `<generateEvent name>` references the new trigger event.
