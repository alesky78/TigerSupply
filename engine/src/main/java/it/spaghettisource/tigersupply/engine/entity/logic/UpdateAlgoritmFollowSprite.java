package it.spaghettisource.tigersupply.engine.entity.logic;

import static it.spaghettisource.tigersupply.engine.utils.StaticResources.ALGPRO_SPRITE;

import it.spaghettisource.tigersupply.engine.entity.Entity;
import it.spaghettisource.tigersupply.engine.entity.Position;
import it.spaghettisource.tigersupply.engine.entity.Speed;
import it.spaghettisource.tigersupply.engine.utils.DynaProperties;

/**
 * 
 * 
 * @author Alessandro D'Ottavio
 *
 */
public class UpdateAlgoritmFollowSprite extends AbstractUpdateAlgorithm {

	Entity targetSprite;
	float xSpeed = 40;
	float ySpeed = 20;		

	@Override
	public void updateLogic(Position position, Speed speed, float deltaSeconds) {

		if(position.getPosX() > targetSprite.getXposition()){
			position.increaseX(-xSpeed*deltaSeconds);;
		}else{
			position.increaseX(xSpeed*deltaSeconds);
		}
		
		if(position.getPosY() > targetSprite.getYposition()){
			position.increaseY(-ySpeed*deltaSeconds);
		}else{
			position.increaseY(ySpeed*deltaSeconds);
		}

	}

	public void init(DynaProperties properties) {
		targetSprite = (Entity)properties.getObject(ALGPRO_SPRITE);
		
	}

	

	
	

		

}
