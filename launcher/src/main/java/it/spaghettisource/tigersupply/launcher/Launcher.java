package it.spaghettisource.tigersupply.launcher;

import it.spaghettisource.tigersupply.engine.control.ApplicationContext;
import it.spaghettisource.tigersupply.engine.control.GameManagerFactory;
import it.spaghettisource.tigersupply.engine.windows.GameFrame;

/**
 * Application entry point and composition root for TigerSupply.
 *
 * <p>Owns the game-specific launch configuration (window title and playfield
 * dimensions), selects the concrete game via {@link TigerSupplyGameManagerFactory},
 * and hands both to the engine {@link GameFrame} window shell.
 */
public class Launcher {

	private static final String WINDOW_TITLE = "Tiger Suply";
	private static final int PLAYFIELD_WIDTH = 1360;
	private static final int PLAYFIELD_HEIGHT = 660;

	/**
	 * Application entry point: composes the game and shows its window.
	 *
	 * @param args ignored
	 * @throws Exception if the game window or its backing resources fail to initialize
	 */
	public static void main(String[] args) throws Exception {

		ApplicationContext applicationContext = new ApplicationContext();
		GameManagerFactory gameManagerFactory = new TigerSupplyGameManagerFactory();

		new GameFrame(WINDOW_TITLE, PLAYFIELD_WIDTH, PLAYFIELD_HEIGHT, applicationContext, gameManagerFactory);
	}

}
