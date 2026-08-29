package it.spaghettisource.tigersupply.engine.sprite;

import it.spaghettisource.tigersupply.engine.image.ImagesPlayerCenterControlled;

/**
 * implementation of the sprite that use the image player with a {@link ImagesPlayerCenterControlled} to manage the animation
 * 
 * @author Alessandro D'Ottavio
 *
 */
public class ImagePlayerCenterControllerSprite extends AbstractSprite {

	protected ImagesPlayerCenterControlled player;
	protected String originalAlias;		
	
	public void updateSprite(float deltaSeconds) throws Exception  {
		player.updateTick();
		image = player.getCurrentImage();
		imageAlias = originalAlias+player.getCurrentPosition();
	}

	public void setPlayer(ImagesPlayerCenterControlled player) {
		this.player = player;
	}
	
	public void goToCentralAnimation(){
		player.goToCentralAnimation();
	}
	
	public void goToUpAnimation(){
		player.goToUpAnimation();
	}

	public void goToDownAnimation(){
		player.goToDownAnimation();
	}		
	
	public void setImageAlias(String imageAlias) {
		this.imageAlias = imageAlias;
		this.originalAlias = imageAlias;
	}		
	


}
