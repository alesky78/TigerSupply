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
import it.spaghettisource.tigersupply.game.entity.effect.ParticleColorScheme;
import it.spaghettisource.tigersupply.game.utils.EntityFactoryWrapper;

/**
 * A code-rendered spherical fire projectile fired by enemies.
 *
 * <p>Mechanically identical to {@link EnergyBall} but drawn with the fire explosion palette (a bright
 * hot core that darkens toward a red edge matching its {@link EnergyTrailParticle} trail) plus an
 * outer halo whose alpha breathes over time, so the core doesn't look like a frozen, unrelated disc
 * next to the fading trail. It emits fire-coloured {@link EnergyTrailParticle} squares as it travels.
 * For gameplay it behaves as a standard enemy shot: it is collision-tested by the shot group and
 * removed on hit (default {@link BaseEntity} reaction) or when it leaves the screen.</p>
 */
public class FireBall extends BaseEntity {

	private static final float TRAIL_INTERVAL = 0.03f; // seconds between trail emissions
	private static final float TRAIL_LIFETIME = 0.4f;  // seconds each trail pixel lives
	private static final int PARTICLES = 4;          // number of particles emitted per trail interval

	private static final float PULSE_FREQUENCY = 6f;    // glow breathing cycles per second
	private static final float GLOW_SIZE_FACTOR = 1.4f; // outer glow diameter relative to the core
	private static final int GLOW_ALPHA_MIN = 10;
	private static final int GLOW_ALPHA_MAX = 110;

	private static final Random random = new Random();

	private float trailCounter = 0;
	private float pulseCounter = 0;

	private EntityGroupScreenBound<Entity> effectManager;

	public void setEffectManager(EntityGroupScreenBound<Entity> effectManager) {
		this.effectManager = effectManager;
	}

	public void updateEntity(float deltaSeconds) throws Exception {
		super.updateEntity(deltaSeconds);

		pulseCounter += deltaSeconds;

		trailCounter += deltaSeconds;
		if (trailCounter >= TRAIL_INTERVAL) {
			trailCounter = 0;
			int diameter = size.getWidth();
			for (int i = 0; i < PARTICLES; i++) {
				int jitterX = random.nextInt(7) - 3;
				int jitterY = random.nextInt(diameter) - diameter / 2; // spread across the full ball height
				int pixelSize = 3 + random.nextInt(4);
				EnergyTrailParticle particle = new EnergyTrailParticle(ParticleColorScheme.FIRE_TRAIL, getXposition() + jitterX,
																					   getYposition() + jitterY,
																					   pixelSize, TRAIL_LIFETIME, context);
				effectManager.addRequest(particle);
			}
		}
	}

	public void renderEntity(Graphics2D dbg) throws Exception {
		int diameter = size.getWidth();
		float glowDiameter = diameter * GLOW_SIZE_FACTOR;
		Point center = new Point(getXposition(), getYposition());

		// breathing halo alpha, echoing the trail particles' own alpha fade
		float pulse = (float) (Math.sin(pulseCounter * PULSE_FREQUENCY * 2 * Math.PI) + 1f) / 2f;
		int glowAlpha = GLOW_ALPHA_MIN + Math.round((GLOW_ALPHA_MAX - GLOW_ALPHA_MIN) * pulse);

		float[] dist = {0f, 0.5f, 0.8f, 1f};
		Color[] colors = {
				Color.WHITE,
				new Color(255, 160, 0),
				new Color(120, 0, 0, 180),
				new Color(120, 0, 0, glowAlpha)
		};
		dbg.setPaint(new RadialGradientPaint(center, glowDiameter / 2f, dist, colors));
		dbg.fillOval((int) (getXposition() - glowDiameter / 2), (int) (getYposition() - glowDiameter / 2),
				(int) glowDiameter, (int) glowDiameter);
	}

}
