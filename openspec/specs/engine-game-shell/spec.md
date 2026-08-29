# engine-game-shell Specification

## Purpose

Defines the engine's reusable, game-agnostic hosting layer — the window shell, panel host, and AWT
input listeners — plus the factory seam through which a concrete game manager is supplied without
the engine referencing any specific game.

## Requirements

### Requirement: Game-agnostic window shell

The engine SHALL provide a reusable window shell that manages the top-level window lifecycle and
hosts the game panel. The window shell, panel host, and input listeners SHALL NOT reference any
concrete game implementation type.

#### Scenario: Shell hosts a game without knowing it

- **WHEN** the window shell is constructed with a window title, playfield dimensions, an application
  context, and a game-manager factory
- **THEN** it displays the window and hosts the game without referencing a specific game type

#### Scenario: Window lifecycle drives the game lifecycle

- **WHEN** the window is iconified or deactivated
- **THEN** the game is paused
- **WHEN** the window is restored or activated
- **THEN** the game resumes
- **WHEN** the window is closed
- **THEN** the game is requested to stop

### Requirement: Game-manager factory seam

The engine SHALL define a game-manager factory abstraction, and the panel host SHALL construct its
game manager exclusively through that abstraction rather than instantiating a concrete game type.

#### Scenario: Panel builds its manager through the factory

- **WHEN** the panel host initializes
- **THEN** it obtains its game manager by invoking the supplied factory with the panel and the
  application context

#### Scenario: Factory produces a bound manager

- **WHEN** a factory is supplied to the shell and invoked
- **THEN** it produces a game manager bound to the given panel and application context
