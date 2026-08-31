package it.spaghettisource.tigersupply.engine.background;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.image.BufferedImage;

/**
 * Scrolling background that tiles a single image across the whole window and slides
 * the tiling horizontally at a constant speed.
 *
 * <p>Unlike {@link ScrollingBackGroundFitImage} the image is not stretched: it is repeated
 * with a {@link TexturePaint} to cover the playfield, so its native resolution is
 * preserved. Each frame the tiling origin is shifted by {@code speedBackGround} scaled
 * by the elapsed time, producing a seamless scroll suited to repeatable textures.
 *
 * @author Alessandro D'Ottavio
 *
 */
public class ScrollingBackGroundTiledImage implements BackGround {

	protected BufferedImage image;	//image of the background
	protected Paint originalPaint;	

	protected int widthImage;  		//widh of the image
	protected int heightImage;  	//heigh of the image	
	protected int widthScreen;  	//widh of the window	
	protected int heightScreen;  	//heigh of the window
	
	protected boolean goToLeft = true;	//direction of the scroll
	protected float speedBackGround;		//background increasing speed	
	
	protected float xPosition = 0;

	/**
	 * Creates a scrolling, tiled-texture background from the given image.
	 *
	 * @param image           the image used as the repeating tile; its width and height are cached at construction
	 * @param speedBackGround  the horizontal scroll speed, in pixels per second
	 * @param widthScreen      the window width in pixels, used as the filled area
	 * @param heightScreen     the window height in pixels, used as the filled area
	 * @param goToLeft         {@code true} to scroll the tiling towards the left, {@code false} to scroll it towards the right
	 */
	public ScrollingBackGroundTiledImage(BufferedImage image,float speedBackGround,int widthScreen,int heightScreen,boolean goToLeft){
		this.image = image;
		this.widthImage = image.getWidth();
		this.heightImage = image.getHeight();
		this.widthScreen = widthScreen;
		this.heightScreen = heightScreen;
		this.speedBackGround = speedBackGround;
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>Moves the horizontal tiling origin by {@code speedBackGround * deltaSeconds},
	 * decreasing it when scrolling left and increasing it when scrolling right.
	 */
	public void updateBackground(float deltaSeconds) {
		if(!goToLeft){
			xPosition = xPosition+speedBackGround*deltaSeconds;			
		}else{
			xPosition = xPosition-speedBackGround*deltaSeconds;			
		}

	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>Saves the current paint, fills the whole window with a {@link TexturePaint}
	 * anchored at the current horizontal offset, then restores the previous paint.
	 */
	public void renderBackground(Graphics2D dbg) {
		
		originalPaint = dbg.getPaint();
		dbg.setPaint(new TexturePaint(image, new Rectangle(0+(int)(xPosition), 0, widthImage, heightImage)));
		dbg.fillRect(0, 0, widthScreen, heightScreen);
		dbg.setPaint(originalPaint);
			

	}


}
