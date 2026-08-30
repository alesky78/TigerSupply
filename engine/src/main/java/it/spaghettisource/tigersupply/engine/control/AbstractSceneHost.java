package it.spaghettisource.tigersupply.engine.control;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;

/**
 * Base {@link SceneHost} implementation that holds the active {@link Scene} and forwards AWT input
 * to it.
 *
 * <p>Subclasses supply the concrete scenes (typically by swapping {@link #activeScene}); this base
 * only stores the hosting {@link JPanel} and {@link GameContext} and routes mouse/key events. Note
 * that it <em>holds</em> a {@link JPanel} rather than being one - the actual panel is
 * {@code engine.windows.GamePanel}.
 *
 * @author Alessandro D'Ottavio
 *
 */
public abstract class AbstractSceneHost implements SceneHost {

	protected JPanel panel;
	protected GameContext context;

	protected Scene activeScene;	


	/** {@inheritDoc} */
	public Scene getActiveScene() throws Exception {
		return activeScene;
	}

	/** {@inheritDoc} */
	public void mousePressed(int x, int y) {
		activeScene.mousePressed(x, y);
	}

	/** {@inheritDoc} */
	public void mouseMoved(MouseEvent event){
		activeScene.mouseMoved(event);
	}

	/** {@inheritDoc} */
	public void keyPressed(KeyEvent event) {
		activeScene.keyPressed(event);			
	}		

	/** {@inheritDoc} */
	public void keyReleased(KeyEvent event) {
		activeScene.keyReleased(event);
	}
	

}
