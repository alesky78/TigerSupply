package it.spaghettisource.tigersupply.engine.entity;

import it.spaghettisource.tigersupply.engine.control.ApplicationContext;
import it.spaghettisource.tigersupply.engine.entity.logic.UpdateAlgorithm;
import it.spaghettisource.tigersupply.engine.entity.logic.UpdateAlgorithmDefault;
import it.spaghettisource.tigersupply.engine.sprite.Sprite;
import it.spaghettisource.tigersupply.engine.utils.ClassFactory;
import it.spaghettisource.tigersupply.engine.entity.Position;
import it.spaghettisource.tigersupply.engine.entity.Size;
import it.spaghettisource.tigersupply.engine.entity.Speed;



/**
 * Factory for any kind of Sprite, the sprite should be created only from this class
 * 
 * @author Alessandro D'Ottavio
 *
 */
public class EntityFactory {

	private static EntityFactory instance;
	private ApplicationContext context;

	private EntityFactory(ApplicationContext context) throws Exception{
		this.context = context;
	}

	public static void init(ApplicationContext context) throws Exception{
		if(instance==null){
			synchronized (EntityFactory.class) {
				if(instance==null){
					instance = new EntityFactory(context);
				}
			}
		}
	}

	public static EntityFactory getInstance() throws Exception{
		if(instance==null){
			Exception ex = new Exception("SpriteFactory class must by initialized before to use it");
			throw ex;
		}
		return instance;
	}


	public <E extends AbstractEntity> E createEntity(int posX, int posY,int posZ,int speedX, int speedY,float scale, UpdateAlgorithm algorithm,Sprite sprite, Class<E> clazz) throws Exception{
		E entity = ClassFactory.newIstance(clazz);
		populateSpriteObject(entity,posX, posY,posZ, speedX, speedY,scale,sprite, algorithm);
		return entity;
	}	


	@SuppressWarnings("unchecked")
	public <E extends AbstractEntity> E createEntity(int posX, int posY, int posZ, int speedX, int speedY,float scale, UpdateAlgorithm algorithm,Sprite sprite, String className) throws Exception{
		Class<E> clazz =   (Class<E>) ClassFactory.loadClass(className);
		return createEntity(posX, posY, posZ, speedX, speedY,scale, algorithm, sprite, clazz);

	}	

	

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
