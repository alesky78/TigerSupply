## Why

The sandbox menu renders every catalogued entity as one long vertical list. With today's catalog
(11 projectiles, 7 enemies, 5 effects, plus family headings) the list already runs past the bottom
of the fixed, non-resizable window: the last effect entries are drawn off-screen and cannot be
selected. The playfield is intentionally a fixed size, so the fix belongs in the menu layout, and it
must keep working as the catalog grows.

## What Changes

- Replace the single scrolling-past-the-edge list in `SandboxMenuScene` with a two-pane
  master-detail layout:
  - **Left pane**: the (short, fixed) list of entity families.
  - **Right pane**: the entities of the currently opened family, shown as an independently
    scrolling list so a family of any length always fits the window.
- Add master-detail navigation: `RIGHT`/`ENTER` opens the highlighted family (focus moves to the
  right pane), `LEFT` returns focus to the family pane, `UP`/`DOWN` move within the focused pane,
  `ENTER` on an item launches its test, `ESC` from the family pane quits the sandbox.
- The opened family is highlighted in the left pane while the right pane has focus.
- The right pane shows a position indicator (e.g. `3/11`) so the user knows more items exist below.
- `LEFT` while focus is already on the family pane is a no-op.
- Opening or switching family resets the item cursor to the first entry (no per-family memory), to
  keep the state minimal.

## Capabilities

### New Capabilities
<!-- none -->

### Modified Capabilities
- `entity-sandbox`: the "A menu lists every testable entity and launches one" requirement changes
  from a single flat list to a two-pane master-detail menu whose per-family lists scroll, so all
  entities remain reachable regardless of catalog size.

## Impact

- Affected code: `sandbox/src/main/java/it/spaghettisource/tigersupply/sandbox/scene/SandboxMenuScene.java`
  (rendering and key handling only).
- No changes to the catalog (`SandboxCatalog`, `SandboxCase`, `Family`), the test scene, the scene
  host, the launcher, or the fixed window dimensions.
- No engine, game, or launcher module changes; the sandbox stays isolated behind existing public
  APIs.

> Note: `entity-sandbox` is defined by the not-yet-archived `add-entity-sandbox` change. This delta
> assumes that change's spec is the base; archive `add-entity-sandbox` before archiving this one.
