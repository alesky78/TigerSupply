package it.spaghettisource.tigersupply.engine.background;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 * implementation of a static background that fit the image with the size of the window
 * so deform the immage, select the size of the image possible as the resolution screen to have a good result
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
	 * 
	 * 
	 * @param image
	 * @param speedBackGround
	 * @param widthScreen
	 * @param heightScreen
	 * @param direction  r means image move to rigth else l means image move to left
	 */
	public StaticBackGroundFitImage(BufferedImage image,int widthScreen,int heightScreen){
		this.image = image;
		this.widthImage = image.getWidth();
		this.heightImage = image.getHeight();
		this.widthScreen = widthScreen;
		this.heightScreen = heightScreen;
	}

	/**
	 * increments coordinate of the speed
	 */
	public void updateBackground(float deltaSeconds) {

	}

	public void renderBackground(Graphics2D dbg) {

		dbg.drawImage(image,0,0,widthScreen,heightScreen,0,0,widthImage,heightImage,null);			

	}


}
