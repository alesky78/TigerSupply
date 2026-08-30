package it.spaghettisource.tigersupply.engine.windows;



import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import it.spaghettisource.tigersupply.engine.control.SceneHost;

public class GamePanelMauseMotionListener extends MouseMotionAdapter {

	private SceneHost sceneHost;

	public GamePanelMauseMotionListener(SceneHost sceneHost){
		this.sceneHost = sceneHost;

	}	
	
	  public void mouseMoved(MouseEvent event) {
		  sceneHost.mouseMoved(event);
	  }
	
	
	
	
}
