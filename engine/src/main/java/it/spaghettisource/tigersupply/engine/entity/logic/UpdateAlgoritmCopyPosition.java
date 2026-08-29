package it.spaghettisource.tigersupply.engine.entity.logic;

import static it.spaghettisource.tigersupply.engine.utils.StaticResources.ALGPRO_DELTAX;
import static it.spaghettisource.tigersupply.engine.utils.StaticResources.ALGPRO_DELTAY;
import static it.spaghettisource.tigersupply.engine.utils.StaticResources.ALGPRO_POINT;
import it.spaghettisource.tigersupply.engine.entity.Position;
import it.spaghettisource.tigersupply.engine.entity.Speed;
import it.spaghettisource.tigersupply.engine.utils.DynaProperties;

/**
 * 
 * @author Alessandro D'Ottavio
 *
 */
public class UpdateAlgoritmCopyPosition extends AbstractUpdateAlgorithm {

	private Position copyPoint;	
	private int deltaX;
	private int deltaY;	

	
	@Override
	public void updateLogic(Position position, Speed speed, float deltaSeconds) {

		position.setPosX(copyPoint.getPosX()+deltaX);
		position.setPosY(copyPoint.getPosY()+deltaY);		
		
	}

	public void init(DynaProperties properties) {
		deltaX = getInt(properties.getString(ALGPRO_DELTAX));
		deltaY = getInt(properties.getString(ALGPRO_DELTAY));
		copyPoint = (Position)properties.getObject(ALGPRO_POINT);
		
	}


	
}
