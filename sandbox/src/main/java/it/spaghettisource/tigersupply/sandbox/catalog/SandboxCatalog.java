package it.spaghettisource.tigersupply.sandbox.catalog;

import java.util.ArrayList;
import java.util.List;

import it.spaghettisource.tigersupply.engine.control.GameContext;
import it.spaghettisource.tigersupply.engine.entity.Entity;
import it.spaghettisource.tigersupply.engine.entity.EntityFactory;
import it.spaghettisource.tigersupply.engine.entity.Position;
import it.spaghettisource.tigersupply.engine.entity.logic.UpdateAlgorithm;
import it.spaghettisource.tigersupply.engine.entity.logic.UpdateAlgorithmFactoryWrapper;
import it.spaghettisource.tigersupply.engine.sprite.Sprite;
import it.spaghettisource.tigersupply.engine.sprite.SpriteFactory;
import it.spaghettisource.tigersupply.game.entity.enemy.Enemy;
import it.spaghettisource.tigersupply.game.entity.effect.ParticleFadeSquare;
import it.spaghettisource.tigersupply.game.entity.effect.ParticleBurst;
import it.spaghettisource.tigersupply.game.entity.effect.ExplosionProfileFactory;
import it.spaghettisource.tigersupply.game.entity.effect.ParticleColorScheme;
import it.spaghettisource.tigersupply.game.scene.builder.EnemyDataBuilder;
import it.spaghettisource.tigersupply.game.scene.builder.EnemyDataBuilderSaxXml;
import it.spaghettisource.tigersupply.game.scene.builder.LevelDataRepository;
import it.spaghettisource.tigersupply.game.scene.builder.definition.EnemyPrototype;
import it.spaghettisource.tigersupply.game.scene.builder.definition.Image;
import it.spaghettisource.tigersupply.game.scene.builder.definition.Scale;
import it.spaghettisource.tigersupply.game.scene.builder.definition.Speed;
import it.spaghettisource.tigersupply.game.utils.EntityFactoryWrapper;

/**
 * The ordered list of every testable entity, grouped by family (projectiles, enemies, effects).
 *
 * <p>Enemy prototypes (sprite, speed, scale) are reused from the real {@code level/level-1.xml}
 * definition so the enemies look and move as they do in the game; each case then wires the primary
 * entity to the sandbox managers and the movable target, reusing the public
 * {@link EntityFactoryWrapper} factory methods.</p>
 */
public class SandboxCatalog {

	private final List<SandboxCase> cases = new ArrayList<SandboxCase>();
	private final LevelDataRepository levelData;

	public SandboxCatalog() throws Exception {
		this.levelData = loadLevelData("level/level-1.xml");
		buildProjectiles();
		buildEnemies();
		buildEffects();
	}

	public List<SandboxCase> getCases() {
		return cases;
	}

	private LevelDataRepository loadLevelData(String file) throws Exception {
		EnemyDataBuilder builder = new EnemyDataBuilderSaxXml(file);
		builder.parse();
		LevelDataRepository data = new LevelDataRepository();
		data.setSteps(builder.buildSteps());
		data.setEnemyPrototypes(builder.buildEnemyPrototypes());
		data.setAlgorithmPrototypes(builder.buildAlgorithmPrototypes());
		data.setScripts(builder.buildScripts());
		return data;
	}

	// ----------------------------------------------------------------- projectiles

