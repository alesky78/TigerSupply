package it.spaghettisource.tigersupply.engine.entity;

import it.spaghettisource.tigersupply.engine.sprite.Sprite;

import java.awt.Graphics2D;
import java.awt.Rectangle;


/**
 * Common contract implemented by every game object that lives in the simulation.
 *
 * <p>An {@code Entity} bundles the simulation state (its {@link Position}, {@link Speed} and
 * {@link Size}), a presentation delegate ({@link Sprite}) and the per-frame behaviour exposed through
 * {@link #updateEntity(float)} and {@link #renderEntity(Graphics2D)}. The engine game loop advances
 * every entity by calling those two methods once per frame.</p>
 *
 * <p>Collision handling is split in two phases: {@link #collidedWith(Entity)} is a pure geometric test,
 * while {@link #collided(Entity)} is the notification that lets the entity react (for example flag
 * itself for removal). Entities that are no longer relevant report {@link #canBeRemoved()} so the owning
 * container can prune them.</p>
 *
 * @author Alessandro D'Ottavio
 *
 */
public interface Entity {

	/**
	 * @return the current position (its centre point) of this entity
	 */
	public Position getPosition();

	/**
	 * Sets the position of this entity.
	 *
	 * @param position the new position
	 */
	public void setPosition(Position position);

	/**
	 * @return the x coordinate of this entity, rounded to an integer pixel
	 */
	public int getXposition();

	/**
	 * @return the y coordinate of this entity, rounded to an integer pixel
	 */
	public int getYposition();

	/**
	 * @return the current speed, in pixel/second, of this entity
	 */
	public Speed getSpeed();

	/**
	 * Sets the speed of this entity.
	 *
	 * @param speed the new speed, in pixel/second
	 */
	public void setSpeed(Speed speed);

	/**
	 * @return the size (width, height and scale) of this entity
	 */
	public Size getsize();

	/**
	 * Sets the size of this entity.
	 *
	 * @param size the new size
	 */
	public void setSize(Size size);

	/**
	 * Sets the sprite used to render this entity.
	 *
	 * @param sprite the presentation delegate that draws this entity
	 */
	public void setSprite(Sprite sprite);


	/**
	 * Advances the entity logic by one frame.
	 *
	 * @param deltaSeconds elapsed time since the previous frame, in seconds
	 * @throws Exception if the update fails
	 */
	public void updateEntity(float deltaSeconds) throws Exception;


	/**
	 * Renders the entity on the given graphics context.
	 *
	 * @param dbg the graphics context to draw on
	 * @throws Exception if the rendering fails
	 */
	public void renderEntity(Graphics2D dbg) throws Exception;


	/**
	 * Tests whether this entity geometrically overlaps another one.
	 *
	 * @param other the entity to test this entity against
	 * @return {@code true} if this entity collides with {@code other}
	 */
	public boolean collidedWith(Entity other);

	/**
	 * Notifies this entity that it collided with another one, letting it react to the impact.
	 *
	 * @param other the entity with which this entity collided
	 */
	public void collided(Entity other);

	/**
	 * Reports whether this entity can be discarded by its owning container, for example because its
	 * energy is exhausted or it was hit.
	 *
	 * @return {@code true} if this entity may be removed from the simulation
	 */
	public boolean canBeRemoved();


	/**
	 * Returns the bounding rectangle(s) enclosing this entity, used for collision detection.
	 *
	 * @return one or more axis-aligned rectangles covering this entity
	 */
	public Rectangle[] getEntityRectangle();

	/**
	 * Tests whether this entity lies outside the rectangle {@code (0, 0, windowWidth, windowHeight)}.
	 *
	 * @param windowWidth the playfield width in pixels
	 * @param windowHeight the playfield height in pixels
	 * @return {@code true} if this entity is out of the screen bounds
	 */
	public boolean isOutOfScreen(int windowWidth, int windowHeight);


}
