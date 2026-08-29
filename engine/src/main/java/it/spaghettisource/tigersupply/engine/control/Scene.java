package it.spaghettisource.tigersupply.engine.control;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;


/**
 * A single interactive screen of the game - for example the presentation, the hangar, a level, or
 * the game-over screen - driven once per frame by the {@link GameLoop}.
 *
 * <p>A scene owns its own update/render/input lifecycle: the loop advances it with
 * {@link #update(float)}, asks it to draw with {@link #render()}, and blits the result with
 * {@link #paintScreen()}. The {@link SceneManager} routes AWT input to the active scene.
 *
 * @author Alessandro D'Ottavio
 *
 */
public interface Scene {
	
	
	  public void update(float deltaTimeSeconds) throws Exception; 
	  
	  public void render() throws Exception;   // render the scene to a buffer
	  
	  public void paintScreen();
	  
	  public void mousePressed(int x, int y);
	  
	  public void mouseMoved(MouseEvent event);	  
	  
	  public void keyPressed(KeyEvent event);	  

	  public void keyReleased(KeyEvent event);	  
	
}
