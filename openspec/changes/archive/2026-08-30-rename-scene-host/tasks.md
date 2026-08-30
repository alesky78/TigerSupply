## 1. Engine `control` type renames

- [x] 1.1 Rename `SceneManager.java` -> `SceneHost.java` (type `SceneManager` -> `SceneHost`) in
  `engine/.../engine/control/`, updating its Javadoc summary to describe hosting the active scene.
  Verify the file compiles in isolation (`mvn -pl engine compile` will confirm once 1.x is done).
- [x] 1.2 Rename `AbstractSceneManagerJPanel.java` -> `AbstractSceneHost.java` (type
  `AbstractSceneManagerJPanel` -> `AbstractSceneHost`, `implements SceneHost`); drop the misleading
  `JPanel` suffix and refresh its Javadoc. Verify the class still declares the `panel`, `context`,
  `activeScene` fields and the input-forwarding methods unchanged.
- [x] 1.3 Rename `SceneManagerFactory.java` -> `SceneHostFactory.java` (type `SceneManagerFactory`
  -> `SceneHostFactory`); update `create(...)` return type to `SceneHost` and refresh Javadoc.
  Verify the factory Javadoc/`@link` references resolve.

## 2. Engine consumers

- [x] 2.1 Update `engine.control.GameLoop`: type `SceneManager` -> `SceneHost`, field/param
  `sceneManager` -> `sceneHost` (constructor param `manager` -> `sceneHost`). Verify the loop still
  calls `sceneHost.getActiveScene()`.
- [x] 2.2 Update the `{@link SceneManager}` Javadoc reference in `engine.control.Scene` ->
  `{@link SceneHost}`. Verify no other change in the file.
- [x] 2.3 Update `engine.windows.GamePanel`: types `SceneManager`/`SceneManagerFactory` ->
  `SceneHost`/`SceneHostFactory`, locals `manager` -> `sceneHost` and `sceneManagerFactory` param
  -> `sceneHostFactory`, and the three listener constructions. Verify imports updated.
- [x] 2.4 Update `engine.windows.GameFrame`: `SceneManagerFactory` -> `SceneHostFactory` param and
  Javadoc `@link`s. Verify the constructor still forwards it to `GamePanel`.
- [x] 2.5 Update `GamePanelKeyListener`, `GamePanelMauseListener`, `GamePanelMauseMotionListener`:
  type `SceneManager` -> `SceneHost`, field/param `sceneManager` -> `sceneHost` (class names with
  the `Mause` typo stay unchanged). Verify each still delegates to `sceneHost`.

## 3. Game module concrete host

- [x] 3.1 Rename `game.control.TigerSupplySceneManager` -> `TigerSupplySceneHost` (file + type,
  `extends AbstractSceneHost`); refresh its Javadoc. Verify `setActiveScene`, `getGamePanel`,
  `getGameContext`, the bootstrap constructor, and `keyPressed` override are intact.
- [x] 3.2 Update `game.control.SceneFlowController`: field/param/local
  `TigerSupplySceneManager sceneManager` -> `TigerSupplySceneHost sceneHost` throughout (import,
  constructor, `init`). Verify it still calls `sceneHost.getGameContext()`/`setActiveScene(...)`.

## 4. Launcher concrete factory

- [x] 4.1 Rename `launcher.TigerSupplySceneManagerFactory` -> `TigerSupplySceneHostFactory` (file +
  type, `implements SceneHostFactory`, returns/constructs `TigerSupplySceneHost`); refresh Javadoc.
- [x] 4.2 Update `launcher.Launcher`: `SceneManagerFactory` -> `SceneHostFactory`, local
  `sceneManagerFactory` -> `sceneHostFactory`, `new TigerSupplySceneHostFactory()`, and the
  `{@link TigerSupplySceneManagerFactory}` Javadoc reference. Verify `main` compiles.

## 5. Documentation

- [x] 5.1 Update `documentation/architecture/system-overview/*.md` (including any Mermaid class
  diagrams) to the new vocabulary (`SceneHost`, `AbstractSceneHost`, `SceneHostFactory`,
  `TigerSupplySceneHost`, `TigerSupplySceneHostFactory`). Verify via grep that no stale
  `SceneManager`-hierarchy identifier remains in `documentation/`.
- [x] 5.2 Update `.github/copilot-instructions.md` references to the renamed types. Verify via grep
  that only intentional mentions (if any) remain.

## 6. Verification

- [x] 6.1 Run `mvn -pl launcher -am clean package` from the repo root and verify the full reactor
  builds and `launcher/target/tigersupply.jar` is produced.
- [x] 6.2 Grep the whole tree (excluding `openspec/changes/archive/`) for `SceneManager`,
  `AbstractSceneManagerJPanel`, `TigerSupplySceneManager`, and `TigerSupplySceneManagerFactory` and
  verify zero matches remain from this hierarchy.
- [x] 6.3 Run `openspec validate rename-scene-host` and verify it passes.
