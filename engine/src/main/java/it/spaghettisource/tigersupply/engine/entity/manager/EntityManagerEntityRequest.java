package it.spaghettisource.tigersupply.engine.entity.manager;

import it.spaghettisource.tigersupply.engine.entity.Entity;


import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the Entity Manager that can manage a request of new {@link Entity} to be manage
 * the {@link Entity} are  managed during the {@linkplain EntityManagerEntityRequest#updateEntity(double)} phase
 * 
 * to interact with the EntityManagerEntityRequest use the method {@linkplain EntityManagerEntityRequest#addRquest(Entity)}
 * 
 * @author DOttavio
 *
 */
public class EntityManagerEntityRequest<T extends Entity> extends EntityManagerRemovable<T> {
		
	protected ArrayList<T> entityRequest = new ArrayList<T>();

	public void updateEntity(float deltaSeconds)  throws Exception{
		super.updateEntity(deltaSeconds);
		createAndMangeNewRequest();		
	}	

	public void addRquest(T entity){
		entityRequest.add(entity);
	}
	
	public void addRquest(List<T> entities){
		entityRequest.addAll(entities);
	}	

	protected  void createAndMangeNewRequest() throws Exception{
		for (T req : entityRequest) {
			entities.add(req);		
			
		}
		entityRequest.clear();
	}

}
