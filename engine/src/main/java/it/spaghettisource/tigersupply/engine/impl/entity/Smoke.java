package it.spaghettisource.tigersupply.engine.impl.entity;

import it.spaghettisource.tigersupply.engine.control.ApplicationContext;

public class Smoke extends Effect {

	private float increaseForLoop;
	private float alpha;	
	
	
	
	public void setContext(ApplicationContext context) {
		super.setContext(context);
		spriteTimeDuration = 0.5f;
		float loops = (float) (spriteTimeDuration/context.getPeriodSeconds());
		increaseForLoop = 1 / loops;
		alpha = 1.0f;		
	}	
	
	
	public void updateEntity(float deltaSeconds)  throws Exception  {
		super.updateEntity(deltaSeconds);
		
		//manage alpha channel to let smoke become transparent
		alpha-=increaseForLoop;
		if(alpha<0){
			alpha = 0f;
		}
		
		sprite.setAlpha(alpha);
		
	}		
	
	
	
	
}
