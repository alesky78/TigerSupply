# Code Quality Assessment

## Test Coverage

- **Overall**: **None.** There is no `src/test/java` directory in `engine`, `game`, or
  `launcher` — zero test classes exist in the repository.
- **Unit Tests**: Not present. `junit-jupiter-api`/`junit-jupiter-params` 5.11.0 are declared
  as `test`-scope dependencies in every module's POM, but nothing consumes them yet.
- **Integration Tests**: Not present.

## Code Quality Indicators

- **Linting**: Not configured. No Checkstyle, PMD, SpotBugs/FindBugs, or `.editorconfig`
  configuration files were found anywhere in the repository.
- **Code Style**: Reasonably consistent Javadoc-style headers (`@author Alessandro
  D'Ottavio`/`DOttavio`) and package organization, but:
  - Comments mix **English and Italian** (e.g. TODOs in
    [AbstractEntity.java](../../../engine/src/main/java/it/spaghettisource/tigersupply/engine/entity/AbstractEntity.java),
    [LevelScene.java](../../../game/src/main/java/it/spaghettisource/tigersupply/game/scene/LevelScene.java),
    [GamePanel.java](../../../engine/src/main/java/it/spaghettisource/tigersupply/engine/windows/GamePanel.java)).
  - Several identifiers contain typos that are now part of the public API surface (renaming
    them is a breaking change, so they persist): `GamePanelMauseListener` /
    `GamePanelMauseMotionListener` ("Mause" for "Mouse"), `RocketLauncer` ("Launcer" for
    "Launcher", present in **both** `impl.weapon.player` and `impl.weapon.enemy`),
    `LithingBolt`/`LightinBoltLaser` ("Lithing"/"Lightin" for "Lightning"), the XML attribute
    `algoritmPrototype` ("algoritm" for "algorithm"), and `Size.getHeigh()`/`heigh` field
    ("Heigh" for "Height") used throughout the entity model.
  - `it.spaghettisource.tigersupply.game.entity.Entity` is an **empty, unused class**
    whose simple name collides with `it.spaghettisource.tigersupply.engine.entity.Entity`
    (the real Entity contract), which is a namespace-confusion risk for future maintainers/IDEs.
- **Documentation**: Fair. Most public classes/interfaces have a short Javadoc summary with
  `@author`; parameter/return/throws tags are inconsistently present, and there is no
  generated Javadoc site configured.

## Technical Debt

- **Hard-coded resolution**: `launcher.Launcher` hard-codes the play field to `1360x660`
  pixels (`PLAYFIELD_WIDTH`/`PLAYFIELD_HEIGHT`), `windows.GameFrame` calls `setResizable(false)`
  ("the game is not ready for other resolutions"), and `level-1.xml`'s enemy spawn coordinates
  assume this fixed resolution — the game cannot currently be resized/scaled.
- **Unexplained `+10` screen-size fudge factor** in
  [GamePanel.java](../../../engine/src/main/java/it/spaghettisource/tigersupply/engine/windows/GamePanel.java)
  (`context.setScreenHeight(pHeight+10)`), flagged by the original author's own TODO as a
  possible bug ("`//TODO +10 al size dello screen è un bug?`").
- **Rotation ignored by collision detection**: `AbstractEntity.getEntityRectangle()` only
  accounts for scale, not the entity's rotation angle, per its own TODO — rotated sprites
  (e.g. spinning `Asteroid`) collide using their unrotated bounding box.
- **Commented-out code left in place**: e.g. the space-background layer in
  `LevelScene`'s constructor and the boss "test horde" block at the top of `level-1.xml`.
- **Hard-coded single level**: `SceneFlowController` only registers `level/level-1.xml`
  (`numberLevel = 1`); the level-progression logic supports more levels but none are
  authored.
- **`System.exit(...)` calls scattered through library code** (`GameLoop`,
  `FileAudioLoader`, `FontLoader`, `ImageLoader`, `GameOverScene`) terminate the whole JVM on
  error paths instead of propagating a recoverable failure — this makes the engine code hard
  to reuse/test in-process (e.g. from a future JUnit test).
- **19 `e.printStackTrace()` call sites** across 16 files swallow exceptions to stdout/stderr
  instead of structured logging or propagation (no logging framework — e.g. SLF4J/Log4j — is
  used anywhere in the codebase).
- **`game.entity.Entity`** is dead code (see above) — a candidate for deletion.
- **`game.ui.MenuCompositionTest`** appears to be exploratory/spike code (its name and the
  fact that it is never referenced from any Scene suggest it was a test harness for the UI
  composition system) left in the main source tree rather than removed or moved to tests.
- **No dependency injection / high Singleton coupling** — nearly every cross-cutting service
  (`EntityFactory`, `SpriteFactory`, `ImageRepositoryManager`, `AudioManager`,
  `FontRepositoryManager`, `EffectManager`, `FinalEffectManager`, `SceneFlowController`) is a
  classic eager/lazy singleton reached via static `getInstance()`, which makes unit testing
  in isolation difficult (a likely contributor to the current lack of tests). The recent
  `engine.control.SceneManagerFactory` seam is a first step away from this: the engine no longer
  hard-references the concrete `TigerSupplySceneManager`.
- **Module split completed** — the framework/game-content split declared by the multi-module
  `pom.xml` has now been carried out (`engine` is framework-only, `game` holds `game.*` — the
  former `impl.*` — and `launcher` is the composition root), resolving what was previously the
  most prominent structural item of technical debt.

## Patterns and Anti-patterns

- **Good Patterns**:
  - Clean separation of **Entity** (simulation) from **Sprite** (presentation) and from
    **Weapon** (fire control), explicitly called out as a deliberate design goal in
    [note.txt](../../../note.txt).
  - **Strategy pattern** for movement (`UpdateAlgorithm`) keeps entity classes free of
    movement-specific branching.
  - **Data-driven level design**: hordes/prototypes/algorithms are declared in XML and
    resolved via reflection, so new enemies/waves can be authored without recompiling (see
    [api-documentation.md](./api-documentation.md)).
  - **Template-method** game loop (`AbstractSceneJPanel`) keeps double-buffering/paint logic
    out of every concrete Scene.
  - The `engine` framework has **no dependency on `game.*`**, a layering boundary now enforced
    by the Maven module split (engine compiles standalone) rather than left informal.
- **Anti-patterns**:
  - Pervasive **Singleton/static global state** (see Technical Debt above) instead of
    constructor/parameter-based dependency injection.
  - **God-object tendencies** in `game.control.SceneFlowController` and
    `game.builder.EnemyDataManager`, which each own/orchestrate a large slice of the game
    (Scene switching + level progression + player/enemy lifecycle in the former; XML
    parsing + repository + entity/algorithm/sprite creation in the latter).
  - **No automated tests** despite a testing framework being declared — regressions can only
    be caught by manually playing the game.
  - **Exception handling by printing + `System.exit`** instead of structured error handling
    or logging.
  - Several **misleading/duplicate type names** (`game.entity.Entity` vs `engine.entity.Entity`;
    `game.scene.definition.Speed` vs `engine.entity.Speed`; two unrelated `RocketLauncer` classes in
    sibling `player`/`enemy` packages) increase the chance of importing the wrong class.
