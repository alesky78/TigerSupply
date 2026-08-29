package it.spaghettisource.tigersupply.engine.control;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;

/**
 * Base {@link SceneManager} implementation that holds the active {@link Scene} and forwards AWT
 * input to it.
 *
 * <p>Subclasses supply the concrete scenes (typically by swapping {@link #activeScene}); this base
 * only stores the hosting {@link JPanel} and {@link GameContext} and routes mouse/key events.
 *
 * @author Alessandro D'Ottavio
 *
 */
public abstract class AbstractSceneManagerJPanel implements SceneManager {

	protected JPanel panel;
	protected GameContext context;

	protected Scene activeScene;	


	public Scene getActiveScene() throws Exception {
		return activeScene;
	}

	public void mousePressed(int x, int y) {
		activeScene.mousePressed(x, y);
	}
	
	public void mouseMoved(MouseEvent event){
		activeScene.mouseMoved(event);
	}

	public void keyPressed(KeyEvent event) {
		activeScene.keyPressed(event);			
	}		

	public void keyReleased(KeyEvent event) {
		activeScene.keyReleased(event);
	}
	

}
