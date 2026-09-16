## Context

See proposal.md - Why. `SandboxMenuScene` currently draws one flat, top-to-bottom list of all
catalogued cases; with the current catalog the list overflows the fixed, non-resizable window and
the last effect entries fall below the visible area. The scene already holds a
`List<SandboxCase>` (each carrying a `Family`) and a single `selectedIndex`, renders with
`Graphics2D` in `internalRender`, and drives selection from `keyPressed`. The window size is fixed
(`GamePanel` derives the drawable from the launcher's playfield), so the layout - not the resolution
- must adapt.

## Goals / Non-Goals

**Goals:**
- Keep every catalogued entity reachable and launchable within the fixed window, independent of
  catalog size, by showing only one family's items at a time in a scrolling pane.
- Keep the state and rendering logic minimal and confined to `SandboxMenuScene`.

**Non-Goals:**
- No changes to the catalog model (`SandboxCatalog`, `SandboxCase`, `Family`), the test scene, the
  scene host, the launcher, or the window dimensions.
- No per-family cursor memory, no mouse interaction, no horizontal scrolling of families.

## Decisions

### Decision: Two-pane master-detail layout instead of multi-column or flat scroll

A flat list (even scrolling) hides most of the catalog, and a fixed multi-column grid only moves the
overflow limit. A master-detail layout bounds the problem: the family pane is inherently short
(one entry per `Family`), and only the opened family's items are shown, in a pane that scrolls. This
scales to any catalog size without magic numbers.

- Alternatives considered: auto-flow multi-column grid (still capped by window width; family headers
  split awkwardly across columns); single scrolling column (loses the at-a-glance family overview).

### Decision: Minimal navigation state

Track focus and two cursors:

```
familyIndex   int      highlighted family in the left pane
itemIndex     int      highlighted item in the right pane (0..familyItems.size()-1)
focusOnItems  boolean  false = focus on family pane, true = focus on item pane
```

The item list for the current family is derived on demand by filtering `cases` by
`families[familyIndex]` (or a small `Family -> List<SandboxCase>` grouping built once in the
constructor for convenience). No new persistent model type is introduced.

Navigation:

```
[family pane]  focusOnItems = false
  UP/DOWN   -> move familyIndex (clamped or wrapped), reset itemIndex = 0
  RIGHT/ENTER -> focusOnItems = true (open family)
  LEFT      -> no-op
  ESC       -> context.requestStopGame()

[item pane]  focusOnItems = true
  UP/DOWN   -> move itemIndex within the current family's items
  ENTER     -> host.showTest(currentFamilyItems.get(itemIndex))
  LEFT/ESC  -> focusOnItems = false (back to family pane)
```

Opening or changing family resets `itemIndex` to 0 - no per-family memory, per the agreed
simplification.

### Decision: Scroll the item pane by a derived visible window

Compute `rowsPerPage` from the pane height and line height. Derive the first visible row from
`itemIndex` so the highlighted item is always on screen (e.g. keep `itemIndex` within
`[scrollTop, scrollTop + rowsPerPage)` and adjust `scrollTop` when it leaves that band). Render a
`(itemIndex+1)/itemCount` indicator (e.g. `3/11`) in the item pane so the user knows more items
exist below. `scrollTop` is derived state, not independently persisted.

## Risks / Trade-offs

- [Family pane also overflows if families grow to dozens] -> Out of scope in practice: `Family` is a
  small fixed enum (three today). If it ever grows, the same scrolling treatment can be applied to
  the family pane later.
- [Two-pane layout must fit the fixed width] -> Panes use a simple fixed split (family column on the
  left, item column filling the rest); text is already short, so width is not a constraint.
- [Base capability `entity-sandbox` is not yet archived] -> This delta modifies that pending
  capability; archive `add-entity-sandbox` before archiving this change so the MODIFIED requirement
  has its base. Noted in proposal.md.
