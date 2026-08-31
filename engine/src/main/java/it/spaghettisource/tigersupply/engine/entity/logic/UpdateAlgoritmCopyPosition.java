package it.spaghettisource.tigersupply.engine.entity.logic;

import static it.spaghettisource.tigersupply.engine.utils.StaticResources.ALGPRO_DELTAX;
import static it.spaghettisource.tigersupply.engine.utils.StaticResources.ALGPRO_DELTAY;
import static it.spaghettisource.tigersupply.engine.utils.StaticResources.ALGPRO_POINT;
import it.spaghettisource.tigersupply.engine.entity.Position;
import it.spaghettisource.tigersupply.engine.entity.Speed;
import it.spaghettisource.tigersupply.engine.utils.DynaProperties;

/**
 * {@link UpdateAlgorithm} that pins the entity to another {@link Position}, offset by a fixed
 * {@code (deltaX, deltaY)} amount. It keeps the entity glued to a moving reference point.
 *
 * <p>Configuration keys (from {@code StaticResources}): {@code ALGPRO_DELTAX} and {@code ALGPRO_DELTAY}
 * for the offset, and {@code ALGPRO_POINT} for the reference {@link Position} to copy.</p>
 *
 * @author Alessandro D'Ottavio
 *
 */
public class UpdateAlgoritmCopyPosition extends AbstractUpdateAlgorithm {

	private Position copyPoint;	
	private int deltaX;
	private int deltaY;	

	
	/**
	 * {@inheritDoc}
	 *
	 * <p>Snaps the entity to the reference point plus the configured offset; the elapsed time and the
	 * entity speed are ignored.</p>
	 */
	@Override
	public void updateLogic(Position position, Speed speed, float deltaSeconds) {

		position.setPosX(copyPoint.getPosX()+deltaX);
		position.setPosY(copyPoint.getPosY()+deltaY);		
		
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>Reads the {@code ALGPRO_DELTAX}/{@code ALGPRO_DELTAY} offsets and the {@code ALGPRO_POINT}
	 * reference position.</p>
	 */
	public void init(DynaProperties properties) {
		deltaX = getInt(properties.getString(ALGPRO_DELTAX));
		deltaY = getInt(properties.getString(ALGPRO_DELTAY));
		copyPoint = (Position)properties.getObject(ALGPRO_POINT);
		
	}


	
}
