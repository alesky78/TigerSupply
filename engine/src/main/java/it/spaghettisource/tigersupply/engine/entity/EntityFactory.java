package it.spaghettisource.tigersupply.engine.entity;

import it.spaghettisource.tigersupply.engine.control.GameContext;
import it.spaghettisource.tigersupply.engine.entity.logic.UpdateAlgorithm;
import it.spaghettisource.tigersupply.engine.entity.logic.UpdateAlgorithmDefault;
import it.spaghettisource.tigersupply.engine.sprite.Sprite;
import it.spaghettisource.tigersupply.engine.utils.ClassFactory;


/**
 * Singleton factory that builds and wires every {@link AbstractEntity} of the game.
 *
 * <p>Entities should be instantiated only through this factory: it reflectively creates the concrete
 * entity (by {@link Class} or by fully-qualified class name), then populates its {@link Position},
 * {@link Speed}, {@link Size}, {@link Sprite} and {@link UpdateAlgorithm} in a consistent way and
 * injects the shared {@link GameContext}. When no algorithm is supplied an {@link UpdateAlgorithmDefault}
 * is used.</p>
 *
 * <p>The factory must be bootstrapped once with {@link #init(GameContext)} before {@link #getInstance()}
 * can be used.</p>
 *
 * @author Alessandro D'Ottavio
 *
 */
public class EntityFactory {

	private static EntityFactory instance;
	private GameContext context;

	private EntityFactory(GameContext context) throws Exception{
		this.context = context;
	}

	/**
	 * Initializes the singleton with the shared game context. Subsequent calls are ignored.
	 *
	 * @param context the {@link GameContext} shared by every entity built by this factory
	 * @throws Exception if the factory cannot be created
	 */
	public static void init(GameContext context) throws Exception{
		if(instance==null){
			synchronized (EntityFactory.class) {
				if(instance==null){
					instance = new EntityFactory(context);
				}
			}
		}
	}

	/**
	 * Returns the singleton instance.
	 *
	 * @return the initialized {@code EntityFactory}
	 * @throws Exception if the factory was not initialized via {@link #init(GameContext)} first
	 */
	public static EntityFactory getInstance() throws Exception{
		if(instance==null){
			Exception ex = new Exception("SpriteFactory class must by initialized before to use it");
			throw ex;
		}
		return instance;
	}


	/**
	 * Creates and fully wires an entity of the given type.
	 *
	 * @param <E> the concrete {@link AbstractEntity} type to build
	 * @param posX the initial x coordinate of the entity centre
	 * @param posY the initial y coordinate of the entity centre
	 * @param posZ the depth used for draw ordering ({@code 0} = closest to the screen)
	 * @param speedX the initial horizontal speed, in pixel/second
	 * @param speedY the initial vertical speed, in pixel/second
	 * @param scale the scale factor applied to the sprite dimension ({@code 1} = original size)
	 * @param algorithm the movement strategy, or {@code null} to use {@link UpdateAlgorithmDefault}
	 * @param sprite the sprite used to render the entity
	 * @param clazz the concrete entity class to instantiate
	 * @return the newly created and wired entity
	 * @throws Exception if the entity cannot be instantiated
	 */
	public <E extends AbstractEntity> E createEntity(int posX, int posY,int posZ,int speedX, int speedY,float scale, UpdateAlgorithm algorithm,Sprite sprite, Class<E> clazz) throws Exception{
		E entity = ClassFactory.newIstance(clazz);
		populateSpriteObject(entity,posX, posY,posZ, speedX, speedY,scale,sprite, algorithm);
		return entity;
	}	


	/**
	 * Creates and fully wires an entity, resolving its class from a fully-qualified class name.
	 *
	 * @param <E> the concrete {@link AbstractEntity} type to build
	 * @param posX the initial x coordinate of the entity centre
	 * @param posY the initial y coordinate of the entity centre
	 * @param posZ the depth used for draw ordering ({@code 0} = closest to the screen)
	 * @param speedX the initial horizontal speed, in pixel/second
	 * @param speedY the initial vertical speed, in pixel/second
	 * @param scale the scale factor applied to the sprite dimension ({@code 1} = original size)
	 * @param algorithm the movement strategy, or {@code null} to use {@link UpdateAlgorithmDefault}
	 * @param sprite the sprite used to render the entity
	 * @param className the fully-qualified name of the concrete entity class to instantiate
	 * @return the newly created and wired entity
	 * @throws Exception if the class cannot be loaded or the entity cannot be instantiated
	 */
	@SuppressWarnings("unchecked")
	public <E extends AbstractEntity> E createEntity(int posX, int posY, int posZ, int speedX, int speedY,float scale, UpdateAlgorithm algorithm,Sprite sprite, String className) throws Exception{
		Class<E> clazz =   (Class<E>) ClassFactory.loadClass(className);
		return createEntity(posX, posY, posZ, speedX, speedY,scale, algorithm, sprite, clazz);

	}	

	

	/**
	 * Populates a freshly created entity with its position, speed, sprite, size, update algorithm and
	 * the shared game context.
	 *
	 * @param entity the entity to populate
	 * @param posX the initial x coordinate of the entity centre
	 * @param posY the initial y coordinate of the entity centre
	 * @param posZ the depth used for draw ordering ({@code 0} = closest to the screen)
	 * @param speedX the initial horizontal speed, in pixel/second
	 * @param speedY the initial vertical speed, in pixel/second
	 * @param scale the scale factor applied to the sprite dimension ({@code 1} = original size)
	 * @param sprite the sprite used to render the entity and to derive its base size
	 * @param algorithm the movement strategy, or {@code null} to use {@link UpdateAlgorithmDefault}
	 */
	protected void populateSpriteObject(AbstractEntity entity,int posX, int posY,int posZ, int speedX,int speedY,float scale, Sprite sprite, UpdateAlgorithm algorithm) {
		Position position = new Position(posX,posY,posZ);
		entity.setPosition(position);

		Speed speed = new Speed(speedX,speedY);
		entity.setSpeed(speed);

		entity.setSprite(sprite);
		
		Size size = new Size(sprite.getImageWidth(), sprite.getImageHeight(),scale);
		entity.setSize(size);
		
		if(algorithm==null){
			entity.setUpdateAlgorithm(new UpdateAlgorithmDefault());
		}else{
			entity.setUpdateAlgorithm(algorithm);
		}

		entity.setContext(context);
		
	}

}
