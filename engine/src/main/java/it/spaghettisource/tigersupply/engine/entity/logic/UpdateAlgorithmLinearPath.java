package it.spaghettisource.tigersupply.engine.entity.logic;

import static it.spaghettisource.tigersupply.engine.utils.StaticResources.ALGPRO_LIST_POINTS;

import it.spaghettisource.tigersupply.engine.entity.Position;
import it.spaghettisource.tigersupply.engine.entity.Speed;
import it.spaghettisource.tigersupply.engine.utils.DynaProperties;

import java.awt.Point;
import java.util.Iterator;
import java.util.List;

/**
 * {@link UpdateAlgorithm} that drives the entity along a piecewise-straight path, travelling from one
 * waypoint to the next in a straight line at the entity reference speed.
 *
 * <p>Unlike {@link UpdateAlgorithmBspline}, which smooths the waypoints into a spline and snaps the
 * entity to precomputed points ignoring the speed, this algorithm keeps each segment straight and moves
 * the entity at a constant pace: {@link #init(DynaProperties)} reads the ordered list of waypoints
 * supplied under the {@code ALGPRO_LIST_POINTS} key (a {@link List} of {@link Point}) and targets the
 * first one, then every frame {@link #updateLogic(Position, Speed, float)} advances the entity toward the
 * current waypoint by a step derived from the magnitude of the reference {@link Speed}. Once a waypoint
 * is reached the next one becomes the target; when the last waypoint has been reached the entity stops
 * moving.</p>
 *
 * @author Alessandro D'Ottavio
 *
 */
public class UpdateAlgorithmLinearPath extends AbstractUpdateAlgorithm {

	/** Ordered waypoints still to be reached. */
	private Iterator<Point> waypoints;

	/** Waypoint the entity is currently heading to, or {@code null} once the path is exhausted. */
	private Point currentTarget;

	/** Magnitude of the reference speed, in pixel/second, derived on the first frame. */
	private float referenceSpeed;

	/** Whether {@link #referenceSpeed} still has to be derived from the entity speed. */
	private boolean computeReferenceSpeed = true;

	/**
	 * {@inheritDoc}
	 *
	 * <p>On the first invocation it derives the constant travel speed from the magnitude of the
	 * reference {@link Speed}. Each frame it moves the entity in a straight line toward the current
	 * waypoint by that speed integrated over the elapsed time; when the entity gets within one step of
	 * the waypoint it snaps onto it and targets the next one. Once every waypoint has been reached the
	 * entity stops.</p>
	 */
	public void updateLogic(Position position, Speed speed, float deltaSeconds) {

		if(computeReferenceSpeed){
			referenceSpeed = (float) Math.sqrt(speed.getSpeedX()*speed.getSpeedX() + speed.getSpeedY()*speed.getSpeedY());
			computeReferenceSpeed = false;
		}

		if(currentTarget == null){
			return;	//path exhausted, the entity stops moving
		}

		float step = referenceSpeed * deltaSeconds;
		float deltaX = currentTarget.x - position.getPosX();
		float deltaY = currentTarget.y - position.getPosY();
		float distance = (float) Math.sqrt(deltaX*deltaX + deltaY*deltaY);

		if(distance <= step){
			//reach the current waypoint and start heading to the next one
			position.setPosX(currentTarget.x);
			position.setPosY(currentTarget.y);
			currentTarget = waypoints.hasNext() ? waypoints.next() : null;
		}else{
			//move in a straight line toward the current waypoint
			float ratio = step / distance;
			position.increaseX(deltaX * ratio);
			position.increaseY(deltaY * ratio);
		}
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>Reads the ordered waypoints from the {@code ALGPRO_LIST_POINTS} property and targets the first
	 * one.</p>
	 */
	@SuppressWarnings("unchecked")
	public void init(DynaProperties properties){
		List<Point> configPoint = (List<Point>) properties.getList(ALGPRO_LIST_POINTS);
		waypoints = configPoint.iterator();
		currentTarget = waypoints.hasNext() ? waypoints.next() : null;
	}

}
