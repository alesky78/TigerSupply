package it.spaghettisource.tigersupply.engine.sprite;

import it.spaghettisource.tigersupply.engine.entity.Position;
import it.spaghettisource.tigersupply.engine.entity.Size;

import java.awt.Graphics2D;


/**
 * 
 * generic class for the spirtes
 * 
 * @author Alessandro D'Ottavio
 *
 */
public interface Sprite {
	

	public int getImageHeight();

	public int getImageWidth();	
	
	public void setAlpha(double alpha);
		
	/**
	 * update the sprite logic
	 * 
	 * @param deltaSeconds time spent before last call
	 */
	public void updateSprite(float deltaSeconds) throws Exception;


	/**
	 * render the sprite to this logic
	 * 
	 * @param dbg
	 * @throws Exception 
	 */
	public void renderSprite(Graphics2D dbg,Position position,Size size) throws Exception;
	

}
