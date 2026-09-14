## Context

See proposal.md - Why. The current `UpdateAlgorithmBspline` precomputes every point of a
`NatCubicSpline` in `init` and, in `updateLogic`, snaps to the next precomputed point each frame,
ignoring `Speed` and `deltaSeconds`. The sibling `UpdateAlgorithmLinearPath` already shows the target
pattern: it derives a constant travel speed from the magnitude of the reference `Speed` on the first
frame, integrates it over `deltaSeconds`, and stops when the waypoints are exhausted.

Constraints: engine module, JDK-only, no new dependencies; movement algorithms are instantiated by
fully-qualified class name from `level-*.xml` and configured through `DynaProperties`; the control
points arrive under the existing `ALGPRO_LIST_POINTS` (`listpoints`) key as a `List<Point>`.

## Goals / Non-Goals

**Goals:**
- One smooth-path algorithm, `UpdateAlgorithmSmoothPath`, replacing `UpdateAlgorithmBspline`.
- Smooth curve through the same control points, computed inline (Catmull-Rom), no `engine.path`
  spline classes.
- Constant, frame-rate-independent speed derived from `|Speed|`, exactly like `UpdateAlgorithmLinearPath`.
- Stop at the end of the path, exactly like `UpdateAlgorithmLinearPath`.

**Non-Goals:**
- Re-tuning `level-1.xml` waypoints or enemy speeds for aesthetics (only the `class` attribute
  changes; visual tuning, if desired, is a separate change).
- Arc-length reparametrization for mathematically exact constant speed (first-order constant speed is
  sufficient for a shmup).
- Introducing new configuration keys or a composite/sequence algorithm.

## Decisions

### Decision 1: Rename to `UpdateAlgorithmSmoothPath` (replace, do not add)

The old name is a double misnomer (not a B-spline, and the cubic-spline math is being dropped).
`SmoothPath` mirrors `LinearPath` (both "scripted path", one angular and one smooth) and is agnostic
to the internal curve math. The factory method `UpdateAlgorithmFactoryWrapper.newBspline` becomes
`newSmoothPath`. Exactly one such algorithm remains. Alternatives considered: `CurvedPath`,
`CatmullRomPath` (couples the name to the math) - rejected.

### Decision 2: Uniform Catmull-Rom, computed on the fly

Catmull-Rom is a local interpolating spline that passes through its control points using a direct
polynomial - no tridiagonal solve like the natural cubic spline, so it is simpler and cheaper. For a
segment between `P1` and `P2` with neighbours `P0` and `P3`, and `u` in `[0,1]`:

```
P(u) = 0.5 * ( (2*P1)
             + (-P0 + P2) * u
             + (2*P0 - 5*P1 + 4*P2 - P3) * u^2
             + (-P0 + 3*P1 - 3*P2 + P3) * u^3 )
```

Endpoints are handled by duplicating the first and last control points (phantom `P[-1] = P[0]`,
`P[n] = P[n-1]`), so the curve passes through the first and last waypoint. Uniform (rather than
centripetal) parametrization is chosen for simplicity and performance; see Risks for the trade-off.

### Decision 3: Constant-speed traversal integrated over `deltaSeconds`

Speed source is identical to `UpdateAlgorithmLinearPath`: on the first frame, derive
`referenceSpeed = sqrt(speedX^2 + speedY^2)` from the reference `Speed` (for the `roker` enemy that
uses `pathAlfa`, that is `|(-150, 0)| = 150` px/s). Each frame consume a distance budget of
`referenceSpeed * deltaSeconds` by walking the curve on the fly, keeping only a small `(segment, u)`
cursor - no precomputed point list:

```
state: seg = 0, u = 0, finished = false, computeReferenceSpeed = true
EPS = small parametric probe (e.g. 1e-3)

updateLogic(position, speed, dt):
  if computeReferenceSpeed: referenceSpeed = |speed|; computeReferenceSpeed = false
  if finished: return                                  // stop at end, like LinearPath

  budget = referenceSpeed * dt
  while budget > 0 and not finished:
     here     = eval(seg, u)
     tangent  = distance(here, eval(seg, min(u + EPS, 1))) / EPS   // approx |dP/du|
     du       = budget / max(tangent, tiny)
     if u + du < 1:
        u += du
        position <- eval(seg, u)
        budget = 0
     else:
        endOfSeg = eval(seg, 1)
        budget  -= distance(here, endOfSeg)
        seg += 1; u = 0
        if seg > lastSegmentIndex:
           position <- endOfSeg                         // last waypoint
           finished = true
        else:
           position <- eval(seg, 0)
```

This is O(1) amortized per frame (a handful of `eval`s), needs no precomputation, and gives constant
world-space speed to first order. Alternative considered: precompute a dense Catmull-Rom polyline in
`init` and walk it by arc length - correct but stores a large list and reintroduces the
"precompute everything" shape we are moving away from; rejected in favor of on-the-fly evaluation.

### Decision 4: Remove dead spline machinery

`NatCubicSpline`, `Cubic`, and `ControlCurve` (package `engine.path`) are referenced only by the old
algorithm. Once it is reimplemented with inline Catmull-Rom, they are dead code and are removed.

## Risks / Trade-offs

- Uniform Catmull-Rom can overshoot or form cusps when control points are unevenly spaced or sharply
  angled → if the `pathAlfa` curve looks wrong, switch the parametrization to centripetal
  (knot spacing `t_{i+1} = t_i + |P_{i+1} - P_i|^0.5`); this is a localized change to `eval` only.
- The per-frame local-speed estimate is first-order (finite-difference tangent), so speed has a tiny
  ripple where curvature is high → negligible for a shmup enemy; keep `EPS` small.
- The new speed is frame-rate independent and driven by `|Speed|`, so the on-screen pace of `pathAlfa`
  enemies will differ from the old point-per-frame behavior → intended; no waypoint re-tuning in this
  change (see Non-Goals).
- Fewer than two control points cannot form a curve → guard and leave the entity stationary, matching
  the old `generatePoints` guard (`npoints >= 2`).

## Migration Plan

1. Rename `UpdateAlgorithmBspline` → `UpdateAlgorithmSmoothPath` and reimplement per Decisions 2-3.
2. Rename factory method `newBspline` → `newSmoothPath`; update the `UpdateAlgorithmLinearPath` Javadoc
   cross-reference.
3. Remove `NatCubicSpline`, `Cubic`, `ControlCurve`.
4. Update `level-1.xml`: `pathAlfa` prototype `class` attribute → new FQN (prototype name and the five
   referencing steps unchanged).
5. Update documentation (catalogo, index, algoritmi-proposti) and the movement bullet in
   `.github/copilot-instructions.md`.
6. Build with Maven to confirm the engine and game modules compile.

Rollback: revert the change; no persisted state or external system is involved.
