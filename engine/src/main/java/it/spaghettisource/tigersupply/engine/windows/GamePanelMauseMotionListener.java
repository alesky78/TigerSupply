package it.spaghettisource.tigersupply.engine.windows;



import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import it.spaghettisource.tigersupply.engine.control.GameManager;

public class GamePanelMauseMotionListener extends MouseMotionAdapter {

	private GameManager game;

	public GamePanelMauseMotionListener(GameManager game){
		this.game = game;

	}	
	
	  public void mouseMoved(MouseEvent event) {
		  game.mouseMove(event);
	  }
	
	
	
	
}
