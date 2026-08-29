# Reverse Engineering Metadata

**Analysis Date**: 2026-08-29T00:00:00Z (original reverse-engineering pass)
**Last Synchronized**: 2026-08-29 — refreshed after the `decouple-launcher` and
`extract-game-module` architecture changes (see **Documentation Synchronization** below).
**Analyzer**: AI-DLC (GitHub Copilot coding agent, reverse-engineering workflow)
**Workspace**: `c:\Users\id100584\Documents\REPOSITORY\GITHUB PUBLIC\alesky78 public\TigerSupply\TigerSupply`
**Total Files**: 172 Java source files (`engine` 102, `game` 68, `launcher` 2), 72 resource
files (images, audio, fonts, catalogs, level XML — now under the `game` module), and 4 Maven
POM files. At the time of the original pass all 168 Java files lived in `engine`; the two
changes below split them across the three modules.

## Scope Covered

- Full multi-module Maven layout (`engine`, `game`, `launcher`) and the root aggregator POM.
- Every package in the framework module
  `engine/src/main/java/it/spaghettisource/tigersupply/engine/` (`control`, `entity`, `sprite`,
  `image`, `audio`, `font`, `background`, `path`, `ui`, `statemachine`, `utils`, `windows`) and
  the concrete-game packages in the game module
  `game/src/main/java/it/spaghettisource/tigersupply/game/` (`control`, `scene`, `builder`,
  `entity`, `weapon`, `ui`, `utils` — formerly `engine.impl.*`), plus the `launcher` composition
  root.
- Resource catalogs (`image-catalog.txt`, `audio-catalog.txt`, `font-catalog.txt`) and the
  level script (`level/level-1.xml`), now under `game/src/main/resources`.
- Repository-level process artifacts: `.github/workflows/copilot-setup-steps.yml` and
  `openspec/` (config plus the `engine-game-shell`/`game-module`/`launcher` specs and the two
  archived changes).
- Pre-existing `documentation/subsystems/` content (noted as referring to an unrelated
  project template and excluded from these findings — see
  [business-overview.md](./business-overview.md)).

## Artifacts Generated

- [x] [business-overview.md](./business-overview.md)
- [x] [architecture.md](./architecture.md)
- [x] [code-structure.md](./code-structure.md)
- [x] [api-documentation.md](./api-documentation.md)
- [x] [component-inventory.md](./component-inventory.md)
- [x] [technology-stack.md](./technology-stack.md)
- [x] [dependencies.md](./dependencies.md)
- [x] [code-quality-assessment.md](./code-quality-assessment.md)
- [x] reverse-engineering-timestamp.md (this file)

## Documentation Synchronization

These documents were updated after two completed OpenSpec changes that reshaped the module
layout (both archived under `openspec/changes/archive/`):

- **`decouple-launcher`** — extracted the composition root into the `launcher` module
  (`Launcher#main` + `TigerSupplyGameManagerFactory`), introduced the
  `engine.control.GameManagerFactory` seam, renamed the engine window shell
  `windows.Application` → `windows.GameFrame`, and added the shade/exec packaging.
- **`extract-game-module`** — moved the concrete game out of `engine.impl.*` into the `game`
  module under `it.spaghettisource.tigersupply.game.*` (dropping the `impl` segment), promoted
  `UpdateAlgorithmFactoryWrapper` into `engine.entity.logic`, split `StaticResources` into the
  framework-only `engine.utils.StaticResources` + game-specific `game.utils.GameResources`, and
  relocated the resources to `game/src/main/resources`.

The runnable entry point is now `it.spaghettisource.tigersupply.launcher.Launcher#main` (was
`engine.windows.Application#main`), packaged as `launcher/target/tigersupply.jar`.
