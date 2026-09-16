package it.spaghettisource.tigersupply.sandbox;

import it.spaghettisource.tigersupply.engine.control.GameContext;
import it.spaghettisource.tigersupply.engine.control.SceneHostFactory;
import it.spaghettisource.tigersupply.engine.windows.GameFrame;

/**
 * Standalone entry point for the entity sandbox.
 *
 * <p>It composes the sandbox exactly as the game launcher composes the game - reusing the engine
 * {@link GameFrame} window shell - but binds the {@link SandboxSceneHostFactory} so the window opens
 * on the sandbox instead of the game. It is a separate deliverable and is not reachable from the
 * shipped game.</p>
 */
public class SandboxLauncher {

	private static final String WINDOW_TITLE = "Tiger Supply - Entity Sandbox";
	private static final int PLAYFIELD_WIDTH = 1350;
	private static final int PLAYFIELD_HEIGHT = 680;

	public static void main(String[] args) throws Exception {
		GameContext gameContext = new GameContext();
		SceneHostFactory sceneHostFactory = new SandboxSceneHostFactory();

		new GameFrame(WINDOW_TITLE, PLAYFIELD_WIDTH, PLAYFIELD_HEIGHT, gameContext, sceneHostFactory);
	}
}
