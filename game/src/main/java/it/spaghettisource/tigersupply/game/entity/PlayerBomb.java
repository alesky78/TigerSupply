package it.spaghettisource.tigersupply.game.entity;

import it.spaghettisource.tigersupply.engine.entity.Entity;
import it.spaghettisource.tigersupply.engine.entity.manager.EntityManagerEntityRequest;

public class PlayerBomb extends BaseEntity {

	public static final int DIRECTION_UP =-1;
	public static final int DIRECTION_DOWN = 1;	
	
	private float maxSpeed;
	private EntityManagerEntityRequest<Entity> effectManager;
	private int direction;
	
	
	public PlayerBomb(){
		maxSpeed = 200;
		direction = DIRECTION_DOWN;
	}
	
	public void setDirection(int direction){
		this.direction = direction;
	}
	
	public void setEffectManager(EntityManagerEntityRequest<Entity> effectManager) {
		this.effectManager = effectManager;
	}

	public void updateEntity(float deltaSeconds)  throws Exception {

		speed.setSpeedY(speed.getSpeedY()+speed.getSpeedY()*0.02f);
		if(speed.getSpeedY()>maxSpeed && direction==DIRECTION_DOWN){
			speed.setSpeedY(maxSpeed);
		}
		
		if(speed.getSpeedY()< DIRECTION_UP*maxSpeed && direction==DIRECTION_UP){
			speed.setSpeedY(DIRECTION_UP*maxSpeed);
		}
		
		super.updateEntity(deltaSeconds);					
		
	}		
	
	
	
	
	
	
}
