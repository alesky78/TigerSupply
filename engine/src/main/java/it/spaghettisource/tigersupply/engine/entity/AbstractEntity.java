package it.spaghettisource.tigersupply.engine.entity;


import java.awt.Graphics2D;
import java.awt.Rectangle;

import it.spaghettisource.tigersupply.engine.control.GameContext;
import it.spaghettisource.tigersupply.engine.entity.logic.UpdateAlgorithm;
import it.spaghettisource.tigersupply.engine.sprite.Sprite;


/**
 * Basic {@link Entity} implementation that holds the common simulation state and wires it to an
 * {@link UpdateAlgorithm} and a {@link Sprite}.
 *
 * <p>Subclasses inherit the default per-frame behaviour: {@link #updateEntity(float)} delegates the
 * motion to the configured {@link UpdateAlgorithm} and then advances the sprite animation, while
 * {@link #renderEntity(Graphics2D)} draws the sprite at the current position and size. Collision
 * detection uses the axis-aligned bounding rectangle returned by {@link #getEntityRectangle()} and the
 * default reaction ({@link #collided(Entity)}) simply flags the entity for removal.</p>
 *
 * @author Alessandro D'Ottavio
 *
 */
public abstract class AbstractEntity implements Entity {
	
	protected Position position;	//position of the entity
	protected Speed 	 speed;		//speed of the entity
	protected Size  	 size;		//size of the entity		

	protected GameContext context;
	
	protected Sprite sprite;
		
	protected UpdateAlgorithm updateAlgorithm;
	
	protected boolean remove = false;	//used to decide when the sprite can be remove
	
	/**
	 * {@inheritDoc}
	 *
	 * <p>Returns a single axis-aligned rectangle centred on the entity position and sized after the
	 * scaled {@link Size}. The rectangle accounts for the scale but not for any rotation, because the
	 * Java {@link Rectangle} used here cannot represent a rotated box.</p>
	 */
	//TODO attenzione il rettangolo ottenuto tiene in considerazione solo la scala ma non la rotazione implementare di java non permette rotazione su rettangoli
	public Rectangle[] getEntityRectangle(){
		Rectangle rect = new Rectangle((int)(position.getPosX() - size.getHalfWidth()), (int)(position.getPosY() - size.getHalfHeight()), size.getWidth(), size.getHeight());
		return new Rectangle[]{rect}; 
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>Delegates the motion to the configured {@link UpdateAlgorithm} and then advances the sprite
	 * animation when a sprite is set.</p>
	 */
	public void updateEntity(float deltaSeconds) throws Exception {
		updateAlgorithm.updateLogic(position, speed, deltaSeconds);
		if(sprite!=null)	//TODO decidere bene come gestire l'update degli srpites dovrebbe essere uno dei command
			sprite.updateSprite(deltaSeconds);
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>Draws the configured sprite at the current position and size.</p>
	 */
	public void renderEntity(Graphics2D dbg) throws Exception {
		sprite.renderSprite(dbg, position, size);
	}


	/**
	 * {@inheritDoc}
	 *
	 * <p>Reports a collision when any of this entity's bounding rectangles intersects any of the other
	 * entity's bounding rectangles.</p>
	 */
	public boolean collidedWith(Entity other) {
		Rectangle[] ownRect = getEntityRectangle();
		Rectangle[] otherRect = other.getEntityRectangle();		
		
		for (int i = 0; i < ownRect.length; i++) {
			for (int j = 0; j < otherRect.length; j++) {
				if(ownRect[i].intersects(otherRect[j])){
					return true;
				}
			}
		}
		return false; 	
	}
	
	/**
	 * {@inheritDoc}
	 *
	 * <p>By default the entity is flagged for removal as soon as it is hit.</p>
	 */
	public void collided(Entity other) {
		remove = true;
	}	
	
	/**
	 * {@inheritDoc}
	 *
	 * <p>Returns the removal flag set by {@link #collided(Entity)}.</p>
	 */
	public boolean canBeRemoved(){
		return remove;
	}
	
	public boolean isOutOfScreen(int windowWidth, int windowHeight){
		if(position.getPosX()<0 || position.getPosX() > windowWidth){
			return true;
		}else if(position.getPosY()<0 || position.getPosY() > windowHeight){
			return true;
		}else{
			return false;
		}
	}

	/**
	 * Injects the shared game context used by this entity and its collaborators.
	 *
	 * @param context the current {@link GameContext}
	 */
	public void setContext(GameContext context) {
		this.context = context;
	}

	/**
	 * Sets the algorithm that drives this entity's motion during {@link #updateEntity(float)}.
	 *
	 * @param updateAlgorithm the movement strategy to apply
	 */
	public void setUpdateAlgorithm(UpdateAlgorithm updateAlgorithm) {
		this.updateAlgorithm = updateAlgorithm;
	}

	public int getXposition(){
		return (int) position.getPosX();
	}

	public int getYposition(){
		return (int) position.getPosY();
	}	
	
	public Position getPosition() {
		return position;
	}

	public void setPosition(Position position) {
		this.position = position;	
	}

	public Speed getSpeed() {
		return speed;
	}

	public void setSpeed(Speed speed) {
		this.speed = speed;		
	}

	public Size getsize() {
		return size;
	}

	public void setSize(Size size) {
		this.size = size;		
	}

	public void setSprite(Sprite sprite) {
		this.sprite = sprite;
	}
	
}
