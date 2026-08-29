package it.spaghettisource.tigersupply.engine.windows;



import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import it.spaghettisource.tigersupply.engine.control.SceneManager;

public class GamePanelKeyListener extends KeyAdapter {

	
	private SceneManager sceneManager;

	public GamePanelKeyListener(SceneManager sceneManager){
		this.sceneManager = sceneManager;
	}
	
	public void keyPressed(KeyEvent event){
		sceneManager.keyPressed(event);
	}
	
	public void keyReleased(KeyEvent event){
		sceneManager.keyReleased(event);
		
	}
	
	
	
}
