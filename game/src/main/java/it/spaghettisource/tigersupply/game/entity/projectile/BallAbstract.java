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

/** Shared implementation for code-rendered spherical enemy projectiles. */
public abstract class BallAbstract extends BaseEntity {

	private static final float TRAIL_INTERVAL = 0.03f;
	private static final float TRAIL_LIFETIME = 0.4f;
	private static final int PARTICLES = 4;
	private static final float PULSE_FREQUENCY = 6f;
	private static final float GLOW_SIZE_FACTOR = 1.4f;
	private static final int GLOW_ALPHA_MIN = 10;
	private static final int GLOW_ALPHA_MAX = 110;

	private static final Random random = new Random();

	private float trailCounter = 0;
	private float pulseCounter = 0;

	private EntityGroupScreenBound<Entity> effectManager;

	protected abstract ParticleColorScheme getTrailScheme();

	protected abstract Color[] getGradientColors(int glowAlpha);

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
				int jitterY = random.nextInt(diameter) - diameter / 2;
				int pixelSize = 3 + random.nextInt(4);
				EnergyTrailParticle particle = new EnergyTrailParticle(getTrailScheme(), getXposition() + jitterX,
						getYposition() + jitterY, pixelSize, TRAIL_LIFETIME, context);
				effectManager.addRequest(particle);
			}
		}
	}

	public void renderEntity(Graphics2D dbg) throws Exception {
		int diameter = size.getWidth();
		float glowDiameter = diameter * GLOW_SIZE_FACTOR;
		Point center = new Point(getXposition(), getYposition());

		float pulse = (float) (Math.sin(pulseCounter * PULSE_FREQUENCY * 2 * Math.PI) + 1f) / 2f;
		int glowAlpha = GLOW_ALPHA_MIN + Math.round((GLOW_ALPHA_MAX - GLOW_ALPHA_MIN) * pulse);

		float[] dist = {0f, 0.5f, 0.8f, 1f};
		dbg.setPaint(new RadialGradientPaint(center, glowDiameter / 2f, dist, getGradientColors(glowAlpha)));
		dbg.fillOval((int) (getXposition() - glowDiameter / 2), (int) (getYposition() - glowDiameter / 2),
				(int) glowDiameter, (int) glowDiameter);
	}
}