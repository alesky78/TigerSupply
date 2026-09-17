package it.spaghettisource.tigersupply.game.entity.projectile;

import java.awt.Color;

import it.spaghettisource.tigersupply.game.entity.effect.ParticleColorScheme;

/** A code-rendered spherical energy projectile fired by enemies. */
public class BallEnergy extends BallAbstract {

	@Override
	protected ParticleColorScheme getTrailScheme() {
		return ParticleColorScheme.ENERGY_TRAIL;
	}

	@Override
	protected Color[] getGradientColors(int glowAlpha) {
		return new Color[] {
				Color.WHITE,
				new Color(102, 230, 255),
				new Color(0, 90, 160, 180),
				new Color(0, 90, 160, glowAlpha)
		};
	}
}