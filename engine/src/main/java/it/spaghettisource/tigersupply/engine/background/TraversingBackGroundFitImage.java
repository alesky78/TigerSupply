package it.spaghettisource.tigersupply.engine.background;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 * One-shot background that stretches a single image to the window size and makes it
 * cross the whole playfield exactly once, entering from one edge and exiting from the
 * opposite one.
 *
 * <p>The image is scaled to the screen resolution, so it is deformed whenever its
 * aspect ratio differs from the window's; choose an image sized close to the screen
 * resolution for a good result. It behaves like a screen-sized object that slides in
 * from off-screen, fully covers the window when centred, then keeps moving until it
 * has completely left on the far side. Once the single traversal is complete
 * {@link #updateBackground(float)} becomes a no-op and the image is no longer drawn.
 *
 * @author Alessandro D'Ottavio
 *
 */
public class TraversingBackGroundFitImage implements BackGround {

	protected BufferedImage image;	//image of the background

	protected int widthImage;  		//widh of the image
	protected int heightImage;  	//heigh of the image
	protected int widthScreen;  	//widh of the window
	protected int heightScreen;  	//heigh of the window

	protected float speedBackGround;	//horizontal travel speed, in pixel per second
	protected boolean goToLeft = true;	//direction of the travel
	protected float xPosition;			//current left edge of the image on the screen
	protected boolean finished = false;	//true once the single traversal is over

	/**
	 * Creates a one-shot, window-fitting background from the given image.
	 *
	 * <p>The image is placed completely off-screen on the entering edge; if
	 * {@code goToLeft} is {@code true} it enters from the right and exits on the left,
	 * otherwise it enters from the left and exits on the right.
	 *
	 * @param image           the background image; its width and height are cached at construction
	 * @param speedBackGround  the horizontal travel speed, in pixels per second (expected to be positive)
	 * @param widthScreen      the window width in pixels, used both as the fit target and the travel distance
	 * @param heightScreen     the window height in pixels, used as the fit target
	 * @param goToLeft         {@code true} to enter from the right and move left, {@code false} to enter from the left and move right
	 */
	public TraversingBackGroundFitImage(BufferedImage image,float speedBackGround,int widthScreen,int heightScreen,boolean goToLeft){
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
	 * <p>Advances the image by {@code speedBackGround * deltaSeconds} in the chosen
	 * direction until it has completely left the opposite edge. From that point on the
	 * traversal is considered finished and further calls do nothing.
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
	 * <p>Draws the image stretched into a screen-sized area whose left edge is at the
	 * current horizontal position, so only the portion currently overlapping the window
	 * is visible. Nothing is drawn once the traversal is finished.
	 */
	public void renderBackground(Graphics2D dbg) {
		if(finished){
			return;
		}
		dbg.drawImage(image,(int)xPosition,0,(int)xPosition+widthScreen,heightScreen,
					  0,0,widthImage,heightImage,null);
	}

}
