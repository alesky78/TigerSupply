package it.spaghettisource.tigersupply.game.entity.projectile;

import java.awt.Color;

import it.spaghettisource.tigersupply.game.entity.effect.ParticleColorScheme;

/** A code-rendered spherical fire projectile fired by enemies. */
public class BallFire extends BallTrail {

	@Override
	protected ParticleColorScheme getTrailScheme() {
		return ParticleColorScheme.FIRE_TRAIL;
	}

	@Override
	protected Color[] getGradientColors(int glowAlpha) {
		return new Color[] {
				Color.WHITE,
				new Color(255, 160, 0),
				new Color(120, 0, 0, 180),
				new Color(120, 0, 0, glowAlpha)
		};
	}
}