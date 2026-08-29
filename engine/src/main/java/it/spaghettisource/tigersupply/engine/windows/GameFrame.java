package it.spaghettisource.tigersupply.engine.windows;

import it.spaghettisource.tigersupply.engine.control.ApplicationContext;
import it.spaghettisource.tigersupply.engine.control.GameManagerFactory;

import java.awt.Container;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;



/**
 * Reusable top-level window shell that hosts the {@link GamePanel} and bridges
 * the AWT window lifecycle to the {@link ApplicationContext}.
 *
 * <p>The shell is game-agnostic: the concrete game is supplied indirectly through
 * the {@link GameManagerFactory} handed to the panel. The window title and the
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

	private ApplicationContext applicationContext;


	/**
	 * Build and show the game window.
	 *
	 * @param title the window title, never {@code null}
	 * @param width the playfield width in pixels
	 * @param height the playfield height in pixels
	 * @param applicationContext the shared game lifecycle context, never {@code null}
	 * @param gameManagerFactory factory that builds the concrete game manager, never {@code null}
	 * @throws Exception if the hosted game panel fails to initialize
	 */
	public GameFrame(String title, int width, int height, ApplicationContext applicationContext, GameManagerFactory gameManagerFactory) throws Exception{

		super(title);

		//hold the context so the window events can drive pause/resume/stop of the game
		this.applicationContext = applicationContext;
		this.pWidth = width;
		this.pHeight = height;

		//create the content of the frame
		Container c = getContentPane();
		GamePanel gamePanel = new GamePanel(applicationContext, pWidth, pHeight, gameManagerFactory);
		c.add(gamePanel, "Center");
		pack();  // size the frame to fit the game panel

		addWindowListener( this );

		setResizable(false);	//the game is not ready for other resolutions
		setVisible(true);

	}




	//manage hire all the listener for the lifecicle of the windows

	public void windowActivated(WindowEvent e) 
	{ applicationContext.requestResumeGame();  }

	public void windowDeactivated(WindowEvent e) 
	{  applicationContext.requestPauseGame();  }

	public void windowDeiconified(WindowEvent e) 
	{  applicationContext.requestResumeGame();  }

	public void windowIconified(WindowEvent e) 
	{  applicationContext.requestPauseGame(); }

	public void windowClosing(WindowEvent e)
	{  applicationContext.requestStopGame();  }

	public void windowClosed(WindowEvent e) {}

	public void windowOpened(WindowEvent e) {}

}
