package it.spaghettisource.tigersupply.engine.windows;



import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import it.spaghettisource.tigersupply.engine.control.SceneManager;

public class GamePanelMauseMotionListener extends MouseMotionAdapter {

	private SceneManager sceneManager;

	public GamePanelMauseMotionListener(SceneManager sceneManager){
		this.sceneManager = sceneManager;

	}	
	
	  public void mouseMoved(MouseEvent event) {
		  sceneManager.mouseMoved(event);
	  }
	
	
	
	
}
