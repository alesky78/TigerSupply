package it.spaghettisource.tigersupply.engine.background;

import java.awt.Color;
import java.awt.Graphics2D;

import it.spaghettisource.tigersupply.engine.control.GameContext;
import it.spaghettisource.tigersupply.engine.image.finaleffect.Star;

/**
 * Black background animated with the same moving stars used by the final-effect system.
 */
public class StarBackGround implements BackGround {

	private final int screenWidth;
	private final int screenHeight;
	private final Star starEffect;

	/**
	 * Creates and starts a black star field.
	 *
	 * @param context the active game context; must not be {@code null}
	 * @param nextStarFrequency seconds between two consecutive stars; must be positive
	 */
	public StarBackGround(GameContext context, float nextStarFrequency){
		if(nextStarFrequency <= 0){
			throw new IllegalArgumentException("nextStarFrequency must be positive");
		}

		this.screenWidth = context.getScreenWidth();
		this.screenHeight = context.getScreenHeight();
		this.starEffect = new Star();
		this.starEffect.configAndStart(nextStarFrequency, context);
	}

	/**
	 * {@inheritDoc}
	 */
	public void updateBackground(float deltaSeconds) {
		try {
			starEffect.updateEffect(deltaSeconds);
		} catch (Exception e) {
			throw new IllegalStateException("Unable to update the star background", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void renderBackground(Graphics2D dbg) {
		Color originalColor = dbg.getColor();
		dbg.setColor(Color.BLACK);
		dbg.fillRect(0, 0, screenWidth, screenHeight);
		dbg.setColor(originalColor);

		try {
			starEffect.renderEffect(dbg, screenWidth, screenHeight);
		} catch (Exception e) {
			throw new IllegalStateException("Unable to render the star background", e);
		}
	}
}