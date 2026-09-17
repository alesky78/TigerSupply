package it.spaghettisource.tigersupply.game.entity.enemy;

import it.spaghettisource.tigersupply.game.entity.effect.ExplosionProfileFactory;


public class Asteroid extends Enemy {	

	float angle = 0;

	public Asteroid(){
		super();
		life = 20;	
		hitProfile = ExplosionProfileFactory.asteroidHit();
		deathProfile = ExplosionProfileFactory.asteroidDeath();
	}	


	public void updateEntity(float deltaSeconds) throws Exception {
		angle+=1;
		position.setAngle(angle);
		//do the sprite logic
		super.updateEntity(deltaSeconds);
	}

}
