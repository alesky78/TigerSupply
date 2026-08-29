## Context

See proposal.md - Why. Today the whole game lives in `engine`; `game` and `launcher` are empty
placeholders. The single backwards edge is `engine.windows.GamePanel` constructing
`engine.impl.control.GameManager` (framework -> concrete game). Everything else in the launch path
already talks to `control.*` abstractions (`ApplicationContext`, `AnimationLoop`, and the
`Game` / `GameManager` interfaces). The concrete `GameManager(JPanel panel, ApplicationContext ctx)`
requires the panel in its constructor, which creates a chicken-and-egg with `GamePanel`.

The Maven chain is already `launcher -> game -> engine`, so the launcher can see `engine` types
(including `engine.impl.*`) transitively through the empty `game` module.

## Goals / Non-Goals

**Goals:**

- Invert the one framework -> game edge so the engine's hosting layer is game-agnostic.
- Move `main()` and all game-specific launch configuration into `launcher` as the composition root.
- Make `launcher` the runnable, packaged artifact.
- Leave exactly one intentional, obvious seam that names the concrete game (the launcher factory).

**Non-Goals:**

- Moving the `impl.*` game tree into the `game` module (deferred; it stays in `engine`).
- Relocating game-content resources out of `engine`.
- Any gameplay or resolution behavior change.

## Decisions

### Decision: Invert construction with a `GameManagerFactory` abstraction

The engine gains `control.GameManagerFactory` with a single method
`GameManager create(JPanel panel, ApplicationContext ctx) throws Exception`. `GamePanel` takes a
factory and calls `factory.create(this, context)` instead of `new`-ing the concrete manager.

- **Why not inject a built `GameManager` instance?** The concrete manager needs the panel in its
  constructor, but the panel needs the manager to build the loop and listeners. A factory resolves
  the chicken-and-egg cleanly; `GamePanel` already uses the value only as the `GameManager`
  interface everywhere except the `new`.
- **Why not `ServiceLoader`/reflection?** Overkill for one wiring point and less explicit than
  constructor injection from the composition root.

### Decision: Rename `Application` -> `GameFrame` and parameterize it

`engine.windows.Application` becomes `engine.windows.GameFrame`, loses `main()`, and takes
`(String title, int width, int height, ApplicationContext context, GameManagerFactory factory)`.
It keeps the JFrame mechanics (pack/resize/visible) and the window-lifecycle listeners that delegate
to `ApplicationContext`. The screen-inset/sizing mechanics stay in `GameFrame`; the *target*
resolution and title are passed in by the launcher.

- **Why rename?** The name `Application` implies "the app" (composition root); the engine class is
  really a reusable window host. The rename makes the boundary self-documenting.

### Decision: Composition root in `launcher`

Add `launcher.Launcher` (owns `main()`, creates `ApplicationContext`, supplies title + 1360x660,
constructs the `GameFrame`) and `launcher.TigerSupplyGameManagerFactory implements
GameManagerFactory` (the single place that names `engine.impl.control.GameManager`).

- **Why launcher, not game?** `game` is empty and the concrete manager still lives in `engine.impl`;
  placing the factory in `launcher` keeps composition in one module now and turns the impl -> game
  move into a later, mechanical step.

### Decision: Package the launcher as the runnable artifact

Add `maven-shade-plugin` to `launcher/pom.xml` with `Main-Class:
it.spaghettisource.tigersupply.launcher.Launcher`, producing an uber-jar that bundles `engine`
(and its resources: level XML + catalogs) so the game runs from a single artifact. Optionally add
`exec-maven-plugin` for `mvn -pl launcher exec:java` during development.

- **Why shade over assembly?** Simplest runnable fat-jar for a pure-JDK app with no external runtime
  deps; resources on the classpath are bundled automatically.

## Risks / Trade-offs

- **Launcher references `engine.impl.control.GameManager` transitionally** -> Mitigation: confine it
  to the one factory class, document it as the seam, and remove it in the follow-up impl -> game
  change.
- **Entry-point move is BREAKING for run configs** -> Mitigation: call it out in the proposal;
  update `documentation/` and `.github/copilot-instructions.md` references as a follow-up.
- **Shade uber-jar could bundle duplicate/unwanted files** -> Mitigation: pure-JDK deps only; if
  needed, add a minimal manifest/filter transformer. Low risk.
- **Rename churn touches every reference to `Application`** -> Mitigation: the only references are
  its own `main` and the module has a single entry path; a language-server rename covers it.

## Migration Plan

1. Add `control.GameManagerFactory` to engine.
2. Change `GamePanel` to take and use the factory.
3. Rename `Application` -> `GameFrame`, drop `main()`, add constructor params.
4. Create `launcher.Launcher` + `launcher.TigerSupplyGameManagerFactory`.
5. Add packaging config to `launcher/pom.xml`.
6. Build the reactor and launch from the launcher artifact to verify parity.

Rollback: revert the change; no data or persistent state is involved.
