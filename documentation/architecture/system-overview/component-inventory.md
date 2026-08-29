# Component Inventory

> TigerSupply's code is split across three Maven modules: `engine` (reusable framework),
> `game` (concrete TigerSupply rules, package root `it.spaghettisource.tigersupply.game`), and
> `launcher` (composition root). The table below inventories it at the **package** level (the
> natural component boundary in this codebase), grouped by role.

## Application Packages

| Package | Module | Purpose |
|---------|--------|---------|
| `it.spaghettisource.tigersupply.launcher` | launcher | Composition root: `Launcher#main` entry point + `TigerSupplyGameManagerFactory`. |
| `it.spaghettisource.tigersupply.engine.windows` | engine | Window shell: `GameFrame` (`JFrame`), `GamePanel`, AWT input listeners. |
| `it.spaghettisource.tigersupply.engine.control` | engine | Game-loop contracts: `Game`, `GameManager`, `GameManagerFactory`, `AnimationLoop`, `ApplicationContext`. |
| `it.spaghettisource.tigersupply.game.control` | game | TigerSupply's concrete `GameManager` + `GameFlowController` (Scene switching, level progression). |
| `it.spaghettisource.tigersupply.game.scene` | game | The four Scenes: Presentation, Hangar, Level, Game Over. |
| `it.spaghettisource.tigersupply.game.scene.definition` | game | Level-XML DTOs (Horde, EnemyDefinition, EnemyPrototype, AlgorithmPrototype, …). |
| `it.spaghettisource.tigersupply.game.scene.statemachine` | game | Horde-spawn-pacing states built on the generic state machine. |
| `it.spaghettisource.tigersupply.game.builder` | game | SAX-based level-script parser and level-data manager. |
| `it.spaghettisource.tigersupply.game.entity` | game | Concrete simulation objects: Player, Enemy hierarchy, projectiles, effects. |
| `it.spaghettisource.tigersupply.game.weapon` (+ `player`, `enemy`) | game | Fire-control components for player and enemy entities. |
| `it.spaghettisource.tigersupply.game.ui` | game | Hangar loadout widgets (ship/weapon buttons, description panel, start button). |
| `it.spaghettisource.tigersupply.game.utils` | game | Concrete-game factory wrappers, a Z-order comparator, and the `GameResources` constants. |

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
| `utils` | Framework | Reflection factory, dynabean properties, framework constants (`StaticResources`), stream helpers. |

## Test Packages

None. No `src/test/java` source sets exist in `engine`, `game`, or `launcher` (see
[code-quality-assessment.md](./code-quality-assessment.md)); `junit-jupiter` is declared as a
dependency in every module's POM but is currently unused.

## Total Count

- **Total Maven modules**: 3 (`engine`, `game`, `launcher`) — all now contain source code.
- **Total Java source files**: 172 (`engine` 102, `game` 68, `launcher` 2).
- **Total resource files**: 72 (images, audio, fonts, catalogs, level XML — now in the `game` module).
- **Application packages** (TigerSupply-specific, `game.*`): 11
- **Composition-root package** (`launcher`): 1
- **Infrastructure packages**: 0
- **Shared/Framework packages** (`engine`): 10
- **Test packages**: 0
