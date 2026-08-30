## Why

The engine `control` type named `SceneManager` does not actually manage scenes: it holds a
single `activeScene`, hands it to the `GameLoop`, and routes AWT input to it. The real scene-flow
manager (transitions and cross-scene state) is `game.control.SceneFlowController`, so the
`SceneManager` name collides conceptually with it and overstates the role. The abstract base
`AbstractSceneManagerJPanel` compounds the confusion: it never extends `JPanel` (it only holds a
reference to one), so the `JPanel` suffix is misleading. Renaming the hierarchy to `SceneHost`
makes the abstraction self-describing — it *hosts* the active scene (plus the hosting panel and
`GameContext`) and delegates lifecycle/input to it.

## What Changes

A pure, behaviour-preserving rename of the engine's active-scene host abstraction and every
consumer of it. No runtime behaviour, module boundary, resource format, or gameplay changes.

- **BREAKING (engine source-level API):** rename the engine `control` types:
  - `SceneManager` -> `SceneHost`
  - `AbstractSceneManagerJPanel` -> `AbstractSceneHost` (drop the misleading `JPanel` suffix; the
    class implements `SceneHost` and holds a `JPanel`, it does not extend one)
  - `SceneManagerFactory` -> `SceneHostFactory`
- Rename the game module's concrete implementation `game.control.TigerSupplySceneManager` ->
  `TigerSupplySceneHost`.
- Rename the launcher's concrete factory `launcher.TigerSupplySceneManagerFactory` ->
  `TigerSupplySceneHostFactory`.
- Realign the identifiers that carried the old vocabulary in every consumer: the field/parameter
  `sceneManager` -> `sceneHost` (in `GameLoop`, the three `GamePanel*Listener`s, and
  `SceneFlowController`), and the local `manager`/`sceneManagerFactory` in `GamePanel` ->
  `sceneHost`/`sceneHostFactory`, plus the `sceneManagerFactory` local in `Launcher`.
- Update the `{@link SceneManager}`/`{@link SceneManagerFactory}` Javadoc references in
  `Scene`, `GameFrame`, `GamePanel`, and the launcher classes, keeping the existing Javadoc
  conventions (`@author`, contract-describing summaries).
- Sync the reference documentation to the new vocabulary
  (`documentation/architecture/system-overview/*.md`, including any Mermaid diagrams) and
  `.github/copilot-instructions.md`.

Explicitly out of scope (intentionally unchanged): `game.control.SceneFlowController` keeps its
name (it remains the real scene-flow manager); long-standing public-identifier typos stay
(`GamePanelMauseListener`/`GamePanelMauseMotionListener`, `RocketLauncer`, `LithingBolt`, the XML
attribute `algoritmPrototype`, `Size.getHeigh()`); the unrelated `*Manager` types
(`EnemyManager`, `AudioManager`, `FinalEffectManager`, `EnemyTxManager`, the repository managers)
are untouched; method signatures, the level XML, and the asset catalogs are untouched.

## Capabilities

### New Capabilities
- None. This change introduces no new behaviour.

### Modified Capabilities
- None. This is a pure Java-identifier rename with no spec-level behaviour change, so this change
  sets `skip_specs: true` in its `.openspec.yaml` rather than inventing a delta to satisfy
  validation.

## Impact

- **Engine (`it.spaghettisource.tigersupply.engine.control`):** 3 types renamed and re-documented
  (`SceneManager`, `AbstractSceneManagerJPanel`, `SceneManagerFactory`); consumers in
  `engine.control.GameLoop` and `engine.windows` (`GamePanel`, `GameFrame`,
  `GamePanelKeyListener`, `GamePanelMauseListener`, `GamePanelMauseMotionListener`) updated to the
  new names; the `{@link SceneManager}` reference in `engine.control.Scene` updated.
- **Game module:** `game.control.TigerSupplySceneManager` -> `TigerSupplySceneHost`;
  `game.control.SceneFlowController`'s `TigerSupplySceneManager sceneManager` field/parameter
  realigned.
- **Launcher:** `TigerSupplySceneManagerFactory` -> `TigerSupplySceneHostFactory`, implementing
  `SceneHostFactory` and constructing `TigerSupplySceneHost`; `Launcher`'s local and Javadoc
  updated. No `SceneManager` identifier from this hierarchy remains anywhere after the change.
- **Data layer (unaffected):** `level-1.xml` and the `*-catalog.txt` files contain no references to
  these types (verified) — reflection-loaded content is limited to `game.entity.*` and algorithm
  classes.
- **Docs:** the reverse-engineering reference documentation and the Copilot instructions are
  updated to the new vocabulary; archived OpenSpec changes are left untouched as a historical
  record.
- **Verification:** every rename is a Java identifier change verifiable by
  `mvn -pl launcher -am clean package`; no behaviour or gameplay change is expected.
