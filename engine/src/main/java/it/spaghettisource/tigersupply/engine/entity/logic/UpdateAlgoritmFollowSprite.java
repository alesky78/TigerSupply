package it.spaghettisource.tigersupply.engine.entity.logic;

import static it.spaghettisource.tigersupply.engine.utils.StaticResources.ALGPRO_SPRITE;

import it.spaghettisource.tigersupply.engine.entity.Entity;
import it.spaghettisource.tigersupply.engine.entity.Position;
import it.spaghettisource.tigersupply.engine.entity.Speed;
import it.spaghettisource.tigersupply.engine.utils.DynaProperties;

/**
 * {@link UpdateAlgorithm} that chases a target {@link Entity}, nudging the entity toward the target's
 * current position at a fixed per-axis speed on every frame.
 *
 * <p>Configuration key (from {@code StaticResources}): {@code ALGPRO_SPRITE} holds the {@link Entity}
 * to follow.</p>
 *
 * @author Alessandro D'Ottavio
 *
 */
public class UpdateAlgoritmFollowSprite extends AbstractUpdateAlgorithm {

	Entity targetSprite;
	float xSpeed = 40;
	float ySpeed = 20;		

	/**
	 * {@inheritDoc}
	 *
	 * <p>Steps the entity toward the target position by {@code xSpeed}/{@code ySpeed} scaled by the
	 * elapsed time, moving in the direction of the target on each axis.</p>
	 */
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

	/**
	 * {@inheritDoc}
	 *
	 * <p>Reads the {@code ALGPRO_SPRITE} target entity to follow.</p>
	 */
	public void init(DynaProperties properties) {
		targetSprite = (Entity)properties.getObject(ALGPRO_SPRITE);
		
	}

	

	
	

		

}
