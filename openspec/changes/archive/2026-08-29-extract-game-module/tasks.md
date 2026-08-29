## 1. Baseline

- [x] 1.1 Create a feature branch and establish a green baseline: build with JDK 17 (`mvn clean package`) and launch `launcher/target/tigersupply.jar`; record that the window opens, a horde spawns, weapons fire, and the boss/level flow completes (reference behavior for the smoke test in 5.2).

## 2. Promote the algorithm wrapper into the framework (design Decision 2)

- [x] 2.1 Move `UpdateAlgorithmFactoryWrapper` from `engine.impl.utils` to `engine.entity.logic` (keep it in the engine module); verify it compiles with only framework imports and no `engine.impl` import.
- [x] 2.2 Update the six importing consumers to the new location (`image.finaleffect.StarEntity`; `impl.entity.ExplosionParticle`; `impl.entity.Player`; `impl.weapon.enemy.PlasmaCannon`; `impl.weapon.enemy.RocketLauncer`; `impl.weapon.enemy.StandardShot`) and add an explicit import in `impl.utils.EntityFactoryWrapper` (which referenced the wrapper same-package before); verify `mvn -pl engine clean compile` succeeds.
- [x] 2.3 Verify the engine framework is now free of the leak: grep the engine's non-`impl` packages for `it.spaghettisource.tigersupply.engine.impl` → zero matches (previously only `StarEntity` matched).

## 3. Move and repackage the game into the game module (design Decision 1)

- [x] 3.1 Enable sources in the `game` module by creating `game/src/main/java` and `game/src/main/resources`; verify the reactor still builds with `mvn clean compile`.
- [x] 3.2 Using the IDE "Move/Rename package" refactor, move every `engine.impl.*` type into the `game` module and rename the root `it.spaghettisource.tigersupply.engine.impl.*` → `it.spaghettisource.tigersupply.game.*` (dropping the `impl` segment); verify `mvn -pl game -am clean compile` resolves all updated imports.
- [x] 3.3 Update `launcher.TigerSupplyGameManagerFactory` to import `it.spaghettisource.tigersupply.game.control.GameManager` instead of the old `engine.impl.control.GameManager`; verify `mvn -pl launcher -am clean compile` succeeds.
- [x] 3.4 Move the game content resources (`level/`, `image/`, `audio/`, `font/`) from `engine/src/main/resources` to `game/src/main/resources`; verify `engine/src/main/resources` no longer contains any level definition, catalog, or media asset.
- [x] 3.5 Update the 13 fully-qualified enemy class names in the moved `level-1.xml` from `...engine.impl.entity.*` to `...game.entity.*`; verify by grepping the moved XML for `engine.impl` → zero matches.

## 4. Split the constants holder (design Decision 3)

- [x] 4.1 Create `game.utils.GameResources` holding the game constants — asset aliases (`FONT_*`, `BCKGROUND_*`, `EFFECT_*`, `ENEMY_*`, `PLAYER_*`, `ASTEROID_*`), `GAME_STATE_*`/`GAME_EVENT_*`, the FSM `STATE_*`/`EVENT_*`, and the `Z_*` render layers; verify it compiles.
- [x] 4.2 Repoint every game reference from `StaticResources.<gameKey>` to `GameResources.<gameKey>`; verify `mvn -pl game -am clean compile` succeeds.
- [x] 4.3 Trim `engine.utils.StaticResources` to the 17 framework keys (`ALGPRO_*`, `COLOR_*`, `FILTER_*`); verify `mvn -pl engine clean compile` succeeds with framework consumers unchanged.
- [x] 4.4 Verify the two holders have disjoint, correctly-owned keys: grep confirms no `GameResources` game key is referenced from the engine, and no `ALGPRO_*`/`COLOR_*`/`FILTER_*` key is referenced from the game.

## 5. Build, run, and verify against the spec

- [x] 5.1 Full reactor build: `mvn clean package` on JDK 17 succeeds for engine, game, and launcher, producing `launcher/target/tigersupply.jar`.
- [x] 5.2 Launch smoke test (spec: Extraction preserves gameplay): run the packaged jar and confirm the window opens with the same title and 1360x660 playfield, a horde spawns, weapons fire, and the boss/level flow completes as recorded in 1.1.
- [x] 5.3 Verify the boundary (spec: Game rules reside in the game module; Game occupies its own package namespace): grep the whole tree for `engine.impl` → zero matches; confirm no game type remains under `it.spaghettisource.tigersupply.engine.*` and every game type is under `it.spaghettisource.tigersupply.game.*`.
- [x] 5.4 Verify engine independence (spec: Engine source is free of references into the game): `mvn -pl engine clean compile` builds the engine alone, and grepping the engine source for `tigersupply.game` returns zero matches.
- [x] 5.5 Verify resource ownership (spec: Game content resources reside in the game module): confirm the built engine artifact contains no level/catalog/media, the built game artifact contains them, and the running app loads them from the game module.
