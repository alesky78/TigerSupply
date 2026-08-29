package it.spaghettisource.tigersupply.engine.control;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;

public abstract class AbstractGameManagerJPanel implements GameManager {

	protected JPanel panel;
	protected ApplicationContext context;

	protected Game actualGame;	


	public Game getActualGame() throws Exception {
		return actualGame;
	}

	public void mousePress(int x, int y) {
		actualGame.mousePress(x, y);
	}
	
	public void mouseMove(MouseEvent event){
		actualGame.mouseMove(event);
	}

	public void keyPressed(KeyEvent event) {
		actualGame.keyPressed(event);			
	}		

	public void keyReleased(KeyEvent event) {
		actualGame.keyReleased(event);
	}
	

}
