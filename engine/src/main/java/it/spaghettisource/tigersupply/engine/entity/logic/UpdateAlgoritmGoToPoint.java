package it.spaghettisource.tigersupply.engine.entity.logic;

import static it.spaghettisource.tigersupply.engine.utils.StaticResources.ALGPRO_SPEEDX;
import static it.spaghettisource.tigersupply.engine.utils.StaticResources.ALGPRO_SPEEDY;
import static it.spaghettisource.tigersupply.engine.utils.StaticResources.ALGPRO_POINT;

import it.spaghettisource.tigersupply.engine.entity.Position;
import it.spaghettisource.tigersupply.engine.entity.Speed;
import it.spaghettisource.tigersupply.engine.utils.DynaProperties;

/**
 * {@link UpdateAlgorithm} that moves the entity in a straight line toward a fixed target
 * {@link Position} at constant speed.
 *
 * <p>On the first frame it derives the per-axis speeds from the entity's initial {@link Speed} so that
 * both axes reach the target at the same time, capping each axis at its configured maximum. After that
 * the entity travels at those fixed speeds. Configuration keys (from {@code StaticResources}):
 * {@code ALGPRO_SPEEDX}/{@code ALGPRO_SPEEDY} for the maximum speeds and {@code ALGPRO_POINT} for the
 * target point.</p>
 *
 * @author Alessandro D'Ottavio
 *
 */
public class UpdateAlgoritmGoToPoint extends AbstractUpdateAlgorithm  {

	private Position targetPoint;	

	private float maxXspeed = 150;
	private float maxYspeed = 150;	
	
	private float newXspeed = 0;
	private float newYspeed = 0;		
	
	private boolean calculate = true;
	
	
	
	/**
	 * {@inheritDoc}
	 *
	 * <p>On the first invocation it computes the per-axis speeds needed to reach the target together
	 * (clamping the vertical speed to its maximum); subsequent invocations just integrate those speeds
	 * over the elapsed time.</p>
	 */
	@Override
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
				
		position.increaseX(newXspeed*deltaSeconds);
		position.increaseY(newYspeed*deltaSeconds);

	}
	
	/**
	 * {@inheritDoc}
	 *
	 * <p>Reads the {@code ALGPRO_SPEEDX}/{@code ALGPRO_SPEEDY} maximum speeds and the
	 * {@code ALGPRO_POINT} target position.</p>
	 */
	public void init(DynaProperties properties) {
		maxXspeed = getInt(properties.getString(ALGPRO_SPEEDX));
		maxYspeed = getInt(properties.getString(ALGPRO_SPEEDY));
		targetPoint = (Position)properties.getObject(ALGPRO_POINT);
		
	}	
			

}
