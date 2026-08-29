package it.spaghettisource.tigersupply.engine.control;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

/**
 * Supplies the currently active {@link Scene} to the {@link GameLoop} and routes AWT input to it.
 *
 * <p>On every iteration the loop calls {@link #getActiveScene()} to obtain the scene to update and
 * render (see {@link GameLoop#run()}); the mouse and key callbacks forward user input to that
 * active scene.
 *
 * @author Alessandro D'Ottavio
 *
 */
public interface SceneManager {

	/**
	 * Return the scene the loop should currently update and render.
	 *
	 * @return the active scene, never {@code null}
	 * @throws Exception if the active scene cannot be resolved
	 */
	public Scene getActiveScene() throws Exception;

	public void mousePressed(int x, int y);

	public void mouseMoved(MouseEvent event);	
	
	public void keyPressed(KeyEvent event);	  

	public void keyReleased(KeyEvent event);	  	


}
