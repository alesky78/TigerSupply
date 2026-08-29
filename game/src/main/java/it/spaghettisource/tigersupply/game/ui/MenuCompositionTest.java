
package it.spaghettisource.tigersupply.game.ui;



import java.awt.Graphics2D;
import java.awt.Paint;

import it.spaghettisource.tigersupply.engine.ui.AbstractInterfaceComposition;

public class MenuCompositionTest extends AbstractInterfaceComposition {

	protected int[] xpoints,ypoints;
	protected int npoints;	

	protected Paint paint;

	protected Paint original; //use to don't build and free in garbage	

	public MenuCompositionTest(int x,int y,int w, int h, Paint paint){
		this.xpoints = new int[]{x, x, (int) (x+0.05*w), (int) (x+0.95*w), x+w, x+w, (int) (x+0.975*w),(int) (x+0.975*w),(int) (x+0.95*w),(int) (x+0.05*w),(int) (x+0.025*w),(int) (x+0.025*w)};
		this.ypoints = new int[]{(int) (y+0.2*h), (int) (y+0.05*h), y, y, (int) (y+0.05*h),(int) (y+0.2*h),(int) (y+0.2*h),(int) (y+0.15*h),(int) (y+0.1*h),(int) (y+0.1*h),(int) (y+0.15*h),(int) (y+0.2*h)};
		this.npoints = 12;	
		this.paint = paint;		
	}	


	protected void doRender(Graphics2D dbg)  throws Exception{
		original = dbg.getPaint();
		dbg.setPaint(paint);
		dbg.fillPolygon(xpoints, ypoints, npoints);
		dbg.setPaint(original);		
	}


	protected void doUpdate(float deltaTimeSeconds) throws Exception {}


}
