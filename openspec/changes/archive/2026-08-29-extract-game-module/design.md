## Context

See proposal.md - Why for motivation. Relevant current state and constraints that shape the
approach:

- The `engine` module holds 169 Java files: 101 framework files (top-level packages) and 68
  game files under `it.spaghettisource.tigersupply.engine.impl.*`. The `game` module is pom-only
  (no `src`). The reactor and dependency wiring (`game -> engine`, `launcher -> game`) and the
  launcher shade/exec packaging already exist from change `decouple-launcher`.
- The framework/game boundary is already almost clean: exactly **one** compile-time framework ->
  game reference exists — `image.finaleffect.StarEntity` imports `impl.utils.UpdateAlgorithmFactoryWrapper`.
  All other `impl.*` references are game -> game or game -> framework.
- Two classes are misfiled relative to the boundary:
  - `impl.utils.UpdateAlgorithmFactoryWrapper` sits game-side but has **zero** game dependencies —
    it only wraps the framework `entity.logic.UpdateAlgorithmFactory`.
  - `utils.StaticResources` sits framework-side but is ~80% game content (asset aliases, game/FSM
    state names, render-layer Z-orders), with only 17 genuine framework keys.
- Runtime coupling: the engine's `utils.ClassFactory` instantiates entities/algorithms by
  fully-qualified name. The only such FQN strings that name `impl.*` classes are the **13** enemy
  `class="..."` attributes in `resources/level/level-1.xml`. There are no `engine.impl` string
  literals in framework Java code (verified by search).

## Goals / Non-Goals

**Goals:**
- Populate the `game` module so the game is a self-contained module, and guarantee the `engine`
  framework has zero compile-time reference into game code.
- Give each module a distinct top-level package root (`engine.*`, `game.*`, `launcher.*`).
- Relocate game content resources (level XML, catalogs, assets) into the `game` module.
- Correct the two boundary misclassifications (the algorithm wrapper and the constants holder).

**Non-Goals:**
- No runtime/gameplay behavior change; the game plays identically.
- Do **not** fix the long-standing identifier typos (`RocketLauncer`, `LithingBolt`,
  `algoritmPrototype`, `Size.getHeigh()`, `GamePanelMauseListener`) — renaming them is a separate,
  deliberate change and would churn the level XML and class hierarchy.
- No new dependencies, DI container, logging framework, or tests introduced by this change.
- Do not further decompose the framework's own packages beyond relocating the single wrapper.
- Do not change the fixed 1360x660 playfield or add new level content.

## Decisions

### Decision 1: Repackage to `game.*` (proposal Option B), not a same-package lift (Option A)

Move the 68 files into `game` **and** rename their package root from
`it.spaghettisource.tigersupply.engine.impl.*` to `it.spaghettisource.tigersupply.game.*`.

- **Why:** For a real separation the package name must reflect ownership. Leaving game code under
  `engine.impl.*` inside the `game` module would keep every game import claiming "this is engine
  code," leave the boundary non-self-enforcing (both modules under `engine.*`), and leave `game`
  the odd module out versus the launcher, which already owns `launcher.*`. Distinct roots make the
  boundary visible in every import and keep the module JARs free of a shared `engine.*` package
  namespace.
- **Alternative (A) considered:** lift-and-shift keeping `engine.impl.*`. Lower churn (imports and
  the 13 XML FQNs stay valid), but it separates the build without separating the design and invites
  drift. Rejected in favor of the cleaner boundary.

Package mapping drops the `impl` segment (which only meant "engine's implementation"):

```
  engine.impl.control.*     -> game.control.*
  engine.impl.entity.*      -> game.entity.*
  engine.impl.weapon.*      -> game.weapon.*
  engine.impl.scene.*       -> game.scene.*
  engine.impl.ui.*          -> game.ui.*
  engine.impl.builder.*     -> game.builder.*
  engine.impl.utils.*       -> game.utils.*   (minus the promoted wrapper; see Decision 2)
```

### Decision 2: Promote `UpdateAlgorithmFactoryWrapper` into the engine framework

Relocate it from `impl.utils` to `engine.entity.logic` (beside the `UpdateAlgorithmFactory` it
wraps), keeping it in the `engine` module.

- **Why:** It is pure framework code (verified: its only dependencies are `entity.logic.*`,
  `entity.Position`, `sprite.Sprite`, `utils.DynaProperties`, and `StaticResources.ALGPRO_*`).
  Promoting it turns the lone framework -> game back-edge (`StarEntity`) into a framework ->
  framework edge, and lets both engine visual effects and the game construct typed algorithms
  without re-introducing a leak.
- **Consumers updated (7):** `image.finaleffect.StarEntity` (framework, now framework -> framework);
  game callers `entity.ExplosionParticle`, `entity.Player`, `weapon.enemy.PlasmaCannon`,
  `weapon.enemy.RocketLauncer`, `weapon.enemy.StandardShot` (now game -> framework); and
  `utils.EntityFactoryWrapper`, which references the wrapper with **no import today** (same package)
  and therefore **gains** an explicit import after the move.
