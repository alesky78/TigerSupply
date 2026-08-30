## Context

See `proposal.md - Why` for motivation. The affected hierarchy is small and fully enumerated:

- Engine `control`: `SceneManager` (interface), `AbstractSceneManagerJPanel` (abstract base),
  `SceneManagerFactory` (interface).
- Game `control`: `TigerSupplySceneManager` (the only concrete `SceneManager`).
- Launcher: `TigerSupplySceneManagerFactory` (the only concrete `SceneManagerFactory`).

Consumers that reference these types by name: `engine.control.GameLoop`, `engine.control.Scene`
(Javadoc only), `engine.windows.GamePanel`, `engine.windows.GameFrame`, the three
`engine.windows.GamePanel*Listener`s, `game.control.SceneFlowController`, and
`launcher.Launcher`. The level XML and `*-catalog.txt` files reference none of these types
(reflection targets are `game.entity.*` and algorithm classes only), so the data layer is inert.

Constraints (per project conventions): no logging framework, keep `@author` tags, broad
`throws Exception`, no new dependencies, and do not touch the intentionally-preserved
public-identifier typos.

## Goals / Non-Goals

**Goals:**
- A behaviour-preserving rename that makes the type self-describing: the hierarchy *hosts* the
  active scene rather than *managing* scenes.
- Keep engine ↔ game ↔ launcher module boundaries and the single factory seam intact.
- Leave the codebase compiling and the uber-jar runnable after the change.

**Non-Goals:**
- No change to `SceneFlowController` (the real scene-flow manager keeps its name).
- No signature, behaviour, resource-format, or gameplay change.
- No collateral cleanup of unrelated `*Manager` types or preserved typos.

## Decisions

- **`SceneHost` over `SceneManager`.** The class holds one `activeScene`, exposes it to the loop,
  and routes input to it — it does not manage a set/stack of scenes or transitions (that is
  `SceneFlowController`). "Host" names the true role (owns the active scene + panel + context and
  delegates to it), and removes the naming collision with `SceneFlowController`.
  - Alternatives considered: keep `SceneManager` (rejected — collides with `SceneFlowController`
    and overstates the role); `SceneDirector` (rejected — "director" implies choosing/sequencing
    scenes, which this type does not do); `SceneController` (rejected — ambiguous with per-scene
    controllers and the `control` package name).
- **Drop the `JPanel` suffix: `AbstractSceneManagerJPanel` -> `AbstractSceneHost`.** The class
  `implements SceneHost` and *holds* a `JPanel` field; it does not extend `JPanel` (the real panel
  is `engine.windows.GamePanel`). The suffix is factually wrong, so it is removed rather than
  transliterated to `AbstractSceneHostJPanel`.
- **Concrete names follow the interface:** `TigerSupplySceneManager` -> `TigerSupplySceneHost`,
  `TigerSupplySceneManagerFactory` -> `TigerSupplySceneHostFactory`. This preserves the existing
  `TigerSupply`-prefixed composition-root naming convention.
- **Realign carried-over identifiers.** Fields/params/locals named `sceneManager`, `manager`, and
  `sceneManagerFactory` become `sceneHost` / `sceneHostFactory` so no old vocabulary lingers, but
  method signatures are otherwise unchanged.
- **Rename files with the types.** Each renamed public type moves to a matching `.java` filename;
  Java requires the file name to match the public type.

## Risks / Trade-offs

- Source-level breaking change for any out-of-tree code referencing these types → Mitigation: this
  is a single-developer, self-contained repo with no published API; all references are in-tree and
  updated in the same change.
- Missed reference leaves a compile error → Mitigation: the impact set is fully enumerated by
  `SceneManager`-token search; verify with a full reactor build (`mvn -pl launcher -am clean
  package`) and a final `grep` that no `SceneManager`/`AbstractSceneManagerJPanel`/
  `TigerSupplySceneManager*` identifier from this hierarchy remains in `src` or docs.
- Accidentally renaming an unrelated `*Manager` (e.g. `EnemyManager`) → Mitigation: restrict
  edits to the enumerated files and the exact `SceneManager*` tokens; do not touch bare `manager`
  words in unrelated types.

## Migration Plan

1. Rename the three engine `control` types (files + identifiers) and update their Javadoc.
2. Update engine consumers (`GameLoop`, `Scene`, `GamePanel`, `GameFrame`, the three listeners).
3. Rename `TigerSupplySceneManager` -> `TigerSupplySceneHost` and update `SceneFlowController`.
4. Rename `TigerSupplySceneManagerFactory` -> `TigerSupplySceneHostFactory` and update `Launcher`.
5. Update docs (`documentation/architecture/system-overview/*.md`, `.github/copilot-instructions.md`).
6. Build the full reactor and grep-verify no stale identifiers remain.

Rollback: revert the change's commit — no data, schema, or resource migration is involved.
