package it.spaghettisource.tigersupply.engine.background;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 * Scrolling background that stretches a single image to fill the whole window and
 * loops it horizontally at a constant speed.
 *
 * <p>The image is scaled to the screen resolution, so it is deformed whenever its
 * aspect ratio differs from the window's; choose an image sized close to the screen
 * resolution for a good result. Each frame the horizontal offset advances by
 * {@code speedBackGround} and wraps around the screen width, so the image repeats
 * seamlessly while it scrolls either to the left or to the right.
 *
 * @author Alessandro D'Ottavio
 *
 */
public class ScrollingBackGroundFitImage implements BackGround {

	protected BufferedImage image;	//image of the background

	protected float speedBackGround;		//background increasing speed
	protected float xCoordinateWindow;  	//coordinate where to start the draw head of image in the screen
	protected float xCoordinateCutImage; 	//coordinate where to cat the image	


	protected int widthImage;  		//widh of the image
	protected int heightImage;  	//heigh of the image	
	protected int widthScreen;  	//widh of the window	
	protected int heightScreen;  	//heigh of the window
	protected float ration;			//ration between screen widh/image widh

	protected boolean goToLeft = true;	//direction of the scroll

	/**
	 * Creates a scrolling, window-fitting background from the given image.
	 *
	 * @param image           the background image; its width and height are cached at construction
	 * @param speedBackGround  the horizontal scroll speed, in pixels advanced per frame
	 * @param widthScreen      the window width in pixels, used both as the fit target and the wrap-around length
	 * @param heightScreen     the window height in pixels, used as the fit target
	 * @param goToLeft         {@code true} to scroll the image towards the left, {@code false} to scroll it towards the right
	 */
	public ScrollingBackGroundFitImage(BufferedImage image,float speedBackGround,int widthScreen,int heightScreen,boolean goToLeft){
		this.image = image;
		this.widthImage = image.getWidth();
		this.heightImage = image.getHeight();
		this.speedBackGround = speedBackGround;
		this.widthScreen = widthScreen;
		this.heightScreen = heightScreen;
		this.ration = (float)widthImage/(float)widthScreen;
		this.goToLeft = goToLeft;
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>Advances the on-screen scroll offset by {@code speedBackGround}, wrapping it
	 * modulo the screen width, and recomputes the matching cut point on the source
	 * image so the head and tail can be drawn as a seamless loop.
	 */
	public void updateBackground(float deltaSeconds) {
		xCoordinateWindow = (xCoordinateWindow + speedBackGround) % widthScreen;
		xCoordinateCutImage = (int) ((widthScreen-xCoordinateWindow) * ration);

	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>When the offset is zero the image is drawn once, stretched to the window.
	 * Otherwise it is drawn in two pieces (a head and a tail) whose split point depends
	 * on the scroll direction, so the wrapped image appears continuous across the seam.
	 */
	public void renderBackground(Graphics2D dbg) {
		if(xCoordinateWindow == 0){
			dbg.drawImage(image,0,0,widthScreen,heightScreen,
						  0,0,widthImage,heightImage,null);			
		}else{
			if(!goToLeft){
				//draw the head
				dbg.drawImage(image,(int)xCoordinateWindow,0,widthScreen,heightScreen,
						      0,0,(int)xCoordinateCutImage,heightImage,null);
				//draw the tail
				dbg.drawImage(image,0,0,(int)xCoordinateWindow,heightScreen,			
						     (int)xCoordinateCutImage,0,widthImage,heightImage,null);
			}else if(goToLeft){
				//draw the head
				dbg.drawImage(image,0,0,(int)(widthScreen-xCoordinateWindow),heightScreen,
							  (int)(widthImage-xCoordinateCutImage),0,widthImage,heightImage,null);
				//draw the tail
				dbg.drawImage(image,(int)(widthScreen-xCoordinateWindow),0,widthScreen,heightScreen,
							  0,0,(int)(widthImage-xCoordinateCutImage),heightImage,null);
			}
		}

	}


}
