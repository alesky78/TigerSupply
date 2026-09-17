package it.spaghettisource.tigersupply.game.entity.enemy;

import it.spaghettisource.tigersupply.game.entity.effect.ExplosionProfileFactory;
import it.spaghettisource.tigersupply.game.weapon.Weapon;
import it.spaghettisource.tigersupply.game.weapon.enemy.SeekerRocketLauncher;

public class EnemyShield extends Enemy {	

	private double shieldPeriod;
	private double shieldCounter;	
	
	
	public EnemyShield(){
		life = 0;			
		hitProfile = ExplosionProfileFactory.standardHit();
		deathProfile = ExplosionProfileFactory.standardDeath();

		shieldPeriod = 1; 
		shieldCounter = shieldPeriod;
		
		weapons = new Weapon[1];
		weapons[0] = new SeekerRocketLauncher(); 
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
