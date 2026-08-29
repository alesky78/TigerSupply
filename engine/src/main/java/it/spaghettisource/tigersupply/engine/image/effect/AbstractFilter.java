package it.spaghettisource.tigersupply.engine.image.effect;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public abstract class AbstractFilter implements Filter {


	public BufferedImage copyImage(BufferedImage image){
		BufferedImage sourceBI = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = (Graphics2D) sourceBI.getGraphics();
		g.drawImage(image, 0, 0, null);
		return sourceBI;
	}

}
