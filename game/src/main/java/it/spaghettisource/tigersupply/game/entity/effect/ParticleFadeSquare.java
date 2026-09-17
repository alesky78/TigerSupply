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
 * A short-lived visual particle used to build the trailing glow of an energy-ball effect.
 *
 * <p>Each instance is rendered as a small filled square positioned at the entity's current
 * coordinates. The particle does not represent a physical object: it lives in the effect layer,
 * is never added to collision checks, and exists only to create a soft fading trail behind a
 * projectile or explosion.</p>
 *
 * <p>Its lifetime is driven by {@link #lifeCounter} and {@link #lifeTimeDuration}. At creation,
 * the color is at full intensity and then continuously fades toward zero via
 * {@link #colorAlteration}. When the counter reaches the configured lifetime, the particle marks
 * itself as removable so the game loop can clean it up. The fade is intentionally brief and
 * deterministic, giving the impression of a fast dissipating energy streak.</p>
 */
public class ParticleFadeSquare extends BaseEntity {

	private float colorAlteration;
	private final float increaseForLoop;
	private final int size;
	private final ParticleColorScheme scheme;

	private final float lifeTimeDuration;
	private float lifeCounter;

	public ParticleFadeSquare(ParticleColorScheme scheme, int posX, int posY, int size, float lifeTimeSeconds, GameContext context) {
		this.scheme = scheme;
		this.size = size;
		this.lifeTimeDuration = lifeTimeSeconds;
		this.lifeCounter = 0;

		float loops = lifeTimeDuration / context.getPeriodSeconds();
		colorAlteration = 1f;
		increaseForLoop = 1 / loops;

		position = new Position(posX, posY, GameResources.Z_EXPLOSION);
		speed = new Speed(0, 0);
		try {
			updateAlgorithm = UpdateAlgorithmFactoryWrapper.newDefault();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Advances the particle through its short life and fades it out.
	 *
	 * <p>The base entity update is applied first, then the lifetime timer is increased by
	 * {@code deltaSeconds}. Once the timer reaches {@link #lifeTimeDuration}, the particle is
	 * flagged for removal. Finally, {@link #colorAlteration} decreases in fixed steps so the color
	 * transitions smoothly from the original energy tint toward transparency.</p>
	 *
	 * @param deltaSeconds elapsed simulation time for this frame
	 * @throws Exception propagated from the parent entity update logic
	 */
	public void updateEntity(float deltaSeconds) throws Exception {
		super.updateEntity(deltaSeconds);

		lifeCounter += deltaSeconds;
		if (lifeCounter >= lifeTimeDuration) {
			remove = true;
		}

		colorAlteration -= increaseForLoop;
		if (colorAlteration < 0) {
			colorAlteration = 0f;
		}
	}

	/**
	 * Draws the particle as a small square using the current faded color.
	 *
	 * <p>The original graphics color is saved and restored so the effect does not leak its state
	 * into other drawing operations. The square size is fixed at construction time and centered on
	 * the particle position.</p>
	 *
	 * @param dbg graphics context used to render the particle
	 * @throws Exception propagated from the underlying rendering contract
	 */
	public void renderEntity(Graphics2D dbg) throws Exception {
		Color originalColor = dbg.getColor();
		dbg.setColor(scheme.colorAt(colorAlteration));
		dbg.fillRect(getXposition() - size / 2, getYposition() - size / 2, size, size);
		dbg.setColor(originalColor);
	}

}