- **Alternative considered:** sever only `StarEntity`'s call (inline
  `UpdateAlgorithmFactory.newInstance(UpdateAlgorithmDefault.class, null)`) and leave the wrapper
  game-side. One-line fix, but it leaves a pure-framework class stranded in the game module and
  blocks any future framework reuse. Rejected.

### Decision 3: Split `StaticResources` by who reads each constant

The consumer map is a clean partition — **no framework class reads a game constant and no game
class reads a framework constant** — so this is a true split, not an untangling.

| Destination | Constants | Read by |
|---|---|---|
| Engine keeps `engine.utils.StaticResources` | `ALGPRO_*` (10), `COLOR_*` (3), `FILTER_*` (4) = 17 | `entity.logic.UpdateAlgorithm*`, the promoted wrapper, `image.effect.AbstractLookUpOpFilter`, `image.effect.EffectManager`, `sprite.SpriteColor` — all framework |
| New `game.utils.GameResources` | asset aliases (`FONT_*`, `BCKGROUND_*`, `EFFECT_*`, `ENEMY_*`, `PLAYER_*`, `ASTEROID_*`), `GAME_STATE_*`/`GAME_EVENT_*`, FSM `STATE_*`/`EVENT_*`, `Z_*` render layers | game scenes, weapons, `EntityFactoryWrapper`, `scene.statemachine.*` — all game |

- **Why:** Each module owns its own constants. Because the promoted wrapper is the only writer of
  `ALGPRO_*` and it lands framework-side, both ends of the `ALGPRO_*` contract are framework. The
  17 framework keys stay in the same class in the same package, so **framework consumers need zero
  changes**; the game-side reference swap (`StaticResources.X` -> `GameResources.X`) rides along
  with the package move in Decision 1.
- **Naming:** framework keeps the existing name `StaticResources` (now accurately "framework
  resource constants"); game side is `game.utils.GameResources`.
- **Alternatives considered:** move the whole class to game and leave a tiny framework class
  (more framework-side churn, same result); or push framework keys down into their subsystems
  (`ALGPRO_*` -> `entity.logic`, `FILTER_*`/`COLOR_*` -> `image.effect`) for tighter cohesion —
  cleaner but more churn, deferred as optional polish (see Open Questions).

### Decision 4: Sequence the cleanups before the bulk move

Order: (1) promote the wrapper and update its consumers; (2) split `StaticResources`; (3) move and
repackage `impl.* -> game.*`, move resources, update the 13 XML FQNs and the launcher factory
import.

- **Why:** After steps 1-2 the `engine` module already has no outbound reference into `impl.*`, so
  step 3 is a clean lift with nothing dangling on the framework side. Each step is independently
  compilable.

## Risks / Trade-offs

- **Level XML FQN drift** (the 13 `class="...engine.impl.entity.X"` strings are invisible to IDE
  refactors) -> Mitigation: update them in the same change and grep resources for `engine.impl` /
  `engine.entity` to confirm zero stale FQNs remain; run a launch smoke test so `ClassFactory`
  reflection actually resolves them.
- **Implicit same-package reference** (`EntityFactoryWrapper` -> wrapper has no import today) ->
  Mitigation: add the explicit import when the wrapper moves; the compiler flags it if missed.
- **Large mechanical diff** (~131 import lines across 68 files) -> Mitigation: use the IDE
  "Move/Rename package" refactor to perform the repackage atomically, then verify with a clean
  JDK 17 build rather than hand-editing imports.
- **Split-package hazard with `engine`** -> Avoided by Decision 1: `game.*` and `engine.*` are
  disjoint roots, so no package name is shared across the two JARs.
- **Wildcard static import** in the wrapper (`import static StaticResources.*`) -> after the split
  it resolves to the framework `StaticResources`, which still holds `ALGPRO_*`; unchanged (optionally
  narrow the wildcard to explicit `ALGPRO_*` imports).

## Migration Plan

1. Promote `UpdateAlgorithmFactoryWrapper` -> `engine.entity.logic`; update the 7 consumers
   (including the new import in `EntityFactoryWrapper`). Confirm `engine` has no `engine.impl`
   reference.
2. Trim `engine.utils.StaticResources` to the 17 framework keys; create `game.utils.GameResources`
   with the game keys (the game-side reference swap happens with step 3's move).
3. Move + repackage `impl.* -> game.*` into the `game` module; move `resources/level`,
   `resources/image`, `resources/audio`, `resources/font` into `game/src/main/resources`; update the
   13 FQN strings in `level-1.xml`; update the launcher `TigerSupplyGameManagerFactory` import.
4. Build with JDK 17 (`mvn clean package`) and run a launch smoke test (window opens, a horde spawns,
   the game plays); grep the whole tree to confirm zero `engine.impl` references remain.

Rollback: this is a pure structural refactor with no data or runtime migration — perform it on a
branch and revert the branch if the smoke test regresses.

## Open Questions

- Constants placement polish: keep the single framework holder `engine.utils.StaticResources`, or
  later push `ALGPRO_*` into `entity.logic` and `FILTER_*`/`COLOR_*` into `image.effect`? Deferrable
  — it does not change the module boundary, the specs, or the task breakdown.
- Final name/location of the game constants class (`game.utils.GameResources` vs `game.GameResources`)
  — cosmetic, safely decided at implementation time.
