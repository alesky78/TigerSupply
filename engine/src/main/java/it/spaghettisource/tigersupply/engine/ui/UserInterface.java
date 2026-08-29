package it.spaghettisource.tigersupply.engine.ui;

import it.spaghettisource.tigersupply.engine.ui.listener.MouseOutListener;
import it.spaghettisource.tigersupply.engine.ui.listener.MouseOverListener;

import java.awt.Graphics2D;


/**
 * basic class for all the user interfaces
 * 
 * @author Alessandro D'Ottavio
 *
 */
public interface UserInterface {

	public void execute();
	
	/**
	 * return true if the x and y coordinate are one point inside this component
	 *  
	 * @param x
	 * @param y
	 * @return
	 */
	public boolean containCoordinate(int x, int y);
	
	/**
	 * this method is called when the mouse in on thi component
	 */
	public void mouseOver(int x, int y);
	
	/**
	 * this method is called when the mouse go out from this component
	 */
	public void mouseOut();
		
	
	/**
	 * rupdate this component
	 * @param dbg
	 * @throws Exception
	 */
	public void update(float deltaTimeSeconds) throws Exception;
		
	
	/**
	 * render this component
	 * @param dbg
	 * @throws Exception
	 */
	public void render(Graphics2D dbg) throws Exception;
	
	
	public void addMouseOverListener(MouseOverListener listener);
	
	public void addMouseOutListener(MouseOutListener listener);	
	
	
}
