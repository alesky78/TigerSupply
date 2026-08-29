## Why

All 168 source files live in the `engine` module while `game` and `launcher` are empty
placeholders, and the reusable framework contains a backwards dependency: the engine's window
host (`GamePanel`) directly constructs the concrete TigerSupply `impl.control.GameManager`. This
framework-to-game edge makes a clean `engine`/`game`/`launcher` separation impossible and leaves
the intended `launcher -> game -> engine` module chain unrealized. Drawing the launch boundary now
breaks the cycle and unblocks the later extraction of game content into the `game` module.

## What Changes

- Introduce a `GameManagerFactory` abstraction in the engine so the window host builds the game
  manager through an interface instead of `new`-ing the concrete `impl.control.GameManager`.
- Extract the reusable window shell: rename `engine.windows.Application` to
  `engine.windows.GameFrame`, remove its `main()`, and parameterize title, dimensions,
  `ApplicationContext`, and `GameManagerFactory`. `GameFrame`, `GamePanel`, and the AWT input
  listeners become fully game-agnostic (no `impl.*` references).
- Stand up the `launcher` module as the composition root: a new `Launcher` class owning `main()`,
  a concrete `TigerSupplyGameManagerFactory`, and the game's title + `1360x660` resolution config.
- Make `launcher` the runnable, packaged artifact by declaring the `mainClass` and a shade/exec
  build configuration in `launcher/pom.xml`.
- **BREAKING**: the application entry point moves from
  `it.spaghettisource.tigersupply.engine.windows.Application#main` to
  `it.spaghettisource.tigersupply.launcher.Launcher#main`. Run configurations must target the
  launcher.

Non-goals (deferred to a later change):

- Moving the `impl.*` game tree (scenes, entities, weapons, `impl.control.GameManager`) into the
  `game` module. It stays in `engine` for now; the launcher's factory references it transitionally.
- Relocating game-content resources (level XML, image/audio/font catalogs) out of `engine`.
- Changing the fixed `1360x660` resolution or any gameplay behavior.

## Capabilities

### New Capabilities

- `launcher`: The composition root and runnable entry point — owns `main()`, selects the concrete
  game via a factory, owns application config (title/resolution), is the packaged runnable
  artifact, and anchors the `launcher -> game -> engine` dependency direction.
- `engine-game-shell`: The engine's reusable, game-agnostic hosting layer — the window shell,
  panel host, and AWT input listeners, plus the `GameManagerFactory` seam through which the
  concrete game manager is supplied.

### Modified Capabilities

None — `openspec/specs/` currently has no authored capabilities, so both entries above are new.

## Impact

- **Engine**: `windows.Application` renamed to `windows.GameFrame` (loses `main()`, gains
  constructor params); `windows.GamePanel` constructor takes a `GameManagerFactory`; new
  `control.GameManagerFactory` interface. `impl.control.GameManager` is unchanged.
- **Launcher**: new module content — `Launcher` (`main()`), `TigerSupplyGameManagerFactory`, and
  packaging config (`mainClass` + shade/exec) in `launcher/pom.xml`.
- **Entry point / tooling**: the runnable main class moves modules (BREAKING); IDE/Maven run
  configurations must point at `launcher`.
- **Docs**: references to `windows.Application#main` in `documentation/` and
  `.github/copilot-instructions.md` become stale (follow-up doc update, not required for this
  change).
- **No runtime dependency changes**: still pure JDK; the shade/exec plugins are build-time only.
