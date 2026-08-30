# API Documentation

## REST APIs

None. TigerSupply is an offline Java Swing desktop application; it exposes no HTTP, REST,
RPC, or other network-accessible endpoints of any kind.

## Internal APIs

The game communicates purely through in-process Java interfaces. The most important contracts
(framework contracts in `it.spaghettisource.tigersupply.engine.*`; concrete-game contracts in
`it.spaghettisource.tigersupply.game.*`) are:

### `control.Scene`
- **Methods**:
  - `update(float deltaTimeSeconds) throws Exception` — advance simulation by one frame.
  - `render() throws Exception` — draw the frame to the off-screen buffer.
  - `paintScreen()` — blit the off-screen buffer to the visible panel.
  - `mousePressed(int x, int y)`, `mouseMoved(MouseEvent event)`, `keyPressed(KeyEvent event)`,
    `keyReleased(KeyEvent event)` — input handling.
- **Parameters**: `deltaTimeSeconds` is the fixed frame period in seconds (derived from
  `GameContext.getPeriodSeconds()`, ~0.0167s at 60 FPS).
- **Return Types**: `void` for all methods; failures are signalled via checked `Exception`.
- **Implementations**: `control.AbstractSceneJPanel` (template method base) →
  `game.scene.PresentationScene` / `HangarScene` / `LevelScene` / `GameOverScene`.

### `control.SceneManager`
- **Methods**: `getActiveScene() throws Exception` (returns the currently active `Scene`), plus
  the same input-delegation methods as `Scene`.
- **Implementations**: `control.AbstractSceneManagerJPanel` → `game.control.TigerSupplySceneManager`.

### `entity.Entity`
- **Methods**: `updateEntity(float deltaSeconds)`, `renderEntity(Graphics2D dbg)`,
  `collidedWith(Entity other) boolean`, `collided(Entity other)`, `canBeRemoved() boolean`,
  `getEntityRectangle() Rectangle[]`, `isOutOfScreen(int windowWidth, int windowHeight) boolean`,
  plus position/speed/size/sprite accessors.
- **Return Semantics**: `collidedWith` is a pure geometric test (AABB intersection);
  `collided` is the *reaction* (mutates state — e.g. decrements life, spawns particles,
  triggers removal) and is expected to be called on **both** entities in a collided pair.
  `canBeRemoved()` is polled every frame by the owning `EntityGroup` to prune dead entities.
- **Implementations**: `entity.AbstractEntity` → `game.entity.BaseEntity` →
  `Player` / `Enemy` (→ `EnemyStandard`, `EnemyBoss`, `EnemyShield`, `EnemyRocket`,
  `EnemyShoterRocket`, `EnemyBackGround`, `Asteroid`) / effect entities
  (`ExplosionParticle`, `Smoke`, `PlayerEngine`, …).
- **Composite implementation**: `entity.EntityGroup<T extends Entity>` also
  implements `Entity`, fanning every call out to its managed list (unsupported accessor
  methods throw `UnsupportedOperationException` by design, since a group has no single
  position/size of its own).

### `sprite.Sprite`
- **Methods**: `getImageWidth()`, `getImageHeight()`, `setAlpha(double alpha)`,
  `updateSprite(float deltaSeconds)`, `renderSprite(Graphics2D dbg, Position position, Size size)`.
- **Implementations**: `sprite.AbstractSprite` → `ImageSingleSprite` / `ImagePlayerSprite` /
  `ImagePlayerCenterControllerSprite`.

### `entity.logic.UpdateAlgorithm`
- **Methods**: `updateLogic(Position position, Speed speed, float deltaSeconds)`,
  `init(DynaProperties properties)`.
- **Parameters**: `properties` is a string-keyed dynamic bag parsed from the level XML's
  `<algorithmProperties>` element (simple string properties and/or a named list of
  `PointDefinition`s).
- **Implementations**: `UpdateAlgorithmDefault`, `UpdateAlgorithmSinusoidal`,
  `UpdateAlgoritmGoToPoint`, `UpdateAlgoritmGoToPointIncreasingSpeed`,
  `UpdateAlgoritmFollowSprite`, `UpdateAlgoritmCopyPosition`, `UpdateAlgorithmBspline`.

### `game.weapon.Weapon<T extends Entity>`
- **Methods**: `setOwner(T owner)`, `targetInRange(Entity target) boolean`,
  `fire(Entity target) throws Exception`, `reload()`, `isUnloaded()/isReloading()/isReady() boolean`,
  `updateWeapon(float deltaSeconds) throws Exception`.
- **Return Semantics**: the weapon is a 4-state machine (`UNLOADED → RELOADING → READY →
  FIREING → UNLOADED`); callers must check `isReady()`/`isUnloaded()` before calling
  `fire`/`reload` respectively (see `Enemy.scanTargetInRange` and `Player.tryToShot` for the
  canonical call pattern).
- **Implementations**: `game.weapon.AbstractWeapon` → player weapons (`Paser`, `DoubleGun`,
  `SynusoidalGun`, `RocketLauncer`, `Bomb`) and enemy weapons (`StandardShot`,
  `RocketLauncer`, `PlasmaCannon`, `LightinBoltLaser`).

