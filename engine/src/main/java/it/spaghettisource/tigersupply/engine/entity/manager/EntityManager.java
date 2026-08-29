package it.spaghettisource.tigersupply.engine.entity.manager;

import it.spaghettisource.tigersupply.engine.control.GameContext;
import it.spaghettisource.tigersupply.engine.entity.Entity;
import it.spaghettisource.tigersupply.engine.entity.Position;
import it.spaghettisource.tigersupply.engine.entity.Size;
import it.spaghettisource.tigersupply.engine.entity.Speed;
import it.spaghettisource.tigersupply.engine.sprite.Sprite;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;


/**
 * basic implementation of a entity manager, it is a composition of {@link Entity} 
 * 
 * @author Alessandro D'Ottavio
 *
 */
public class EntityManager<T extends Entity> implements Entity {

	protected GameContext context;
	
	//live entities
	protected ArrayList<T> entities = new ArrayList<T>();
	

	public void init() {
	
	}
	
	
	public void updateEntity(float deltaSeconds)  throws Exception{
		for (Entity entity : entities) {
			entity.updateEntity(deltaSeconds);
		}
	}

	public void renderEntity(Graphics2D dbg) throws Exception {
		for (Entity entity : entities) {
			entity.renderEntity(dbg);
		}	
	}

	public void addSrpiteToBeManaged(T entity){
		entities.add(entity);
	}
	
	public boolean hasEntities(){
		return !entities.isEmpty();
	}
	
	public List<Entity> getManagedEntities(){
		return new ArrayList<Entity>(entities);
	}
	
	public boolean collidedWith(Entity other) {
		for (Entity entity : entities) {
			if(entity.collidedWith(other)){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * return the first sprite that collide with this
	 * 
	 * @param other
	 * @return
	 */
	public Entity getEntityCollidedWith(Entity other) {
		for (Entity entity : entities) {
			if(entity.collidedWith(other)){
				return entity;
			}
		}
		return null;
	}
	
	public List<Entity> getAllSpriteCollidedWith(Entity other) {
		ArrayList<Entity> collidedList = new ArrayList<Entity>();
		for (Entity entity : entities) {
			if(entity.collidedWith(other)){
				collidedList.add(entity);
			}
		}
		return collidedList;
	}	

	public void collided(Entity other) {
		throw new UnsupportedOperationException();
	}	
	
	public boolean isOutOfScreen(int windowWidth, int windowHeight) {
		for (Entity sprite : entities) {
			if(sprite.isOutOfScreen(windowWidth, windowHeight)){
				return true;
			}
		}
		return false;
	}
	

	public int getXposition() {
		throw new UnsupportedOperationException();
	}

	public int getYposition() {
		throw new UnsupportedOperationException();
	}

	public boolean canBeRemoved() {
		throw new UnsupportedOperationException();
	}

	public Position getPosition() {
		throw new UnsupportedOperationException();
	}

	public void setPosition(Position position) {
		throw new UnsupportedOperationException();		
	}
	
	public Speed getSpeed() {
		throw new UnsupportedOperationException();
	}
	
	public void setSpeed(Speed speed) {
		throw new UnsupportedOperationException();
	}

	public Size getsize() {
		throw new UnsupportedOperationException();	
	}

	public void setSize(Size size) {
		throw new UnsupportedOperationException();
	}

	public Rectangle[] getEntityRectangle() {
		throw new UnsupportedOperationException();
	}

	public void setSprite(Sprite sprite) {
		throw new UnsupportedOperationException();		
	}


}
