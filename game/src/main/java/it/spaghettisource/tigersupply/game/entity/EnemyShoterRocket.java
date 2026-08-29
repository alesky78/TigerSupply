package it.spaghettisource.tigersupply.game.entity;

import it.spaghettisource.tigersupply.game.weapon.Weapon;
import it.spaghettisource.tigersupply.game.weapon.enemy.RocketLauncer;



public class EnemyShoterRocket extends Enemy {	

	public EnemyShoterRocket(){
		super();
		life = 10;
		particleNum = 100;
		particleMaxSize = 40;
		particleDeathMaxSize = 80;	
		particleMaxSpeed = 130;
		particleDeathMaxSpeed = 130 ;	
		particleMaxLifeTime = 0.3f;	
		particleDeathMaxLifeTime= 0.4f;	
		
		weapons = new Weapon[1];
		weapons[0] = new RocketLauncer(); 
		weapons[0].setOwner(this);
		
	}	


}
