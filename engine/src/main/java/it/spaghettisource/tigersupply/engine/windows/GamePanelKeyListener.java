package it.spaghettisource.tigersupply.engine.windows;



import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import it.spaghettisource.tigersupply.engine.control.GameManager;

public class GamePanelKeyListener extends KeyAdapter {

	
	private GameManager game;

	public GamePanelKeyListener(GameManager game){
		this.game = game;
	}
	
	public void keyPressed(KeyEvent event){
		game.keyPressed(event);
	}
	
	public void keyReleased(KeyEvent event){
		game.keyReleased(event);
		
	}
	
	
	
}
