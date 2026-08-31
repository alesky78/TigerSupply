package it.spaghettisource.tigersupply.engine.entity.logic;

import it.spaghettisource.tigersupply.engine.utils.DynaProperties;
import it.spaghettisource.tigersupply.engine.entity.Position;
import it.spaghettisource.tigersupply.engine.entity.Speed;


/**
 * Default {@link UpdateAlgorithm}: straight, constant-speed motion obtained by integrating the entity
 * {@link Speed} over the elapsed time. It requires no configuration.
 *
 * @author Alessandro D'Ottavio
 *
 */
public class UpdateAlgorithmDefault extends AbstractUpdateAlgorithm {

	/**
	 * {@inheritDoc}
	 *
	 * <p>Moves the entity by {@code speed * deltaSeconds} along both axes.</p>
	 */
	public void updateLogic(Position position, Speed speed, float deltaSeconds) {
		position.increaseX(deltaSeconds*speed.getSpeedX());
		position.increaseY(deltaSeconds*speed.getSpeedY());
		
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>This algorithm has no configurable parameter, so the properties are ignored.</p>
	 */
	public void init(DynaProperties properties) {
		
	}



}
