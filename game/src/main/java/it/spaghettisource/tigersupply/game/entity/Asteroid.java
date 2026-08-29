package it.spaghettisource.tigersupply.game.entity;



public class Asteroid extends Enemy {	

	float angle = 0;

	public Asteroid(){
		super();
		life = 20;	
		particleNum = 120;
		particleMaxSize = 40;
		particleDeathMaxSize = 60;	
		particleMaxSpeed = 90;
		particleDeathMaxSpeed = 100 ;	
		particleMaxLifeTime = 0.3f;	
		particleDeathMaxLifeTime= 0.5f;				
	}	


	public void updateEntity(float deltaSeconds) throws Exception {
		angle+=1;
		position.setAngle(angle);
		//do the sprite logic
		super.updateEntity(deltaSeconds);
	}

}
