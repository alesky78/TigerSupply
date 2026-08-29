package it.spaghettisource.tigersupply.engine.image.effect;

import it.spaghettisource.tigersupply.engine.entity.Position;
import it.spaghettisource.tigersupply.engine.entity.Size;
import it.spaghettisource.tigersupply.engine.sprite.SpriteColor;

import java.awt.image.BufferedImage;
import java.awt.image.LookupOp;
import java.awt.image.LookupTable;
import java.awt.image.ShortLookupTable;

/**
 * 
 * manage the alpha cannel of the image
 * 
 * @author DOttavio
 *
 */
public class Transparent extends AbstractLookUpOpFilter {

	public Transparent(){
		super();
	}

	public BufferedImage filterImage(BufferedImage image, Position pos,Size siz, SpriteColor col) throws Exception {

		if(col.getAlphaChannel() == 1)
			return image;
		
		BufferedImage source = copyImage(image);
		
		short[] alpha = calculateArrayByPercentage(col.getAlphaChannel());
		
		short[][] table;
	
		table = new short[4][];
		table[0] = noChange;			//RED
		table[1] = noChange;			//GREEN
		table[2] = noChange;			//BLUE
		table[3] = alpha;    			//ALPHA
	
		LookupTable lkTable = new ShortLookupTable(0, table);
		LookupOp operation = new LookupOp(lkTable, null);

		return operation.filter(source, null);
	}

	//altro modo per gestire la trasparenza
//	Composite originalComposite = dbg.getComposite();  // backup the old composite 
//	dbg.setComposite( AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
//	dbg.setComposite(originalComposite);
		

}
