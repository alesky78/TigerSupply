package it.spaghettisource.tigersupply.engine.windows;

import it.spaghettisource.tigersupply.engine.control.ApplicationContext;

import java.awt.Container;
import java.awt.GraphicsConfiguration;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;



/**
 * Application game is the entry point of the application
 * 
 * it has to take care about the windows process outside the logic of the game:
 * resize of the windows
 * inconize e deiconize
 * etc..
 * 
 * it has to interact with the low level windows api to get the size of the screen for the initialization of the game
 * 
 * 
 * @author Alessandro D'Ottavio
 *
 */
public class Application extends JFrame implements WindowListener{

	private int pWidth, pHeight;   // diemensions of the panel

	private ApplicationContext applicationContext;


	/**
	 * this is the entry point
	 * 
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		new Application();
	}


	/**
	 * default costructor
	 * @throws Exception 
	 */
	public Application() throws Exception{

		super("Tiger Suply");

		//hire to allow to panel to required the pause of game
		applicationContext = new ApplicationContext();

		pack();
		setResizable(false);   // sizes may change when non-resizable, so disable before calculate dimensions
		calculateSizesGameScreen();
		setResizable(true);

		//create the content of the frame
		Container c = getContentPane();
		GamePanel gamePanel = new GamePanel(applicationContext, pWidth, pHeight);
		c.add(gamePanel, "Center");
		pack();  // second, after JPanel added

		addWindowListener( this );

		setResizable(false);	//finally disable the resize
		setVisible(true);

	}


	/**
	 * Calculate the dimension of the internal Jpanel when the game will be draw
	 * but leaving room for the JFrame's title bar and insets, the OS's insets (e.g. taskbar) 
	 */
	private void calculateSizesGameScreen() {

		GraphicsConfiguration gc = getGraphicsConfiguration();
		Rectangle screenSize = gc.getBounds();	//max size of the screen

		Toolkit tk = Toolkit.getDefaultToolkit();
		Insets desktopOSInsets = tk.getScreenInsets(gc); //size of the insets of the Operativ System, usually windows as a toolbar in the bottom

		Insets frameInsets = getInsets();     // only works after a pack() call

		//original code to calculate the size of the panel, when i implemented the game was on another resolution, now i have to set the size of the panel to a fixed value, because the game is not ready for other resolution
		//pWidth = screenSize.width - (desktopOSInsets.left + desktopOSInsets.right)- (frameInsets.left + frameInsets.right);
		//pHeight = screenSize.height - (desktopOSInsets.top + desktopOSInsets.bottom) - (frameInsets.top + frameInsets.bottom);

		pWidth = 1360;
		pHeight = 660;
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
