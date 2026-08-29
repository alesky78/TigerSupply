package it.spaghettisource.tigersupply.engine.path;

import java.awt.Point;
import java.awt.Polygon;

/**
 * 
 * 
 * 
 * @author Alessandro D'Ottavio
 *
 */
public class ControlCurve {

	protected Polygon pts;

	public ControlCurve() {
		pts = new Polygon();
	}

	/**
	 * add a point to the control curve
	 * 
	 * @param x
	 * @param y
	 */
	public void addPoint(int x, int y) {
		pts.addPoint(x,y);
	}

	/**
	 * 
	 * @param x
	 * @param y
	 */
	public void addPoint(Point point) {
		pts.addPoint(point.x,point.y);
	}  


}