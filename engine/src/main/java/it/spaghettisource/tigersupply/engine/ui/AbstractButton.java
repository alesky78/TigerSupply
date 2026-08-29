package it.spaghettisource.tigersupply.engine.ui;



import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import it.spaghettisource.tigersupply.engine.ui.listener.MouseOutListener;
import it.spaghettisource.tigersupply.engine.ui.listener.MouseOverListener;

public  abstract class AbstractButton implements UserInterface {
	
	protected Rectangle area;	
	private List<MouseOverListener> mouseOverListeners = new ArrayList<MouseOverListener>();
	private List<MouseOutListener> mouseOutListeners = new ArrayList<MouseOutListener>();	
	
	

	public boolean containCoordinate(int x, int y) {
		return area.contains(x, y);
	}


	public void addMouseOverListener(MouseOverListener listener) {
		mouseOverListeners.add(listener);
	}


	public void addMouseOutListener(MouseOutListener listener) {
		mouseOutListeners.add(listener);
	}

	public void mouseOver(int x, int y) {
		doMouseOver(x,y);
		for (MouseOverListener listener : mouseOverListeners) {
			listener.onMouseOver();
		}
	}

	protected abstract void doMouseOver(int x, int y);


	public void mouseOut() {
		doMouseOut();
		for (MouseOutListener listener : mouseOutListeners) {
			listener.onMouseOut();
		}
	}


	protected abstract void doMouseOut();

}
