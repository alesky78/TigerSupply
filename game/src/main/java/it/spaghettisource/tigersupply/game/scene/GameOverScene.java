package it.spaghettisource.tigersupply.game.scene;

import it.spaghettisource.tigersupply.engine.control.AbstractSceneJPanel;
import it.spaghettisource.tigersupply.engine.control.GameContext;
import it.spaghettisource.tigersupply.engine.entity.Entity;
import it.spaghettisource.tigersupply.engine.entity.manager.EntityManagerEntityRequest;
import it.spaghettisource.tigersupply.engine.font.repository.FontRepositoryManager;
import it.spaghettisource.tigersupply.engine.image.repository.ImageRepositoryManager;
import it.spaghettisource.tigersupply.game.control.SceneFlowController;
import it.spaghettisource.tigersupply.game.utils.EntityFactoryWrapper;
import it.spaghettisource.tigersupply.game.utils.GameResources;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;

public class GameOverScene extends AbstractSceneJPanel {

	private GameContext context;

	//explosion management
	protected int particleNum;
	protected int particleDeathMaxSize;	
	protected int particleDeathMaxSpeed;		
	protected float particleDeathMaxLifeTime;

	protected EntityManagerEntityRequest<Entity> spriteManger; 
	
	
	public GameOverScene(GameContext context){
		this.context = context;
		this.pWidth = context.getScreenWidth();
		this.pHeight = context.getScreenHeight();
	
		particleNum = 4;
		particleDeathMaxSize = 75;	
		particleDeathMaxSpeed = 40;	
		particleDeathMaxLifeTime= 10f;	

		spriteManger = new EntityManagerEntityRequest<Entity>();
		spriteManger.init(context);
	}

	
	protected void addExplosionToManager(){
		for (int i = 0; i < particleNum; i++) {					
			spriteManger.addSrpiteToBeManaged(EntityFactoryWrapper.newExplosionParticleEnergetic(context.getScreenWidth()/2, context.getScreenHeight()/2, particleDeathMaxSize, particleDeathMaxSpeed, particleDeathMaxLifeTime,context));			
		}	
	}	
	
	
	public void update(float deltaTimeSeconds) throws Exception{
		addExplosionToManager();
		spriteManger.updateEntity(deltaTimeSeconds);
	}


	public void internalRender(Graphics2D dbg) throws Exception {
	    
		dbg.setColor(new Color(0, 0, 0));
	    dbg.fillRect(0, 0, pWidth, pHeight);		

		dbg.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
	    dbg.setFont(FontRepositoryManager.getInstance().getFont(GameResources.FONT_TECHNO, 20));
	    
		dbg.setColor(new Color(255, 0, 0));
		dbg.drawString("GAME OVER PRESS FIRE TO START!", 700, 500);
		
		dbg.drawString("Esplosione!", 10, 80);
		dbg.drawString("sprites:"+spriteManger.getManagedEntities().size(), 10, 110);		
		
		

		
		spriteManger.renderEntity(dbg);
		
	}

	public void doFinalEffect(Graphics2D dbg) throws Exception {
	}	

	public void keyPressed(KeyEvent event) {}

	public void keyReleased(KeyEvent event){
		if ((event.getKeyCode() == KeyEvent.VK_SPACE) ) {
			try {
				SceneFlowController.getInstance().doPresentation();
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}


	public void mousePressed(int x, int y) {}

	public void mouseMoved(MouseEvent event) {}


	
}
