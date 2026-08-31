package it.spaghettisource.tigersupply.engine.background;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.image.BufferedImage;

/**
 * Static background that tiles a single image across the whole window and never scrolls.
 *
 * <p>Unlike {@link StaticBackGroundFitImage} the image is not stretched: it is repeated
 * with a {@link TexturePaint} to cover the playfield, so its native resolution is
 * preserved. This is the still counterpart of {@link ScrollingBackGroundTiledImage}: the paint is
 * built once at construction, {@link #updateBackground(float)} is a no-op, and each
 * frame fills the window with the same fixed tiling.
 *
 * @author Alessandro D'Ottavio
 *
 */
public class StaticBackGroundTiledImage implements BackGround {

	protected BufferedImage image;	//image of the background
	protected TexturePaint paint;
	protected Paint originalPaint;	

	protected int widthImage;  		//widh of the image
	protected int heightImage;  	//heigh of the image	
	protected int widthScreen;  	//widh of the window	
	protected int heightScreen;  	//heigh of the window

	/**
	 * Creates a static, tiled-texture background from the given image.
	 *
	 * <p>The repeating {@link TexturePaint} is built once here and reused on every frame.
	 *
	 * @param image        the image used as the repeating tile; its width and height are cached at construction
	 * @param widthScreen  the window width in pixels, used as the filled area
	 * @param heightScreen the window height in pixels, used as the filled area
	 */
	public StaticBackGroundTiledImage(BufferedImage image,int widthScreen,int heightScreen){
		this.image = image;
		this.widthImage = image.getWidth();
		this.heightImage = image.getHeight();
		this.widthScreen = widthScreen;
		this.heightScreen = heightScreen;
		this.paint =  new TexturePaint(image, new Rectangle(0, 0, widthImage, heightImage));
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
	 * <p>Saves the current paint, fills the whole window with the pre-built tiled
	 * texture, then restores the previous paint.
	 */
	public void renderBackground(Graphics2D dbg) {
		
		originalPaint = dbg.getPaint();
		dbg.setPaint(paint);
		dbg.fillRect(0, 0, widthScreen, heightScreen);
		dbg.setPaint(originalPaint);
			

	}


}
