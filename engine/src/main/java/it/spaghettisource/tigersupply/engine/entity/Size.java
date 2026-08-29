package it.spaghettisource.tigersupply.engine.entity;

/**
 * size of the entity dimension
 * 
 * @author Alessandro D'Ottavio
 *
 */
public class Size {

	protected int width;	//width of the sprite
	protected int heigh;	//height of the sprite
	protected float scale;	//scale for the dimension of the sprite deafult 1 that mean original size, 1.1 mean 110%	
	
	public Size() {
	}

	public Size(int width, int heigh) {
		this.width = width;
		this.heigh = heigh;
		this.scale = 1;
	}

	public Size(int width, int heigh,float scale) {
		this.width = width;
		this.heigh = heigh;
		this.scale = scale;
	}	
	
	public int getWidth() {
		return (int)(width*scale);
	}
	
	public int getHalfWidth() {
		return (int)(width*scale)/2;
	}	

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeigh() {
		return (int) (heigh*scale);
	}

	public int getHalfHeigh() {
		return (int) (heigh*scale)/2;
	}	
	
	public void setHeigh(int heigh) {
		this.heigh = heigh;
	}

	public double getScale() {
		return scale;
	}

	public void setScale(float scale) {
		this.scale = scale;
	}

		
}
