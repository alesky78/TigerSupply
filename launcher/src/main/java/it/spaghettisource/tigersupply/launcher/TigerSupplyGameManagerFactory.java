package it.spaghettisource.tigersupply.launcher;

import javax.swing.JPanel;

import it.spaghettisource.tigersupply.engine.control.ApplicationContext;

import it.spaghettisource.tigersupply.engine.control.GameManagerFactory;
import it.spaghettisource.tigersupply.engine.impl.control.GameManager;

/**
 * Composition-root factory that builds the concrete TigerSupply game manager.
 *
 * <p>This is the single place in the codebase, outside the {@code impl} tree, that
 * names the concrete {@code impl.control.GameManager}. It is the intentional seam
 * between the reusable engine and the TigerSupply game; when the {@code impl.*} tree
 * later moves into the {@code game} module, only this reference needs to follow it.
 */
public class TigerSupplyGameManagerFactory implements GameManagerFactory {

	@Override
	public GameManager create(JPanel panel, ApplicationContext context) throws Exception {
		return new GameManager(panel, context);
	}

}
