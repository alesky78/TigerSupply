package it.spaghettisource.tigersupply.engine.entity;

import it.spaghettisource.tigersupply.engine.control.GameContext;
import it.spaghettisource.tigersupply.engine.sprite.Sprite;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;


/**
 * A composite {@link Entity} that owns and drives a homogeneous group of child entities.
 *
 * <p>An {@code EntityGroup} is itself an {@link Entity}: it fans {@link #updateEntity(float)} and
 * {@link #renderEntity(Graphics2D)} out to every managed child and exposes group-wide collision
 * queries (see {@link #getEntityCollidedWith(Entity)} and {@link #getAllSpriteCollidedWith(Entity)}).
 * It is the composite half of a Composite design pattern, letting callers treat a single entity and a
 * whole group of entities through the same {@link Entity} contract.</p>
 *
 * <p><b>Deferred spawning.</b> New children can be queued with {@link #addRquest(Entity)} /
 * {@link #addRquest(List)} while the group is being iterated; queued entities are inserted at the end
 * of the next {@link #updateEntity(float)} call rather than immediately. This avoids a
 * {@link java.util.ConcurrentModificationException} when an entity spawns siblings during its own
 * update (for example a weapon emitting shots, or a dying enemy emitting explosion particles).</p>
 *
 * <p><b>Automatic pruning.</b> During each update the group also removes every child for which
 * {@link #shouldRemove(Entity)} returns {@code true}. The default policy removes a child once its own
 * {@link Entity#canBeRemoved()} flag is set; subclasses may widen the policy by overriding
 * {@link #shouldRemove(Entity)} (see {@link EntityGroupScreenBound}). The per-frame order is fixed:
 * <em>update all children &rarr; prune removable children &rarr; add newly requested children</em>, so
 * an entity requested during a frame is never pruned in that same frame.</p>
 *
 * <p>The positional and geometric methods inherited from {@link Entity} (such as {@link #getPosition()}
 * or {@link #getEntityRectangle()}) are not meaningful for a group and throw
 * {@link UnsupportedOperationException}.</p>
 *
 * @param <T> the concrete {@link Entity} type held by this group
 *
 * @author Alessandro D'Ottavio
 */
public class EntityGroup<T extends Entity> implements Entity {

	protected GameContext context;

	//live entities currently managed and rendered by this group
	protected ArrayList<T> entities = new ArrayList<T>();

	//entities requested during the current frame, inserted on the next update
	protected ArrayList<T> entityRequest = new ArrayList<T>();


	/**
	 * Updates every managed child, prunes the children that must be removed and finally inserts the
	 * children requested since the previous update.
	 *
	 * @param deltaSeconds elapsed time since the previous frame, in seconds
	 * @throws Exception if a child fails to update
	 */
	public void updateEntity(float deltaSeconds) throws Exception {
		List<T> toRemove = new ArrayList<T>();
		for (T entity : entities) {
			entity.updateEntity(deltaSeconds);
			if (shouldRemove(entity)) {
				toRemove.add(entity);
			}
		}
		entities.removeAll(toRemove);

		createAndMangeNewRequest();
	}

	/**
	 * Decides whether a managed child must be pruned during {@link #updateEntity(float)}.
	 *
	 * <p>The default policy removes the child once its {@link Entity#canBeRemoved()} flag is set.
	 * Subclasses may override this method to add further removal conditions; they should usually
	 * combine their own condition with {@code super.shouldRemove(entity)}.</p>
	 *
	 * @param entity the managed child under evaluation
	 * @return {@code true} if the child must be removed from the group
	 */
	protected boolean shouldRemove(T entity) {
		return entity.canBeRemoved();
	}

	/**
	 * Renders every managed child.
	 *
	 * @param dbg the graphics context to draw on
	 * @throws Exception if a child fails to render
	 */
	public void renderEntity(Graphics2D dbg) throws Exception {
		for (Entity entity : entities) {
			entity.renderEntity(dbg);
		}
	}

