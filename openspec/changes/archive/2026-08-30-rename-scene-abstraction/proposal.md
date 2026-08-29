## Why

The engine's `control` package names its core hosting types after "the game" (`Game`,
`GameManager`, `ApplicationContext`, ...), but they actually model **scenes driven by a game loop**.
The mismatch is already visible in the code: the `Game` interface is implemented only by
`PresentationScene`, `HangarScene`, `LevelScene`, and `GameOverScene`; `GameManager`'s own Javadoc
calls it "the manager of the scene"; `AnimationLoop` comments read "get the scene to render"; and
the concrete game already exposes `getGameContext()` for a value typed as `ApplicationContext`.
Renaming these types to match the concepts they represent removes the "Game" overload — the Maven
module, the concrete manager, the per-frame scene, and the DI-flavoured `ApplicationContext` all
currently share the word — and makes the abstraction self-explanatory.

## What Changes

A pure, behaviour-preserving rename of the engine's `control` abstraction and every consumer of it.
No runtime behaviour, module boundary, resource format, or gameplay changes.

- **BREAKING (engine source-level API):** rename the engine `control` types:
  - `Game` -> `Scene`
  - `GameManager` -> `SceneManager`
  - `GameManagerFactory` -> `SceneManagerFactory`
  - `AbstractGameJPanel` -> `AbstractSceneJPanel`
  - `AbstractGameManagerJPanel` -> `AbstractSceneManagerJPanel`
  - `AnimationLoop` -> `GameLoop`
  - `ApplicationContext` -> `GameContext`
- Rename the members that carried the old vocabulary: `updateGame` -> `update`,
  `renderGame` -> `render`, `internalRenderGame` -> `internalRender`,
  `getActualGame` -> `getActiveScene`, `setActualGame` -> `setActiveScene`, the field
  `actualGame` -> `activeScene`, and fix the typo `requierdStart` -> `requestStartGame`.
- Normalize the input callbacks to AWT past-tense naming across both input APIs:
  `mousePress` -> `mousePressed` and `mouseMove` -> `mouseMoved` on the `Scene`/`SceneManager`
  contract **and** on `engine.ui.UserInterfaceManager`, which the scenes forward their input to
  (method signatures are unchanged; `keyPressed`/`keyReleased` already conform).
- Rename the game module's control classes to realign them and remove the now-ambiguous
  `GameManager` name: `game.control.GameManager` (the concrete engine `SceneManager`
  implementation) -> `TigerSupplySceneManager`, and `game.control.GameFlowController` ->
  `SceneFlowController`. For consistency the launcher's concrete factory
  `TigerSupplyGameManagerFactory` -> `TigerSupplySceneManagerFactory`.
- Update every remaining consumer: the four scenes, the `game.entity.*` and `game.builder` code
  that uses the renamed context, the engine `windows` host (`GamePanel`, `GameFrame`), and the AWT
  listener bodies, realigning local/field identifiers along the way (for example `game` ->
  `sceneManager`, `actualGame` -> `activeScene`).
- Give every renamed engine `control` type a clear, contract-describing Javadoc (summary sentence
  plus parameter/return/throws semantics), per the project's Javadoc conventions.
- Sync the reference documentation to the new vocabulary
  (`documentation/architecture/system-overview/*.md`, including the Mermaid class diagram, and
  `.github/copilot-instructions.md`).

Explicitly out of scope (intentionally unchanged): long-standing public-identifier typos stay (the
`GamePanelMauseListener`/`GamePanelMauseMotionListener` class names, `RocketLauncer`, `LithingBolt`,
the XML attribute `algoritmPrototype`, `Size.getHeigh()`); the game's internal level counters
(`actualLevel`/`numberLevel`); method signatures, the level XML, and the asset catalogs are
untouched.

## Capabilities

### New Capabilities

- None. This change introduces no new behaviour.

### Modified Capabilities

- None. This is a pure identifier rename. The `engine-game-shell`, `launcher`, and `game-module`
  specs describe behaviour in name-agnostic prose (for example "game-manager factory abstraction"
  and "application context") and remain accurate verbatim after the rename — no requirement or
  scenario changes. This change therefore sets `skip_specs: true` in its `.openspec.yaml` rather
  than inventing a delta to satisfy validation.

## Impact

- **Engine (`it.spaghettisource.tigersupply.engine.control`):** 7 types renamed and re-documented;
  consumers in `engine.windows` (`GamePanel`, `GameFrame`) and `engine.entity` (`AbstractEntity`)
  updated to the new names.
- **Game module:** `game.control.GameManager` -> `TigerSupplySceneManager` and
  `game.control.GameFlowController` -> `SceneFlowController`; the four `game.scene.*` scenes, the
  `game.entity.*` types and `game.builder.EnemyDataManager` that consume the renamed context are
  updated; `engine.ui.UserInterfaceManager`'s input methods are normalized.
- **Launcher:** `TigerSupplyGameManagerFactory` -> `TigerSupplySceneManagerFactory`, implementing
  `SceneManagerFactory` and constructing `TigerSupplySceneManager` (its stale `impl.*` Javadoc is
  refreshed). No `GameManager` identifier remains anywhere in the codebase after the change.
- **Data layer (unaffected):** `level-1.xml` and the `*-catalog.txt` files contain no references to
  these types (verified) — reflection-loaded content is limited to `game.entity.*` and algorithm
  classes.
- **Docs:** the reverse-engineering reference documentation and the Copilot instructions are updated
  to the new vocabulary; archived OpenSpec changes are left untouched as a historical record.
- **Verification:** every rename is a Java identifier change verifiable by
  `mvn -pl launcher -am clean package` plus a smoke launch; no behaviour or gameplay change is
  expected.
