package it.spaghettisource.tigersupply.launcher;

import javax.swing.JPanel;

import it.spaghettisource.tigersupply.engine.control.GameContext;

import it.spaghettisource.tigersupply.engine.control.SceneHostFactory;
import it.spaghettisource.tigersupply.game.control.TigerSupplySceneHost;

/**
 * Composition-root factory that builds TigerSupply's concrete
 * {@link TigerSupplySceneHost}.
 *
 * <p>This is the single place in the codebase, outside the {@code game} module, that names the
 * concrete scene host. It is the intentional seam between the reusable engine and the
 * TigerSupply game.
 */
public class TigerSupplySceneHostFactory implements SceneHostFactory {

	@Override
	public TigerSupplySceneHost create(JPanel panel, GameContext context) throws Exception {
		return new TigerSupplySceneHost(panel, context);
	}

}
