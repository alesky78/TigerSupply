package it.spaghettisource.tigersupply.engine.windows;



import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JPanel;

import it.spaghettisource.tigersupply.engine.control.AnimationLoop;
import it.spaghettisource.tigersupply.engine.control.ApplicationContext;
import it.spaghettisource.tigersupply.engine.control.GameManager;
import it.spaghettisource.tigersupply.engine.control.GameManagerFactory;

/**
 * this is the panel that will be use to draw the game
 * 
 * 
 * @author DOttavio
 *
 */
public class GamePanel extends JPanel{


	private final static int FPS_REQUIRED = 60;		//frame required in 1000 ms
	
	private ApplicationContext context;
	private AnimationLoop animationLoop;

	public GamePanel(ApplicationContext context, int pWidth, int pHeight, GameManagerFactory gameManagerFactory) throws Exception{

		float period = 1000.0f/FPS_REQUIRED;
		
		this.context = context; 

		setBackground(Color.white);
		setPreferredSize( new Dimension(pWidth, pHeight));

		setFocusable(true);
		requestFocus();    // the JPanel now has focus, so receives key events
		
		context.setPeriodInMilliseconds(period);
		//TODO +10 al size dello screen � un bug? avviene anche nelle app del professore
		context.setScreenHeight(pHeight+10);
		context.setScreenWidth(pWidth+10);
		
		GameManager manager = gameManagerFactory.create(this, context);
		
		animationLoop = new AnimationLoop(context,manager);
				
		//create the game listeners
	    addMouseListener( new GamePanelMauseListener(manager));
	    addKeyListener(new GamePanelKeyListener(manager));
		addMouseMotionListener(new GamePanelMauseMotionListener(manager));
	    
	    
	}

	
	// only start the game once the JPanel has been added to the JFrame
	public void addNotify(){ 
		super.addNotify();   // creates the peer
		context.requierdStart();
		animationLoop.start();
	}


}
