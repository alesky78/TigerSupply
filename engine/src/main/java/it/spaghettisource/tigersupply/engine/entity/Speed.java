package it.spaghettisource.tigersupply.engine.entity;


/**
 * speed in pixel/second of the entity
 * 
 * @author Alessandro D'Ottavio
 *
 */
public class Speed {

	// pixel/second speed
	protected float speedX; 	
	protected float speedY;
	
	
	public Speed() {
		speedX = 0;
		speedY = 0;
	}

	public Speed(float speedX, float speedY) {
		this.speedX = speedX;
		this.speedY = speedY;
	}
	
	public Speed(Speed copy) {
		this.speedX = copy.speedX;
		this.speedY = copy.speedY;
	}

	public float getSpeedX() {
		return speedX;
	}

	public void setSpeedX(float speedX) {
		this.speedX = speedX;
	}

	public float getSpeedY() {
		return speedY;
	}

	public void setSpeedY(float speedY) {
		this.speedY = speedY;
	}	
	
}
