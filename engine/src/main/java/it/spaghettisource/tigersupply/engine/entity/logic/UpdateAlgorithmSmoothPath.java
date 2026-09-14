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
 * {@link UpdateAlgorithm} that drives the entity along a smooth path sampled from a natural cubic
 * spline.
 *
 * <p>{@link #init(DynaProperties)} builds a {@link NatCubicSpline} from the control points supplied
 * under the {@code ALGPRO_LIST_POINTS} key (a {@link List} of {@link Point}) and precomputes the
 * interpolated path; every frame {@link #updateLogic(Position, Speed, float)} snaps the entity to the
 * next precomputed point. Once the path is exhausted the entity stops moving.</p>
 *
 * @author Alessandro D'Ottavio
 *
 */
public class UpdateAlgorithmBspline extends AbstractUpdateAlgorithm {

	private Iterator<Point> points;	
		
	/**
	 * {@inheritDoc}
	 *
	 * <p>Advances the entity to the next precomputed spline point, if any; the speed and elapsed time
	 * are ignored because the motion is fully determined by the precomputed path.</p>
	 */
	public void updateLogic(Position position, Speed speed, float deltaSeconds) {
	
		if(points.hasNext()){
			Point point = points.next();
			position.setPosX(point.x);
			position.setPosY(point.y);
		}
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>Reads the control points from the {@code ALGPRO_LIST_POINTS} property, feeds them to a
	 * {@link NatCubicSpline} and precomputes the interpolated path traversed by {@code updateLogic}.</p>
	 */
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
