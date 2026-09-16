## Why

Testing the behaviour and look of a single entity (e.g. `EnergyBall`) today requires launching
the full game and playing through Presentation -> Hangar -> Level until a level step happens to
spawn it. This is a slow, indirect iteration loop, and it is especially painful for entities whose
value is almost entirely visual (procedural gradients, particle trails) or motion-driven (homing,
following, aiming). There is no way to exercise one entity in isolation and watch it react.

## What Changes

- Add a standalone, runnable **entity sandbox** — a separate deliverable, launched by its own
  `main`, that boots the same engine window and asset repositories as the game but shows a debug
  environment instead of the game flow. It is NOT reachable from the shipped game (no hidden key,
  no menu entry).
- The sandbox uses a **two-scene** model, mirroring the existing scene pattern:
  - a **menu scene** that lists every testable entity (grouped by family) and lets the user pick
    one with the arrow keys and launch it with Enter;
  - a **test scene** that runs the chosen entity in isolation, owns the entity groups it needs,
    draws a debug overlay, and returns to the menu on Escape.
- Provide a **movable dummy target** (a plain white square that is itself an `Entity`) driven by
  the arrow keys in the test scene, so entities that aim/home/follow can be passed this target and
  observed reacting in real time.
- Provide a **catalog** enumerating every concrete testable entity across the three families —
  projectiles, enemies, and effects — where each entry declares its family (which group it belongs
  to) and how to build and wire it, reusing the existing `EntityFactoryWrapper` factory methods.
- House everything in a **new `sandbox` Maven module** with its own package root
  (`it.spaghettisource.tigersupply.sandbox.*`), depending on `game`, so no sandbox class lands in
  `game.*` or `launcher.*` and the game/launcher remain untouched.

## Capabilities

### New Capabilities
- `entity-sandbox`: a standalone debug tool that lets a developer select any concrete game entity
  from a catalog and exercise it in isolation, with a movable target for testing motion/aim
  behaviours, without going through the game flow.

### Modified Capabilities
<!-- None. The game, launcher, and engine capabilities are unchanged; the sandbox is a new,
     parallel deliverable that only consumes existing public game/engine APIs. -->

## Impact

- **New module**: `sandbox` (Maven reactor becomes `engine -> game -> { launcher, sandbox }`);
  new root package `it.spaghettisource.tigersupply.sandbox.*`.
- **New code (sandbox module only)**: a `main` entry point, a `SceneHostFactory` implementation, a
  scene host that boots the shared repositories and navigates between the two scenes, the menu and
  test scenes, the entity catalog, and the movable target.
- **Reused, unchanged**: the engine `GameFrame`/`SceneHostFactory` seam, the singleton asset
  repositories and their `init(...)` methods, and the public `EntityFactoryWrapper` factory
  methods and entity setters.
- **Untouched**: `engine`, `game`, and `launcher` modules and their packages; the shipped game has
  no reference to the sandbox and no new runtime dependency is introduced (pure JDK, as elsewhere).
- **Build/run**: the new module is launched on its own (e.g. `mvn -pl sandbox exec:java`),
  separate from the game jar.
