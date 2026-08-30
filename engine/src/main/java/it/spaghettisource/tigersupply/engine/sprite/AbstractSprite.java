package it.spaghettisource.tigersupply.engine.sprite;



import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import it.spaghettisource.tigersupply.engine.entity.Position;
import it.spaghettisource.tigersupply.engine.entity.Size;
import it.spaghettisource.tigersupply.engine.image.effect.EffectManager;
import it.spaghettisource.tigersupply.engine.image.repository.ImageRepositoryManager;


/**
 * basic implementation of a sprite
 * 
 * @author Alessandro D'Ottavio
 *
 */
public abstract class AbstractSprite implements Sprite {

	protected SpriteColor color;			
	protected BufferedImage image;	//sprite image
	/**
	 * name of the image, used to chace the images, look at {@linkAbstractSprite#renderSprite()} 
	 * the animation sprites that have more that one image must return different alias for each image
	 */
	protected String imageAlias;	

	protected List<String> filters = new ArrayList<String>();	//filter configured for this sprite

	public void setImageAlias(String imageAlias) {
		this.imageAlias = imageAlias;
	}

	public void addRegisterFilter(List<String> filters){
		this.filters = filters;
	}	

	public void addRegisterFilterByName(String filterName){
		filters.add(filterName);
	}

	public boolean removeRegisterFilterByName(String filterName){
		return  filters.remove(filterName);
	}	



	public void renderSprite(Graphics2D dbg,Position position,Size size) throws Exception {

		//memory saves: create a key for the image with the transformation and save in the repository
		//TODO la creazione della chiave potrebbe essere ottimizzata invece di creazione sempre nuove chiavi
		StringBuffer bf = new StringBuffer();
		bf.append(imageAlias);
		bf.append(position.getAngle());
		bf.append(size.getScale());
		bf.append(color.getAlphaChannel()+color.getRChannel()+color.getGChannel()+color.getBChannel());

		BufferedImage filtered = ImageRepositoryManager.getInstance().getVolatileImage(bf.toString());
		if(filtered!=null){
			dbg.drawImage(filtered, (int)(position.getPosX() - filtered.getWidth()/2), (int)(position.getPosY() - size.getHeight()/2), null);			
		}else{
			filtered = EffectManager.getInstance().chain(filters,image, position, size, color);
			dbg.drawImage(filtered, (int)(position.getPosX() - filtered.getWidth()/2), (int)(position.getPosY() - size.getHeight()/2), null);
			ImageRepositoryManager.getInstance().addVolatileImage(bf.toString(), filtered);
		}

	}

	public void setImage(BufferedImage image) {
		this.image = image;
	}

	public void setColor(SpriteColor color) {
		this.color = color;
	}

	public void setAlpha(double alpha){
		color.setAlphaChannel(alpha);
	}

	public int getImageHeight() {
		return image.getHeight();
	}

	public int getImageWidth() {
		return image.getWidth();
	}


}
