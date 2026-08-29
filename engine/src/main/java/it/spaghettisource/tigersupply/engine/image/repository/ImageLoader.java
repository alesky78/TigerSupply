package it.spaghettisource.tigersupply.engine.image.repository;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.net.URL;

import javax.imageio.ImageIO;


/**
 * 
 * utility to load image
 * 
 * @author Alessandro D'Ottavio
 *
 */
public class ImageLoader {
	
	public BufferedImage loadImage(String urlResource){
		URL url;
		BufferedImage sourceImage = null;
		BufferedImage copyImage = null;

		url = this.getClass().getClassLoader().getResource(urlResource);
		try {
			sourceImage = ImageIO.read(url);
		} catch (Exception e) {
			System.out.println("error loading resource:"+urlResource);
			e.printStackTrace();
			System.exit(1);
		}		
		// create an accelerated image of the right size to store our sprite in
		GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
		copyImage = gc.createCompatibleImage(sourceImage.getWidth(),sourceImage.getHeight(),Transparency.BITMASK);

		// draw our source image into the accelerated image
		copyImage.getGraphics().drawImage(sourceImage,0,0,null);
		return copyImage;
		
	}

}
