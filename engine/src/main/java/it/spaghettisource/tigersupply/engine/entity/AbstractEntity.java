package it.spaghettisource.tigersupply.engine.entity;


import java.awt.Graphics2D;
import java.awt.Rectangle;

import it.spaghettisource.tigersupply.engine.control.GameContext;
import it.spaghettisource.tigersupply.engine.entity.logic.UpdateAlgorithm;
import it.spaghettisource.tigersupply.engine.sprite.Sprite;


/**
 * basic implementation of an entity
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
	
	//TODO attenzione il rettangolo ottenuto tiene in considerazione solo la scala ma non la rotazione implementare di java non permette rotazione su rettangoli
	public Rectangle[] getEntityRectangle(){
		Rectangle rect = new Rectangle((int)(position.getPosX() - size.getHalfWidth()), (int)(position.getPosY() - size.getHalfHeight()), size.getWidth(), size.getHeight());
		return new Rectangle[]{rect}; 
	}

	
	public void updateEntity(float deltaSeconds) throws Exception {
		updateAlgorithm.updateLogic(position, speed, deltaSeconds);
		if(sprite!=null)	//TODO decidere bene come gestire l'update degli srpites dovrebbe essere uno dei command
			sprite.updateSprite(deltaSeconds);
	}

	public void renderEntity(Graphics2D dbg) throws Exception {
		sprite.renderSprite(dbg, position, size);
	}


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
	 * by default the sprite is removed when hit
	 */
	public void collided(Entity other) {
		remove = true;
	}	
	
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

	public void setContext(GameContext context) {
		this.context = context;
	}

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
