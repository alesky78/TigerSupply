## 1. Move fire-control into the enemy

- [x] 1.1 In `game.entity.Enemy.updateEntity(float)`, call `scanTargetInRange()` after the
      weapon-update loop; verify `game.entity.Enemy` compiles and a workspace search shows the only
      `scanTargetInRange()` call site is now inside `Enemy` (no group pass).

## 2. Remove EnemyGroup

- [x] 2.1 Delete `game.entity.EnemyGroup` (the redundant `updateEntity` override and the dead
      `reset()`); verify a workspace search for `EnemyGroup` returns no hits in `.java` sources.

## 3. Repoint LevelScene to the generic group

- [x] 3.1 In `game.scene.LevelScene`, add the `it.spaghettisource.tigersupply.game.entity.Enemy`
      import, remove the `EnemyGroup` import, and change the `enemyManager` field and its
      `new EnemyGroup()` instantiation to `EntityGroupScreenBound<Enemy>`; verify the field, the three
      `CollisionDetector` constructions, the per-frame `updateEntity` tick, and the
      `getManagedEntities()` render call all type-check against the base group.
- [x] 3.2 Verify the game module compiles (`mvn -q -pl game -am compile`).

## 4. Docs and verification

- [x] 4.1 Update the `level-director-sequencing` subsystem pages that name `EnemyGroup`
      (`index.md`, `caricamento-dati-livello.md`, `sequenziamento-step.md`) to describe the enemy
      container as `EntityGroupScreenBound<Enemy>` and note that per-enemy fire-control now lives in
      `Enemy.updateEntity`; verify no live doc still implies a distinct `EnemyGroup` type exists.
- [x] 4.2 Full reactor build passes (`mvn -q clean install`); launch and confirm a horde spawns and
      enemies still fire at the player as before.
