package it.spaghettisource.tigersupply.launcher;

import javax.swing.JPanel;

import it.spaghettisource.tigersupply.engine.control.GameContext;

import it.spaghettisource.tigersupply.engine.control.SceneManagerFactory;
import it.spaghettisource.tigersupply.game.control.TigerSupplySceneManager;

/**
 * Composition-root factory that builds TigerSupply's concrete
 * {@link TigerSupplySceneManager}.
 *
 * <p>This is the single place in the codebase, outside the {@code game} module, that names the
 * concrete scene manager. It is the intentional seam between the reusable engine and the
 * TigerSupply game.
 */
public class TigerSupplySceneManagerFactory implements SceneManagerFactory {

	@Override
	public TigerSupplySceneManager create(JPanel panel, GameContext context) throws Exception {
		return new TigerSupplySceneManager(panel, context);
	}

}
