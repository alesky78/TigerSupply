package it.spaghettisource.tigersupply.engine.background;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.image.BufferedImage;

/**
 * One-shot background that tiles a single image at its native resolution inside a
 * screen-sized block and makes that block cross the whole playfield exactly once,
 * entering from one edge and exiting from the opposite one.
 *
 * <p>Unlike {@link TraversingBackGroundFitImage} the image is not stretched: it is
 * repeated with a {@link TexturePaint} to fill a screen-sized area, so its native
 * resolution is preserved. The filled area behaves like a screen-sized object that
 * slides in from off-screen, fully covers the window when centred, then keeps moving
 * until it has completely left on the far side. Once the single traversal is complete
 * {@link #updateBackground(float)} becomes a no-op and nothing is drawn any more.
 *
 * @author Alessandro D'Ottavio
 *
 */
public class TraversingBackGroundTiledImage implements BackGround {

	protected BufferedImage image;	//image of the background
	protected Paint originalPaint;

	protected int widthImage;  		//widh of the image
	protected int heightImage;  	//heigh of the image
	protected int widthScreen;  	//widh of the window
	protected int heightScreen;  	//heigh of the window

	protected float speedBackGround;	//horizontal travel speed, in pixel per second
	protected boolean goToLeft = true;	//direction of the travel
	protected float xPosition;			//current left edge of the tiled block on the screen
	protected boolean finished = false;	//true once the single traversal is over

	/**
	 * Creates a one-shot, tiled-texture background from the given image.
	 *
	 * <p>The tiled block is placed completely off-screen on the entering edge; if
	 * {@code goToLeft} is {@code true} it enters from the right and exits on the left,
	 * otherwise it enters from the left and exits on the right.
	 *
	 * @param image           the image used as the repeating tile; its width and height are cached at construction
	 * @param speedBackGround  the horizontal travel speed, in pixels per second (expected to be positive)
	 * @param widthScreen      the window width in pixels, used as the block size and the travel distance
	 * @param heightScreen     the window height in pixels, used as the block size
	 * @param goToLeft         {@code true} to enter from the right and move left, {@code false} to enter from the left and move right
	 */
	public TraversingBackGroundTiledImage(BufferedImage image,float speedBackGround,int widthScreen,int heightScreen,boolean goToLeft){
		this.image = image;
		this.widthImage = image.getWidth();
		this.heightImage = image.getHeight();
		this.widthScreen = widthScreen;
		this.heightScreen = heightScreen;
		this.speedBackGround = speedBackGround;
		this.goToLeft = goToLeft;
		this.xPosition = goToLeft ? widthScreen : -widthScreen;
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>Advances the tiled block by {@code speedBackGround * deltaSeconds} in the
	 * chosen direction until it has completely left the opposite edge. From that point
	 * on the traversal is considered finished and further calls do nothing.
	 */
	public void updateBackground(float deltaSeconds) {
		if(finished){
			return;
		}
		if(goToLeft){
			xPosition = xPosition - speedBackGround*deltaSeconds;
			if(xPosition <= -widthScreen){
				xPosition = -widthScreen;
				finished = true;
			}
		}else{
			xPosition = xPosition + speedBackGround*deltaSeconds;
			if(xPosition >= widthScreen){
				xPosition = widthScreen;
				finished = true;
			}
		}
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>Saves the current paint, fills a screen-sized block whose left edge is at the
	 * current horizontal position with a {@link TexturePaint} anchored to that same
	 * edge, then restores the previous paint. Nothing is drawn once the traversal is
	 * finished.
	 */
	public void renderBackground(Graphics2D dbg) {
		if(finished){
			return;
		}
		originalPaint = dbg.getPaint();
		dbg.setPaint(new TexturePaint(image, new Rectangle((int)xPosition, 0, widthImage, heightImage)));
		dbg.fillRect((int)xPosition, 0, widthScreen, heightScreen);
		dbg.setPaint(originalPaint);
	}

}
