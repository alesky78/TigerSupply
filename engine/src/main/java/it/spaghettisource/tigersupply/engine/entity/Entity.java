package it.spaghettisource.tigersupply.engine.entity;

import it.spaghettisource.tigersupply.engine.sprite.Sprite;

import java.awt.Graphics2D;
import java.awt.Rectangle;


/**
 * 
 * interface of all the entity of the game
 * 
 * @author Alessandro D'Ottavio
 *
 */
public interface Entity {

	public Position getPosition();	
	public void setPosition(Position position);
	
	public int getXposition();
	public int getYposition();
	
	public Speed getSpeed();	
	public void setSpeed(Speed speed);
	
	public Size getsize();
	public void setSize(Size size);	
	
	public void setSprite(Sprite sprite);
	

	
	
	/**
	 * update the entity logic
	 * 
	 * @param deltaSeconds time spent before last call
	 */
	public void updateEntity(float deltaSeconds) throws Exception;


	/**
	 * render the entity
	 * 
	 * @param dbg
	 * @throws Exception 
	 */
	public void renderEntity(Graphics2D dbg) throws Exception;
	
	
	/**
	 * verify if this entity collided with another.
	 * 
	 * @param other entity to verify if this entity collided.
	 * @return true if collide with this sprite
	 */
	public boolean collidedWith(Entity other);
	
	/**
	 * Notification that this entity collided with another.
	 * 
	 * @param other The entity with which this entity collided.
	 * @return true if collide with this sprite
	 */
	public void collided(Entity other);	
	
	/**
	 * verify if a enemy can be removed.
	 * for example energy is finish
	 * 
	 * @param other The entity with which this entity collided.
	 * @return true if collide with this sprite
	 */
	public boolean canBeRemoved();	
	
	
	/**
	 * return the rectangle that contain this entity, used for the verify of the collision
	 * 
	 * @return
	 */
	public Rectangle[] getEntityRectangle();	
	
	/**
	 * verify if the sprite is inside a rectangle of dimension (0,0,windowWidth,windowHeight)
	 * 
	 * @param windowWidth
	 * @param windowHeight
	 * @return
	 */
	public boolean isOutOfScreen(int windowWidth, int windowHeight);	


}