	private void buildProjectiles() {
		cases.add(new SandboxCase("EnergyBall", Family.PROJECTILE, (ctx, m, target) -> {
			var ball = EntityFactoryWrapper.newEnemyShotEnergyBall(ctx, enemyOrigin(ctx),target);
			ball.setEffectManager(m.effect());
			return ball;
		}));
		cases.add(new SandboxCase("FireBall", Family.PROJECTILE, (ctx, m, target) -> {
			var ball = EntityFactoryWrapper.newEnemyShotFireBall(ctx, enemyOrigin(ctx),target);
			ball.setEffectManager(m.effect());
			return ball;
		}));
		cases.add(new SandboxCase("EnemyRocket", Family.PROJECTILE, (ctx, m, target) -> {
			var rocket = EntityFactoryWrapper.newEnemyShotRocket(enemyOrigin(ctx), target);
			rocket.setEffectManager(m.effect());
			return rocket;
		}));
		cases.add(new SandboxCase("SeekerRocket (homing)", Family.PROJECTILE, (ctx, m, target) -> {
			var rocket = EntityFactoryWrapper.newEnemyShotSeekerRocket(enemyOrigin(ctx), target);
			rocket.setEffectManager(m.effect());
			return rocket;
		}));
		cases.add(new SandboxCase("EnemyShot default", Family.PROJECTILE,
				(ctx, m, target) -> EntityFactoryWrapper.newEnemyShotDefault(enemyOrigin(ctx), target)));
		cases.add(new SandboxCase("PlasmaCannon", Family.PROJECTILE,
				(ctx, m, target) -> EntityFactoryWrapper.newEnemyShotPlasmaCannon(enemyOrigin(ctx), target)));
		cases.add(new SandboxCase("LightningBolt", Family.PROJECTILE,
				(ctx, m, target) -> EntityFactoryWrapper.newEnemyShotLightningBolt(ctx, enemyOrigin(ctx), 2f, 1f)));
		cases.add(new SandboxCase("PlayerRocket", Family.PROJECTILE, (ctx, m, target) -> {
			var rocket = EntityFactoryWrapper.playerShotRocket(playerOrigin(ctx));
			rocket.setEffectManager(m.effect());
			return rocket;
		}));
		cases.add(new SandboxCase("PlayerBomb", Family.PROJECTILE, (ctx, m, target) -> {
			var bomb = EntityFactoryWrapper.playerShotBomb(playerOrigin(ctx), 1);
			bomb.setEffectManager(m.effect());
			return bomb;
		}));
		cases.add(new SandboxCase("PlayerGun", Family.PROJECTILE,
				(ctx, m, target) -> EntityFactoryWrapper.playerShotGun(playerOrigin(ctx))));
		cases.add(new SandboxCase("PlayerPaser", Family.PROJECTILE,
				(ctx, m, target) -> EntityFactoryWrapper.playerShotPaser(playerOrigin(ctx))));
		cases.add(new SandboxCase("PlayerGun sinusoidal", Family.PROJECTILE,
				(ctx, m, target) -> EntityFactoryWrapper.playerShotGunSynuisodal(playerOrigin(ctx), 0f)));
	}

	// ----------------------------------------------------------------- enemies

	private void buildEnemies() {
		addEnemyCase("EnemyStandard", "standard-2");
		addEnemyCase("EnemyShoterRocket", "roker");
		addEnemyCase("EnemyEnergyShooter", "energyShooter");
		addEnemyCase("EnemyShield", "enemyShield");
		addEnemyCase("Asteroid", "asteroid1");
		addEnemyCase("EnemyBoss", "boss");
		addEnemyCase("EnemyBackGround", "enemyBackgroundSt");
	}

	private void addEnemyCase(String label, String prototypeName) {
		cases.add(new SandboxCase(label, Family.ENEMY,
				(ctx, m, target) -> buildEnemy(prototypeName, ctx, m, target)));
	}

