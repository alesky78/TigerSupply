package it.spaghettisource.tigersupply.engine.image.effect;

import it.spaghettisource.tigersupply.engine.entity.Position;
import it.spaghettisource.tigersupply.engine.entity.Size;
import it.spaghettisource.tigersupply.engine.sprite.SpriteColor;

import java.awt.image.BufferedImage;
import java.awt.image.LookupOp;
import java.awt.image.LookupTable;
import java.awt.image.ShortLookupTable;

public class Brighten extends AbstractLookUpOpFilter {

	public Brighten(){
		super();
	}

	public BufferedImage filterImage(BufferedImage image, Position pos,Size siz, SpriteColor col) throws Exception {

		if(col.getRChannel() == 1 && col.getGChannel() == 1 && col.getBChannel() == 1)
			return image;
		
		BufferedImage source = copyImage(image);
		
		short[] R = calculateArrayByPercentage(col.getRChannel());
		short[] G = calculateArrayByPercentage(col.getGChannel());
		short[] B = calculateArrayByPercentage(col.getBChannel());		
		
		
		short[][] table;
	
		table = new short[4][];
		table[0] = R;			//RED
		table[1] = G;			//GREEN
		table[2] = B;			//BLUE
		table[3] = noChange;    //ALPHA
	
		LookupTable lkTable = new ShortLookupTable(0, table);
		LookupOp operation = new LookupOp(lkTable, null);

		return operation.filter(source, null);
	}		

}
