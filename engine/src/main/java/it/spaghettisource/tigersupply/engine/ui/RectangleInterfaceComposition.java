package it.spaghettisource.tigersupply.engine.ui;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;

public class RectangleInterfaceComposition extends AbstractInterfaceComposition {

	protected int x,y;	//x and y coordinate
	protected int w,h; //width and height 
	protected Paint paint;
	
	protected Paint original; //use to don't build and free in garbage	
	
	
	public RectangleInterfaceComposition(int x,int y,int w, int h, Paint paint){
		this.x = x;
		this.y = y;
		this.h = h;
		this.w = w;
		this.paint = paint;		
		this.area = new Rectangle(x, y, w, h);
	}


	protected void doRender(Graphics2D dbg)  throws Exception{
		original = dbg.getPaint();
		dbg.setPaint(paint);
		dbg.fillRoundRect(x, y, w, h, 15, 15);
		dbg.setPaint(original);		
	}


	 protected void doUpdate(float deltaTimeSeconds) throws Exception {}
	
}
