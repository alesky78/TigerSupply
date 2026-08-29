package it.spaghettisource.tigersupply.engine.impl.entity;

import it.spaghettisource.tigersupply.engine.impl.weapon.Weapon;
import it.spaghettisource.tigersupply.engine.impl.weapon.enemy.StandardShot;

public class EnemyStandard extends Enemy {	
	
	public EnemyStandard(){
		super();
		life = 0;			
		particleNum = 50;
		particleMaxSize = 35;
		particleDeathMaxSize = 35;	
		particleMaxSpeed = 60;
		particleDeathMaxSpeed = 60 ;	
		particleMaxLifeTime = 0.3f;	
		particleDeathMaxLifeTime= 0.5f;		
		
		weapons = new Weapon[1];
		weapons[0] = new StandardShot(); 
		weapons[0].setOwner(this);
				
	}	
	
}
