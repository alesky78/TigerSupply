package it.spaghettisource.tigersupply.engine.control;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;


/**
 * A single interactive screen of the game - for example the presentation, the hangar, a level, or
 * the game-over screen - driven once per frame by the {@link GameLoop}.
 *
 * <p>A scene owns its own update/render/input lifecycle: the loop advances it with
 * {@link #update(float)}, asks it to draw with {@link #render()}, and blits the result with
 * {@link #paintScreen()}. The {@link SceneHost} routes AWT input to the active scene.
 *
 * @author Alessandro D'Ottavio
 *
 */
public interface Scene {

	/**
	 * Advance the scene simulation by one frame.
	 *
	 * @param deltaTimeSeconds the elapsed frame time in seconds, always positive
	 * @throws Exception if the update fails
	 */
	public void update(float deltaTimeSeconds) throws Exception;

	/**
	 * Draw the current state of the scene into its off-screen buffer.
	 *
	 * @throws Exception if rendering fails
	 */
	public void render() throws Exception;

	/**
	 * Blit the rendered off-screen buffer onto the on-screen surface.
	 */
	public void paintScreen();

	/**
	 * Handle a mouse-press at the given screen coordinates.
	 *
	 * @param x the x coordinate of the press, in pixels
	 * @param y the y coordinate of the press, in pixels
	 */
	public void mousePressed(int x, int y);

	/**
	 * Handle a mouse-move event.
	 *
	 * @param event the AWT mouse event, never {@code null}
	 */
	public void mouseMoved(MouseEvent event);

	/**
	 * Handle a key-press event.
	 *
	 * @param event the AWT key event, never {@code null}
	 */
	public void keyPressed(KeyEvent event);

	/**
	 * Handle a key-release event.
	 *
	 * @param event the AWT key event, never {@code null}
	 */
	public void keyReleased(KeyEvent event);

}