### `background.BackGround`
- **Methods**: `updateBackground(float deltaSeconds)`, `renderBackground(Graphics2D dbg)`.
- **Implementations**: `BackGroundFitImage`/`StaticBackGroundFitImage`,
  `BackGroundTexture`/`StaticBackGroundTexture`, `ParallaxBackGround` (composite).

### `statemachine.StateMachine` / `State` / `TransactionManager`
- **Methods**: `StateMachine.event() throws StateMachineException`;
  `State.processState(TransactionManager) State`;
  `TransactionManager.findNextState(State state, Event event) State`.
- **Return Semantics**: `processState` both executes the current state's side effect (e.g.
  "spawn next horde") **and** returns the next `State` to transition to, by asking the
  `TransactionManager` to resolve `(state name, generated event name)`.
- **Implementation used in-game**: `game.scene.statemachine.EnemyTxManager` (the horde-pacing
  state graph) driving `StateWaitTime` / `StateWaitKill` / `StateGenerateHorde` /
  `StateKillBoss`.

### `game.builder.EnemyDataBuilder`
- **Methods**: `parse() throws Exception`, `buildHordes() List<Horde>`,
  `buildEnemyPrototypes() List<EnemyPrototype>`, `buildAlgorithmPrototypes() List<AlgorithmPrototype>`.
- **Implementation**: `EnemyDataBuilderSaxXml` (SAX parser reading a classpath resource path,
  e.g. `level/level-1.xml`).

### `game.weapon.HangarWeapon`
- **Methods**: `getSprite() Sprite throws Exception`, `getDescription() String`.
- **Purpose**: Lets the Hangar UI preview a weapon's sprite/description before it is
  equipped; implemented by the concrete player weapon classes.

## Data Models

### `entity.Position`
- **Fields**: `posX: float`, `posY: float` (centre point of the sprite), `posZ: int` (draw
  depth — lower is closer to the screen), `angle: float` (degrees, clockwise-positive,
  normalized to `[0, 360)`).
- **Relationships**: Owned 1:1 by every `AbstractEntity`.
- **Validation**: `setAngle`/`increaseAngle` normalize into `[0, 360)` via `correctAngle`; no
  other range validation is performed.

### `entity.Size`
- **Fields**: `width: int`, `heigh: int` *(sic — "height" is misspelled throughout the
  codebase)*, `scale: float` (default `1.0` = 100%).
- **Relationships**: Owned 1:1 by every `AbstractEntity`; effective width/height =
  `(width|heigh) * scale`.

### `entity.Speed`
- **Fields**: `speedX: float`, `speedY: float` (pixels/second).
- **Relationships**: Owned 1:1 by every `AbstractEntity`; consumed by `UpdateAlgorithm`
  implementations.

### `sprite.SpriteColor`
- **Fields**: alpha/red/green/blue channel values used as part of the sprite render cache key
  and consumed by colour-affecting filters (`Brighten`, `Transparent`).

### `utils.DynaProperties`
- **Fields**: a single `HashMap<String, Object>` accessed through typed getters/setters
  (`getString`, `getInt`, `getFloat`, `getList`, `getMap`, …).
- **Purpose**: Generic "dynabean" used to pass algorithm configuration parsed from XML into
  `UpdateAlgorithm.init(...)` without a dedicated Java type per algorithm.
- **Validation**: None — callers are expected to know which keys/types a given algorithm
  requires (documented informally via `StaticResources.ALGPRO_*` constants).

### Level Script Schema (`resources/level/level-1.xml`, parsed by `EnemyDataBuilderSaxXml`)
A `<level>` document has:
```
<level>
  <hordes>
    <horde>
      <generateEvent name="waitTime|waitKill" time="<seconds, optional>" />
      <enemy enemyPrototype="<name>" algoritmPrototype="<name>"
             posX="<int>" posY="<int>" posZ="<int>" />
      <!-- one or more <enemy> per horde -->
    </horde>
    <!-- one or more <horde>, processed in document order -->
  </hordes>
</level>
```
- **`Horde`** — `GenerateEvent` (`name` = `waitTime` gates on an elapsed-seconds timer,
  `waitKill` gates on all current enemies being dead) + list of `EnemyDefinition`.
- **`EnemyDefinition`** — references an `EnemyPrototype` and `AlgorithmPrototype` by name and
  gives the spawn `Position` (`posX`/`posY`/`posZ`).
- **`EnemyPrototype`** *(declared separately, referenced by name)* — `name`, `type` (currently
  only `imageSingleSprite` is handled by `EnemyDataManager`), `class` (fully-qualified
  `game.entity.Enemy` subclass instantiated via reflection), nested `<speed>`, `<image
  alias="…">`, `<scale value="…">`.
- **`AlgorithmPrototype`** *(declared separately, referenced by name)* — `name`, `class`
  (fully-qualified `UpdateAlgorithm` implementation), nested `<algorithmProperties>` containing
  either simple `<property name="…" value="…">` entries or a named `<listPoints name="…">` of
  `<point posX="…" posY="…">` waypoints.
- **Validation**: None at the XML level (no XSD/DTD) — malformed or unknown prototype names
  fail at runtime with a `NullPointerException`/`ClassNotFoundException` surfaced as a wrapped
  `Exception` from `EnemyDataManager`/`ClassFactory`.
