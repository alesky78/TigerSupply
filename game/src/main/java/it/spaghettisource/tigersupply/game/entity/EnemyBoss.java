package it.spaghettisource.tigersupply.game.entity;

import it.spaghettisource.tigersupply.game.weapon.Weapon;
import it.spaghettisource.tigersupply.game.weapon.enemy.LightinBoltLaser;
import it.spaghettisource.tigersupply.game.weapon.enemy.PlasmaCannon;

public class EnemyBoss extends Enemy {

	
	private int explosionCounter = 0;
	private float maxSpeed = 30;	
	
	public EnemyBoss(){
		super();
		life = 100;	
		particleNum = 200;
		particleMaxSize = 40;
		particleDeathMaxSize = 90;	
		particleMaxSpeed = 180;
		particleDeathMaxSpeed = 150 ;	
		particleMaxLifeTime = 0.3f;	
		particleDeathMaxLifeTime= 0.9f;		
		
		weapons = new Weapon[2];
		weapons[0] = new PlasmaCannon(); 
		weapons[0].setOwner(this);		
		weapons[1] = new LightinBoltLaser(); 
		weapons[1].setOwner(this);				
		
	}

	public void updateEntity(float deltaSeconds) throws Exception  {
		//the enemy try to hit player with the laser beam
		
		if(target.getPosition().getPosY() > position.getPosY()){
			speed.setSpeedY(maxSpeed);
		}else if(target.getPosition().getPosY() < position.getPosY()){
			speed.setSpeedY(-maxSpeed);
		}else{
			speed.setSpeedY(0);
		}
			
		
		super.updateEntity(deltaSeconds);
		
		explosionCounter += deltaSeconds;
		if(life<15 && explosionCounter > 2){
			explosionCounter = 0;
			addRandomExplosion();
		}
	}
	
	public boolean isOutOfScreen(int windowWidth, int windowHeight){
			return false;
	}	
	
	
	private void addRandomExplosion(){			
		createExplosionParticleFire(particleNum, (int)(position.getPosX()+(Math.random()*size.getHalfWidth())),(int)(position.getPosY()+(Math.random()*size.getHalfHeigh())), 
				60, 150, 0.5f);
	}

}
