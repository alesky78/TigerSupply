package it.spaghettisource.tigersupply.engine.background;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 * Static background that stretches a single image to fill the whole window and never
 * scrolls.
 *
 * <p>The image is scaled to the screen resolution, so it is deformed whenever its
 * aspect ratio differs from the window's; choose an image sized close to the screen
 * resolution for a good result. This is the still counterpart of
 * {@link BackGroundFitImage}: {@link #updateBackground(float)} is a no-op and each
 * frame draws the same fitted image.
 *
 * @author Alessandro D'Ottavio
 *
 */
public class StaticBackGroundFitImage implements BackGround {

	protected BufferedImage image;	//image of the background


	protected int widthImage;  		//widh of the image
	protected int heightImage;  	//heigh of the image	
	protected int widthScreen;  	//widh of the window	
	protected int heightScreen;  	//heigh of the window

	/**
	 * Creates a static, window-fitting background from the given image.
	 *
	 * @param image        the background image; its width and height are cached at construction
	 * @param widthScreen  the window width in pixels, used as the fit target
	 * @param heightScreen the window height in pixels, used as the fit target
	 */
	public StaticBackGroundFitImage(BufferedImage image,int widthScreen,int heightScreen){
		this.image = image;
		this.widthImage = image.getWidth();
		this.heightImage = image.getHeight();
		this.widthScreen = widthScreen;
		this.heightScreen = heightScreen;
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>No-op: this background is static and holds no animation state.
	 */
	public void updateBackground(float deltaSeconds) {

	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>Draws the image once, stretched to cover the whole window.
	 */
	public void renderBackground(Graphics2D dbg) {

		dbg.drawImage(image,0,0,widthScreen,heightScreen,0,0,widthImage,heightImage,null);			

	}


}
