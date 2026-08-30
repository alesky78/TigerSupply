package it.spaghettisource.tigersupply.engine.windows;


import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import it.spaghettisource.tigersupply.engine.control.SceneHost;

/**
 * 
 * @author Alessandro D'Ottavio
 *
 */
public class GamePanelMauseListener extends MouseAdapter {


	private SceneHost sceneHost;

	public GamePanelMauseListener(SceneHost sceneHost){
		this.sceneHost = sceneHost;
	}

	public void mousePressed(MouseEvent e){
		sceneHost.mousePressed(e.getX(), e.getY());
	}



}
