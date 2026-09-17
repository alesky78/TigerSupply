package it.spaghettisource.tigersupply.game.entity.projectile;

import java.awt.Color;

/** A code-rendered spherical projectile fired by the standard enemy weapon; no particle trail. */
public class BallStandard extends BallCore {

	@Override
	protected Color[] getGradientColors(int glowAlpha) {
		return new Color[] {
				Color.WHITE,
				new Color(255, 90, 60),
				new Color(160, 10, 10, 180),
				new Color(160, 10, 10, glowAlpha)
		};
	}
}
