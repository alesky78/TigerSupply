# Component Inventory

> TigerSupply is a single-module monolith today: all executable code lives in the `engine`
> Maven module. The table below inventories it at the **package** level (the natural
> component boundary in this codebase), grouped by role, alongside the two placeholder Maven
> modules.

## Application Packages

| Package | Purpose |
|---------|---------|
| `it.spaghettisource.tigersupply.engine.windows` | Process bootstrap: `JFrame`/`JPanel`, AWT input listeners. |
| `it.spaghettisource.tigersupply.engine.control` | Game-loop contracts: `Game`, `GameManager`, `AnimationLoop`, `ApplicationContext`. |
| `it.spaghettisource.tigersupply.engine.impl.control` | TigerSupply's concrete `GameManager` + `GameFlowController` (Scene switching, level progression). |
| `it.spaghettisource.tigersupply.engine.impl.scene` | The four Scenes: Presentation, Hangar, Level, Game Over. |
| `it.spaghettisource.tigersupply.engine.impl.scene.definition` | Level-XML DTOs (Horde, EnemyDefinition, EnemyPrototype, AlgorithmPrototype, …). |
| `it.spaghettisource.tigersupply.engine.impl.scene.statemachine` | Horde-spawn-pacing states built on the generic state machine. |
| `it.spaghettisource.tigersupply.engine.impl.builder` | SAX-based level-script parser and level-data manager. |
| `it.spaghettisource.tigersupply.engine.impl.entity` | Concrete simulation objects: Player, Enemy hierarchy, projectiles, effects. |
| `it.spaghettisource.tigersupply.engine.impl.weapon` (+ `player`, `enemy`) | Fire-control components for player and enemy entities. |
| `it.spaghettisource.tigersupply.engine.impl.ui` | Hangar loadout widgets (ship/weapon buttons, description panel, start button). |
| `it.spaghettisource.tigersupply.engine.impl.utils` | Concrete-game factory wrappers and a Z-order comparator. |

## Infrastructure Packages

None. TigerSupply has no CDK, Terraform, CloudFormation, or other infrastructure-as-code —
it is a local desktop application with no deployed infrastructure.

## Shared/Framework Packages

| Package | Role | Purpose |
|---------|------|---------|
| `entity` (+ `manager`, `logic`, `collision`) | Framework | Generic Entity model, composite managers, movement strategies, collision detection. |
| `sprite` | Framework | Sprite abstraction + factory, image-filter cache key logic. |
| `image` (+ `repository`, `effect`, `finaleffect`) | Framework | Animation sequencing, image asset repository, per-sprite filters, full-screen post-effects. |
| `audio` (+ `repository`) | Framework | Music/FX playback and the audio-asset repository. |
| `font.repository` | Framework | Font asset repository. |
| `background` | Framework | Static/scrolling/parallax background rendering. |
| `path` | Framework | Natural-cubic-spline path generation. |
| `ui` (+ `listener`) | Framework | Generic composable clickable-widget UI toolkit. |
| `statemachine` | Framework | Generic, reusable finite-state-machine contract/implementation. |
| `utils` | Framework | Reflection factory, dynabean properties, shared constants, stream helpers. |

## Test Packages

None. No `src/test/java` source sets exist in `engine`, `game`, or `launcher` (see
[code-quality-assessment.md](./code-quality-assessment.md)); `junit-jupiter` is declared as a
dependency in every module's POM but is currently unused.

## Total Count

- **Total Maven modules**: 3 (`engine`, `game`, `launcher`)
- **Total Java source files**: 168 (all in `engine`; 0 in `game`; 0 in `launcher`)
- **Total resource files**: 72 (images, audio, fonts, catalogs, level XML — all in `engine`)
- **Application packages** (TigerSupply-specific, `impl.*`): 11
- **Infrastructure packages**: 0
- **Shared/Framework packages**: 10
- **Test packages**: 0
