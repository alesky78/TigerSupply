package it.spaghettisource.tigersupply.engine.control;

import javax.swing.JPanel;

/**
 * Factory abstraction used by the engine window host to build the concrete
 * {@link SceneHost} without depending on any specific game implementation.
 *
 * <p>The factory receives the hosting {@link JPanel} and the shared
 * {@link GameContext} at construction time. This resolves the
 * panel/host chicken-and-egg: the panel needs a host to drive the loop
 * and route input, while the concrete host needs the panel it renders into.
 *
 * <p>The concrete implementation is supplied by the composition root (the
 * launcher module), keeping the engine free of any reference to a concrete
 * game type.
 *
 * @author Alessandro D'Ottavio
 *
 */
public interface SceneHostFactory {

	/**
	 * Create the {@link SceneHost} bound to the given panel and context.
	 *
	 * @param panel the panel hosting the game, never {@code null}
	 * @param context the shared game lifecycle context, never {@code null}
	 * @return the scene host to drive, never {@code null}
	 * @throws Exception if the host or its backing resources fail to initialize
	 */
	SceneHost create(JPanel panel, GameContext context) throws Exception;

}
