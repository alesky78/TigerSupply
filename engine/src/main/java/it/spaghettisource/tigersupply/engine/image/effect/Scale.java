package it.spaghettisource.tigersupply.engine.image.effect;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;

import it.spaghettisource.tigersupply.engine.entity.Position;
import it.spaghettisource.tigersupply.engine.entity.Size;
import it.spaghettisource.tigersupply.engine.sprite.SpriteColor;

public class Scale extends AbstractFilter {

	@Override
	public BufferedImage filterImage(BufferedImage image, Position pos,Size siz, SpriteColor col) throws Exception {
		
		if(siz.getScale() == 1)
			return image;
		
		BufferedImage sourceBI = copyImage(image);

		AffineTransform at = new AffineTransform();

		// rotate angle degrees around image center
		at.scale(siz.getScale(), siz.getScale());

		// instantiate and apply affine transformation filter
		BufferedImageOp bio;
		bio = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);

		return bio.filter(sourceBI, null);
	}

}
