package it.spaghettisource.tigersupply.engine.windows;


import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import it.spaghettisource.tigersupply.engine.control.SceneManager;

/**
 * 
 * @author Alessandro D'Ottavio
 *
 */
public class GamePanelMauseListener extends MouseAdapter {


	private SceneManager sceneManager;

	public GamePanelMauseListener(SceneManager sceneManager){
		this.sceneManager = sceneManager;
	}

	public void mousePressed(MouseEvent e){
		sceneManager.mousePressed(e.getX(), e.getY());
	}



}
