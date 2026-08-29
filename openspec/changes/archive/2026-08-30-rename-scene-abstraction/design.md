## Context

See [proposal.md](proposal.md) for motivation. The engine's `control` package holds the hosting
layer: the per-frame scene contract, the manager that supplies the active scene to the loop, the
fixed-timestep loop itself, and the shared lifecycle/screen state. All of it is named after "the
game" even though the engine is game-agnostic and the concrete implementations are already named
`*Scene`.

Constraints that shape the approach:

- The engine has **no external consumers** other than the in-repo `game` and `launcher` modules, so
  a breaking source-level rename is safe to do in one atomic change.
- Every rename is a **Java identifier** change, so the compiler and the IDE rename refactoring find
  and update all references. The only references that are *not* compiler-checked are Markdown docs
  and any reflection/string references — the latter were verified to be **absent** for these types
  (`level-1.xml` and the `*-catalog.txt` files name only `game.entity.*` and algorithm classes).
- Build requires JDK 17 (`--release 17`); the reactor order is `engine -> game -> launcher`.

## Goals / Non-Goals

**Goals:**

- Name each `engine.control` type after the concept it models (a `Scene`, a `SceneManager`, a
  `GameLoop`, a `GameContext`) so the hosting abstraction is self-describing.
- Produce one coherent hierarchy: `Scene <- AbstractSceneJPanel <- {Presentation,Hangar,Level,GameOver}Scene`
  and `SceneManager <- AbstractSceneManagerJPanel <- TigerSupplySceneManager`.
- Eliminate the `GameManager` identifier entirely, so nothing shares a simple name with the former
  engine interface.
- Give every renamed `engine.control` type clear, contract-describing Javadoc.
- Keep the reference documentation consistent with the new vocabulary.

**Non-Goals:**

- No behaviour, gameplay, module-boundary, resource-format, or method-signature change.
- No spec changes (pure refactor; `skip_specs: true` — see proposal Capabilities).
- Not fixing unrelated long-standing typos (`GamePanelMauseListener` /
  `GamePanelMauseMotionListener` class names, `RocketLauncer`, `LithingBolt`, XML `algoritmPrototype`,
  `Size.getHeigh()`) — renaming those would ripple into the level XML / class hierarchy and is a
  separate, deliberate decision per the repo conventions.
- Not renaming the game's internal level counters (`actualLevel`/`numberLevel`) — unrelated to the
  scene abstraction.

## Decisions

### Decision 1: The authoritative rename map

This table is the single source of truth for implementation. Renames are semantic (identifier-level),
applied with the IDE "rename symbol" refactoring so all references update atomically.

**Engine `control` types** (`it.spaghettisource.tigersupply.engine.control`):

| Old | New |
| --- | --- |
| `Game` | `Scene` |
| `GameManager` | `SceneManager` |
| `GameManagerFactory` | `SceneManagerFactory` |
| `AbstractGameJPanel` | `AbstractSceneJPanel` |
| `AbstractGameManagerJPanel` | `AbstractSceneManagerJPanel` |
| `AnimationLoop` | `GameLoop` |
| `ApplicationContext` | `GameContext` |

**Engine members:**

| Old | New | Declared on |
| --- | --- | --- |
| `updateGame(float)` | `update(float)` | `Scene` (+ overrides) |
| `renderGame()` | `render()` | `Scene` / `AbstractSceneJPanel` |
| `internalRenderGame(Graphics2D)` | `internalRender(Graphics2D)` | `AbstractSceneJPanel` (+ overrides) |
| `getActualGame()` | `getActiveScene()` | `SceneManager` / `AbstractSceneManagerJPanel` |
| field `actualGame` | `activeScene` | `AbstractSceneManagerJPanel` |
| `requierdStart()` | `requestStartGame()` | `GameContext` |
| `mousePress(int,int)` | `mousePressed(int,int)` | `Scene` / `SceneManager` |
| `mouseMove(MouseEvent)` | `mouseMoved(MouseEvent)` | `Scene` / `SceneManager` |

**Engine UI** (`engine.ui`):

| Old | New |
| --- | --- |
| `UserInterfaceManager.mousePress(int,int)` | `mousePressed(int,int)` |
| `UserInterfaceManager.mouseMove(MouseEvent)` | `mouseMoved(MouseEvent)` |

**Game module** (`game.control`):

| Old | New |
| --- | --- |
| `GameManager` (concrete) | `TigerSupplySceneManager` |
| `GameManager.setActualGame(...)` | `setActiveScene(...)` |
| `GameFlowController` | `SceneFlowController` |

**Launcher:**

| Old | New |
| --- | --- |
| `TigerSupplyGameManagerFactory` | `TigerSupplySceneManagerFactory` |

