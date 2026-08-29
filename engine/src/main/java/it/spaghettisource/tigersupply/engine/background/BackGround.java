package it.spaghettisource.tigersupply.engine.background;

import java.awt.Graphics2D;

public interface BackGround {
	
	/**
	 * update the infromation related to the background
	 * 
	 * @param deltaSeconds
	 */
	public void updateBackground(float deltaSeconds);
	
	
	/**
	 * render the background in this graph
	 * 
	 * @param dbg
	 */
	public void renderBackground(Graphics2D dbg);	
	
	
}
