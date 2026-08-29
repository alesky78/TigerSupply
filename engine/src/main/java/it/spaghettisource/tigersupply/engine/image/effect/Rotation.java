package it.spaghettisource.tigersupply.engine.image.effect;

import it.spaghettisource.tigersupply.engine.entity.Position;
import it.spaghettisource.tigersupply.engine.entity.Size;
import it.spaghettisource.tigersupply.engine.sprite.SpriteColor;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;

public class Rotation extends AbstractFilter {

	@Override
	public BufferedImage filterImage(BufferedImage image, Position pos,Size siz, SpriteColor col) throws Exception {
					
		if(pos.getAngle() == 0)
			return image;
		
		BufferedImage sourceBI = copyImage(image);
		
		// rotate angle degrees around image center
		AffineTransform at = new AffineTransform();		
		at.rotate(pos.getAngleRadiant() , sourceBI.getWidth() / 2.0, sourceBI.getHeight() / 2.0);

		AffineTransform translationTransform;
		translationTransform = findTranslation(at,pos.getAngle(), sourceBI);
		at.preConcatenate(translationTransform);

		// instantiate and apply affine transformation filter
		BufferedImageOp bio;
		bio = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);

		return bio.filter(sourceBI, null);
	}

	/*
	 * find proper translations to keep rotated image correctly displayed
	 */
	private AffineTransform findTranslation(AffineTransform at, double angle,BufferedImage bi) {
		Point2D p2din, p2dout;
		AffineTransform tat = new AffineTransform();

		if(angle<=90){
//			p2din = new Point2D.Double(0.0, 0.0);
//			p2dout = at.transform(p2din, null);
//			double ytrans = p2dout.getY();
			p2din = new Point2D.Double(0, bi.getHeight());
			p2dout = at.transform(p2din, null);
			double xtrans = p2dout.getX();
			tat.translate(-xtrans, -0);
		}else if(angle<=180){
//			p2din = new Point2D.Double(0, bi.getHeight());
//			p2dout = at.transform(p2din, null);
//			double ytrans = p2dout.getY();
			p2din = new Point2D.Double(bi.getWidth(), bi.getHeight());
			p2dout = at.transform(p2din, null);
			double xtrans = p2dout.getX();
			tat.translate(-xtrans, -0);
		}else if(angle<=270){
//			p2din = new Point2D.Double(bi.getWidth(),bi.getHeight());
//			p2dout = at.transform(p2din, null);
//			double ytrans = p2dout.getY();
			p2din = new Point2D.Double(bi.getWidth(),0);
			p2dout = at.transform(p2din, null);
			double xtrans = p2dout.getX();
			tat.translate(-xtrans, -0);
		}else if(angle<=360){
//			p2din = new Point2D.Double(bi.getWidth(),0);
//			p2dout = at.transform(p2din, null);
//			double ytrans = p2dout.getY();
			p2din = new Point2D.Double(0,0);
			p2dout = at.transform(p2din, null);
			double xtrans = p2dout.getX();
			tat.translate(-xtrans, -0);
		}
		return tat;
	}
	
	
	
}
