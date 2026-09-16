## Context

See proposal.md - Why. The game is a fixed-timestep Swing shmup where everything on screen is a
`Scene` driven by the engine `GameLoop`, and the reusable seam between engine and game is
`engine.control.SceneHostFactory`: `windows.GameFrame` receives a factory, builds the concrete
`SceneHost`, and asks it each frame for the active `Scene`. In the game, `Launcher.main` passes
`TigerSupplySceneHostFactory`; the resulting `TigerSupplySceneHost` constructor calls the static
`init(...)` methods of the singleton repositories (`ImageRepositoryManager`, `FontRepositoryManager`,
`AudioManager`, `FinalEffectManager`, `SpriteFactory`, `EntityFactory`) and then sets the first
scene via `SceneFlowController.doPresentation()`.

Entities are built and wired by the public `game.utils.EntityFactoryWrapper` factory methods and
public setters (`setEffectManager`, `setPosition`, `setSpeed`, `setSize`, `setUpdateAlgorithm`,
`setContext`). They are not self-contained: each family needs different collaborators - projectiles
live in a shot group (some also need an effect group for trails), effects live in an effect group
(or are owned by the `FinalEffectManager` singleton), and enemies need a shot group, an effect
group, and a target `Entity` to aim/fire at. `LevelScene` shows exactly this wiring today.

## Goals / Non-Goals

**Goals:**
- Reuse the existing `SceneHostFactory` seam so the game window, loop, and asset repositories are
  used unchanged.
- Keep the whole tool in one new module with its own package root, so nothing lands in `game.*` or
  `launcher.*` and the game/launcher/engine modules are not edited.
- Keep each sandbox scene single-responsibility by splitting menu and test into two scenes.
- Make adding a future testable entity a one-line catalog addition.

**Non-Goals:**
- Automated/assertion-based testing (headless harness, PNG diffing). This tool is for interactive,
  visual/behavioural inspection only.
- Changing, refactoring, or "fixing" any game entity, weapon, or the engine to make it testable.
- Collision resolution, scoring, level flow, or any real gameplay in the sandbox.

## Decisions

### Separate `sandbox` Maven module with its own package root

Add a fourth module so the reactor is `engine -> game -> { launcher, sandbox }`. All sandbox code
lives under `it.spaghettisource.tigersupply.sandbox.*`; the module depends on `game` (and thus
transitively `engine`) and uses only public APIs, so no package-private access is needed.

- **Why**: mirrors how `launcher` is already a separate runnable module with its own root; keeps a
  crisp boundary (no game/launcher class ever names the sandbox) and gives the tool its own
  `exec:java` target without shipping inside the game jar.
- **Alternatives**: a new package root `sandbox.*` inside the `game` module (rejected: two roots in
  one module, shares the game artifact, easier accidental coupling); a debug branch inside the game
  flow behind a hidden key or `-D` flag (rejected by requirement - must not be reachable from the
  game).

### Two scenes (`SandboxMenuScene` + `SandboxTestScene`) over one dual-mode scene

The tool is modelled as two `Scene`s, navigated by the scene host, mirroring the existing
`Presentation -> Hangar -> Level` pattern. The menu scene resembles `PresentationScene` (mostly
render + selection); the test scene resembles a reduced `LevelScene` (owns the entity groups and the
target, no flow/collisions/player-ship).

- **Why**: each scene has one linear `keyPressed` handler and one meaning for the arrow keys,
  avoiding a fragile "if mode == MENU ... else ..." branch and mode-dependent key semantics. In the
  menu the arrows move the list cursor; in the test scene the arrows move the target. Entering a
  fresh scene also gives clean per-run state.
- **Alternatives**: a single scene with an internal `mode` flag (rejected: overloaded arrow keys,
  mixed responsibilities, manual state reset).

### `SandboxSceneHost` boots repositories and acts as the navigator

A `sandbox.control.SandboxSceneHost extends engine.control.AbstractSceneHost` repeats the same
repository `init(...)` bootstrap as `TigerSupplySceneHost`, but its initial scene is
`SandboxMenuScene`, and it exposes `showMenu()` / `showTest(SandboxCase)` that build the target
scene and call `setActiveScene(...)`. The selected case travels through the constructor
`new SandboxTestScene(context, case)`, exactly as `new LevelScene(context, player)` does. It also
keeps the engine-level global pause/quit key interception that `TigerSupplySceneHost` already
demonstrates.

- **Why**: avoids introducing a second singleton flow controller for only two transitions, and the
  scene-host already owns the active scene and context. Duplicating ~8 lines of repository init is
  an acceptable cost for keeping the game untouched.
- **Alternatives**: a `SandboxFlowController` singleton mirroring `SceneFlowController` (more
  stylistically uniform with the game but heavier for two transitions); extracting the repository
  bootstrap into a shared engine helper (rejected: that would edit the engine/game, which this
  change avoids).

### Catalog of `SandboxCase` entries keyed by family

`SandboxCatalog` holds an ordered list of `SandboxCase`, each declaring a `label`, a `family`
(`PROJECTILE` | `ENEMY` | `EFFECT`) that tells the test scene which group to insert it into, and a
build step `build(GameContext, managers, target)` that constructs the entity via the existing
`EntityFactoryWrapper` method and performs the family-specific wiring (e.g. `setEffectManager` for
`EnergyBall`, passing `target` to a seeker rocket). The test scene owns all three groups
(`effect`, `shot`, `enemy`) plus the target and drives them each frame.

- **Why**: matches the Factory pattern used across the codebase; a new testable entity is a single
  new list entry; the three groups cover every family's collaborator needs; effects owned by the
  `FinalEffectManager` singleton can be represented as cases that toggle that manager rather than a
  group.
- **Alternatives**: reflection-driven discovery of entity classes (rejected: entities need
  bespoke, per-type wiring that reflection can't infer); one scene per entity (rejected: explosion
  of near-identical scenes).

### Movable target is itself an `Entity`

`SandboxTarget` is a plain white square that extends the game entity base so it can be passed
directly as the `target` argument to target-seeking factory methods (`newEnemyShotSeekerRocket`,
homing, `FollowSprite`). In the test scene the arrow keys drive its position.

- **Why**: reuses the existing "entity that others aim at" contract (the player fills this role in
  the game) so no new targeting abstraction is needed; moving it live shows aim/home/follow
  behaviours reacting.

## Risks / Trade-offs

- **Repository init duplication** between `SandboxSceneHost` and `TigerSupplySceneHost` → accepted
  to keep the game untouched; if it drifts, a future change can extract a shared engine bootstrap.
- **Catalog must be kept in sync by hand** as new entities are added → mitigated by making each
  entry a single line and grouping by family; a missing entry only means "not yet testable", never
  a broken build.
- **Some entities have irregular construction** (e.g. `LightningBolt` needs fire/load times,
  `EnergeticShield` needs an effect group + position) → absorbed inside each case's build step,
  which is exactly where per-type knowledge belongs; no uniform constructor is assumed.
- **New Maven module adds reactor/build surface** → small and self-contained; it depends on `game`
  and introduces no new runtime dependency (pure JDK, consistent with the rest of the project).
- **Purely visual/interactive validation** (no automated assertions) → intentional per Non-Goals;
  the tool's value is fast human inspection, and correctness of the entities themselves is out of
  scope here.
