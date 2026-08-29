package it.spaghettisource.tigersupply.engine.entity.logic;

import static it.spaghettisource.tigersupply.engine.utils.StaticResources.ALGPRO_LIST_POINTS;
import it.spaghettisource.tigersupply.engine.entity.Position;
import it.spaghettisource.tigersupply.engine.entity.Speed;
import it.spaghettisource.tigersupply.engine.path.NatCubicSpline;
import it.spaghettisource.tigersupply.engine.utils.DynaProperties;

import java.awt.Point;
import java.util.Iterator;
import java.util.List;

/**
 * the use of this class required a {@link DynaProperties} configured in this way
 * 1 list properties with key {@link StaticResources#ALGPRO_LIST_POINTS} valorized with a List of {@link Point}
 * 
 * 
 * @author Alessandro D'Ottavio
 *
 */
public class UpdateAlgorithmBspline extends AbstractUpdateAlgorithm {

	private Iterator<Point> points;	
		
	public void updateLogic(Position position, Speed speed, float deltaSeconds) {
	
		if(points.hasNext()){
			Point point = points.next();
			position.setPosX(point.x);
			position.setPosY(point.y);
		}
	}

	@SuppressWarnings("unchecked")
	public void init(DynaProperties properties){
		NatCubicSpline path = new NatCubicSpline();
		List<Point> configPoint = (List<Point>) properties.getList(ALGPRO_LIST_POINTS);
		
		for (Point point : configPoint) {
			path.addPoint(point);
		}
		
		points = (path.generatePoints()).iterator();
		
	}	


}
