package it.spaghettisource.tigersupply.engine.windows;

import it.spaghettisource.tigersupply.engine.control.GameContext;
import it.spaghettisource.tigersupply.engine.control.SceneManagerFactory;

import java.awt.Container;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;



/**
 * Reusable top-level window shell that hosts the {@link GamePanel} and bridges
 * the AWT window lifecycle to the {@link GameContext}.
 *
 * <p>The shell is game-agnostic: the concrete game is supplied indirectly through
 * the {@link SceneManagerFactory} handed to the panel. The window title and the
 * playfield dimensions are provided by the composition root (the launcher module),
 * rather than hard-coded here.
 *
 * <p>Its responsibilities are limited to the windowing concerns that live outside
 * the game logic: sizing the frame to the panel and reacting to the window
 * lifecycle (iconify/deiconify, activate/deactivate, close).
 *
 * @author Alessandro D'Ottavio
 *
 */
public class GameFrame extends JFrame implements WindowListener{

	private int pWidth, pHeight;   // dimensions of the panel

	private GameContext gameContext;


	/**
	 * Build and show the game window.
	 *
	 * @param title the window title, never {@code null}
	 * @param width the playfield width in pixels
	 * @param height the playfield height in pixels
	 * @param gameContext the shared game lifecycle context, never {@code null}
	 * @param sceneManagerFactory factory that builds the concrete scene manager, never {@code null}
	 * @throws Exception if the hosted game panel fails to initialize
	 */
	public GameFrame(String title, int width, int height, GameContext gameContext, SceneManagerFactory sceneManagerFactory) throws Exception{

		super(title);

		//hold the context so the window events can drive pause/resume/stop of the game
		this.gameContext = gameContext;
		this.pWidth = width;
		this.pHeight = height;

		//create the content of the frame
		Container c = getContentPane();
		GamePanel gamePanel = new GamePanel(gameContext, pWidth, pHeight, sceneManagerFactory);
		c.add(gamePanel, "Center");
		pack();  // size the frame to fit the game panel

		addWindowListener( this );

		setResizable(false);	//the game is not ready for other resolutions
		setVisible(true);

	}




	//manage hire all the listener for the lifecicle of the windows

	public void windowActivated(WindowEvent e) 
	{ gameContext.requestResumeGame();  }

	public void windowDeactivated(WindowEvent e) 
	{  gameContext.requestPauseGame();  }

	public void windowDeiconified(WindowEvent e) 
	{  gameContext.requestResumeGame();  }

	public void windowIconified(WindowEvent e) 
	{  gameContext.requestPauseGame(); }

	public void windowClosing(WindowEvent e)
	{  gameContext.requestStopGame();  }

	public void windowClosed(WindowEvent e) {}

	public void windowOpened(WindowEvent e) {}

}
