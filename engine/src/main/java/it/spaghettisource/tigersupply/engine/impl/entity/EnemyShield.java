package it.spaghettisource.tigersupply.engine.impl.entity;

import it.spaghettisource.tigersupply.engine.impl.weapon.Weapon;
import it.spaghettisource.tigersupply.engine.impl.weapon.enemy.StandardShot;

public class EnemyShield extends Enemy {	

	private double shieldPeriod;
	private double shieldCounter;	
	
	
	public EnemyShield(){
		life = 0;			
		particleNum = 50;
		particleMaxSize = 35;
		particleDeathMaxSize = 35;	
		particleMaxSpeed = 60;
		particleDeathMaxSpeed = 60 ;	
		particleMaxLifeTime = 0.3f;	
		particleDeathMaxLifeTime= 0.5f;

		shieldPeriod = 1; 
		shieldCounter = shieldPeriod;
		
		weapons = new Weapon[1];
		weapons[0] = new StandardShot(); 
		weapons[0].setOwner(this);	
	}
	
	
	public void updateEntity(float deltaSeconds) throws Exception {
		shieldCounter+=deltaSeconds;
		if(shieldCounter > shieldPeriod){
			generateShield(75,3);
			shieldCounter = 0;
		}
		
		super.updateEntity(deltaSeconds);

	}

}
