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
 * A single square pixel of an energy-ball comet trail.
 *
 * <p>Rendered procedurally as a small filled square whose alpha fades to zero over a short lifetime,
 * after which it flags itself for removal. It is a visual-only effect meant to live in the effect
 * group, so it never participates in collision detection.</p>
 */
public class EnergyTrailParticle extends BaseEntity {

	private float colorAlteration;
	private final float increaseForLoop;
	private final int size;
	private final ParticleColorScheme scheme;

	private final float lifeTimeDuration;
	private float lifeCounter;

	public EnergyTrailParticle(ParticleColorScheme scheme, int posX, int posY, int size, float lifeTimeSeconds, GameContext context) {
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

	public void renderEntity(Graphics2D dbg) throws Exception {
		Color originalColor = dbg.getColor();
		dbg.setColor(scheme.colorAt(colorAlteration));
		dbg.fillRect(getXposition() - size / 2, getYposition() - size / 2, size, size);
		dbg.setColor(originalColor);
	}

}
