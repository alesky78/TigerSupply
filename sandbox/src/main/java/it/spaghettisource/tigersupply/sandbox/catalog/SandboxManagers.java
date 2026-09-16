package it.spaghettisource.tigersupply.sandbox.catalog;

import it.spaghettisource.tigersupply.engine.control.GameContext;
import it.spaghettisource.tigersupply.engine.entity.Entity;
import it.spaghettisource.tigersupply.engine.entity.EntityGroupScreenBound;
import it.spaghettisource.tigersupply.game.entity.enemy.Enemy;

/**
 * Holds the three screen-bound groups a sandbox run needs: effects, shots and enemies.
 *
 * <p>These mirror the groups the {@code LevelScene} owns, minus the level flow and collisions. A
 * case wires its primary entity to the group its {@link Family} dictates, and enemies/projectiles
 * spawn their own children (shots, particles) into these same groups.</p>
 */
public class SandboxManagers {

	private final EntityGroupScreenBound<Entity> effect;
	private final EntityGroupScreenBound<Entity> shot;
	private final EntityGroupScreenBound<Enemy> enemy;

	public SandboxManagers(GameContext context) {
		effect = new EntityGroupScreenBound<Entity>();
		shot = new EntityGroupScreenBound<Entity>();
		enemy = new EntityGroupScreenBound<Enemy>();
		effect.init(context);
		shot.init(context);
		enemy.init(context);
	}

	public EntityGroupScreenBound<Entity> effect() {
		return effect;
	}

	public EntityGroupScreenBound<Entity> shot() {
		return shot;
	}

	public EntityGroupScreenBound<Enemy> enemy() {
		return enemy;
	}
}
