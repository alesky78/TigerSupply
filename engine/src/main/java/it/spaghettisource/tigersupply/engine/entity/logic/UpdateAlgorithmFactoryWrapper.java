package it.spaghettisource.tigersupply.engine.entity.logic;

import java.awt.Point;
import java.util.List;

import static it.spaghettisource.tigersupply.engine.utils.StaticResources.*;
import it.spaghettisource.tigersupply.engine.entity.Entity;
import it.spaghettisource.tigersupply.engine.entity.Position;
import it.spaghettisource.tigersupply.engine.sprite.Sprite;
import it.spaghettisource.tigersupply.engine.utils.DynaProperties;

/**
 * Convenience wrapper over {@link UpdateAlgorithmFactory} that exposes one typed factory method per
 * {@link UpdateAlgorithm}, hiding the {@link DynaProperties} keys each algorithm expects.
 *
 * @author Alessandro D'Ottavio
 *
 */
public class UpdateAlgorithmFactoryWrapper {
	
	
	private UpdateAlgorithmFactoryWrapper(){}

	/**
	 * Creates a {@link UpdateAlgorithmDefault} (straight, constant-speed motion).
	 *
	 * @return the configured algorithm
	 * @throws Exception if the algorithm cannot be created
	 */
	public static UpdateAlgorithmDefault newDefault() throws Exception{
		return UpdateAlgorithmFactory.newInstance(UpdateAlgorithmDefault.class, null);
	}		
	
	/**
	 * Creates a {@link UpdateAlgorithmSmoothPath} that follows a smooth curve through the given control
	 * points.
	 *
	 * @param points the control points defining the path
	 * @return the configured algorithm
	 * @throws Exception if the algorithm cannot be created
	 */
	public static UpdateAlgorithmSmoothPath newSmoothPath(List<Point> points) throws Exception{
		DynaProperties properties = new DynaProperties();
		properties.setList(ALGPRO_LIST_POINTS, points);
		return UpdateAlgorithmFactory.newInstance(UpdateAlgorithmSmoothPath.class, properties);		
	}
	
	/**
	 * Creates a {@link UpdateAlgorithmLinearPath} that travels the given waypoints in straight lines at
	 * the entity reference speed.
	 *
	 * @param points the ordered waypoints defining the path
	 * @return the configured algorithm
	 * @throws Exception if the algorithm cannot be created
	 */
	public static UpdateAlgorithmLinearPath newLinearPath(List<Point> points) throws Exception{
		DynaProperties properties = new DynaProperties();
		properties.setList(ALGPRO_LIST_POINTS, points);
		return UpdateAlgorithmFactory.newInstance(UpdateAlgorithmLinearPath.class, properties);		
	}
	
	/**
	 * Creates a {@link UpdateAlgorithmSinusoidal} starting from angle {@code 0}.
	 *
	 * @param delta the oscillation amplitude (limits the max/min vertical offset reached)
	 * @param angleIncrement the angular speed, in degree/second
	 * @return the configured algorithm
	 * @throws Exception if the algorithm cannot be created
	 */
	public static UpdateAlgorithmSinusoidal newSinusoidal(float delta,float angleIncrement) throws Exception{
		DynaProperties properties = new DynaProperties();
		properties.setString(ALGPRO_DELTA, Float.toString(delta));
		properties.setString(ALGPRO_INCREMENT, Float.toString(angleIncrement));		
		return UpdateAlgorithmFactory.newInstance(UpdateAlgorithmSinusoidal.class, properties);	
	}	
	
	/**
	 * Creates a {@link UpdateAlgorithmSinusoidal} starting from a given angle.
	 *
	 * @param delta the oscillation amplitude (limits the max/min vertical offset reached)
	 * @param angleIncrementPerSecond the angular speed, in degree/second
	 * @param angleStart the initial angle, in degrees
	 * @return the configured algorithm
	 * @throws Exception if the algorithm cannot be created
	 */
	public static UpdateAlgorithmSinusoidal newSinusoidal(float delta,float angleIncrementPerSecond,float angleStart) throws Exception{
		DynaProperties properties = new DynaProperties();
		properties.setString(ALGPRO_DELTA, Float.toString(delta));
		properties.setString(ALGPRO_INCREMENT, Float.toString(angleIncrementPerSecond));
		properties.setString(ALGPRO_START, Float.toString(angleStart));				
		return UpdateAlgorithmFactory.newInstance(UpdateAlgorithmSinusoidal.class, properties);	
	}	
		
