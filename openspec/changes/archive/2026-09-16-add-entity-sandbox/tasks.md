## 1. New sandbox module skeleton

- [x] 1.1 Add a new Maven module `sandbox` to the root `pom.xml` reactor (after `launcher`) and create `sandbox/pom.xml` depending on `game`, mirroring the launcher POM (Java 17 release, no new runtime deps). Verify `mvn -q -pl sandbox -am -DskipTests compile` resolves the module.
- [x] 1.2 Create the package root `it.spaghettisource.tigersupply.sandbox` with `control`, `scene`, `catalog`, and `entity` subpackages (a placeholder or the first real class), and verify the module compiles empty/near-empty.

## 2. Bootstrap and navigation

- [x] 2.1 Add `sandbox.control.SandboxSceneHost extends engine.control.AbstractSceneHost` that runs the same repository bootstrap as `TigerSupplySceneHost` (`ImageRepositoryManager/FontRepositoryManager/AudioManager/FinalEffectManager/SpriteFactory/EntityFactory` init) and intercepts the global pause/quit keys, exposing `getGameContext()`/`getGamePanel()`/`setActiveScene(...)`. Verify it compiles against `game`/`engine` public APIs.
- [x] 2.2 Add `showMenu()` and `showTest(SandboxCase)` on `SandboxSceneHost` that build the respective scene and call `setActiveScene(...)`; set the initial scene to the menu in the constructor. Verify navigation methods compile.
- [x] 2.3 Add `sandbox.SandboxSceneHostFactory implements engine.control.SceneHostFactory` returning a `SandboxSceneHost`, and `sandbox.SandboxLauncher` with a `main` that builds a `GameContext` and a `GameFrame(title, width, height, context, factory)`. Verify `mvn -pl sandbox exec:java -Dexec.mainClass=...SandboxLauncher` opens a window.

## 3. Movable target

- [x] 3.1 Add `sandbox.entity.SandboxTarget` as a white-square `Entity` (extending the game entity base) with a settable position and a render that draws the square. Verify it can be constructed and rendered, and can be passed where an `Entity` target is expected.
- [x] 3.2 Wire arrow-key movement for the target inside the test scene (up/down/left/right adjust its position each press/hold). Verify the square moves on screen in all four directions.

## 4. Catalog

- [x] 4.1 Add `sandbox.catalog.SandboxCase` (label, family enum `PROJECTILE|ENEMY|EFFECT`, and a `build(GameContext, managers, target)` step) and a `SandboxManagers` holder exposing the effect/shot/enemy groups. Verify both compile.
- [x] 4.2 Add `sandbox.catalog.SandboxCatalog` that returns the ordered list of cases, and populate PROJECTILE entries for every projectile/`EntityFactoryWrapper` shot method (`EnergyBall` wired to the effect group, `EnemyRocket`, `LightningBolt`, `PlayerRocket`, `PlayerBomb`, `enemyShotDefault`, `plasmaCannon`, `seekerRocket` passing the target, `playerGun`, `playerPaser`, `playerGunSynusoidal`). Verify each case builds a positioned, wired entity.
- [x] 4.3 Populate ENEMY entries for every concrete `Enemy` (`EnemyStandard`, `EnemyShoterRocket`, `EnemyEnergyShooter`, `EnemyShield`, `Asteroid`, `EnemyBoss`, `EnemyBackGround`), wiring shot group, effect group, and the movable target so weapon-firing enemies fire. Verify a firing enemy's shots appear in the shot group.
- [x] 4.4 Populate EFFECT entries (`ExplosionParticle` fire/energetic, `EnergyTrailParticle`, `Smoke`, `EnergeticShield`), inserting group-managed effects into the effect group and representing `FinalEffectManager`-owned effects via that manager. Verify each effect animates.

## 5. Menu scene

- [x] 5.1 Add `sandbox.scene.SandboxMenuScene` (modelled on `PresentationScene`) that renders the catalog grouped by family with a selection cursor. Verify all cases are listed under their family heading.
- [x] 5.2 Handle input: up/down move the cursor, Enter calls `host.showTest(selectedCase)`, Escape requests stop. Verify selecting an entry switches to the test scene.

## 6. Test scene

- [x] 6.1 Add `sandbox.scene.SandboxTestScene` (reduced `LevelScene`) that owns the effect/shot/enemy groups and the target, builds the selected case into the correct group via its family, and each frame updates the target, the case, and the groups; respawn the entity when it leaves the playfield. Verify the launched entity animates and is renewed off-screen.
- [x] 6.2 Render a debug overlay (position, speed, bounding size, a reference grid/axes) plus the target and all group entities, ordered for drawing. Verify the overlay shows live values.
- [x] 6.3 Handle input: arrows move the target, `R` respawns, `SPACE` toggles pause, `,`/`.` single-step while paused, Escape calls `host.showMenu()`. Verify each control behaves and that Escape returns to the menu without restarting the app.

## 7. Verification

- [x] 7.1 Build the whole reactor (`mvn -q -DskipTests clean package` with JDK 17) and confirm the game and launcher artifacts are unchanged/green and the sandbox module builds.
- [x] 7.2 Grep the `game` and `launcher` package roots to confirm no class references the sandbox (`sandbox` isolation requirement) and that engine/game/launcher sources are untouched by this change.
- [ ] 7.3 Manually launch the sandbox and exercise one entity from each family (projectile, enemy, effect) plus a target-seeking entity, confirming selection, isolation, debug overlay, target movement + homing reaction, respawn, pause/step, and return-to-menu.
