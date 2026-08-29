## Why

TigerSupply is a three-module Maven reactor (`engine` -> `game` -> `launcher`), but the `game`
module is still an empty placeholder: all TigerSupply-specific game rules and content live inside
the `engine` module under `impl.*`. The framework and the game are therefore physically fused, so
the "engine as a reusable arcade framework" boundary is only a naming convention, not a real module
boundary. The launcher was already extracted (change `decouple-launcher`); populating the `game`
module is the deliberate next step recorded there.

## What Changes

- **BREAKING** (internal package rename): move all 68 `...engine.impl.*` source files from the
  `engine` module into the `game` module, repackaging them from
  `it.spaghettisource.tigersupply.engine.impl.*` to `it.spaghettisource.tigersupply.game.*` so each
  module owns a distinct top-level package (`engine.*`, `game.*`, `launcher.*`), matching the
  launcher precedent. The `impl` segment — which only ever meant "implementation of the engine" —
  disappears.
- Move the TigerSupply game content resources (`level/level-1.xml`, the image/audio/font catalog
  files, and their assets) from `engine/src/main/resources` into `game/src/main/resources`, and
  update the 13 fully-qualified enemy class names embedded in `level-1.xml` to the new `game.*`
  packages.
- Promote `UpdateAlgorithmFactoryWrapper` from `impl.utils` into the engine framework (it is pure
  framework code with zero game dependencies). This removes the single framework -> game reference
  (`image.finaleffect.StarEntity`), so the engine compiles with no reference into game code.
- Split `StaticResources`: the 17 framework contract keys (`ALGPRO_*`, `COLOR_*`, `FILTER_*`) stay
  in the engine; the game content constants (asset aliases, game/FSM state names, render-layer
  Z-orders) move to a new game-owned constants class.
- Update the launcher's `TigerSupplyGameManagerFactory` import to the game's new package.

No runtime behavior changes: the game builds and plays identically.

## Capabilities

### New Capabilities
- `game-module`: The TigerSupply game exists as a self-contained module — its rules, content, and
  resources — under its own `it.spaghettisource.tigersupply.game.*` package namespace, depending
  only on the engine, with no compile-time reverse dependency from the engine into the game.

### Modified Capabilities
<!-- None. The existing `launcher` spec already declares the "Module dependency direction"
     requirement (launcher -> game -> engine, engine builds without the outer modules); this change
     satisfies that requirement for the now-populated game module without altering the requirement
     itself. No engine-game-shell requirement changes. -->

## Impact

- **Code**: 68 files repackaged `engine.impl.*` -> `game.*` (~131 import lines updated); one
  framework class relocated (`UpdateAlgorithmFactoryWrapper` -> `engine.entity.logic`);
  `StaticResources` split into an engine holder (17 keys) and a new game constants class;
  `image.finaleffect.StarEntity` and `impl.utils.EntityFactoryWrapper` imports updated; launcher
  `TigerSupplyGameManagerFactory` import updated.
- **Resources**: `level-1.xml` (including its 13 FQN strings), the image/audio/font catalogs, and
  the image/audio/font assets move from `engine` to the `game` module.
- **Build**: `game/pom.xml` gains source (currently pom-only). The reactor order and dependency
  wiring (`game -> engine`, `launcher -> game`) and the launcher shade/exec packaging are already
  in place from `decouple-launcher`, so no POM dependency rewiring is required.
- **Specs**: introduces the new `game-module` capability; no change to `engine-game-shell`;
  consistent with the existing `launcher` "Module dependency direction" requirement.
- **Runtime / tests / dependencies**: no behavior change; no external runtime dependencies added or
  removed; no existing tests to update (the repository has none).
