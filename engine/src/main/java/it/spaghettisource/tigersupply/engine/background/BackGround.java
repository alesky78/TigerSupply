package it.spaghettisource.tigersupply.engine.background;

import java.awt.Graphics2D;

/**
 * Contract for a single background layer of the playfield.
 *
 * <p>Every frame the game loop drives a background in two ordered phases: it first
 * calls {@link #updateBackground(float)} to advance the layer's animation state using
 * the elapsed time, then {@link #renderBackground(Graphics2D)} to paint the current
 * state onto the back buffer. Implementations may cover the whole window (fit or
 * texture backgrounds), stay still (static backgrounds), or be combined to produce a
 * parallax effect (see {@link ParallaxBackGround}).
 *
 * @author Alessandro D'Ottavio
 */
public interface BackGround {

	/**
	 * Advances the background animation state by the given time step.
	 *
	 * <p>Invoked once per frame, before {@link #renderBackground(Graphics2D)}. Static
	 * backgrounds may ignore the parameter and leave their state unchanged.
	 *
	 * @param deltaSeconds the time elapsed since the previous frame, in seconds;
	 *                     expected to be non-negative
	 */
	public void updateBackground(float deltaSeconds);


	/**
	 * Paints the current background state onto the supplied graphics context.
	 *
	 * <p>Invoked once per frame, after {@link #updateBackground(float)}. Implementations
	 * that change global {@link Graphics2D} state (for example the current paint) must
	 * restore it before returning.
	 *
	 * @param dbg the double-buffer graphics context to draw onto; must not be {@code null}
	 */
	public void renderBackground(Graphics2D dbg);


}
