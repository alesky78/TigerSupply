package it.spaghettisource.tigersupply.launcher;

import it.spaghettisource.tigersupply.engine.control.GameContext;
import it.spaghettisource.tigersupply.engine.control.SceneManagerFactory;
import it.spaghettisource.tigersupply.engine.windows.GameFrame;

/**
 * Application entry point and composition root for TigerSupply.
 *
 * <p>Owns the game-specific launch configuration (window title and playfield
 * dimensions), selects the concrete game via {@link TigerSupplySceneManagerFactory},
 * and hands both to the engine {@link GameFrame} window shell.
 */
public class Launcher {

	private static final String WINDOW_TITLE = "Tiger Supply";
	private static final int PLAYFIELD_WIDTH = 1350;
	private static final int PLAYFIELD_HEIGHT = 680;

	/**
	 * Application entry point: composes the game and shows its window.
	 *
	 * @param args ignored
	 * @throws Exception if the game window or its backing resources fail to initialize
	 */
	public static void main(String[] args) throws Exception {

		GameContext gameContext = new GameContext();
		SceneManagerFactory sceneManagerFactory = new TigerSupplySceneManagerFactory();

		new GameFrame(WINDOW_TITLE, PLAYFIELD_WIDTH, PLAYFIELD_HEIGHT, gameContext, sceneManagerFactory);
	}

}
