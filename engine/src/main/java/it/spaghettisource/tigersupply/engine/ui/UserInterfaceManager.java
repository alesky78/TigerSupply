package it.spaghettisource.tigersupply.engine.ui;

import java.awt.Graphics2D;
import java.awt.event.MouseEvent;

public class UserInterfaceManager {

	UserInterface composition;
	
	public void mousePressed(int x, int y) {
		if(composition.containCoordinate(x, y)){
			composition.execute();
		}
			
	}

	public void mouseMoved(MouseEvent event) {
		if(composition.containCoordinate(event.getX(), event.getY())){
			composition.mouseOver(event.getX(), event.getY());
		}else{
			composition.mouseOut();
		}
	}	

	public void setComposition(UserInterface composition) {
		this.composition = composition;
	}

	public void renderUserInterface(Graphics2D dbg) throws Exception{
		composition.render(dbg);
	}
	
	public void updateUserInterface(float deltaTimeSeconds) throws Exception{
		composition.update(deltaTimeSeconds);
	}
	
}
