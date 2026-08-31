package it.spaghettisource.tigersupply.engine.entity;

/**
 * Location and orientation of an {@link Entity} in the playfield.
 *
 * <p>The {@code (x, y)} coordinates refer to the centre point of the entity image. {@code z} is the
 * depth used for draw ordering ({@code 0} means closest to the screen, higher values are further away).
 * {@code angle} is the clockwise rotation in degrees, normalised to the range {@code [0, 360)}.</p>
 *
 * @author Alessandro D'Ottavio
 *
 */
public class Position {

	protected float posX;	//x position that is the central poin in the image
	protected float posY;	//y position that is the central poin in the image
	protected int posZ;		//z position that is the deep of the sprite 0 meas more close to screen hig number more far	
	protected float angle;	// angle of rotation of the sprite in degree it goes for positive number in clock direction


	/**
	 * Creates a position at the origin, with no rotation.
	 */
	public Position() {
	}

	/**
	 * Creates a position at the given coordinates, with no rotation.
	 *
	 * @param posX the x coordinate of the entity centre
	 * @param posY the y coordinate of the entity centre
	 * @param posZ the depth used for draw ordering ({@code 0} = closest to the screen)
	 */
	public Position(float posX, float posY, int posZ) {
		this.posX = posX;
		this.posY = posY;
		this.posZ = posZ;		
		this.angle = 0;
	}

	/**
	 * Copy constructor.
	 *
	 * @param copy the position to duplicate
	 */
	public Position(Position copy) {
		this.posX = copy.posX;
		this.posY = copy.posY;
		this.posZ = copy.posZ;		
		this.angle = copy.angle;
	}	

	/**
	 * Moves the position horizontally by the given amount.
	 *
	 * @param x the signed horizontal displacement, in pixels
	 */
	public void increaseX(float x){
		posX +=x;
	}

	/**
	 * Moves the position vertically by the given amount.
	 *
	 * @param y the signed vertical displacement, in pixels
	 */
	public void increaseY(float y){
		posY +=y;
	}	

	/**
	 * @return the x coordinate of the entity centre
	 */
	public float getPosX() {
		return posX;
	}

	/**
	 * Sets the x coordinate of the entity centre.
	 *
	 * @param posX the new x coordinate
	 */
	public void setPosX(float posX) {
		this.posX = posX;
	}

	/**
	 * @return the y coordinate of the entity centre
	 */
	public float getPosY() {
		return posY;
	}

	/**
	 * Sets the y coordinate of the entity centre.
	 *
	 * @param posY the new y coordinate
	 */
	public void setPosY(float posY) {
		this.posY = posY;
	}
	
	/**
	 * @return the depth used for draw ordering ({@code 0} = closest to the screen)
	 */
	public int getPosZ() {
		return posZ;
	}

	/**
	 * Sets the depth used for draw ordering.
	 *
	 * @param posZ the new depth ({@code 0} = closest to the screen)
	 */
	public void setPosZ(int posZ) {
		this.posZ = posZ;
	}

	/**
	 * @return the rotation angle in degrees, in the range {@code [0, 360)}
	 */
	public float getAngle() {
		return angle;
	}

	/**
	 * @return the rotation angle converted to radians
	 */
	public double getAngleRadiant() {
		return Math.toRadians(angle);
	}
	
	/**
	 * Sets the rotation angle, normalising it to the range {@code [0, 360)}.
	 *
	 * @param angle the new angle in degrees (clockwise positive)
	 */
	public void setAngle(float angle) {
		this.angle = correctAngle(angle);
	}

	/**
	 * Rotates the position by the given amount, keeping the angle in the range {@code [0, 360)}.
	 *
	 * @param thera the signed rotation increment in degrees (clockwise positive)
	 */
	public void increaseAngle(float thera){
		angle+=thera;
		angle = correctAngle(angle);
	}		

	
	/**
	 * Normalises an angle to the range {@code [0, 360)}.
	 *
	 * @param angle the raw angle in degrees
	 * @return the equivalent angle in the range {@code [0, 360)}
	 */
	private float correctAngle(float angle){
		angle = angle%360;
		if(angle <0)
			angle = 360+angle;
		
		return angle;
	}	
	
}
