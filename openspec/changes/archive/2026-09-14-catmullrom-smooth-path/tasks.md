## 1. Rename and reimplement the smooth-path algorithm

- [x] 1.1 Rename `engine/.../entity/logic/UpdateAlgorithmBspline.java` to `UpdateAlgorithmSmoothPath.java` (class + file) and verify `git status` shows the rename and the engine module still resolves the type.
- [x] 1.2 In `init`, read the control points from `ALGPRO_LIST_POINTS`, store them as an array/list, and set up the `(segment, u)` cursor with duplicated first/last endpoints; verify a fewer-than-two-points list leaves the entity stationary (guard like the old `npoints >= 2`).
- [x] 1.3 Implement inline uniform Catmull-Rom `eval(seg, u)` per design Decision 2 and the constant-speed `updateLogic` traversal per Decision 3 (reference speed from `|Speed|` on the first frame, integrated over `deltaSeconds`, stop at end); verify by reasoning through the pseudocode that progress over a fixed real time is independent of frame rate.

## 2. Update factory and cross-references

- [x] 2.1 Rename `UpdateAlgorithmFactoryWrapper.newBspline` to `newSmoothPath` (return type and instantiated class updated to `UpdateAlgorithmSmoothPath`); verify no remaining references to `newBspline` via search.
- [x] 2.2 Update the `{@link UpdateAlgorithmBspline}` Javadoc reference in `UpdateAlgorithmLinearPath` to `UpdateAlgorithmSmoothPath`; verify no source references to `UpdateAlgorithmBspline` remain via search.

## 3. Remove dead spline machinery

- [x] 3.1 Delete `engine/.../path/NatCubicSpline.java`, `Cubic.java`, and `ControlCurve.java`; verify a workspace search finds no remaining references to these types.

## 4. Update the level file

- [x] 4.1 In `game/src/main/resources/level/level-1.xml`, change the `pathAlfa` prototype `class` attribute to `it.spaghettisource.tigersupply.engine.entity.logic.UpdateAlgorithmSmoothPath` (leave the prototype name `pathAlfa` and its five referencing steps unchanged); verify the XML still declares `pathAlfa` exactly once.

## 5. Update documentation

- [x] 5.1 Update `documentation/subsystems/entity-movement-algorithms/catalogo-algoritmi-attuali.md`, `index.md`, and `algoritmi-proposti-e-composizione.md` to the new name and behavior (Catmull-Rom, uses `dt` = "Sì", stops at end; rewrite the "Differenza da Bspline" note); verify no "Bspline" occurrences remain in those files via search.
- [x] 5.2 Update the movement-strategies bullet in `.github/copilot-instructions.md` (replace `Bspline` with `SmoothPath`); verify no "Bspline" occurrences remain there.

## 6. Build and verify

- [x] 6.1 Run `mvn -q -pl engine,game -am compile` and verify the engine and game modules compile with no references to the removed types or old names.
