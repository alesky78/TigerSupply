package it.spaghettisource.tigersupply.engine.impl.control;


import java.awt.event.KeyEvent;

import javax.swing.JPanel;

import it.spaghettisource.tigersupply.engine.audio.AudioManager;
import it.spaghettisource.tigersupply.engine.control.AbstractGameManagerJPanel;
import it.spaghettisource.tigersupply.engine.control.ApplicationContext;
import it.spaghettisource.tigersupply.engine.control.Game;
import it.spaghettisource.tigersupply.engine.entity.EntityFactory;
import it.spaghettisource.tigersupply.engine.font.repository.FontRepositoryManager;
import it.spaghettisource.tigersupply.engine.image.finaleffect.FinalEffectManager;
import it.spaghettisource.tigersupply.engine.image.repository.ImageRepositoryManager;
import it.spaghettisource.tigersupply.engine.sprite.SpriteFactory;

public class GameManager extends AbstractGameManagerJPanel {


	public void setActualGame(Game actualGame){
		this.actualGame = actualGame;
	}
	
	public JPanel getGamePanel(){
		return panel;
	}
	
	public ApplicationContext getGameContext(){
		return context;
	}	
	
	public GameManager(JPanel panel,ApplicationContext context) throws Exception{
		this.panel = panel;
		this.context = context;		

		ImageRepositoryManager.init();
		FontRepositoryManager.init();
		AudioManager.init();		
		FinalEffectManager.init(context);
		SpriteFactory.init();
		EntityFactory.init(context);
		GameFlowController.init(this);
		
		//start the game with the presentation
		GameFlowController.getInstance().doPresentation();

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
			actualGame.keyPressed(event);			
		}
	}
	
	
}
