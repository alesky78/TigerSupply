package it.spaghettisource.tigersupply.game.scene;



import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import it.spaghettisource.tigersupply.engine.audio.AudioManager;
import it.spaghettisource.tigersupply.engine.background.BackGround;
import it.spaghettisource.tigersupply.engine.background.BackGroundFitImage;
import it.spaghettisource.tigersupply.engine.background.BackGroundTexture;
import it.spaghettisource.tigersupply.engine.background.ParallaxBackGround;
import it.spaghettisource.tigersupply.engine.control.AbstractSceneJPanel;
import it.spaghettisource.tigersupply.engine.control.GameContext;
import it.spaghettisource.tigersupply.engine.entity.Entity;
import it.spaghettisource.tigersupply.engine.entity.collision.CollisionDetector;
import it.spaghettisource.tigersupply.engine.entity.EntityGroupScreenBound;
import it.spaghettisource.tigersupply.engine.font.repository.FontRepositoryManager;
import it.spaghettisource.tigersupply.engine.image.repository.ImageRepositoryManager;
import it.spaghettisource.tigersupply.game.control.SceneFlowController;
import it.spaghettisource.tigersupply.game.entity.EnemyManager;
import it.spaghettisource.tigersupply.game.entity.Player;
import it.spaghettisource.tigersupply.game.utils.EntityZComparator;
import it.spaghettisource.tigersupply.game.utils.GameResources;

public class LevelScene extends AbstractSceneJPanel {

	private GameContext context;

	//game entities
	private Player playerShip;
	private EnemyManager enemyManager;


	private EntityGroupScreenBound<Entity> playerShootManager;
	private EntityGroupScreenBound<Entity> enemyShootManager;
	private EntityGroupScreenBound<Entity> effectManager;		

	private CollisionDetector collisionDetectorPlayerVsEnemyShot;
	private CollisionDetector collisionDetectorPlayerVsEnemy;
	private CollisionDetector collisionDetectorPlayerShotVsEnemy;

	private BackGround backGround;

	List<Entity> renderSprites = new ArrayList<Entity>();	//used to manage the sprites to render	
	EntityZComparator comparator = new EntityZComparator();	//use to order the renderSprites list

	public LevelScene(GameContext context,Player player,EnemyManager eManager) throws Exception{
		this.context = context;
		this.pWidth = context.getScreenWidth();
		this.pHeight = context.getScreenHeight();

		//shot managers
		playerShootManager = new EntityGroupScreenBound<Entity>();
		enemyShootManager = new EntityGroupScreenBound<Entity>();
		playerShootManager.init(context);
		enemyShootManager.init(context);
		
		//effect manager
		effectManager = new EntityGroupScreenBound<Entity>();
		effectManager.init(context);

		//player ship
		playerShip = player;		
		playerShip.setShootManager(playerShootManager);
		playerShip.setEffectManager(effectManager);
		playerShip.setContext(context);
		playerShip.startPosition();

		//enemy manager		
		enemyManager = eManager; 
		enemyManager.setShotManager(enemyShootManager);
		enemyManager.setEffectManager(effectManager);
		enemyManager.setPlayer(playerShip);
		enemyManager.init(context);
		enemyManager.initComponents();


		//collisions detector
		collisionDetectorPlayerVsEnemy = new CollisionDetector(playerShip, enemyManager);
		collisionDetectorPlayerVsEnemyShot = new CollisionDetector(playerShip,enemyShootManager);
		collisionDetectorPlayerShotVsEnemy = new CollisionDetector(playerShootManager, enemyManager);

		//TODO il backGround deve essere configurabile cosi e statico in questo costruttore e non si puo' generalizzare 
		ParallaxBackGround px = new ParallaxBackGround();				
		//px.addBackGround(new BackGroundFitImage(ImageRepositoryManager.getInstance().getSingleImage("bcgr_space"), 0.2f, pWidth, pHeight,true)); 
		px.addBackGround(new BackGroundTexture(ImageRepositoryManager.getInstance().getSingleImage("bcgr_texture_space"),10f, pWidth, pHeight,true) );
		px.addBackGround(new BackGroundFitImage(ImageRepositoryManager.getInstance().getSingleImage("bcgr_planet"), 0.01f, pWidth, pHeight,false));		
		backGround = px;		

	}


	public void update(float deltaTimeSeconds) throws Exception{
		if (!context.isPaused() && !context.isStop()){
			magageGameFlow();
			playerShip.updateEntity(deltaTimeSeconds);
			enemyManager.updateEntity(deltaTimeSeconds);

			effectManager.updateEntity(deltaTimeSeconds);

			playerShootManager.updateEntity(deltaTimeSeconds);
			enemyShootManager.updateEntity(deltaTimeSeconds);	

			collisionDetectorPlayerVsEnemy.detectCollision();
			collisionDetectorPlayerVsEnemyShot.detectCollision();
			collisionDetectorPlayerShotVsEnemy.detectCollision(); 

			backGround.updateBackground(deltaTimeSeconds);
		}

	}

	/**
	 * internal method to manage the flow of the game
	 * @throws Exception 
	 */
	private void magageGameFlow() throws Exception {
		if(!playerShip.isLive()){
			AudioManager.getInstance().stopAllAudio();
			SceneFlowController.getInstance().doGameOver();
		}
		if(enemyManager.isBossDeath()){
			AudioManager.getInstance().stopAllAudio();
			SceneFlowController.getInstance().doNextLevel();
		}
	}

	public void internalRender(Graphics2D dbg) throws Exception {
		
		dbg.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING,RenderingHints.VALUE_COLOR_RENDER_SPEED);
		dbg.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION,RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED );
		dbg.setRenderingHint(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_SPEED );		
		dbg.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
	    
		dbg.setFont(FontRepositoryManager.getInstance().getFont(GameResources.FONT_TECHNO, 20));
		
		backGround.renderBackground(dbg);
		
		//TODO on the top energy and info like points, life etc....
		dbg.setColor(Color.red);
		dbg.drawString("Life:"+playerShip.numberLive(), 5, 15);
		
		//order the s sprite before to draw by z coordinate
		renderSprites.addAll(playerShootManager.getManagedEntities());
		renderSprites.addAll(enemyShootManager.getManagedEntities());
		renderSprites.addAll(effectManager.getManagedEntities());
		renderSprites.addAll(enemyManager.getManagedEntities());
		renderSprites.add(playerShip);
		Collections.sort(renderSprites, comparator);
		
		for (Entity entity : renderSprites) {
			entity.renderEntity(dbg);
		}
		
		renderSprites.clear();

	}
	
	public void doFinalEffect(Graphics2D dbg) throws Exception {
	}		


	public void keyPressed(KeyEvent event) {
		playerShip.KeyboardPressed(event);
	}

	public void keyReleased(KeyEvent event){
		playerShip.KeyboardReleased(event);
	}


	public void mousePressed(int x, int y) {}

	public void mouseMoved(MouseEvent event) {}	

}
