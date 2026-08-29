package it.spaghettisource.tigersupply.engine.entity.logic;

import it.spaghettisource.tigersupply.engine.utils.DynaProperties;
import it.spaghettisource.tigersupply.engine.entity.Position;
import it.spaghettisource.tigersupply.engine.entity.Speed;


public class UpdateAlgorithmDefault extends AbstractUpdateAlgorithm {

	public void updateLogic(Position position, Speed speed, float deltaSeconds) {
		position.increaseX(deltaSeconds*speed.getSpeedX());
		position.increaseY(deltaSeconds*speed.getSpeedY());
		
	}

	public void init(DynaProperties properties) {
		
	}



}
