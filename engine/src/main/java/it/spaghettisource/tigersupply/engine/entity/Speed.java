package it.spaghettisource.tigersupply.engine.entity;


/**
 * Velocity of an {@link Entity}, expressed in pixel/second along the two screen axes.
 *
 * @author Alessandro D'Ottavio
 *
 */
public class Speed {

	// pixel/second speed
	protected float speedX; 	
	protected float speedY;
	
	
	/**
	 * Creates a zero speed (still entity).
	 */
	public Speed() {
		speedX = 0;
		speedY = 0;
	}

	/**
	 * Creates a speed with the given horizontal and vertical components.
	 *
	 * @param speedX the horizontal speed, in pixel/second
	 * @param speedY the vertical speed, in pixel/second
	 */
	public Speed(float speedX, float speedY) {
		this.speedX = speedX;
		this.speedY = speedY;
	}
	
	/**
	 * Copy constructor.
	 *
	 * @param copy the speed to duplicate
	 */
	public Speed(Speed copy) {
		this.speedX = copy.speedX;
		this.speedY = copy.speedY;
	}

	/**
	 * @return the horizontal speed, in pixel/second
	 */
	public float getSpeedX() {
		return speedX;
	}

	/**
	 * Sets the horizontal speed.
	 *
	 * @param speedX the horizontal speed, in pixel/second
	 */
	public void setSpeedX(float speedX) {
		this.speedX = speedX;
	}

	/**
	 * @return the vertical speed, in pixel/second
	 */
	public float getSpeedY() {
		return speedY;
	}

	/**
	 * Sets the vertical speed.
	 *
	 * @param speedY the vertical speed, in pixel/second
	 */
	public void setSpeedY(float speedY) {
		this.speedY = speedY;
	}	
	
}
