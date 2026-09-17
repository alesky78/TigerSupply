package it.spaghettisource.tigersupply.game.entity.projectile;

import java.util.Random;

import it.spaghettisource.tigersupply.engine.entity.Entity;
import it.spaghettisource.tigersupply.engine.entity.EntityGroupScreenBound;
import it.spaghettisource.tigersupply.game.entity.effect.ParticleColorScheme;
import it.spaghettisource.tigersupply.game.entity.effect.ParticleFadeSquare;

/** {@link BallCore} that also emits a fading particle trail as it travels. */
public abstract class BallTrail extends BallCore {

	private static final float TRAIL_INTERVAL = 0.03f;
	private static final float TRAIL_LIFETIME = 0.4f;
	private static final int PARTICLES = 4;

	private static final Random random = new Random();

	private float trailCounter = 0;

	private EntityGroupScreenBound<Entity> effectManager;

	protected abstract ParticleColorScheme getTrailScheme();

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
				int jitterY = random.nextInt(diameter) - diameter / 2;
				int pixelSize = 3 + random.nextInt(4);
				ParticleFadeSquare particle = new ParticleFadeSquare(getTrailScheme(), getXposition() + jitterX,
						getYposition() + jitterY, pixelSize, TRAIL_LIFETIME, context);
				effectManager.addRequest(particle);
			}
		}
	}
}
