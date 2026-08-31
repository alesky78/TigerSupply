package it.spaghettisource.tigersupply.engine.entity.logic;

import it.spaghettisource.tigersupply.engine.entity.Position;
import it.spaghettisource.tigersupply.engine.entity.Speed;
import it.spaghettisource.tigersupply.engine.utils.DynaProperties;

/**
 * Strategy that computes how an {@link it.spaghettisource.tigersupply.engine.entity.Entity} moves on
 * each frame.
 *
 * <p>A concrete algorithm is configured once through {@link #init(DynaProperties)} and then invoked
 * once per frame by the owning entity via {@link #updateLogic(Position, Speed, float)}, which mutates
 * the entity {@link Position} in place (optionally reading its {@link Speed}). Instances are built by
 * {@link UpdateAlgorithmFactory}, or through the typed helpers in {@link UpdateAlgorithmFactoryWrapper}
 * for the common cases.</p>
 *
 * @author Alessandro D'Ottavio
 *
 */
public interface UpdateAlgorithm {

	/**
	 * Advances the entity by one frame, mutating its position in place.
	 *
	 * @param position the entity position to update
	 * @param speed the current entity speed, in pixel/second
	 * @param deltaSeconds elapsed time since the previous frame, in seconds
	 */
	public void updateLogic(Position position ,Speed speed, float deltaSeconds);	
	
	/**
	 * Configures this algorithm from the given properties, before its first use.
	 *
	 * @param properties the configuration parameters required by the concrete algorithm
	 */
	public void init(DynaProperties properties);
	
}
