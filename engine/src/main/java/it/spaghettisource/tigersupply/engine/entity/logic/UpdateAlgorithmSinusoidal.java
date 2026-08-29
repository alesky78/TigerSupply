package it.spaghettisource.tigersupply.engine.entity.logic;

import static it.spaghettisource.tigersupply.engine.utils.StaticResources.ALGPRO_DELTA;
import static it.spaghettisource.tigersupply.engine.utils.StaticResources.ALGPRO_INCREMENT;
import static it.spaghettisource.tigersupply.engine.utils.StaticResources.ALGPRO_START;

import it.spaghettisource.tigersupply.engine.entity.Position;
import it.spaghettisource.tigersupply.engine.entity.Speed;
import it.spaghettisource.tigersupply.engine.utils.DynaProperties;

/**
 * 
 * @author Alessandro D'Ottavio
 *
 */
public class UpdateAlgorithmSinusoidal  extends AbstractUpdateAlgorithm {

	private float delta;
	private float increment;//speed of increase of the angle   degree/second 		
	private float angle;

	@Override
	public void updateLogic(Position position, Speed speed, float deltaSeconds) {


		position.increaseX(deltaSeconds*speed.getSpeedX());

		//Y = y+ Sy*T + K( sin(angle + Sangle* T) - sin(angle)  ) 		
		position.increaseY((float) (deltaSeconds*speed.getSpeedY() + delta * ( Math.sin(Math.toRadians(angle + deltaSeconds*increment)) - Math.sin(Math.toRadians(angle) ))));
		angle = angle + deltaSeconds*increment;


	}

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
