package it.spaghettisource.tigersupply.game.control;


import java.awt.event.KeyEvent;

import javax.swing.JPanel;

import it.spaghettisource.tigersupply.engine.audio.AudioManager;
import it.spaghettisource.tigersupply.engine.control.AbstractSceneHost;
import it.spaghettisource.tigersupply.engine.control.GameContext;
import it.spaghettisource.tigersupply.engine.control.Scene;
import it.spaghettisource.tigersupply.engine.entity.EntityFactory;
import it.spaghettisource.tigersupply.engine.font.repository.FontRepositoryManager;
import it.spaghettisource.tigersupply.engine.image.finaleffect.FinalEffectManager;
import it.spaghettisource.tigersupply.engine.image.repository.ImageRepositoryManager;
import it.spaghettisource.tigersupply.engine.sprite.SpriteFactory;

/**
 * TigerSupply's concrete scene host: it extends {@link AbstractSceneHost} to bootstrap the shared
 * repositories and managers, hold the active {@link Scene}, and intercept the global pause/quit
 * keys before delegating input to the active scene.
 *
 * @author Alessandro D'Ottavio
 */
public class TigerSupplySceneHost extends AbstractSceneHost {


	public void setActiveScene(Scene activeScene){
		this.activeScene = activeScene;
	}
	
	public JPanel getGamePanel(){
		return panel;
	}
	
	public GameContext getGameContext(){
		return context;
	}	
	
	public TigerSupplySceneHost(JPanel panel,GameContext context) throws Exception{
		this.panel = panel;
		this.context = context;		

		ImageRepositoryManager.init();
		FontRepositoryManager.init();
		AudioManager.init();		
		FinalEffectManager.init(context);
		SpriteFactory.init();
		EntityFactory.init(context);
		SceneFlowController.init(this);
		
		//start the game with the presentation
		SceneFlowController.getInstance().doPresentation();

	}
	

	public void keyPressed(KeyEvent event) {
		if ((event.getKeyCode() == KeyEvent.VK_ESCAPE) || (event.getKeyCode() == KeyEvent.VK_Q) || (event.getKeyCode() == KeyEvent.VK_END)) {
			context.requestStopGame();
		}else if(event.getKeyCode() == KeyEvent.VK_P){
			if(context.isPaused()){
				context.requestResumeGame();
			}else{
				context.requestPauseGame();				
			}
		}else{
			activeScene.keyPressed(event);			
		}
	}
	
	
}
