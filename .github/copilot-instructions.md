# TIGER SUPPLY - Copilot Instructions

TigerSupply is a retro-inspired, 90s-style arcade shoot 'em up ("shmup") built in Java 17 on a
hand-rolled Swing/AWT game engine. There is no server, database, or network layer — it is a
single-process, offline desktop application. See
[documentation/architecture/system-overview/](../documentation/architecture/system-overview/)
for the full reverse-engineered reference (business overview, architecture, code structure,
API/data model, component inventory, technology stack, dependencies, code-quality assessment).

## Documentation

- **[documentation root location](../documentation)**
- **[data dictionary](../documentation/data-dictionary/dictionary-map.md)**

### Architecture
- **[system overview](../documentation/architecture/system-overview/)**

### Subsystems
- None registered yet. See
  [documentation/subsystems/subsystem-documentation-guide.md](../documentation/subsystems/subsystem-documentation-guide.md)
  for how to document one (e.g. the sprite/animation pipeline, the horde/level-script loader,
  or the weapon fire-control model) when it becomes worth extracting.

## Technology Stack

- **Language**: Java 17 (`maven.compiler.release=17`).
- **UI/rendering**: Java AWT/Swing (`JFrame`, `JPanel`, `Graphics2D`, `BufferedImage`) with
  manual double-buffering — no JavaFX, no LWJGL/libGDX.
- **Audio**: Java Sound API (`javax.sound.sampled`).
- **Data-driven content**: level "hordes" are scripted in XML (`game/src/main/resources/level/*.xml`)
  and parsed with SAX (`javax.xml.parsers`); images/audio/fonts are declared in plain-text catalog
  files (`*-catalog.txt`) and preloaded into in-memory repositories; entities and movement
  algorithms named in the XML are instantiated via `java.lang.reflect`.
- **Build**: Maven multi-module reactor (`engine` → `game` → `launcher`).
- **Testing**: JUnit Jupiter 5.11.0 declared (`test` scope) in every module — currently
  unused, no test sources exist yet.
- **No external runtime dependencies** — the engine is built entirely on the JDK standard
  library. Do not add a DI container, ORM, or web framework without discussing it first.

## Architecture

### Module organization
Organized into 3 Maven modules, each depending on the previous one (`launcher` → `game` →
`engine`):

### 1. `engine`
**Purpose**: The reusable, game-agnostic arcade-game framework only: game loop and
`Game`/`GameManager`/`GameManagerFactory` contracts, entity/sprite system, collision detection,
audio/image/font repositories, UI widgets, path splines, generic state machine, and the
`windows.GameFrame` window shell.
**Package**: `it.spaghettisource.tigersupply.engine`
**Contains**: 102 Java source files. Holds **no** reference to any concrete game type (engine
compiles standalone).

### 2. `game`
**Purpose**: The concrete TigerSupply game rules (player, enemies, weapons, the four scenes,
XML-driven level/horde builder) plus the game resources (image/audio/font catalogs,
`level/level-1.xml`).
**Package**: `it.spaghettisource.tigersupply.game` (the former `engine.impl.*` packages with the
`impl` segment dropped).
**Contains**: 68 Java source files + `src/main/resources`. Depends on `engine`.

### 3. `launcher`
**Purpose**: The composition root and packaging module — the single place that binds a concrete
game to the engine and produces the runnable jar.
**Package**: `it.spaghettisource.tigersupply.launcher`
**Contains**: 2 Java source files — `Launcher` (the runnable entry point `launcher.Launcher#main`;
owns window title + 1360x660 playfield) and `TigerSupplyGameManagerFactory` (the only class
outside `game.*` that names `game.control.GameManager`). Its POM builds the uber-jar
`launcher/target/tigersupply.jar` (shade) and provides `mvn -pl launcher exec:java` (exec).

---


## Coding Conventions

### Java Doc
- Place Javadoc immediately above the class/method; starts with /** and ends with */.
- The first sentence is a concise summary used in generated summaries and indexes.
- Document the contract: parameter constraints (ranges/nullability), return semantics (including special cases).
- Keep tag order consistent: @param, @return, @throws (optionally @since, @see).
- Formatting: use <p> between paragraphs; use inline tags {@link ...} and {@code ...} for references and code.
- Overrides: use {@inheritDoc} when inherited docs suffice; otherwise document only the differences.
- Deprecation: use @deprecated in Javadoc with the alternative (pair with @Deprecated in code if applicable).
- This codebase's existing convention is a short `@author` tag on most classes/interfaces
  (e.g. `@author Alessandro D'Ottavio`). Preserve it when editing existing files; don't add it
  to brand-new files unless asked.