	private Enemy buildEnemy(String prototypeName, GameContext ctx, SandboxManagers m, Entity target) throws Exception {
		EnemyPrototype proto = levelData.getEnemyPrototypeByName(prototypeName);
		Speed speed = proto.getSpeed();
		Image image = proto.getImage();
		Scale scale = proto.getScale();
		UpdateAlgorithm algorithm = UpdateAlgorithmFactoryWrapper.newDefault();
		Sprite sprite = SpriteFactory.getInstance().createImageSingleSprite(image.getAlias());

		int startX = ctx.getScreenWidth() - 200;
		int startY = ctx.getScreenHeight() / 2;
		Enemy enemy = EntityFactory.getInstance().createEntity(startX, startY, 20,
				speed.getX(), speed.getY(), scale.getScale(), algorithm, sprite, proto.getClassName());

		enemy.setEffectManager(m.effect());
		enemy.setShotManager(m.shot());
		enemy.setEnemyManager(m.enemy());
		enemy.setTarget(target);
		enemy.setContext(ctx);
		return enemy;
	}

	// ----------------------------------------------------------------- effects

	private void buildEffects() {
		cases.add(new SandboxCase("ParticleBurst (fire)", Family.EFFECT, (ctx, m, target) ->
				new ParticleBurst(ParticleColorScheme.FIRE, centerX(ctx), centerY(ctx), 60, 120, 0.8f, ctx)));
		cases.add(new SandboxCase("ParticleBurst (energetic)", Family.EFFECT, (ctx, m, target) ->
				new ParticleBurst(ParticleColorScheme.ENERGETIC, centerX(ctx), centerY(ctx), 60, 120, 0.8f, ctx)));
		cases.add(new SandboxCase("Explosion (enemy hit)", Family.EFFECT, (ctx, m, target) ->
				EntityFactoryWrapper.newTimedExplosion(ExplosionProfileFactory.standardHit(), centerX(ctx), centerY(ctx), ExplosionProfileFactory.standardHit().getParticleNum(), 1f, m.effect(), ctx)));
		cases.add(new SandboxCase("Explosion (enemy death)", Family.EFFECT, (ctx, m, target) ->
				EntityFactoryWrapper.newTimedExplosion(ExplosionProfileFactory.shooterDeath(), centerX(ctx), centerY(ctx), ExplosionProfileFactory.shooterDeath().getParticleNum(), 1.5f, m.effect(), ctx)));
		cases.add(new SandboxCase("Explosion (boss agony, follows target)", Family.EFFECT, (ctx, m, target) ->
				EntityFactoryWrapper.newFollowingExplosion(ExplosionProfileFactory.bossAgony(), target, 200, 2f, m.effect(), ctx)));
		cases.add(new SandboxCase("ParticleFadeSquare (energy)", Family.EFFECT, (ctx, m, target) ->
				new ParticleFadeSquare(ParticleColorScheme.ENERGY_TRAIL, centerX(ctx), centerY(ctx), 10, 0.8f, ctx)));
		cases.add(new SandboxCase("ParticleFadeSquare (fire)", Family.EFFECT, (ctx, m, target) ->
				new ParticleFadeSquare(ParticleColorScheme.FIRE_TRAIL, centerX(ctx), centerY(ctx), 10, 0.8f, ctx)));
		cases.add(new SandboxCase("Smoke", Family.EFFECT, (ctx, m, target) ->
				EntityFactoryWrapper.newSmoke(new Position(centerX(ctx), centerY(ctx), 0))));
		cases.add(new SandboxCase("EnergeticShield (follows target)", Family.EFFECT, (ctx, m, target) ->
				EntityFactoryWrapper.newEnergeticShield(90, 6f, m.effect(), target.getPosition(), ctx)));
	}

	// ----------------------------------------------------------------- helpers

	private Position enemyOrigin(GameContext ctx) {
		return new Position(ctx.getScreenWidth() * 3 / 4, ctx.getScreenHeight() / 2, 0);
	}

	private Position playerOrigin(GameContext ctx) {
		return new Position(ctx.getScreenWidth() / 6, ctx.getScreenHeight() / 2, 0);
	}

	private int centerX(GameContext ctx) {
		return ctx.getScreenWidth() / 2;
	}

	private int centerY(GameContext ctx) {
		return ctx.getScreenHeight() / 2;
	}
}
