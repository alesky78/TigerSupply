package it.spaghettisource.tigersupply.engine.control;

import javax.swing.JPanel;

/**
 * Factory abstraction used by the engine window host to build the concrete
 * {@link GameManager} without depending on any specific game implementation.
 *
 * <p>The factory receives the hosting {@link JPanel} and the shared
 * {@link ApplicationContext} at construction time. This resolves the
 * panel/manager chicken-and-egg: the panel needs a manager to drive the loop
 * and route input, while the concrete manager needs the panel it renders into.
 *
 * <p>The concrete implementation is supplied by the composition root (the
 * launcher module), keeping the engine free of any reference to a concrete
 * game type.
 */
public interface GameManagerFactory {

	/**
	 * Create the {@link GameManager} bound to the given panel and context.
	 *
	 * @param panel the panel hosting the game, never {@code null}
	 * @param context the shared game lifecycle context, never {@code null}
	 * @return the game manager to drive, never {@code null}
	 * @throws Exception if the manager or its backing resources fail to initialize
	 */
	GameManager create(JPanel panel, ApplicationContext context) throws Exception;

}
