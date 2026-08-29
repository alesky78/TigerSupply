package it.spaghettisource.tigersupply.engine.entity.manager;

import it.spaghettisource.tigersupply.engine.control.ApplicationContext;
import it.spaghettisource.tigersupply.engine.entity.Entity;

import java.util.ArrayList;


/**
 * Entity manager that remove automatically the sprite, if goes out of screen
 * or it is marked as removable from the {@link Entity} method canBeRemoved()  
 * 
 * init must be called to initialize the size of management of the sprite
 * 
 * @author Alessandro D'Ottavio
 *
 */
public class EntityManagerRemovable<T extends Entity> extends EntityManager<T> {

	protected int width,height;
	
	//marked to be removed has managed sprite
	protected ArrayList<Entity> markedSrpites = new ArrayList<Entity>();
	
	public void init(ApplicationContext context) {
		this.context = context;
		this.width = context.getScreenWidth();
		this.height = context.getScreenHeight();
	}	

	public void updateEntity(float deltaSeconds)  throws Exception{
		
		for (Entity entity : entities) {	//remove the sprite out of screen and the sprite collided
			entity.updateEntity(deltaSeconds);
			if(entity.isOutOfScreen(width, height) || entity.canBeRemoved()){
				markedSrpites.add(entity);
			}
		}
		
		entities.removeAll(markedSrpites);
		markedSrpites.clear();
			
	}	

}
