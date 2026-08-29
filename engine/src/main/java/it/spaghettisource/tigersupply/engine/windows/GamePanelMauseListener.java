package it.spaghettisource.tigersupply.engine.windows;


import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import it.spaghettisource.tigersupply.engine.control.GameManager;

/**
 * 
 * @author Alessandro D'Ottavio
 *
 */
public class GamePanelMauseListener extends MouseAdapter {


	private GameManager game;

	public GamePanelMauseListener(GameManager game){
		this.game = game;
	}

	public void mousePressed(MouseEvent e){
		game.mousePress(e.getX(), e.getY());
	}



}