	/**
	 * Creates a {@link UpdateAlgoritmCopyPosition} that pins the entity to a reference point.
	 *
	 * @param deltax the horizontal offset from the reference point
	 * @param deltay the vertical offset from the reference point
	 * @param copyPoint the reference {@link Position} to follow
	 * @return the configured algorithm
	 * @throws Exception if the algorithm cannot be created
	 */
	public static UpdateAlgoritmCopyPosition newCopyPosition(int deltax,int deltay,Position copyPoint) throws Exception{
		DynaProperties properties = new DynaProperties();
		properties.setString(ALGPRO_DELTAX, Integer.toString(deltax));
		properties.setString(ALGPRO_DELTAY, Integer.toString(deltay));		
		properties.setObject(ALGPRO_POINT, copyPoint);
		return UpdateAlgorithmFactory.newInstance(UpdateAlgoritmCopyPosition.class, properties);
	}

	/**
	 * Creates a {@link UpdateAlgoritmFollowSprite} that chases the given target.
	 *
	 * @param target the sprite to follow
	 * @return the configured algorithm
	 * @throws Exception if the algorithm cannot be created
	 */
	public static UpdateAlgoritmFollowSprite newFollowSprite(Sprite target) throws Exception{
		DynaProperties properties = new DynaProperties();		
		properties.setObject(ALGPRO_SPRITE, target);
		return UpdateAlgorithmFactory.newInstance(UpdateAlgoritmFollowSprite.class, properties);
	}
	
	/**
	 * Creates a {@link UpdateAlgoritmGoToPoint} that moves toward a target point using the entity's
	 * own {@link it.spaghettisource.tigersupply.engine.entity.Speed} as the travel speed.
	 *
	 * @param copyPoint the target {@link Position} to reach
	 * @return the configured algorithm
	 * @throws Exception if the algorithm cannot be created
	 */
	public static UpdateAlgoritmGoToPoint newGoToPoint(Position copyPoint) throws Exception{
		DynaProperties properties = new DynaProperties();
		properties.setObject(ALGPRO_POINT, copyPoint);
		return UpdateAlgorithmFactory.newInstance(UpdateAlgoritmGoToPoint.class, properties);
	}	
	
	/**
	 * Creates a {@link UpdateAlgoritmGoToPointIncreasingSpeed} that accelerates toward a target point.
	 *
	 * @param maxSpeedx the initial maximum horizontal speed, in pixel/second
	 * @param maxSpeedy the initial maximum vertical speed, in pixel/second
	 * @param copyPoint the target {@link Position} to reach
	 * @return the configured algorithm
	 * @throws Exception if the algorithm cannot be created
	 */
	public static UpdateAlgoritmGoToPointIncreasingSpeed newGoToPointIncr(int maxSpeedx,int maxSpeedy,Position copyPoint) throws Exception{
		DynaProperties properties = new DynaProperties();
		properties.setString(ALGPRO_SPEEDX, Integer.toString(maxSpeedx));
		properties.setString(ALGPRO_SPEEDY, Integer.toString(maxSpeedy));		
		properties.setObject(ALGPRO_POINT, copyPoint);
		return UpdateAlgorithmFactory.newInstance(UpdateAlgoritmGoToPointIncreasingSpeed.class, properties);
	}		

	/**
	 * Creates a {@link UpdateAlgorithmHoming} that seeks a live target at a bounded turn rate.
	 *
	 * @param target the live {@link Entity} to home in on
	 * @param speed the constant travel speed, in pixel/second
	 * @param maxTurnDegPerSec the maximum turn rate, in degree/second
	 * @param seekSeconds how long the algorithm homes before losing the lock and flying straight, in seconds
	 * @return the configured algorithm
	 * @throws Exception if the algorithm cannot be created
	 */
	public static UpdateAlgorithmHoming newHoming(Entity target,float speed,float maxTurnDegPerSec,float seekSeconds) throws Exception{
		DynaProperties properties = new DynaProperties();
		properties.setObject(ALGPRO_SPRITE, target);
		properties.setString(ALGPRO_SPEED, Float.toString(speed));
		properties.setString(ALGPRO_TURN_RATE, Float.toString(maxTurnDegPerSec));
		properties.setString(ALGPRO_SEEK_TIME, Float.toString(seekSeconds));
		return UpdateAlgorithmFactory.newInstance(UpdateAlgorithmHoming.class, properties);
	}
		
}
