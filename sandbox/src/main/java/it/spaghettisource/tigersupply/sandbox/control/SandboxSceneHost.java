package it.spaghettisource.tigersupply.sandbox.control;

import java.awt.event.KeyEvent;

import javax.swing.JPanel;

import it.spaghettisource.tigersupply.engine.audio.AudioManager;
import it.spaghettisource.tigersupply.engine.control.AbstractSceneHost;
import it.spaghettisource.tigersupply.engine.control.GameContext;
import it.spaghettisource.tigersupply.engine.entity.EntityFactory;
import it.spaghettisource.tigersupply.engine.font.repository.FontRepositoryManager;
import it.spaghettisource.tigersupply.engine.image.finaleffect.FinalEffectManager;
import it.spaghettisource.tigersupply.engine.image.repository.ImageRepositoryManager;
import it.spaghettisource.tigersupply.engine.sprite.SpriteFactory;
import it.spaghettisource.tigersupply.sandbox.catalog.SandboxCase;
import it.spaghettisource.tigersupply.sandbox.catalog.SandboxCatalog;
import it.spaghettisource.tigersupply.sandbox.scene.SandboxMenuScene;
import it.spaghettisource.tigersupply.sandbox.scene.SandboxTestScene;

/**
 * The sandbox scene host: bootstraps the shared asset repositories (exactly like the game's host)
 * and navigates between the menu and test scenes.
 *
 * <p>It is the sandbox counterpart of the game's {@code TigerSupplySceneHost}: it initialises the
 * singleton repositories/factories, then shows the menu instead of the game's presentation scene.
 * Global quit keys are intercepted here; all other input is routed to the active scene.</p>
 */
public class SandboxSceneHost extends AbstractSceneHost {

	private final SandboxCatalog catalog;

	public SandboxSceneHost(JPanel panel, GameContext context) throws Exception {
		this.panel = panel;
		this.context = context;

		ImageRepositoryManager.init();
		FontRepositoryManager.init();
		AudioManager.init();
		FinalEffectManager.init(context);
		SpriteFactory.init();
		EntityFactory.init(context);

		catalog = new SandboxCatalog();

		showMenu();
	}

	public GameContext getGameContext() {
		return context;
	}

	public JPanel getGamePanel() {
		return (JPanel) panel;
	}

	/** Show the catalog menu. */
	public void showMenu() throws Exception {
		SandboxMenuScene menu = new SandboxMenuScene(context, this, catalog.getCases());
		menu.setGamePanel((JPanel) panel);
		this.activeScene = menu;
	}

	/**
	 * Run the given case in isolation.
	 *
	 * @param testCase the catalog case to launch
	 * @throws Exception if the test scene cannot be built
	 */
	public void showTest(SandboxCase testCase) throws Exception {
		SandboxTestScene test = new SandboxTestScene(context, this, testCase);
		test.setGamePanel((JPanel) panel);
		this.activeScene = test;
	}

	@Override
	public void keyPressed(KeyEvent event) {
		int code = event.getKeyCode();
		if (code == KeyEvent.VK_Q || code == KeyEvent.VK_END) {
			context.requestStopGame();
		} else {
			activeScene.keyPressed(event);
		}
	}
}
