package it.spaghettisource.tigersupply.engine.entity;

/**
 * Dimension of an {@link Entity}, combining a base width/height with a uniform {@code scale} factor.
 *
 * <p>The scale is applied on top of the base size: a scale of {@code 1} keeps the original dimension,
 * while {@code 1.1} renders it at 110%. All accessors that return the effective width/height already
 * apply the scale.</p>
 *
 * @author Alessandro D'Ottavio
 *
 */
public class Size {

	protected int width;	//width of the sprite
	protected int height;	//height of the sprite
	protected float scale;	//scale for the dimension of the sprite deafult 1 that mean original size, 1.1 mean 110%	
	
	/**
	 * Creates an empty size with zero width, zero height and no scale.
	 */
	public Size() {
	}

	/**
	 * Creates a size with the given base dimension and a unit scale.
	 *
	 * @param width the base width in pixels
	 * @param height the base height in pixels
	 */
	public Size(int width, int height) {
		this.width = width;
		this.height = height;
		this.scale = 1;
	}

	/**
	 * Creates a size with the given base dimension and scale factor.
	 *
	 * @param width the base width in pixels
	 * @param height the base height in pixels
	 * @param scale the scale factor applied to the base dimension ({@code 1} = original size)
	 */
	public Size(int width, int height,float scale) {
		this.width = width;
		this.height = height;
		this.scale = scale;
	}	
	
	/**
	 * @return the effective width, i.e. the base width multiplied by the scale
	 */
	public int getWidth() {
		return (int)(width*scale);
	}
	
	/**
	 * @return half of the effective width, useful to centre the entity on its position
	 */
	public int getHalfWidth() {
		return (int)(width*scale)/2;
	}	

	/**
	 * Sets the base width, before scaling.
	 *
	 * @param width the base width in pixels
	 */
	public void setWidth(int width) {
		this.width = width;
	}

	/**
	 * @return the effective height, i.e. the base height multiplied by the scale
	 */
	public int getHeight() {
		return (int) (height*scale);
	}

	/**
	 * @return half of the effective height, useful to centre the entity on its position
	 */
	public int getHalfHeight() {
		return (int) (height*scale)/2;
	}	
	
	/**
	 * Sets the base height, before scaling.
	 *
	 * @param height the base height in pixels
	 */
	public void setHeight(int height) {
		this.height = height;
	}

	/**
	 * @return the scale factor applied to the base dimension
	 */
	public double getScale() {
		return scale;
	}

	/**
	 * Sets the scale factor applied to the base dimension.
	 *
	 * @param scale the scale factor ({@code 1} = original size)
	 */
	public void setScale(float scale) {
		this.scale = scale;
	}

		
}
