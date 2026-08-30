package it.spaghettisource.tigersupply.engine.windows;



import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JPanel;

import it.spaghettisource.tigersupply.engine.control.GameLoop;
import it.spaghettisource.tigersupply.engine.control.GameContext;
import it.spaghettisource.tigersupply.engine.control.SceneHost;
import it.spaghettisource.tigersupply.engine.control.SceneHostFactory;

/**
 * this is the panel that will be use to draw the game
 * 
 * 
 * @author DOttavio
 *
 */
public class GamePanel extends JPanel{


	private final static int FPS_REQUIRED = 60;		//frame required in 1000 ms
	
	private GameContext context;
	private GameLoop gameLoop;

	public GamePanel(GameContext context, int pWidth, int pHeight, SceneHostFactory sceneHostFactory) throws Exception{

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
		
		SceneHost sceneHost = sceneHostFactory.create(this, context);
		
		gameLoop = new GameLoop(context,sceneHost);
				
		//create the game listeners
	    addMouseListener( new GamePanelMauseListener(sceneHost));
	    addKeyListener(new GamePanelKeyListener(sceneHost));
		addMouseMotionListener(new GamePanelMauseMotionListener(sceneHost));
	    
	    
	}

	
	// only start the game once the JPanel has been added to the JFrame
	public void addNotify(){ 
		super.addNotify();   // creates the peer
		context.requestStartGame();
		gameLoop.start();
	}


}
