package it.spaghettisource.tigersupply.engine.image.effect;

import it.spaghettisource.tigersupply.engine.entity.Position;
import it.spaghettisource.tigersupply.engine.entity.Size;
import it.spaghettisource.tigersupply.engine.sprite.SpriteColor;


import java.awt.image.BufferedImage;

public interface Filter {

	public BufferedImage filterImage(final BufferedImage image,Position pos,Size siz,SpriteColor col) throws Exception;
	
	
}
