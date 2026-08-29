package it.spaghettisource.tigersupply.engine.entity.logic;

import it.spaghettisource.tigersupply.engine.entity.Position;
import it.spaghettisource.tigersupply.engine.entity.Speed;
import it.spaghettisource.tigersupply.engine.utils.DynaProperties;

/**
 * 
 * 
 * @author Alessandro D'Ottavio
 *
 */
public interface UpdateAlgorithm {

	public void updateLogic(Position position ,Speed speed, float deltaSeconds);	
	
	public void init(DynaProperties properties);
	
}
