package it.spaghettisource.tigersupply.engine.sprite;


import java.awt.image.BufferedImage;

import it.spaghettisource.tigersupply.engine.image.ImagesPlayer;
import it.spaghettisource.tigersupply.engine.image.ImagesPlayerCenterControlled;
import it.spaghettisource.tigersupply.engine.image.effect.EffectManager;
import it.spaghettisource.tigersupply.engine.image.repository.ImageRepositoryManager;


/**
 * Factory for any kind of Sprite, the sprite should be created only from this class
 * 
 * @author Alessandro D'Ottavio
 *
 */
public class SpriteFactory {

	private static SpriteFactory instance;
	private ImageRepositoryManager repository;

	private SpriteFactory() throws Exception{
		this.repository = ImageRepositoryManager.getInstance();
	}

	public static void init() throws Exception{
		if(instance==null){
			synchronized (SpriteFactory.class) {
				if(instance==null){
					instance = new SpriteFactory();
				}
			}
		}
	}

	public static SpriteFactory getInstance() throws Exception{
		if(instance==null){
			Exception ex = new Exception("SpriteFactory class must by initialized before to use it");
			throw ex;
		}
		return instance;
	}


	public ImageSingleSprite createImageSingleSprite(String aliasImage){
		ImageSingleSprite sprite = new ImageSingleSprite();
		populateSpriteObject(sprite,repository.getSingleImage(aliasImage),aliasImage);	
		return sprite;
	}

	public ImagePlayerSprite createImagePlayerSprite(float animationPeriod, float sequenceDuration, boolean repeatImage, String aliasImage){
		ImagePlayerSprite sprite = new ImagePlayerSprite();
		BufferedImage[] images = repository.getLoopImage(aliasImage);
		populateSpriteObject(sprite,images[0],aliasImage+"0");
		ImagesPlayer player = new ImagesPlayer(animationPeriod, sequenceDuration, repeatImage, images);
		sprite.setPlayer(player);
		return sprite;
	}

	public ImagePlayerCenterControllerSprite createImagePlayerCenterControllerSprite(float animationPeriod, float sequenceDuration, int centralPosition, String aliasImage){
		ImagePlayerCenterControllerSprite sprite = new ImagePlayerCenterControllerSprite();
		BufferedImage[] images = repository.getLoopImage(aliasImage);
		populateSpriteObject(sprite,images[centralPosition],aliasImage+centralPosition);
		ImagesPlayerCenterControlled player  = new ImagesPlayerCenterControlled(animationPeriod, sequenceDuration, centralPosition, images);
		sprite.setPlayer(player);
		return sprite;

	}


	protected void populateSpriteObject(AbstractSprite sprite, BufferedImage spriteImage,String imageAlias) {

		SpriteColor color = new SpriteColor();
		sprite.setColor(color);

		sprite.setImageAlias(imageAlias);
		
		sprite.setImage(spriteImage);

		//register all default filters to the sprite
		sprite.addRegisterFilter(EffectManager.getInstance().getAllRegisteredFilter());

	}

}
