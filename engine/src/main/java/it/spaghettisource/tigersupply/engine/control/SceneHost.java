package it.spaghettisource.tigersupply.engine.control;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

/**
 * Hosts the currently active {@link Scene}: supplies it to the {@link GameLoop} and routes AWT
 * input to it.
 *
 * <p>On every iteration the loop calls {@link #getActiveScene()} to obtain the scene to update and
 * render (see {@link GameLoop#run()}); the mouse and key callbacks forward user input to that
 * active scene. This type does not decide <em>which</em> scene is active or when to switch - that
 * scene-flow responsibility lives in the game module's controller.
 *
 * @author Alessandro D'Ottavio
 *
 */
public interface SceneHost {

	/**
	 * Return the scene the loop should currently update and render.
	 *
	 * @return the active scene, never {@code null}
	 * @throws Exception if the active scene cannot be resolved
	 */
	public Scene getActiveScene() throws Exception;

	/**
	 * Forward a mouse-press to the active scene.
	 *
	 * @param x the x coordinate of the press, in pixels
	 * @param y the y coordinate of the press, in pixels
	 */
	public void mousePressed(int x, int y);

	/**
	 * Forward a mouse-move to the active scene.
	 *
	 * @param event the AWT mouse event, never {@code null}
	 */
	public void mouseMoved(MouseEvent event);

	/**
	 * Forward a key-press to the active scene.
	 *
	 * @param event the AWT key event, never {@code null}
	 */
	public void keyPressed(KeyEvent event);

	/**
	 * Forward a key-release to the active scene.
	 *
	 * @param event the AWT key event, never {@code null}
	 */
	public void keyReleased(KeyEvent event);


}
