package it.spaghettisource.tigersupply.engine.entity.logic;

import static it.spaghettisource.tigersupply.engine.utils.StaticResources.ALGPRO_POINT;

import it.spaghettisource.tigersupply.engine.entity.Position;
import it.spaghettisource.tigersupply.engine.entity.Speed;
import it.spaghettisource.tigersupply.engine.utils.DynaProperties;

/**
 * {@link UpdateAlgorithm} that moves the entity in a straight line toward a fixed target
 * {@link Position} at constant speed (a direct shot).
 *
 * <p>On the first frame it orients the entity {@link Speed} toward the target, preserving its
 * magnitude; every frame it then integrates that {@link Speed} over the elapsed time. The travel
 * speed is therefore whatever the caller sets on the entity, not a property of the algorithm.
 * Configuration key (from {@code StaticResources}): {@code ALGPRO_POINT} for the target point.</p>
 *
 * @author Alessandro D'Ottavio
 *
 */
public class UpdateAlgoritmGoToPoint extends AbstractUpdateAlgorithm  {

	private Position targetPoint;	
	
	private boolean initialized = false;
	
	
	
	/**
	 * {@inheritDoc}
	 *
	 * <p>On the first invocation it orients the entity {@link Speed} toward the target while keeping
	 * its magnitude; subsequent invocations just integrate that {@link Speed} over the elapsed time.</p>
	 */
	@Override
	public void updateLogic(Position position, Speed speed, float deltaSeconds) {

		if(!initialized){
			float dx = targetPoint.getPosX() - position.getPosX();
			float dy = targetPoint.getPosY() - position.getPosY();
			float distance = (float)Math.hypot(dx, dy);

			if(distance > 0f){
				float magnitude = (float)Math.hypot(speed.getSpeedX(), speed.getSpeedY());
				speed.setSpeedX((dx / distance) * magnitude);
				speed.setSpeedY((dy / distance) * magnitude);
			}
			initialized = true;
		}
				
		position.increaseX(speed.getSpeedX()*deltaSeconds);
		position.increaseY(speed.getSpeedY()*deltaSeconds);

	}
	
	/**
	 * {@inheritDoc}
	 *
	 * <p>Reads the {@code ALGPRO_POINT} target position.</p>
	 */
	public void init(DynaProperties properties) {
		targetPoint = (Position)properties.getObject(ALGPRO_POINT);
		
	}	
			

}
