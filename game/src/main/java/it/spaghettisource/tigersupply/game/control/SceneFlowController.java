package it.spaghettisource.tigersupply.game.control;

import java.util.HashMap;
import java.util.Map;

import it.spaghettisource.tigersupply.engine.audio.AudioManager;
import it.spaghettisource.tigersupply.engine.control.AbstractScene;
import it.spaghettisource.tigersupply.engine.image.repository.ImageRepositoryManager;
import it.spaghettisource.tigersupply.game.entity.EnemyManager;
import it.spaghettisource.tigersupply.game.entity.Player;
import it.spaghettisource.tigersupply.game.scene.PresentationScene;
import it.spaghettisource.tigersupply.game.scene.GameOverScene;
import it.spaghettisource.tigersupply.game.scene.HangarScene;
import it.spaghettisource.tigersupply.game.scene.LevelScene;
import it.spaghettisource.tigersupply.game.utils.EntityFactoryWrapper;



/**
 * Singleton that drives the flow between the scenes (presentation, hangar, level, game-over) and
 * owns the state that persists across them (player, enemy manager, level progression).
 * 
 * it as to be a singleton and accessible from all the part of the game
 * 
 * 
 * @author Alessandro D'Ottavio
 *
 */
public class SceneFlowController {

	private static SceneFlowController instance;		
	private TigerSupplySceneHost sceneHost;
	private Player player;
	private EnemyManager enemyManager;	


	private Map<String, String> levelConfiguration = new HashMap<String, String>();
	private int numberLevel = 1;
	private int actualLevel = 0;	

	private SceneFlowController(TigerSupplySceneHost sceneHost) throws Exception {
		this.sceneHost = sceneHost;		
		player = EntityFactoryWrapper.newPlayer(sceneHost.getGameContext().getScreenHeight(), sceneHost.getGameContext().getPeriodMilliseconds());
		player.getsize().setScale(1.2f);
		enemyManager = new EnemyManager();

		//hire put all the levels levels
		levelConfiguration.put("1", "level/level-1.xml");

	}

	public static void init(TigerSupplySceneHost sceneHost) throws Exception{
		if(instance==null){
			synchronized (SceneFlowController.class) {
				if(instance==null){
					instance = new SceneFlowController(sceneHost);
				}
			}
		}
	}

	public static SceneFlowController getInstance() throws Exception{
		if(instance==null){
			Exception ex = new Exception("FlowController class must by initialized before to use it");
			throw ex;
		}
		return instance;
	}	 	

	/**
	 * clear all variables and obtain free memory
	 * 
	 * @param clearPlayer
	 * @throws Exception
	 */
	private void clear(boolean clearPlayer) throws Exception{
		if(clearPlayer){
			player.reset();
		}
		enemyManager.reset();
		ImageRepositoryManager.getInstance().cleanVolatileImages();
		System.gc();
	}
	

	public void doPresentation() throws Exception{
		try{
			actualLevel = 0;	//set to no level so that the first call to level move to first level
			clear(true);		
			AbstractScene scene = new PresentationScene(sceneHost.getGameContext());
			scene.setGamePanel(sceneHost.getGamePanel());
			sceneHost.setActiveScene(scene);
		}catch(Exception ex){
			Exception e =new Exception("error in the GameflowManger:"+ex.getMessage(),ex);
			throw e;
		}
	}

	public void doGameOver() throws Exception{
		try{
			clear(true);
			AbstractScene scene = new GameOverScene(sceneHost.getGameContext());
			scene.setGamePanel(sceneHost.getGamePanel());
			sceneHost.setActiveScene(scene);
		}catch(Exception ex){
			Exception e =new Exception("error in the GameflowManger:"+ex.getMessage(),ex);
			throw e;
		}
	}	

	public void doHangar() throws Exception{
		try{
			clear(false);
			AbstractScene scene = new HangarScene(sceneHost.getGameContext(),player);
			scene.setGamePanel(sceneHost.getGamePanel());
			sceneHost.setActiveScene(scene);
		}catch(Exception ex){
			Exception e =new Exception("error in the GameflowManger:"+ex.getMessage(),ex);
			throw e;
		}
	}		
	
	public void doNextLevel() throws Exception{
		try{	
			actualLevel++;
			if(actualLevel==1){
				clear(true);	
			}else{
				clear(false);
			}
			
			if(actualLevel>numberLevel){//GAME FINISH
				doPresentation();
			}else{//GO NEXT LEVEL
				AudioManager.getInstance().playMusic("mainTheme", true);
				
				String nextLevelCode =Integer.toString(actualLevel);
				enemyManager.setLevelDataFile(levelConfiguration.get(nextLevelCode));
				AbstractScene scene = new LevelScene(sceneHost.getGameContext(),player,enemyManager);
				scene.setGamePanel(sceneHost.getGamePanel());
				sceneHost.setActiveScene(scene);
			}
		}catch(Exception ex){
			Exception e =new Exception("error in the GameflowManger:"+ex.getMessage(),ex);
			throw e;
		}
	}	

}
