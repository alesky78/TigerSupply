package it.spaghettisource.tigersupply.engine.control;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;


/**
 * this is the interface of the game
 * 
 * @author Alessandro D'Ottavio
 *
 */
public interface Game {
	
	
	  public void updateGame(float deltaTimeSeconds) throws Exception; 
	  
	  public void renderGame() throws Exception;   // render the game to a buffer
	  
	  public void paintScreen();
	  
	  public void mousePress(int x, int y);
	  
	  public void mouseMove(MouseEvent event);	  
	  
	  public void keyPressed(KeyEvent event);	  

	  public void keyReleased(KeyEvent event);	  
	
}