### Logging
- No logging framework is used anywhere (no SLF4J/Log4j/`java.util.logging`). The existing
  convention on error paths is `e.printStackTrace()`. Don't introduce a new logging framework
  as a drive-by change — if structured logging is explicitly requested, confine it to the
  affected code and flag the inconsistency to the user.

### Exception Handling
- Most engine APIs declare a broad `throws Exception` rather than specific checked exception
  types — match this existing convention instead of introducing a new exception hierarchy.
- Some resource loaders (`FileAudioLoader`, `FontLoader`, `ImageLoader`) call `System.exit(1)`
  on startup failure; scene/controller code instead prints the stack trace and either calls
  `context.requestStopGame()` or rethrows a wrapped `Exception` with added context (see
  `GameFlowController`). Match the surrounding file's existing pattern.

---



## Do's and Don'ts

### DO
- Keep framework code in the `engine` module's top-level packages (`entity`, `sprite`, `image`,
  `audio`, `font`, `background`, `path`, `ui`, `statemachine`, `control`, `utils`, `windows`)
  and TigerSupply-specific game rules in the `game` module under `game.*` (`control`, `scene`,
  `builder`, `entity`, `weapon`, `ui`, `utils`), mirroring the module split. The single seam
  between them is `engine.control.GameManagerFactory`, implemented by
  `launcher.TigerSupplyGameManagerFactory`.
- Use the existing Factory/Singleton pattern (`XxxFactory.getInstance()` /
  `XxxManager.getInstance()`) for new asset types or managers, consistent with
  `EntityFactory`, `SpriteFactory`, `ImageRepositoryManager`, `AudioManager`,
  `FontRepositoryManager`.
- Register new image/audio/font assets in the matching catalog file
  (`image-catalog.txt`/`audio-catalog.txt`/`font-catalog.txt`) instead of loading resources
  ad hoc.
- Add new enemies/movement algorithms as new `game.entity.Enemy` / `engine.entity.logic.UpdateAlgorithm`
  subclasses referenced by fully-qualified class name from the level XML, following the
  existing data-driven pattern.
- Keep Entity (simulation), Sprite (presentation) and Weapon (fire control) decoupled — a
  deliberate design goal recorded in [note.txt](../note.txt).

### DON'T
- Don't introduce a DI framework, ORM, servlet/web layer, or database — this is an offline
  Swing desktop game with zero external runtime dependencies by design.
- Don't assume a REST API, database, or network call exists anywhere in this codebase — none do.
- Don't silently "fix" long-standing typos in public identifiers (`RocketLauncer`,
  `GamePanelMauseListener`, `LithingBolt`, the XML attribute `algoritmPrototype`,
  `Size.getHeigh()`) as a drive-by change — renaming them breaks the level XML/class hierarchy
  and should only be done as its own deliberate, requested change.
- Don't add new dependencies without a clear reason — beyond the JDK, the only declared
  dependency is JUnit (test scope, currently unused).

### Be Careful With
- `it.spaghettisource.tigersupply.game.entity.Entity` is an empty, unused class that
  shadows the real `it.spaghettisource.tigersupply.engine.entity.Entity` interface — double
  check imports when working with "Entity" under `game.entity`.
- Simple-name collisions across sibling packages, e.g. `game.weapon.player.RocketLauncer` vs
  `game.weapon.enemy.RocketLauncer`, and `game.scene.definition.Speed` vs `engine.entity.Speed`
  — verify the fully-qualified import before reusing a name.
- The play field is hard-coded to 1360x660 in `launcher.Launcher` (`PLAYFIELD_WIDTH`/
  `PLAYFIELD_HEIGHT`, passed into `windows.GameFrame`), and `level-1.xml`'s spawn coordinates
  assume this fixed resolution.

---

## Testing Guidelines

### Unit Testing
- No unit tests exist yet in any module. `junit-jupiter-api`/`junit-jupiter-params` 5.11.0 are
  declared as `test`-scope dependencies in every module's POM and are ready to use.
- Most services are singletons reached via static `getInstance()`; isolating one for a test
  typically means calling its `init(...)` method first (mirroring how `game.control.GameManager`
  bootstraps them). Discuss with the user before refactoring existing singletons toward
  constructor injection purely to make them testable.

### Test Organization
- Follow standard Maven conventions: place new tests under `<module>/src/test/java/...`,
  mirroring the main package structure (e.g. a test for
  `it.spaghettisource.tigersupply.engine.entity.Position` belongs at
  `engine/src/test/java/it/spaghettisource/tigersupply/engine/entity/PositionTest.java`).
---

## Summary

TigerSupply is a small, single-developer, offline Java 17 Swing arcade shoot-'em-up with no
external runtime dependencies, no tests, and no CI build/test pipeline. Prefer minimal,
consistent changes that follow the existing Factory/Singleton/Strategy/State patterns already
used throughout the `engine` and `game` modules.

---

