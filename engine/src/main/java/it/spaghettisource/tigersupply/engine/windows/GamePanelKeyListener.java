package it.spaghettisource.tigersupply.engine.windows;



import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import it.spaghettisource.tigersupply.engine.control.SceneHost;

public class GamePanelKeyListener extends KeyAdapter {

	
	private SceneHost sceneHost;

	public GamePanelKeyListener(SceneHost sceneHost){
		this.sceneHost = sceneHost;
	}
	
	public void keyPressed(KeyEvent event){
		sceneHost.keyPressed(event);
	}
	
	public void keyReleased(KeyEvent event){
		sceneHost.keyReleased(event);
	}
	
}
