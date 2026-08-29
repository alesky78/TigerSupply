package it.spaghettisource.tigersupply.engine.sprite;

import it.spaghettisource.tigersupply.engine.image.ImagesPlayer;

/**
 * implementation of the sprite that use the image player to show the animation
 * 
 * 
 * @author Alessandro D'Ottavio
 *
 */
public class ImagePlayerSprite extends AbstractSprite {

	protected ImagesPlayer player;
	protected String originalAlias;		
	
	public void updateSprite(float deltaSeconds) throws Exception {		
		player.updateTick();
		image = player.getCurrentImage();		
		imageAlias = originalAlias+player.getCurrentPosition();

	}
	
	public boolean isSequenceCompleted(){
		return player.atSequenceEnd();
	}

	public void setPlayer(ImagesPlayer player) {
		this.player = player;
	}
	
	public void setImageAlias(String imageAlias) {
		this.imageAlias = imageAlias;
		this.originalAlias = imageAlias;
	}	
	
	
}
