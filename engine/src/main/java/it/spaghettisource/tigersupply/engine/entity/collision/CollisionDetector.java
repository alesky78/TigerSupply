package it.spaghettisource.tigersupply.engine.entity.collision;

import it.spaghettisource.tigersupply.engine.entity.Entity;
import it.spaghettisource.tigersupply.engine.entity.manager.EntityManager;


public  class CollisionDetector{

	protected final int ONE_TO_ONE = 0;
	protected final int ONE_TO_MANY = 1;	
	protected final int MANY_TO_MANY = 2;	

	protected int collisionType;

	protected EntityManager<Entity> managerA;
	protected EntityManager<Entity> managerB;
	protected Entity spriteA;
	protected Entity spriteB;	


	@SuppressWarnings("rawtypes")
	public <T extends EntityManager> CollisionDetector(T a, T b) {
		managerA = a;
		managerB = b;
		collisionType = MANY_TO_MANY;
	}


	public <T extends EntityManager> CollisionDetector(Entity a,T b) {
		spriteA = a;
		managerA = b;
		collisionType = ONE_TO_MANY;
	}

	public CollisionDetector(Entity a, Entity b) {
		spriteA = a;
		spriteB = b;
		collisionType = ONE_TO_ONE;
	}	

	public void detectCollision()  throws Exception{
		if(collisionType == MANY_TO_MANY){
			Entity s2m2m;
			for (Entity s1m2m : managerA.getManagedEntities()) {
				s2m2m = managerB.getEntityCollidedWith(s1m2m);			
				if(s2m2m!=null){
					s1m2m.collided(s2m2m);
					s2m2m.collided(s1m2m);
				}
			}
		}else if(collisionType == ONE_TO_MANY){
			Entity s1m21 = managerA.getEntityCollidedWith(spriteA);			
			if(s1m21!=null){
				spriteA.collided(s1m21);
				s1m21.collided(spriteA);
			}
		}else if(collisionType == ONE_TO_ONE){
			if(spriteA.collidedWith(spriteB)){
				spriteA.collided(spriteB);
				spriteB.collided(spriteA);
			}
		}


	}	


}
