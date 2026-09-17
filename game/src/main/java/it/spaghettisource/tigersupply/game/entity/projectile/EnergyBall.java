package it.spaghettisource.tigersupply.game.entity.projectile;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RadialGradientPaint;
import java.util.Random;

import it.spaghettisource.tigersupply.engine.entity.Entity;
import it.spaghettisource.tigersupply.engine.entity.EntityGroupScreenBound;
import it.spaghettisource.tigersupply.game.entity.BaseEntity;
import it.spaghettisource.tigersupply.game.entity.effect.EnergyTrailParticle;
import it.spaghettisource.tigersupply.game.utils.EntityFactoryWrapper;

/**
 * A code-rendered spherical energy projectile fired by enemies.
 *
 * <p>The ball is drawn procedurally with a {@link RadialGradientPaint} (a bright core that darkens
 * toward the edge) and advances straight across the playfield. As it travels it emits fading
 * {@link EnergyTrailParticle} squares into the effect group, producing a comet-like trail. For
 * gameplay it behaves as a standard enemy shot: it is collision-tested by the shot group and removed
 * on hit (default {@link BaseEntity} reaction) or when it leaves the screen.</p>
 */
public class EnergyBall extends BaseEntity {

	private static final float TRAIL_INTERVAL = 0.03f; // seconds between trail emissions
	private static final float TRAIL_LIFETIME = 0.4f;  // seconds each trail pixel lives
	private static final int PARTICLES = 4;          // number of particles emitted per trail interval

	private static final Random random = new Random();
	
	private float trailCounter = 0;

	private EntityGroupScreenBound<Entity> effectManager;

	public void setEffectManager(EntityGroupScreenBound<Entity> effectManager) {
		this.effectManager = effectManager;
	}

	public void updateEntity(float deltaSeconds) throws Exception {
		super.updateEntity(deltaSeconds);

		trailCounter += deltaSeconds;
		if (trailCounter >= TRAIL_INTERVAL) {
			trailCounter = 0;
			int diameter = size.getWidth();
			for (int i = 0; i < PARTICLES; i++) {
				int jitterX = random.nextInt(7) - 3;
				int jitterY = random.nextInt(diameter) - diameter / 2; // spread across the full ball height
				int pixelSize = 3 + random.nextInt(4);
				EnergyTrailParticle particle = EntityFactoryWrapper.newEnergyTrailParticle(getXposition() + jitterX, 
																						   getYposition() + jitterY, 
																						   pixelSize, TRAIL_LIFETIME, context);
				effectManager.addRequest(particle);
			}
		}
	}

	public void renderEntity(Graphics2D dbg) throws Exception {
		int diameter = size.getWidth();
		Point center = new Point(getXposition(), getYposition());
		float[] dist = {0f, 0.6f, 1f};
		Color[] colors = {Color.WHITE, new Color(102, 230, 255), new Color(0, 90, 160)}; // core -> trail-matching cyan edge
		dbg.setPaint(new RadialGradientPaint(center, diameter / 2f, dist, colors));
		dbg.fillOval(getXposition() - diameter / 2, getYposition() - diameter / 2, diameter, diameter);
	}

}
