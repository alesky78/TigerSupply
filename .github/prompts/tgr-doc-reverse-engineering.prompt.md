---
agent: 'agent'
description: Generate reverse engineering documentation for an arcade game codebase.
tools: ['vscode', 'execute', 'read', 'edit', 'search', 'agent', 'todo', 'web']
---


**Purpose**: Analyze an existing arcade-game codebase (a Java/Swing shoot-'em-up built on a
hand-rolled game engine) and generate comprehensive design artifacts. Assume an offline,
single-process desktop game: no server, database, cloud, or network layer.


## Step 1: Multi-Module Discovery

### 1.1 Scan Workspace
- All build modules (not just the mentioned ones)
- Module relationships via build config (Maven reactor: `engine` → `game` → `launcher`)
- Module roles: Engine (reusable framework), Game (concrete rules + resources), Launcher
  (composition root / runnable jar), Test

### 1.2 Understand the Game Design Context
- The core game the system implements overall (genre, premise, player fantasy)
- The core game loop (input → update → collision → render → audio)
- The design intent of every module (what part of the game or engine it owns)

### 1.3 Asset & Content Pipeline Discovery
- Asset catalogs (plain-text `*-catalog.txt` for images/audio/fonts) and the in-memory
  repositories they feed
- Data-driven level content (XML level/"horde" scripts under `resources/level/*.xml`) and the
  parser that reads them (e.g. SAX)
- Reflection-based instantiation (entities and movement algorithms named by fully-qualified
  class name in the XML)
- Fonts, sprites/animations, and audio clips referenced by the catalogs

### 1.4 Build System Discovery
- Build system: Maven (multi-module reactor), including `maven.compiler.release`
- POM files and inter-module declarations
- Build/runtime dependencies between modules
- Packaging (uber-jar / shade, exec plugin, runnable entry point)

### 1.5 Engine Architecture Discovery
- Game loop and timing (frame update / render cadence, double-buffering)
- Scene system (`Scene`, `SceneManager`, scene factory, scene flow/transitions)
- Entity/simulation system, Sprite/presentation system, and their decoupling
- Collision detection
- Weapon / fire-control model
- Asset repositories and managers (image, audio, font) and their Factory/Singleton access
- Level/horde builder, path/spline movement, generic state machine
- Window shell / rendering surface (`JFrame`/`JPanel`/`Graphics2D`/`BufferedImage`)

### 1.6 Code Quality Analysis
- Programming languages and frameworks (expect JDK standard library only)
- Test coverage indicators (test sources present/absent, JUnit configured)
- Linting/formatting configurations
- CI/CD pipelines (present or absent)

## Step 2: Generate Game Design Overview Documentation

Create `/documentation/architecture/system-overview/business-overview.md`:

```markdown
# Game Design Overview

## Game Context Diagram
[Mermaid diagram showing the game context: player, enemies, scenes, assets, level scripts]

## Game Description
- **Game Description**: [Overall description of the game — genre, premise, core loop]
- **Game Glossary**: [Domain terms the game uses and their meaning — e.g. horde, wave,
  weapon, enemy, player, scene, sprite, path]

## Module-Level Descriptions
### [Module/Component Name]
- **Purpose**: [What it does from the game/design perspective]
- **Responsibilities**: [Key responsibilities]
```

## Step 3: Generate Architecture Documentation

Create `/documentation/architecture/system-overview/architecture.md`:

```markdown
# System Architecture

## System Overview
[High-level description of the game and its engine]

## Architecture Diagram
[Mermaid diagram showing all modules, subsystems, asset repositories, and relationships]

## Component Descriptions
### [Module/Component Name]
- **Purpose**: [What it does]
- **Responsibilities**: [Key responsibilities]
- **Dependencies**: [What it depends on]
- **Type**: [Engine/Game/Launcher/Resource/Test]

## Game Loop & Scene Flow
[Mermaid sequence or state diagram of the game loop and scene transitions]

## Rendering & Audio Pipeline
- **Rendering**: [Surface, double-buffering, sprite draw path]
- **Audio**: [Playback path, clip lifecycle]

## Content & Asset Integration Points
- **Asset Catalogs**: [image/audio/font catalogs and the repositories they populate]
- **Level Scripts**: [XML level/horde files and the parser/builder that consume them]
- **Reflection Extension Points**: [entities/movement algorithms instantiated by class name]
```

## Step 4: Generate Code Structure Documentation

Create `/documentation/architecture/system-overview/code-structure.md`:

```markdown
# Code Structure

## Build System
- **Type**: [Maven multi-module reactor]
- **Configuration**: [Key POM files, compiler release, packaging plugins]

## Key Classes/Modules
[Mermaid class diagram or module/package hierarchy]

### Existing Files Inventory
[List all source files with their purposes - candidates for modification in brownfield work]

**Example format**:
- `[path/to/file]` - [Purpose/responsibility]

## Design Patterns
### [Pattern Name]
- **Location**: [Where used]
- **Purpose**: [Why used]
- **Implementation**: [How implemented — e.g. Factory/Singleton for asset managers,
  Strategy for movement algorithms, State machine for scene flow]

## Critical Dependencies
### [Dependency Name]
- **Version**: [Version number]
- **Usage**: [How and where used]
- **Purpose**: [Why needed]
```

