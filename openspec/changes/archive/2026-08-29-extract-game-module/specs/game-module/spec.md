## Purpose

Defines the TigerSupply game as a self-contained module that owns all game-specific rules, content,
and resources and depends only on the reusable engine framework — with no dependency from the engine
back into the game.

## ADDED Requirements

### Requirement: Game rules reside in the game module

All TigerSupply-specific game behavior — the player, the enemies, the weapons, the scenes, the
level/horde loader, and the game's own constants — SHALL reside in the game module. The engine module
SHALL retain only reusable, game-agnostic framework code.

#### Scenario: Engine holds no game-specific code

- **WHEN** the engine module is inspected for TigerSupply-specific game types
- **THEN** no player, enemy, weapon, scene, level-loader, or game-constants type is present in the engine

#### Scenario: Game module holds the concrete game

- **WHEN** the game module is inspected
- **THEN** it contains the concrete player, enemies, weapons, scenes, and the level/horde loader that define TigerSupply

### Requirement: Game content resources reside in the game module

The TigerSupply content assets — the level definition, the image/audio/font catalogs, and their
referenced media — SHALL reside in the game module rather than the engine module, and SHALL remain
loadable by the packaged application.

#### Scenario: Engine carries no game content

- **WHEN** the engine module's resources are inspected
- **THEN** they contain no TigerSupply level definition, catalog, or media asset

#### Scenario: Packaged application loads game content

- **WHEN** the packaged application starts and a level begins
- **THEN** the level definition, images, audio, and fonts load from the game module's resources and the level plays

### Requirement: Game occupies its own package namespace

The game module's code SHALL reside under its own top-level package namespace, distinct from the
engine's namespace, so that no game type shares the engine's package root and each module's ownership
is visible in its package name.

#### Scenario: Game types under the game namespace

- **WHEN** the game module's types are inspected
- **THEN** every game type is under the game module's own top-level package `it.spaghettisource.tigersupply.game`

#### Scenario: No game type under the engine namespace

- **WHEN** the engine namespace `it.spaghettisource.tigersupply.engine` is inspected
- **THEN** it contains no TigerSupply game type

### Requirement: Engine source is free of references into the game

No source file in the engine module SHALL reference any game type; the engine SHALL host the game
only through its existing game-agnostic factory seam. This keeps the engine reusable by games other
than TigerSupply.

#### Scenario: No engine-to-game source reference

- **WHEN** the engine module's source is searched for references to the game module's packages
- **THEN** no such reference is found

#### Scenario: Engine is reusable without the game

- **WHEN** the engine module is consumed by a module other than the TigerSupply game
- **THEN** it provides its framework capabilities without requiring or pulling in any TigerSupply game type

### Requirement: Extraction preserves gameplay

Relocating the game into its own module SHALL NOT change observable gameplay; the application SHALL
start and play as it did before the extraction.

#### Scenario: Game plays identically after extraction

- **WHEN** the packaged application is launched after the extraction
- **THEN** the window opens with the same title and playfield, hordes spawn, and the game plays as before
