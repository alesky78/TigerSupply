package it.spaghettisource.tigersupply.engine.image.finaleffect;

import java.awt.Graphics2D;


/**
 * the final effect is used to make a final draw over the image that will be send to the screen
 * for example screen darkness or lightness, rain etc...
 * 
 * 
 * @author Alessandro D'Ottavio
 *
 */
public interface FinalEffect {

	
	/**
	 * verify if this effect is active or not
	 * 
	 * @return true if the effect is active
	 */
	public boolean isActive();
	
	/**
	 * update the effect logic
	 * 
	 * @param deltaSeconds time spent before last call
	 */
	public void updateEffect(float deltaSeconds) throws Exception;


	/**
	 * render the effect
	 * 
	 * @param dbg
	 * @throws Exception 
	 */
	public void renderEffect(Graphics2D dbg,int screenWidth,int screenHeight) throws Exception;
		
	
	
}
