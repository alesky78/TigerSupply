package it.spaghettisource.tigersupply.engine.entity.logic;

import static it.spaghettisource.tigersupply.engine.utils.StaticResources.ALGPRO_SPEEDX;
import static it.spaghettisource.tigersupply.engine.utils.StaticResources.ALGPRO_SPEEDY;
import static it.spaghettisource.tigersupply.engine.utils.StaticResources.ALGPRO_POINT;

import it.spaghettisource.tigersupply.engine.entity.Position;
import it.spaghettisource.tigersupply.engine.entity.Speed;
import it.spaghettisource.tigersupply.engine.utils.DynaProperties;

/**
 * {@link UpdateAlgorithm} that moves the entity toward a fixed target {@link Position} while
 * accelerating over time.
 *
 * <p>It behaves like {@link UpdateAlgoritmGoToPoint} but multiplies the per-axis speeds by a small
 * fixed percentage on every frame, so the entity speeds up as it travels. Configuration keys (from
 * {@code StaticResources}): {@code ALGPRO_SPEEDX}/{@code ALGPRO_SPEEDY} for the initial maximum speeds
 * and {@code ALGPRO_POINT} for the target point.</p>
 *
 * @author Alessandro D'Ottavio
 *
 */
public class UpdateAlgoritmGoToPointIncreasingSpeed  extends AbstractUpdateAlgorithm {

	private Position targetPoint;	

	private float maxXspeed = 150;
	private float maxYspeed = 150;	
	
	private float newXspeed = 0;
	private float newYspeed = 0;		
	
	private boolean calculate = true;
	
	private float increasingPercentage = 0.003f;
	
	/**
	 * {@inheritDoc}
	 *
	 * <p>On the first invocation it computes the per-axis speeds needed to reach the target together;
	 * on every frame it then increases both speeds by {@code increasingPercentage} before integrating
	 * them over the elapsed time.</p>
	 */
	public void updateLogic(Position position, Speed speed, float deltaSeconds) {

		if(calculate){
			float secondToReachByX = Math.abs((targetPoint.getPosX() - position.getPosX())/speed.getSpeedX());
			float calculatedSpeedY = Math.abs((position.getPosY() - targetPoint.getPosY())/secondToReachByX);

			if(position.getPosX()>targetPoint.getPosX()){
				newXspeed = -1*maxXspeed;
			}else{
				newXspeed = maxXspeed;
			}
				
			if(calculatedSpeedY>maxYspeed){
				calculatedSpeedY = maxYspeed;
			}
			
			if(position.getPosY()>targetPoint.getPosY()){
				newYspeed= -1*calculatedSpeedY;
			}else{
				newYspeed= calculatedSpeedY;
			}
			calculate = false;
		}
		
		newXspeed+=newXspeed*increasingPercentage;
		newYspeed+=newYspeed*increasingPercentage;		
		
		position.increaseX(newXspeed*deltaSeconds);
		position.increaseY(newYspeed*deltaSeconds);

	}
	
	/**
	 * {@inheritDoc}
	 *
	 * <p>Reads the {@code ALGPRO_SPEEDX}/{@code ALGPRO_SPEEDY} initial maximum speeds and the
	 * {@code ALGPRO_POINT} target position.</p>
	 */
	public void init(DynaProperties properties) {
		maxXspeed = getInt(properties.getString(ALGPRO_SPEEDX));
		maxYspeed = getInt(properties.getString(ALGPRO_SPEEDY));
		targetPoint = (Position)properties.getObject(ALGPRO_POINT);
		
	}			

}
