package it.spaghettisource.tigersupply.engine.background;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 * implementation of a background that scroll at constant speed and fit the image with the size of the window
 * so deform the immage, select the size of the image possible as the resolution screen to have a good result
 * 
 * @author Alessandro D'Ottavio
 *
 */
public class BackGroundFitImage implements BackGround {

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
	 * 
	 * 
	 * @param image
	 * @param speedBackGround
	 * @param widthScreen
	 * @param heightScreen
	 * @param direction  r means image move to rigth else l means image move to left
	 */
	public BackGroundFitImage(BufferedImage image,float speedBackGround,int widthScreen,int heightScreen,boolean goToLeft){
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
	 * increments coordinate of the speed
	 */
	public void updateBackground(float deltaSeconds) {
		xCoordinateWindow = (xCoordinateWindow + speedBackGround) % widthScreen;
		xCoordinateCutImage = (int) ((widthScreen-xCoordinateWindow) * ration);

	}

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
