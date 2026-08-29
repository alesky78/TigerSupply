## 1. Baseline

- [x] 1.1 Establish a clean starting point: run `mvn -pl launcher -am clean package` with JDK 17 and verify the reactor builds green and `launcher/target/tigersupply.jar` launches (window opens, a level starts).

## 2. Rename engine control types

Each rename uses the IDE "rename symbol" refactoring so references update atomically across all three modules; verify the reactor compiles after each.

- [x] 2.1 Rename `Game` -> `Scene` and verify `mvn -pl launcher -am clean compile` is green.
- [x] 2.2 Rename `GameManager` -> `SceneManager` and verify compile.
- [x] 2.3 Rename `GameManagerFactory` -> `SceneManagerFactory` and verify compile.
- [x] 2.4 Rename `AbstractGameJPanel` -> `AbstractSceneJPanel` and verify compile.
- [x] 2.5 Rename `AbstractGameManagerJPanel` -> `AbstractSceneManagerJPanel` and verify compile.
- [x] 2.6 Rename `AnimationLoop` -> `GameLoop` and verify compile.
- [x] 2.7 Rename `ApplicationContext` -> `GameContext` and verify compile.

## 3. Rename engine members

- [x] 3.1 Rename `Scene.updateGame(float)` -> `update(float)`; verify the four scene overrides and the `GameLoop` call sites update and the reactor compiles.
- [x] 3.2 Rename `Scene.renderGame()` -> `render()` and `AbstractSceneJPanel.internalRenderGame(Graphics2D)` -> `internalRender(Graphics2D)`; verify the scene overrides update and compile passes.
- [x] 3.3 Rename `SceneManager.getActualGame()` -> `getActiveScene()` and the `AbstractSceneManagerJPanel` field `actualGame` -> `activeScene`; verify compile.
- [x] 3.4 Rename `GameContext.requierdStart()` -> `requestStartGame()`; verify the sole caller `GamePanel.addNotify()` updates and compile passes.

## 4. Input-callback normalization

- [x] 4.1 On the `Scene`/`SceneManager` contract (plus `AbstractSceneManagerJPanel` routing and the four scene overrides), rename `mousePress(int,int)` -> `mousePressed(int,int)` and `mouseMove(MouseEvent)` -> `mouseMoved(MouseEvent)`; verify compile.
- [x] 4.2 On `engine.ui.UserInterfaceManager`, rename `mousePress` -> `mousePressed` and `mouseMove` -> `mouseMoved`, and update the forwarding call sites in `HangarScene`; verify compile.
- [x] 4.3 Update the AWT listener bodies (`GamePanelMauseListener`, `GamePanelMauseMotionListener`) so their `mousePressed`/`mouseMoved` overrides call the renamed `SceneManager` methods, and realign the field `game` -> `sceneManager` in all three listeners; verify compile. (Listener class names keep the `Mause` spelling — out of scope.)

## 5. Rename game-module control classes

- [x] 5.1 Rename the concrete `game.control.GameManager` -> `TigerSupplySceneManager` and its `setActualGame(...)` -> `setActiveScene(...)`; verify compile.
- [x] 5.2 Rename `game.control.GameFlowController` -> `SceneFlowController`; realign its `gameManager` field -> `sceneManager` and the local `AbstractSceneJPanel game` -> `scene` in the `do*` methods; verify compile.

## 6. Rename launcher factory

- [x] 6.1 Rename `launcher.TigerSupplyGameManagerFactory` -> `TigerSupplySceneManagerFactory`, updating it to implement `SceneManagerFactory`, construct `TigerSupplySceneManager`, and use `GameContext`; realign `Launcher`'s locals (`applicationContext` -> `gameContext`, `gameManagerFactory` -> `sceneManagerFactory`); verify compile.
- [x] 6.2 Refresh the stale `impl.*` Javadoc on the renamed factory so it describes the current `game.control.TigerSupplySceneManager` seam.

## 7. Javadoc pass

- [x] 7.1 Add or clarify a contract Javadoc on each of the seven renamed `engine.control` types (`Scene`, `SceneManager`, `SceneManagerFactory`, `AbstractSceneJPanel`, `AbstractSceneManagerJPanel`, `GameLoop`, `GameContext`): a summary first sentence plus `@param`/`@return`/`@throws` where the API declares them, preserving existing `@author` tags. Verify every one of the seven has a class Javadoc and `mvn -pl engine compile` is green.
- [x] 7.2 Update the class Javadoc on `TigerSupplySceneManager` and `SceneFlowController` so the summary sentence reflects the new names and roles.

## 8. Documentation sync (English)

- [x] 8.1 Update `documentation/architecture/system-overview/*.md` to the new vocabulary — including the Mermaid class diagram in `code-structure.md` and the type/method references in `api-documentation.md`, `architecture.md`, `business-overview.md`, `component-inventory.md`, and `dependencies.md`; verify the Mermaid diagram still renders and none of the old identifiers remain in those files.
- [x] 8.2 Update the type references in `.github/copilot-instructions.md` (`Game`/`GameManager`/`GameManagerFactory`/`AnimationLoop`/`ApplicationContext`/`AbstractGameJPanel`/`AbstractGameManagerJPanel`) to the new names.

## 9. Final verification

- [x] 9.1 Run `mvn -pl launcher -am clean package` (JDK 17) and verify a green reactor build.
- [x] 9.2 Smoke-launch `launcher/target/tigersupply.jar` and verify the window opens with the same title and playfield, a level starts, and hordes spawn — confirming no behaviour change.
- [x] 9.3 Grep the `engine/`, `game/`, and `launcher/` source trees for residual old identifiers (`control.Game;`, `GameManager`, `GameManagerFactory`, `ApplicationContext`, `AnimationLoop`, `updateGame`, `renderGame`, `internalRenderGame`, `getActualGame`, `setActualGame`, `actualGame`, `requierdStart`, `.mousePress(`, `.mouseMove(`, `TigerSupplyGameManagerFactory`) and verify none remain (new names such as `GameContext`, `GameLoop`, `GameFrame`, `GamePanel` are expected and fine).
