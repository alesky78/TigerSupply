package it.spaghettisource.tigersupply.engine.background;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.image.BufferedImage;

/**
 * implementation of a static background that fit the image with the size of the window
 * so deform the immage, select the size of the image possible as the resolution screen to have a good result
 * 
 * @author Alessandro D'Ottavio
 *
 */
public class StaticBackGroundTexture implements BackGround {

	protected BufferedImage image;	//image of the background
	protected TexturePaint paint;
	protected Paint originalPaint;	

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
	public StaticBackGroundTexture(BufferedImage image,int widthScreen,int heightScreen){
		this.image = image;
		this.widthImage = image.getWidth();
		this.heightImage = image.getHeight();
		this.widthScreen = widthScreen;
		this.heightScreen = heightScreen;
		this.paint =  new TexturePaint(image, new Rectangle(0, 0, widthImage, heightImage));
	}

	/**
	 * increments coordinate of the speed
	 */
	public void updateBackground(float deltaSeconds) { 
	}

	public void renderBackground(Graphics2D dbg) {
		
		originalPaint = dbg.getPaint();
		dbg.setPaint(paint);
		dbg.fillRect(0, 0, widthScreen, heightScreen);
		dbg.setPaint(originalPaint);
			

	}


}
