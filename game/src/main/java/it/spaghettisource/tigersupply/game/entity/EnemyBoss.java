package it.spaghettisource.tigersupply.game.entity;

import it.spaghettisource.tigersupply.game.weapon.Weapon;
import it.spaghettisource.tigersupply.game.weapon.enemy.LightningBoltLaser;
import it.spaghettisource.tigersupply.game.weapon.enemy.PlasmaCannon;

public class EnemyBoss extends Enemy {

	
	private int explosionCounter = 0;
	
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
		weapons[1] = new LightningBoltLaser(); 
		weapons[1].setOwner(this);				
		
	}

	public void updateEntity(float deltaSeconds) throws Exception  {
		
		//Y: the boss try to hit player with the laser beam
		if(target.getPosition().getPosY() > position.getPosY()){
			speed.setSpeedY(Math.abs(speed.getSpeedY()));
		}else if(target.getPosition().getPosY() < position.getPosY()){
			speed.setSpeedY(-Math.abs(speed.getSpeedY()));
		}
		
		//X: the boss move from right to screen center and then back (90% screen width)
		if(position.getPosX() < (0.5 * context.getScreenWidth())){
			speed.setSpeedX(Math.abs(speed.getSpeedX()));
		}else if(position.getPosX() > (0.9 * context.getScreenWidth()) ){
			speed.setSpeedX(- Math.abs(speed.getSpeedX()));
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
		createExplosionParticleFire(particleNum, (int)(position.getPosX()+(Math.random()*size.getHalfWidth())),(int)(position.getPosY()+(Math.random()*size.getHalfHeight())), 
				60, 150, 0.5f);
	}

}