## Step 5: Generate Extension Points & Content Formats Documentation

Create `/documentation/architecture/system-overview/api-documentation.md`:

```markdown
# Extension Points & Content Formats

## Engine Contracts (Internal APIs)
### [Interface/Class Name]
- **Methods**: [List with signatures]
- **Parameters**: [Parameter descriptions]
- **Return Types**: [Return type descriptions]
- **Extend By**: [How a game subclasses/implements it — e.g. new Enemy, new
  UpdateAlgorithm, new Weapon]

## Level Script Format (XML)
- **File(s)**: [level/*.xml]
- **Elements/Attributes**: [Key elements, attributes, and their meaning]
- **Referenced Types**: [Fully-qualified class names instantiated via reflection]

## Asset Catalog Formats
### [image-catalog.txt / audio-catalog.txt / font-catalog.txt]
- **Line Format**: [How an asset entry is declared]
- **Loaded Into**: [Which repository/manager consumes it]

## Domain / Entity Model
### [Entity/Model Name]
- **Fields/State**: [Field descriptions]
- **Relationships**: [Related entities — player, enemy, weapon, projectile, scene]
- **Constraints**: [Ranges, coordinate assumptions, playfield size]
```

## Step 6: Generate Component Inventory

Create `/documentation/architecture/system-overview/component-inventory.md`:

```markdown
# Component Inventory

## Engine Modules
- [Module/package name] - [Purpose]

## Game Modules
- [Module/package name] - [Purpose]

## Launcher / Composition Root
- [Module name] - [Purpose]

## Resources & Content
- [Asset catalog / level script] - [Purpose]

## Test Modules
- [Module name] - [Integration/Unit] - [Purpose]

## Total Count
- **Total Modules**: [Number]
- **Engine**: [Number]
- **Game**: [Number]
- **Launcher**: [Number]
- **Test**: [Number]
```

## Step 7: Generate Technology Stack Documentation

Create `/documentation/architecture/system-overview/technology-stack.md`:

```markdown
# Technology Stack

## Programming Languages
- [Language] - [Version] - [Usage]

## Frameworks / Libraries
- [UI/rendering toolkit] - [Version] - [Purpose]  (e.g. Java AWT/Swing)
- [Audio API] - [Version] - [Purpose]  (e.g. Java Sound)
- [XML parsing] - [Version] - [Purpose]  (e.g. SAX / JAXP)

## Build Tools
- [Tool] - [Version] - [Purpose]

## Testing Tools
- [Tool] - [Version] - [Purpose]

## Runtime Dependencies
- [List external runtime dependencies, or note "JDK standard library only"]
```

## Step 8: Generate Dependencies Documentation

Create `/documentation/architecture/system-overview/dependencies.md`:

```markdown
# Dependencies

## Internal Dependencies
[Mermaid diagram showing module dependencies: launcher → game → engine]

### [Module A] depends on [Module B]
- **Type**: [Compile/Runtime/Test]
- **Reason**: [Why dependency exists]

## External Dependencies
### [Dependency Name]
- **Version**: [Version]
- **Purpose**: [Why used]
- **Scope**: [Compile/Runtime/Test]
- **License**: [License type]
```

## Step 9: Generate Code Quality Assessment

Create `/documentation/architecture/system-overview/code-quality-assessment.md`:

```markdown
# Code Quality Assessment

## Test Coverage
- **Overall**: [Percentage or Good/Fair/Poor/None]
- **Unit Tests**: [Status]
- **Integration Tests**: [Status]

## Code Quality Indicators
- **Linting**: [Configured/Not configured]
- **Code Style**: [Consistent/Inconsistent]
- **Documentation**: [Good/Fair/Poor]

## Technical Debt
- [Issue description and location]

## Patterns and Anti-patterns
- **Good Patterns**: [List]
- **Anti-patterns**: [List with locations — e.g. duplicated simple names, public typos,
  empty shadowing classes]
```

## Step 10: Create Timestamp File

Create `/documentation/architecture/system-overview/reverse-engineering-timestamp.md`:

```markdown
# Reverse Engineering Metadata

**Analysis Date**: [ISO timestamp]
**Analyzer**: AI-DLC
**Workspace**: [Workspace path]
**Total Files Analyzed**: [Number]

## Artifacts Generated
- [x] business-overview.md
- [x] architecture.md
- [x] code-structure.md
- [x] api-documentation.md
- [x] component-inventory.md
- [x] technology-stack.md
- [x] dependencies.md
- [x] code-quality-assessment.md
```


## Step 11: Present Completion Message to User

```markdown
# 🔍 Reverse Engineering Complete

[AI-generated summary of key findings from analysis in the form of bullet points]

> **📋 <u>**REVIEW REQUIRED:**</u>**  
> Please examine the reverse engineering artifacts at: `/documentation/architecture/system-overview/`

```
