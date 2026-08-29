package it.spaghettisource.tigersupply.engine.ui;

import it.spaghettisource.tigersupply.engine.ui.listener.MouseOutListener;
import it.spaghettisource.tigersupply.engine.ui.listener.MouseOverListener;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractInterfaceComposition implements UserInterface {

	List<UserInterface> componenets = new ArrayList<UserInterface>();
	UserInterface executeComponent;
	UserInterface mouseOverComponent;	

	protected Rectangle area;	//area of the container of components		

	public void addInterface(UserInterface component){
		componenets.add(component);
	}


	public void execute() {
		if(executeComponent!=null){
			executeComponent.execute();			
		}

	}

	public boolean containCoordinate(int x, int y) {
		for (UserInterface componenet : componenets) {	//verify one of the internal components contains coordinates
			if(componenet.containCoordinate(x, y)){
				executeComponent = componenet;
				return true;
			}		
		}
		
		if(area.contains(x, y)){	//if u are hire no component satisfy the point but is in the container
			executeComponent = null;
			return true;
		}
		
		return false;
	}

	public void mouseOver(int x, int y) {
		if(mouseOverComponent!=null){	//first verify if go out from a previous component that was over
			if(!mouseOverComponent.containCoordinate(x, y)){
				mouseOverComponent.mouseOut();
				mouseOverComponent = null;
			}
		}

		for (UserInterface componenet : componenets) {	//look if is goin on a component
			if(componenet.containCoordinate(x, y)){
				mouseOverComponent = componenet;
				mouseOverComponent.mouseOver(x, y);
			}		
		}
	}

	public void mouseOut() {
		if(mouseOverComponent!=null){	//frist verity if go out from a previous component that was over
			mouseOverComponent.mouseOut();
			mouseOverComponent = null;
		}
	}	

	public void render(Graphics2D dbg)  throws Exception {
		doRender(dbg);	//draw itself and after all the element that it manage
		for (UserInterface componenet : componenets) {
			componenet.render(dbg);
		}
	}

	public void update(float deltaTimeSeconds) throws Exception {
		doUpdate(deltaTimeSeconds);	//updatge itself and after all the element that it manage
		for (UserInterface componenet : componenets) {
			componenet.update(deltaTimeSeconds);
		}
	}	

	public void addMouseOverListener(MouseOverListener listener) {
	}


	public void addMouseOutListener(MouseOutListener listener) {	
	}	
	
	protected abstract void doUpdate(float deltaTimeSeconds)  throws Exception;


	protected abstract void doRender(Graphics2D dbg) throws Exception;

}
