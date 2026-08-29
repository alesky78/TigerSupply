package it.spaghettisource.tigersupply.engine.impl.control;

import java.util.HashMap;
import java.util.Map;

import it.spaghettisource.tigersupply.engine.audio.AudioManager;
import it.spaghettisource.tigersupply.engine.control.AbstractGameJPanel;
import it.spaghettisource.tigersupply.engine.image.repository.ImageRepositoryManager;
import it.spaghettisource.tigersupply.engine.impl.entity.EnemyManager;
import it.spaghettisource.tigersupply.engine.impl.entity.Player;
import it.spaghettisource.tigersupply.engine.impl.scene.PresentationScene;
import it.spaghettisource.tigersupply.engine.impl.scene.GameOverScene;
import it.spaghettisource.tigersupply.engine.impl.scene.HangarScene;
import it.spaghettisource.tigersupply.engine.impl.scene.LevelScene;
import it.spaghettisource.tigersupply.engine.impl.utils.EntityFactoryWrapper;



/**
 * this controller base on the MVC logic 
 * it as to keep the loginc to navigate from one scree ot other of the game
 * 
 * it as to be a singleton and accessible from all the part of the game
 * 
 * 
 * @author Alessandro D'Ottavio
 *
 */
public class GameFlowController {

	private static GameFlowController instance;		
	private GameManager gameManager;
	private Player player;
	private EnemyManager enemyManager;	


	private Map<String, String> levelConfiguration = new HashMap<String, String>();
	private int numberLevel = 1;
	private int actualLevel = 0;	

	private GameFlowController(GameManager gameManager) throws Exception {
		this.gameManager = gameManager;		
		player = EntityFactoryWrapper.newPlayer(gameManager.getGameContext().getScreenHeight(), gameManager.getGameContext().getPeriodMilliseconds());
		player.getsize().setScale(1.2f);
		enemyManager = new EnemyManager();

		//hire put all the levels levels
		levelConfiguration.put("1", "level/level-1.xml");

	}

	public static void init(GameManager gameManager) throws Exception{
		if(instance==null){
			synchronized (GameFlowController.class) {
				if(instance==null){
					instance = new GameFlowController(gameManager);
				}
			}
		}
	}

	public static GameFlowController getInstance() throws Exception{
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
			AbstractGameJPanel game = new PresentationScene(gameManager.getGameContext());
			game.setGamePanel(gameManager.getGamePanel());
			gameManager.setActualGame(game);
		}catch(Exception ex){
			Exception e =new Exception("error in the GameflowManger:"+ex.getMessage(),ex);
			throw e;
		}
	}

	public void doGameOver() throws Exception{
		try{
			clear(true);
			AbstractGameJPanel game = new GameOverScene(gameManager.getGameContext());
			game.setGamePanel(gameManager.getGamePanel());
			gameManager.setActualGame(game);
		}catch(Exception ex){
			Exception e =new Exception("error in the GameflowManger:"+ex.getMessage(),ex);
			throw e;
		}
	}	

	public void doHangar() throws Exception{
		try{
			clear(false);
			AbstractGameJPanel game = new HangarScene(gameManager.getGameContext(),player);
			game.setGamePanel(gameManager.getGamePanel());
			gameManager.setActualGame(game);
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
				AbstractGameJPanel game = new LevelScene(gameManager.getGameContext(),player,enemyManager);
				game.setGamePanel(gameManager.getGamePanel());
				gameManager.setActualGame(game);
			}
		}catch(Exception ex){
			Exception e =new Exception("error in the GameflowManger:"+ex.getMessage(),ex);
			throw e;
		}
	}	

}
