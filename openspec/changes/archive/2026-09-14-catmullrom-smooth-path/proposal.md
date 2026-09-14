## Why

`UpdateAlgorithmBspline` advances the entity by one precomputed spline point per frame, ignoring
`Speed` and `deltaSeconds`, so the perceived travel speed scales with the frame rate (twice as fast
at 60 FPS as at 30 FPS). Its name is also a double misnomer: it is not a B-spline, and the underlying
math is being replaced. We want a single smooth-path algorithm whose speed is frame-rate independent,
consistent with `UpdateAlgorithmLinearPath`.

## What Changes

- **BREAKING** Rename `UpdateAlgorithmBspline` to `UpdateAlgorithmSmoothPath` (there remains exactly
  one smooth-path algorithm; the old type is not kept).
- Replace the natural-cubic-spline sampling with an inline, on-the-fly **Catmull-Rom** curve through
  the same control points (endpoints duplicated so the curve passes through the first and last point).
- Drive the motion by `Speed` integrated over `deltaSeconds`: the constant travel speed is derived
  from the magnitude of the reference `Speed` on the first frame, **exactly like**
  `UpdateAlgorithmLinearPath`; the algorithm now respects the frame rate.
- Stop the entity once the end of the path is reached, **exactly like** `UpdateAlgorithmLinearPath`.
- **BREAKING** Rename the factory method `UpdateAlgorithmFactoryWrapper.newBspline` to `newSmoothPath`.
- Remove the now-dead spline machinery `NatCubicSpline`, `Cubic`, `ControlCurve` (package
  `engine.path`), used only by the old algorithm.
- Update the level file and documentation to the new class name and behavior.

## Capabilities

### New Capabilities
- `engine-movement-algorithms`: the engine's reusable, data-driven entity movement strategies; this
  change introduces the requirement for the smooth-path (Catmull-Rom) strategy that moves at a
  constant, frame-rate-independent speed and stops at the end of the path.

### Modified Capabilities
<!-- No existing movement-algorithm capability spec exists yet. -->

## Impact

- **Engine code**: `UpdateAlgorithmBspline` → `UpdateAlgorithmSmoothPath` (renamed + reimplemented);
  `UpdateAlgorithmFactoryWrapper.newBspline` → `newSmoothPath`; Javadoc reference in
  `UpdateAlgorithmLinearPath`; removal of `NatCubicSpline`, `Cubic`, `ControlCurve`.
- **Game resources**: `game/src/main/resources/level/level-1.xml` — the `pathAlfa` prototype's
  `class` attribute (the prototype name `pathAlfa` and the five steps referencing it are unchanged).
- **Documentation**: `documentation/subsystems/entity-movement-algorithms/*` (catalogo, index,
  algoritmi-proposti) and the movement bullet in `.github/copilot-instructions.md`.
- **Config surface**: the `listpoints` property key is unchanged, so no other level XML edits are
  required.
