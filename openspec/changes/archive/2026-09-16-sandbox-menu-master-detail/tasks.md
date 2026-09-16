## 1. Navigation state and layout

- [x] 1.1 In `SandboxMenuScene`, replace the single `selectedIndex` with the master-detail state
  (`familyIndex`, `itemIndex`, `focusOnItems`) and a `Family -> List<SandboxCase>` grouping derived
  from `cases`; verify the scene compiles (`mvn -pl sandbox compile`).
- [x] 1.2 Add a helper that returns the current family's item list and one that computes
  `rowsPerPage` and the visible scroll window from the item pane height and line height; verify by
  reasoning that the highlighted `itemIndex` always falls inside the visible window.

## 2. Rendering

- [x] 2.1 Render the left family pane, highlighting the opened family when `focusOnItems` is true;
  verify by launching the sandbox (`mvn -pl sandbox exec:java -Dexec.mainClass=...SandboxLauncher`)
  and seeing the three families listed.
- [x] 2.2 Render the right item pane for the current family as a scrolling list plus a
  `(itemIndex+1)/itemCount` indicator (e.g. `3/11`); verify a family with more items than fit shows
  only the visible window and the indicator updates as the cursor moves.

## 3. Input handling

- [x] 3.1 Implement family-pane keys: `UP`/`DOWN` move `familyIndex` and reset `itemIndex` to 0,
  `RIGHT`/`ENTER` set `focusOnItems = true`, `LEFT` is a no-op, `ESC` calls
  `context.requestStopGame()`; verify each key behaves as specified in the sandbox.
- [x] 3.2 Implement item-pane keys: `UP`/`DOWN` move `itemIndex` within the family and scroll the
  pane, `ENTER` calls `host.showTest(currentItem)`, `LEFT`/`ESC` return focus to the family pane;
  verify launching an item still opens its test scene.

## 4. Verification

- [x] 4.1 With the current catalog, open the EFFECT family and confirm every effect entry
  (including the last ones that were previously clipped) is reachable and launchable within the fixed
  window.
- [x] 4.2 Confirm no changes were made outside `SandboxMenuScene` (catalog, test scene, host,
  launcher, engine/game modules unchanged) and the full build passes (`mvn -q -pl sandbox -am
  package`).
