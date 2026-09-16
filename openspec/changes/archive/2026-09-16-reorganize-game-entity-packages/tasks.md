## 1. Create sub-packages and move classes

- [x] 1.1 Create `game.entity.player` and move `Player`, `PlayerEngine` into it, updating each `package` declaration; verify the files reside under `game/src/main/java/it/spaghettisource/tigersupply/game/entity/player/`
- [x] 1.2 Create `game.entity.enemy` and move `Enemy`, `EnemyStandard`, `EnemyShield`, `EnemyShoterRocket`, `EnemyBoss`, `EnemyBackGround`, `Asteroid`, `EnergeticShield` into it, updating each `package` declaration; verify the files reside under `.../game/entity/enemy/`
- [x] 1.3 Create `game.entity.projectile` and move `PlayerRocket`, `PlayerBomb`, `EnemyRocket`, `LightningBolt` into it, updating each `package` declaration; verify the files reside under `.../game/entity/projectile/`
- [x] 1.4 Create `game.entity.effect` and move `Effect`, `Smoke`, `ExplosionParticle` into it, updating each `package` declaration; verify the files reside under `.../game/entity/effect/`
- [x] 1.5 Confirm `BaseEntity` remains in the root `game.entity` package unchanged

## 2. Update Java references

- [x] 2.1 Update imports and `Xxx.class` references in `game.utils.EntityFactoryWrapper` for all moved types; verify the file compiles
- [x] 2.2 Update imports in the enemy weapon classes (`game.weapon.enemy.*`) that reference `EnemyRocket`, `LightningBolt`, `Enemy`; verify they compile
- [x] 2.3 Update imports in the player weapon classes (`game.weapon.player.*`) that reference `Player`, `PlayerRocket`, `PlayerBomb`; verify they compile
- [x] 2.4 Update imports in scene/control/UI classes (`SceneFlowController`, `LevelScene`, `HangarScene`, `SpawnHordeAction`, `DirectorContext`, `LevelDirector`, `HangarDataModel`, `StartButtonHangar`, `WeaponButtonHangar`) that reference `Player`/`Enemy`; verify they compile
- [x] 2.5 Grep the `game` module for any remaining stale `import it.spaghettisource.tigersupply.game.entity.<Moved>` references and confirm none remain outside the new sub-packages

## 3. Update level XML reflection strings

- [x] 3.1 Update the seven `<enemyPrototype class="...">` values in `game/src/main/resources/level/level-1.xml` (`EnemyBackGround`, `Asteroid`, `EnemyStandard`, `EnemyShield`, `EnemyShoterRocket`, `EnemyBoss`) to `it.spaghettisource.tigersupply.game.entity.enemy.*`
- [x] 3.2 Grep `level-1.xml` for `game.entity.` and confirm every match points to an existing new sub-package path

## 4. Build and runtime verification

- [x] 4.1 Run `mvn compile` on the reactor and verify the `engine`, `game`, and `launcher` modules build with no errors
- [x] 4.2 Launch the application (`mvn -pl launcher exec:java`) and start level 1, verifying enemies, projectiles, and effects spawn correctly with no `ClassNotFoundException` at level load
