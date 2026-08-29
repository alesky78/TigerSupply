# launcher Specification

## Purpose

Defines the launcher module as the application's composition root and single runnable entry point,
so the choice of which game to run — and how it is configured and packaged — lives outside the
reusable engine.

## Requirements

### Requirement: Application entry point lives in the launcher

The launcher module SHALL provide the application's `main` entry point. The engine module SHALL NOT
expose any runnable `main` entry point.

#### Scenario: Starting the game

- **WHEN** the packaged launcher artifact is executed
- **THEN** the game window opens and runs with the same title and playfield as before the change

#### Scenario: Engine exposes no entry point

- **WHEN** the engine module is inspected for a runnable entry point
- **THEN** no `main` method is present in the engine

### Requirement: Launcher owns application configuration

The launcher SHALL own the game-specific launch configuration — the window title and the playfield
dimensions — and supply them to the engine's window shell. The engine SHALL NOT hard-code the title
or the playfield dimensions.

#### Scenario: Window built from launcher configuration

- **WHEN** the launcher starts the application
- **THEN** the window is created with the title and the 1360x660 playfield supplied by the launcher

### Requirement: Launcher selects the concrete game

The launcher SHALL select the concrete game implementation and provide it to the engine through the
engine's game-manager factory abstraction, so composition happens in one place.

#### Scenario: Concrete game supplied at composition time

- **WHEN** the launcher composes the application
- **THEN** it supplies a factory that constructs the concrete game manager
- **AND** the engine hosting layer builds the game solely through that factory

### Requirement: Module dependency direction

Module dependencies SHALL flow launcher -> game -> engine only. The engine module's build SHALL NOT
declare a dependency on the game or launcher modules.

#### Scenario: Engine compiles without the outer modules

- **WHEN** the engine module is compiled in isolation
- **THEN** it compiles without any build dependency on the game or launcher modules

### Requirement: Launcher is the packaged runnable artifact

The launcher module SHALL declare the runnable main class and produce a runnable package, so the
application can be built and started from the launcher artifact without specifying the main class
externally.

#### Scenario: Build produces a runnable launcher

- **WHEN** the project is built
- **THEN** the launcher artifact declares its main class in its packaged manifest
- **AND** the application can be launched from that artifact directly
