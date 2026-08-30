package it.spaghettisource.tigersupply.engine.entity;

/**
 * size of the entity dimension
 * 
 * @author Alessandro D'Ottavio
 *
 */
public class Size {

	protected int width;	//width of the sprite
	protected int height;	//height of the sprite
	protected float scale;	//scale for the dimension of the sprite deafult 1 that mean original size, 1.1 mean 110%	
	
	public Size() {
	}

	public Size(int width, int height) {
		this.width = width;
		this.height = height;
		this.scale = 1;
	}

	public Size(int width, int height,float scale) {
		this.width = width;
		this.height = height;
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

	public int getHeight() {
		return (int) (height*scale);
	}

	public int getHalfHeight() {
		return (int) (height*scale)/2;
	}	
	
	public void setHeight(int height) {
		this.height = height;
	}

	public double getScale() {
		return scale;
	}

	public void setScale(float scale) {
		this.scale = scale;
	}

		
}
