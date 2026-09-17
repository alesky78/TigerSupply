package it.spaghettisource.tigersupply.game.entity.effect;

import it.spaghettisource.tigersupply.game.entity.BaseEntity;

import java.awt.Color;
import java.awt.Graphics2D;

import it.spaghettisource.tigersupply.engine.control.GameContext;
import it.spaghettisource.tigersupply.engine.entity.Position;
import it.spaghettisource.tigersupply.engine.entity.Speed;
import it.spaghettisource.tigersupply.engine.entity.logic.UpdateAlgorithmFactoryWrapper;
import it.spaghettisource.tigersupply.game.utils.GameResources;

/**
 * A moving visual particle that leaves a decelerating, fading trail behind an entity.
 *
 * <p>Unlike {@link ParticleFadeSquare}, which stays fixed where it is spawned, this particle is
 * created with an initial velocity — typically inherited from the emitter (an engine exhaust, a
 * projectile) — and keeps drifting in that direction while progressively slowing down. This makes
 * the fragments spread out into a real tail that trails and dissipates rather than a static cloud of
 * squares.</p>
 *
 * <p>The particle is a visual-only effect: it lives in the effect layer and never takes part in
 * collision detection. Over its lifetime it moves in the direction of its initial velocity while a
 * per-second damping factor bleeds that velocity toward zero, so the drift decelerates smoothly.
 * At the same time {@link #colorAlteration} decreases proportionally to the elapsed fraction of the
 * lifetime, making both the colour and the drawn diameter shrink toward zero in a framerate
 * independent way. When the lifetime elapses the particle flags itself for removal so the game loop
 * can discard it.</p>
 *
 * @author Alessandro D'Ottavio
 */
public class ParticleTrailSquare extends BaseEntity {

	/** Fraction of the velocity retained after one second of drift. */
	private static final double DAMPING_PER_SECOND = 0.15;

	private final ParticleColorScheme scheme;
	private final int size;

	private final float lifeTimeDuration;
	private float lifeCounter;
	private float colorAlteration;

	/**
	 * Creates a trail particle drifting from {@code (posX, posY)} with the given initial velocity.
	 *
	 * @param scheme          the colour palette used to render and fade the particle
	 * @param posX            the initial x position, in pixels
	 * @param posY            the initial y position, in pixels
	 * @param size            the initial drawn diameter, in pixels
	 * @param speedX          the initial horizontal velocity, in pixel/second
	 * @param speedY          the initial vertical velocity, in pixel/second
	 * @param lifeTimeSeconds the lifetime after which the particle removes itself, in seconds
	 * @param context         the game context (kept for symmetry with the other particle effects)
	 */
	public ParticleTrailSquare(ParticleColorScheme scheme, int posX, int posY, int size, float speedX, float speedY, float lifeTimeSeconds, GameContext context) {
		this.scheme = scheme;
		this.size = Math.max(1, size);
		this.lifeTimeDuration = Math.max(0.01f, lifeTimeSeconds);
		this.lifeCounter = 0;
		this.colorAlteration = 1f;

		position = new Position(posX, posY, GameResources.Z_EXPLOSION);
		speed = new Speed(speedX, speedY);
		try {
			updateAlgorithm = UpdateAlgorithmFactoryWrapper.newDefault();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Advances the particle along its decelerating trajectory and fades it out over time.
	 *
	 * <p>The base entity update moves the particle according to its current velocity, then that
	 * velocity is damped by a framerate independent factor so the drift slows down. The lifetime
	 * timer is increased by {@code deltaSeconds} and, once it reaches {@link #lifeTimeDuration}, the
	 * particle is flagged for removal. Finally {@link #colorAlteration} is recomputed from the elapsed
	 * fraction of the lifetime and clamped to zero.</p>
	 *
	 * @param deltaSeconds elapsed simulation time for this frame
	 * @throws Exception propagated from the parent entity update logic
	 */
	public void updateEntity(float deltaSeconds) throws Exception {
		super.updateEntity(deltaSeconds);

		//framerate independent exponential deceleration of the inherited velocity
		float damping = (float) Math.pow(DAMPING_PER_SECOND, deltaSeconds);
		speed.setSpeedX(speed.getSpeedX() * damping);
		speed.setSpeedY(speed.getSpeedY() * damping);

		lifeCounter += deltaSeconds;
		if (lifeCounter >= lifeTimeDuration) {
			remove = true;
		}

		//fade based on elapsed lifetime, framerate independent
		colorAlteration = 1 - (lifeCounter / lifeTimeDuration);
		if (colorAlteration < 0) {
			colorAlteration = 0f;
		}
	}

	/**
	 * Draws the particle as a filled oval whose colour and diameter shrink as it fades.
	 *
	 * <p>The current drawing colour is saved and restored so the effect does not leak its state into
	 * later rendering. The oval is centred on the particle position and its drawn size is scaled by
	 * {@link #colorAlteration}, so the fragment visually contracts toward nothing as it dies.</p>
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
