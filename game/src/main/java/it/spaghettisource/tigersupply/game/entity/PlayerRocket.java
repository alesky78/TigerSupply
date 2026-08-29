package it.spaghettisource.tigersupply.game.entity;

import it.spaghettisource.tigersupply.engine.entity.Entity;
import it.spaghettisource.tigersupply.engine.entity.Position;
import it.spaghettisource.tigersupply.engine.entity.manager.EntityManagerEntityRequest;
import it.spaghettisource.tigersupply.game.utils.EntityFactoryWrapper;

public class PlayerRocket extends BaseEntity {

	
	private float maxSpeed;
	private float smokeCounter = 0;	
	private EntityManagerEntityRequest<Entity> effectManager;
	
	
	public PlayerRocket(){
		maxSpeed = 350;
	}
	
	public void setEffectManager(EntityManagerEntityRequest<Entity> effectManager) {
		this.effectManager = effectManager;
	}

	public void updateEntity(float deltaSeconds)  throws Exception {

		speed.setSpeedX(speed.getSpeedX()+speed.getSpeedX()*0.01f);
		if(speed.getSpeedX()>maxSpeed){
			speed.setSpeedX(maxSpeed);
		}
		
		super.updateEntity(deltaSeconds);	

		smokeCounter+=deltaSeconds;
		if(smokeCounter >deltaSeconds*2){
			smokeCounter = 0;
			Position smokePosition = new Position(position);
			smokePosition.increaseX(-size.getHalfWidth() -6);
			Smoke smokeEffect = EntityFactoryWrapper.newSmoke(smokePosition);			
			effectManager.addRquest(smokeEffect);	
		}
				
		
	}		
	
	
	
	
	
	
}
