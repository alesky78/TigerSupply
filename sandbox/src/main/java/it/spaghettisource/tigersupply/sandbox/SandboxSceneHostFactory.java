package it.spaghettisource.tigersupply.sandbox;

import javax.swing.JPanel;

import it.spaghettisource.tigersupply.engine.control.GameContext;
import it.spaghettisource.tigersupply.engine.control.SceneHostFactory;
import it.spaghettisource.tigersupply.sandbox.control.SandboxSceneHost;

/**
 * Composition-root factory that builds the sandbox {@link SandboxSceneHost}.
 *
 * <p>This is the sandbox counterpart of the launcher's {@code TigerSupplySceneHostFactory}: the seam
 * that binds the sandbox scene host to the reusable engine window shell.</p>
 */
public class SandboxSceneHostFactory implements SceneHostFactory {

	@Override
	public SandboxSceneHost create(JPanel panel, GameContext context) throws Exception {
		return new SandboxSceneHost(panel, context);
	}
}
