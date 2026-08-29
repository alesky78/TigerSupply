package it.spaghettisource.tigersupply.engine.ui;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;

public class RectangleButton extends AbstractButton {

	protected int x,y;	//x and y coordinate
	protected int w,h; //width and height 
	protected Paint paint;
	
	protected Paint original; //use to don't build and free in garbage
	
	public RectangleButton(int x,int y,int w, int h, Paint paint){
		this.x = x;
		this.y = y;
		this.h = h;
		this.w = w;
		this.paint = paint;
		area = new Rectangle(x, y, w, h);
		
	}
	
	public void render(Graphics2D dbg) throws Exception {
		original = dbg.getPaint();
		dbg.setPaint(paint);
		dbg.fillRoundRect(x, y, w, h, 15, 15);
		dbg.setPaint(original);
		
	}

	public void execute() {}	
	
	public void update(float deltaTimeSeconds) throws Exception {}

	protected void doMouseOver(int x, int y) {}

	protected void doMouseOut() {}

}
