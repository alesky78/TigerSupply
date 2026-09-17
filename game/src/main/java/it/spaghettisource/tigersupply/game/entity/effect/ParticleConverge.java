package it.spaghettisource.tigersupply.game.entity.effect;

import it.spaghettisource.tigersupply.game.entity.BaseEntity;

import java.awt.Color;
import java.awt.Graphics2D;

import it.spaghettisource.tigersupply.engine.control.GameContext;
import it.spaghettisource.tigersupply.engine.entity.Entity;
import it.spaghettisource.tigersupply.engine.entity.Position;
import it.spaghettisource.tigersupply.engine.entity.Speed;
import it.spaghettisource.tigersupply.engine.entity.logic.UpdateAlgorithmFactoryWrapper;
import it.spaghettisource.tigersupply.game.utils.GameResources;

/**
 * A single fragment that streaks inward toward a live target, gaining color and size as it nears it.
 *
 * <p>The inverse of {@link ParticleBurst}: instead of scattering outward from a point and fading to
 * transparent, a converge particle spawns transparent on a random ring around the target, then homes
 * toward it every frame, growing more opaque and reaching full size/color exactly as it arrives.
 * Re-aiming every frame (rather than a one-shot straight line) keeps it aligned even while the target
 * itself is moving. Spawning a stream of these around a point gives the impression that ambient energy
 * is being drawn in and feeding it, e.g. a weapon charging up.</p>
 *
 * @author Alessandro D'Ottavio
 */
public class ParticleConverge extends BaseEntity {

	private static final double PI2 = 2 * Math.PI;

	private float colorAlteration;
	private int   size;
	private ParticleColorScheme scheme;

	private float lifeTime;
	private float lifeCounter;

	private final Entity target;
	private final int targetOffsetX;

	public ParticleConverge(ParticleColorScheme scheme, Entity target, int targetOffsetX, int minDistance, int maxDistance,
			int maxSize, float maxLifeTimeInSeconds, GameContext context) {
		this.scheme = scheme;
		this.target = target;
		this.targetOffsetX = targetOffsetX;

		lifeTime = Math.max(0.01f, (float) (Math.random() * maxLifeTimeInSeconds));
		lifeCounter = 0;

		colorAlteration = 0f;
		size = Math.max(1, (int) (Math.random() * maxSize));

		//random point on a ring around the target's current position
		double angle = PI2 * Math.random();
		double distance = minDistance + Math.random() * (maxDistance - minDistance);
		int posX = (target.getXposition() - targetOffsetX) + (int) (Math.cos(angle) * distance);
		int posY = target.getYposition() + (int) (Math.sin(angle) * distance);

		speed = new Speed(0, 0);	//re-aimed toward the live target every frame, see updateEntity

		position = new Position(posX, posY, GameResources.Z_EXPLOSION);
		try {
			updateAlgorithm = UpdateAlgorithmFactoryWrapper.newDefault();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Re-aims at the live target, advances the particle toward it and brightens it over time.
	 *
	 * <p>The velocity is recomputed every frame from the remaining distance to the target's current
	 * position divided by the remaining lifetime, so the particle keeps converging on it exactly by
	 * the time its lifetime elapses even if the target has moved meanwhile. The base entity update then
	 * integrates that velocity, the lifetime timer is increased by {@code deltaSeconds}, and once it
	 * reaches {@link #lifeTime} the particle is flagged for removal. {@link #colorAlteration} is
	 * recomputed from the elapsed fraction of the lifetime, making the brightening framerate
	 * independent, and is clamped to one.</p>
	 *
	 * @param deltaSeconds elapsed simulation time for this frame
	 * @throws Exception propagated from the parent entity update logic
	 */
	public void updateEntity(float deltaSeconds) throws Exception {
		//clamped so the remaining leg never shrinks below one frame, avoiding a speed spike/division blow-up
		float remaining = Math.max(deltaSeconds, lifeTime - lifeCounter);
		float dx = (target.getXposition() - targetOffsetX) - getXposition();
		float dy = target.getYposition() - getYposition();
		speed.setSpeedX(dx / remaining);
		speed.setSpeedY(dy / remaining);

		super.updateEntity(deltaSeconds);

		lifeCounter += deltaSeconds;
		if (lifeCounter >= lifeTime) {
			remove = true;
		}

		//brighten based on elapsed lifetime, framerate independent
		colorAlteration = lifeCounter / lifeTime;
		if (colorAlteration > 1) {
			colorAlteration = 1f;
		}
	}

	/**
	 * Draws the particle as a filled square whose colour and size grow as it approaches the target.
	 *
	 * <p>The current drawing colour is saved and restored so the effect does not leak its state into
	 * later rendering. The square is centred on the particle position and its drawn size is scaled by
	 * {@link #colorAlteration}, so the fragment visually condenses out of nothing as it arrives.</p>
	 *
	 * @param dbg graphics context used to render the particle
	 * @throws Exception propagated from the underlying rendering contract
	 */
	public void renderEntity(Graphics2D dbg) throws Exception {
		Color originalColor = dbg.getColor();
		dbg.setColor(scheme.colorAt(colorAlteration));

		dbg.fillRect(getXposition() - size / 2, getYposition() - size / 2, (int) (size * colorAlteration), (int) (size * colorAlteration));

		dbg.setColor(originalColor);
	}

}
