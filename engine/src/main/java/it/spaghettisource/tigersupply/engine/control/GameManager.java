package it.spaghettisource.tigersupply.engine.control;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

/**
 * this is the manager of the scene used by the {@link AnimationLoop#run()}
 * to get the correct scene to render
 * 
 * @author Alessandro D'Ottavio
 *
 */
public interface GameManager {

	/**
	 * this method is call by any loop of the framework to know what scene draw
	 * look at {@link AnimationLoop#run()}
	 * 
	 * @return
	 * @throws Exception
	 */
	public Game getActualGame() throws Exception;

	public void mousePress(int x, int y);

	public void mouseMove(MouseEvent event);	
	
	public void keyPressed(KeyEvent event);	  

	public void keyReleased(KeyEvent event);	  	


}
