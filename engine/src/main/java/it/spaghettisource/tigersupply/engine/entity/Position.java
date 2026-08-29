package it.spaghettisource.tigersupply.engine.entity;

/**
 * position of an {@link Entity}
 * 
 * @author Alessandro D'Ottavio
 *
 */
public class Position {

	protected float posX;	//x position that is the central poin in the image
	protected float posY;	//y position that is the central poin in the image
	protected int posZ;		//z position that is the deep of the sprite 0 meas more close to screen hig number more far	
	protected float angle;	// angle of rotation of the sprite in degree it goes for positive number in clock direction


	public Position() {
	}

	public Position(float posX, float posY, int posZ) {
		this.posX = posX;
		this.posY = posY;
		this.posZ = posZ;		
		this.angle = 0;
	}

	public Position(Position copy) {
		this.posX = copy.posX;
		this.posY = copy.posY;
		this.posZ = copy.posZ;		
		this.angle = copy.angle;
	}	

	public void increaseX(float x){
		posX +=x;
	}

	public void increaseY(float y){
		posY +=y;
	}	

	public float getPosX() {
		return posX;
	}

	public void setPosX(float posX) {
		this.posX = posX;
	}

	public float getPosY() {
		return posY;
	}

	public void setPosY(float posY) {
		this.posY = posY;
	}
	
	public int getPosZ() {
		return posZ;
	}

	public void setPosZ(int posZ) {
		this.posZ = posZ;
	}

	/**
	 * 
	 * @return angle in degree
	 */
	public float getAngle() {
		return angle;
	}

	/**
	 * 
	 * @return angle in radiant
	 */
	public double getAngleRadiant() {
		return Math.toRadians(angle);
	}
	
	public void setAngle(float angle) {
		this.angle = correctAngle(angle);
	}

	public void increaseAngle(float thera){
		angle+=thera;
		angle = correctAngle(angle);
	}		

	
	private float correctAngle(float angle){
		angle = angle%360;
		if(angle <0)
			angle = 360+angle;
		
		return angle;
	}	
	
}