	/**
	 * Adds a child immediately to the managed group.
	 *
	 * <p>Prefer {@link #addRquest(Entity)} when adding a child while the group is being iterated (for
	 * example from within another entity's update).</p>
	 *
	 * @param entity the child to manage
	 */
	public void addEntityToBeManaged(T entity) {
		entities.add(entity);
	}

	/**
	 * Queues a single child to be added on the next {@link #updateEntity(float)} call.
	 *
	 * @param entity the child to add
	 */
	public void addRquest(T entity) {
		entityRequest.add(entity);
	}

	/**
	 * Queues several children to be added on the next {@link #updateEntity(float)} call.
	 *
	 * @param entities the children to add
	 */
	public void addRquest(List<T> entities) {
		entityRequest.addAll(entities);
	}

	/**
	 * Flushes the pending spawn requests into the managed group.
	 *
	 * @throws Exception if a requested child cannot be added
	 */
	protected void createAndMangeNewRequest() throws Exception {
		entities.addAll(entityRequest);
		entityRequest.clear();
	}

	/**
	 * @return {@code true} if the group currently manages at least one child
	 */
	public boolean hasEntities() {
		return !entities.isEmpty();
	}

	/**
	 * @return a snapshot copy of the currently managed children
	 */
	public List<Entity> getManagedEntities() {
		return new ArrayList<Entity>(entities);
	}

	/**
	 * @param other the entity to test against every managed child
	 * @return {@code true} if any managed child collides with {@code other}
	 */
	public boolean collidedWith(Entity other) {
		for (Entity entity : entities) {
			if (entity.collidedWith(other)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns the first managed child that collides with the given entity.
	 *
	 * @param other the entity to test against every managed child
	 * @return the first colliding child, or {@code null} if none collide
	 */
	public Entity getEntityCollidedWith(Entity other) {
		for (Entity entity : entities) {
			if (entity.collidedWith(other)) {
				return entity;
			}
		}
		return null;
	}

	/**
	 * Returns every managed child that collides with the given entity.
	 *
	 * @param other the entity to test against every managed child
	 * @return the list of colliding children (empty if none collide)
	 */
	public List<Entity> getAllSpriteCollidedWith(Entity other) {
		ArrayList<Entity> collidedList = new ArrayList<Entity>();
		for (Entity entity : entities) {
			if (entity.collidedWith(other)) {
				collidedList.add(entity);
			}
		}
		return collidedList;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @throws UnsupportedOperationException always; collision reaction is handled per child
	 */
	public void collided(Entity other) {
		throw new UnsupportedOperationException();
	}

	/**
	 * @param windowWidth the playfield width in pixels
	 * @param windowHeight the playfield height in pixels
	 * @return {@code true} if any managed child is out of the screen bounds
	 */
	public boolean isOutOfScreen(int windowWidth, int windowHeight) {
		for (Entity sprite : entities) {
			if (sprite.isOutOfScreen(windowWidth, windowHeight)) {
				return true;
			}
		}
		return false;
	}


	public int getXposition() {
		throw new UnsupportedOperationException();
	}

	public int getYposition() {
		throw new UnsupportedOperationException();
	}

	public boolean canBeRemoved() {
		throw new UnsupportedOperationException();
	}

	public Position getPosition() {
		throw new UnsupportedOperationException();
	}

	public void setPosition(Position position) {
		throw new UnsupportedOperationException();
	}

	public Speed getSpeed() {
		throw new UnsupportedOperationException();
	}

	public void setSpeed(Speed speed) {
		throw new UnsupportedOperationException();
	}

	public Size getsize() {
		throw new UnsupportedOperationException();
	}

	public void setSize(Size size) {
		throw new UnsupportedOperationException();
	}

	public Rectangle[] getEntityRectangle() {
		throw new UnsupportedOperationException();
	}

	public void setSprite(Sprite sprite) {
		throw new UnsupportedOperationException();
	}


}
