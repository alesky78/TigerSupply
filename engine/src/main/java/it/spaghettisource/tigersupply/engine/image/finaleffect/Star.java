package it.spaghettisource.tigersupply.engine.image.finaleffect;



import java.awt.Graphics2D;

import it.spaghettisource.tigersupply.engine.control.GameContext;
import it.spaghettisource.tigersupply.engine.entity.manager.EntityManagerRemovable;

public class Star extends AbstractFinalEffect {

	private int screenWidth;
	private int screenHeight;	
	private float secondNextStar;
	private float periodCounter;	
	
	
	private EntityManagerRemovable<StarEntity> manager = new EntityManagerRemovable<StarEntity>();
	

	public void configAndStart(float secondNextStar, GameContext context){
		active = true;
		periodCounter = 0;
		
		manager.init(context);
		
		this.screenHeight = context.getScreenHeight();
		this.screenWidth = context.getScreenWidth();	
		this.secondNextStar = secondNextStar;
	}	
	
	
	public void reset(){
		periodCounter = 0;
		active = false;
	}	
	
	
	public void updateEffect(float deltaSeconds) throws Exception {
		
		periodCounter += deltaSeconds; 
		
		if(periodCounter>=secondNextStar){
			manager.addSrpiteToBeManaged(new StarEntity(screenWidth-1, screenHeight));
			periodCounter = 0;
		}
		
		manager.updateEntity(deltaSeconds);
	}


	public void renderEffect(Graphics2D dbg, int screenWidth, int screenHeight) throws Exception {
		manager.renderEntity(dbg);
	}

}
