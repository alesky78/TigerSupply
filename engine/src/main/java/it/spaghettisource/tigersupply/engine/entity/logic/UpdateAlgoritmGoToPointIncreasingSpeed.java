package it.spaghettisource.tigersupply.engine.entity.logic;

import static it.spaghettisource.tigersupply.engine.utils.StaticResources.ALGPRO_POINT;
import static it.spaghettisource.tigersupply.engine.utils.StaticResources.ALGPRO_ACCELERATION;

import it.spaghettisource.tigersupply.engine.entity.Position;
import it.spaghettisource.tigersupply.engine.entity.Speed;
import it.spaghettisource.tigersupply.engine.utils.DynaProperties;

/**
 * {@link UpdateAlgorithm} that moves the entity toward a fixed target {@link Position} while
 * accelerating over time.
 *
 * <p>It behaves like {@link UpdateAlgoritmGoToPoint} but multiplies the entity {@link Speed} by a small
 * fixed percentage on every frame, so the entity speeds up as it travels. The initial speed is
 * whatever the caller sets on the entity, not a property of the algorithm. Configuration keys (from
 * {@code StaticResources}): {@code ALGPRO_POINT} for the target point and {@code ALGPRO_ACCELERATION}
 * for the per-frame acceleration factor.</p>
 *
 * @author Alessandro D'Ottavio
 *
 */
public class UpdateAlgoritmGoToPointIncreasingSpeed  extends AbstractUpdateAlgorithm {

	private Position targetPoint;	
	
	private boolean initialized = false;
	
	private float acceleration = 0.003f;
	
	/**
	 * {@inheritDoc}
	 *
	 * <p>On the first invocation it orients the entity {@link Speed} toward the target while keeping
	 * its magnitude; on every frame it then increases that {@link Speed} by {@code acceleration}
	 * before integrating it over the elapsed time.</p>
	 */
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
		
		speed.setSpeedX(speed.getSpeedX() * (1 + acceleration));
		speed.setSpeedY(speed.getSpeedY() * (1 + acceleration));		
		
		position.increaseX(speed.getSpeedX()*deltaSeconds);
		position.increaseY(speed.getSpeedY()*deltaSeconds);

	}
	
	/**
	 * {@inheritDoc}
	 *
	 * <p>Reads the {@code ALGPRO_POINT} target position and the {@code ALGPRO_ACCELERATION} per-frame
	 * acceleration factor.</p>
	 */
	public void init(DynaProperties properties) {
		targetPoint = (Position)properties.getObject(ALGPRO_POINT);
		acceleration = properties.getFloat(ALGPRO_ACCELERATION);
		
	}			

}
