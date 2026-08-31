package it.spaghettisource.tigersupply.engine.entity.logic;

import static it.spaghettisource.tigersupply.engine.utils.StaticResources.ALGPRO_DELTA;
import static it.spaghettisource.tigersupply.engine.utils.StaticResources.ALGPRO_INCREMENT;
import static it.spaghettisource.tigersupply.engine.utils.StaticResources.ALGPRO_START;

import it.spaghettisource.tigersupply.engine.entity.Position;
import it.spaghettisource.tigersupply.engine.entity.Speed;
import it.spaghettisource.tigersupply.engine.utils.DynaProperties;

/**
 * {@link UpdateAlgorithm} that overlays a sinusoidal oscillation on the entity's vertical motion while
 * it keeps moving horizontally at its {@link Speed}.
 *
 * <p>The vertical position follows
 * {@code Y = Y + Sy*T + delta*(sin(angle + increment*T) - sin(angle))}, where {@code delta} is the
 * oscillation amplitude and {@code increment} the angular speed in degree/second. Configuration keys
 * (from {@code StaticResources}): {@code ALGPRO_DELTA} (amplitude), {@code ALGPRO_INCREMENT} (angular
 * speed) and the optional {@code ALGPRO_START} (initial angle, defaults to {@code 0}).</p>
 *
 * @author Alessandro D'Ottavio
 *
 */
public class UpdateAlgorithmSinusoidal  extends AbstractUpdateAlgorithm {

	private float delta;
	private float increment;//speed of increase of the angle   degree/second 		
	private float angle;

	/**
	 * {@inheritDoc}
	 *
	 * <p>Advances the horizontal position at constant speed and adds a sine-based vertical offset,
	 * then advances the internal angle by {@code increment * deltaSeconds}.</p>
	 */
	@Override
	public void updateLogic(Position position, Speed speed, float deltaSeconds) {


		position.increaseX(deltaSeconds*speed.getSpeedX());

		//Y = y+ Sy*T + K( sin(angle + Sangle* T) - sin(angle)  ) 		
		position.increaseY((float) (deltaSeconds*speed.getSpeedY() + delta * ( Math.sin(Math.toRadians(angle + deltaSeconds*increment)) - Math.sin(Math.toRadians(angle) ))));
		angle = angle + deltaSeconds*increment;


	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>Reads {@code ALGPRO_DELTA} (amplitude) and {@code ALGPRO_INCREMENT} (angular speed), and the
	 * optional {@code ALGPRO_START} initial angle (defaults to {@code 0} when absent).</p>
	 */
	public void init(DynaProperties properties) {
		delta = getFloat(properties.getString(ALGPRO_DELTA));
		increment = getFloat(properties.getString(ALGPRO_INCREMENT));
		if(properties.contains(ALGPRO_START)){
			angle = getFloat(properties.getString(ALGPRO_START));
		}else{
			angle = 0;
		}



	}


}
