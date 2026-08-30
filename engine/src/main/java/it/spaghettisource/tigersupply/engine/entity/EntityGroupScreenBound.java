package it.spaghettisource.tigersupply.engine.entity;

import it.spaghettisource.tigersupply.engine.control.GameContext;


/**
 * An {@link EntityGroup} that additionally prunes children once they leave the visible screen.
 *
 * <p>This specialization widens the base removal policy: besides removing children whose
 * {@link Entity#canBeRemoved()} flag is set (inherited from {@link EntityGroup}), it also removes any
 * child reported as out of the screen bounds by {@link Entity#isOutOfScreen(int, int)}. It is the
 * workhorse group for gameplay content that scrolls off-screen and must be reclaimed automatically,
 * such as shots, visual effects and enemies.</p>
 *
 * <p>{@link #init(GameContext)} must be called once before the first update so the group knows the
 * playfield size used by the off-screen test.</p>
 *
 * @param <T> the concrete {@link Entity} type held by this group
 *
 * @author Alessandro D'Ottavio
 */
public class EntityGroupScreenBound<T extends Entity> extends EntityGroup<T> {

	protected int width, height;


	/**
	 * Initializes the group with the current playfield size taken from the game context.
	 *
	 * @param context the {@link GameContext} providing the screen width and height
	 */
	public void init(GameContext context) {
		this.context = context;
		this.width = context.getScreenWidth();
		this.height = context.getScreenHeight();
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>Extends the base policy by also pruning children that have moved out of the screen bounds.</p>
	 */
	@Override
	protected boolean shouldRemove(T entity) {
		return super.shouldRemove(entity) || entity.isOutOfScreen(width, height);
	}

}
