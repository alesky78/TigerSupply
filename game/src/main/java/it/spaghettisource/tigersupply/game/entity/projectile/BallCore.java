package it.spaghettisource.tigersupply.game.entity.projectile;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RadialGradientPaint;

import it.spaghettisource.tigersupply.game.entity.BaseEntity;

/** Shared rendering for code-rendered spherical projectiles: a pulsing radial-gradient glow. */
public abstract class BallCore extends BaseEntity {

	private static final float PULSE_FREQUENCY = 6f;
	private static final float GLOW_SIZE_FACTOR = 1.4f;
	private static final int GLOW_ALPHA_MIN = 10;
	private static final int GLOW_ALPHA_MAX = 110;

	private float pulseCounter = 0;

	protected abstract Color[] getGradientColors(int glowAlpha);

	public void updateEntity(float deltaSeconds) throws Exception {
		super.updateEntity(deltaSeconds);
		pulseCounter += deltaSeconds;
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
