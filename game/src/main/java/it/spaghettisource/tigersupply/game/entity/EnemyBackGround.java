package it.spaghettisource.tigersupply.game.entity;

import it.spaghettisource.tigersupply.engine.entity.Entity;


/**
 * this are the enemy that are in background and don't partecipate to the war
 * can not interact with the player and can not be destroied
 * 
 * @author DOttavio
 *
 */
public class EnemyBackGround extends Enemy {	
	

	public EnemyBackGround(){
		super();
		life = 0;			
	}	


	public void updateEntity(float deltaSeconds) throws Exception {
		position.setAngle(180);
		super.updateEntity(deltaSeconds);
	}
	

	public boolean collidedWith(Entity other) {
		return false;
	}

}