**Kept intentionally:** `paintScreen()` (descriptive), `getGameContext()` (already correct — now
returns `GameContext`), `getGamePanel()`, and the AWT override methods on the listeners
(`mousePressed`/`mouseMoved`/`keyPressed`/`keyReleased`, which are the `java.awt.event` contract and
already past-tense). Local/field identifiers are realigned for readability (`game` -> `sceneManager`
in the listeners, `gameManager` -> `sceneManager` and local `game` -> `scene` in
`SceneFlowController`, launcher locals -> `gameContext`/`sceneManagerFactory`).

### Decision 2: `SceneManager`, not `SceneDirector`

The engine is `*Manager`-heavy (`AudioManager`, `EnemyManager`, `ImageRepositoryManager`,
`FontRepositoryManager`, `FinalEffectManager`); `SceneManager` is the least-surprising fit and
`getActiveScene()` reads naturally on it. `SceneDirector` would introduce a new metaphor with no
payoff.

### Decision 3: Concrete manager becomes `TigerSupplySceneManager`

The concrete `game.control.GameManager` must not reuse the old interface's `GameManager` simple name.
`TigerSupplySceneManager` marks it as the concrete TigerSupply implementation of the engine
`SceneManager`, mirroring the existing `TigerSupply…` composition-root naming
(`TigerSupplyGameManagerFactory`). The launcher factory follows the same logic and becomes
`TigerSupplySceneManagerFactory`, which also lets us refresh its stale `impl.*` Javadoc. Result: the
`GameManager` identifier disappears from the codebase, removing the cross-module simple-name
collision for good.

### Decision 4: `GameFlowController` becomes `SceneFlowController`

Its entire public API is scene transitions (`doPresentation`/`doHangar`/`doNextLevel`/`doGameOver`),
and the player/enemy/level state it owns is exactly the state that persists *across* scene
transitions. `SceneFlowController` realigns the name with what it does while staying accurate.

### Decision 5: Include input-tense normalization, and extend it to `UserInterfaceManager`

`mousePress`/`mouseMove` (present tense) clashed with `keyPressed`/`keyReleased` (past tense) and with
the AWT `MouseListener`/`KeyListener` convention. Because a scene forwards its input straight into
`UserInterfaceManager` (see `HangarScene`), normalizing only the `Scene` side would leave a
mixed-tense call (`mousePressed(...) { uiManager.mousePress(...); }`). Normalizing both keeps the full
input chain consistent. Signatures are left unchanged — the `(int,int)` vs `MouseEvent` asymmetry is a
separate concern.

### Decision 6: Execution order that keeps the reactor compiling

Each symbol is renamed with the IDE rename refactoring, which updates references across all three
modules atomically, so the reactor stays green after every symbol. Steps are grouped by concern (see
[tasks.md](tasks.md)) and a full `mvn -pl launcher -am clean package` runs after each group as a gate.
Documentation is synced last, once the code names are final.

### Decision 7: Javadoc standard for renamed types

Every renamed `engine.control` type gets a class Javadoc whose first sentence is a concise summary,
followed by the contract (parameter constraints, return semantics including special cases, `@throws`
where the API declares it), per the project's Javadoc conventions. Existing `@author` tags are
preserved. `GameFrame` is the in-repo template for the target quality level.

## Risks / Trade-offs

- **Documentation drift (not compiler-checked).** The Mermaid class diagram and prose in
  `documentation/architecture/system-overview/*.md` and `.github/copilot-instructions.md` reference
  the old names. -> Mitigation: a dedicated docs-sync task plus a final repo-wide grep for the old
  identifiers (excluding `openspec/changes/archive/**` and this change's own artifacts).
- **Partial rename could break the reactor mid-way.** -> Mitigation: use IDE rename-symbol (atomic
  cross-module reference updates) and build after each task group.
- **Accidental over-reach** onto the excluded typos or the concrete-name boundary. -> Mitigation: the
  Non-Goals list is explicit; renames are scoped to the exact symbols in Decision 1.
- **`requierdStart()` typo fix touches its caller.** The only caller is `GamePanel.addNotify()`
  (`context.requierdStart()`); the rename refactoring updates it in place. -> Low risk.
- **Trade-off: two scene-related names in the game module** (`TigerSupplySceneManager` holds/serves
  the active scene; `SceneFlowController` decides transitions). This is an intentional separation of
  responsibilities, not a duplication.

## Migration Plan

- **Rollback:** the change is a pure source rename with no persisted state or data migration;
  reverting the commit fully restores the prior state.
- **Verification:** `mvn -pl launcher -am clean package` (JDK 17) must be green, followed by a smoke
  launch of `launcher/target/tigersupply.jar` (window opens with the same title/playfield, a level
  starts and hordes spawn). A final grep confirms no residual `Game`/`GameManager`/`GameManagerFactory`/
  `ApplicationContext`/`AnimationLoop`/`updateGame`/`renderGame`/`getActualGame`/`mousePress`/
  `mouseMove` identifiers remain in `src` outside the archived changes.

## Open Questions

None — all naming decisions are resolved.
