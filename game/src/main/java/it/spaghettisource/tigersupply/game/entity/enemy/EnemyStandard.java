package it.spaghettisource.tigersupply.game.entity.enemy;

import it.spaghettisource.tigersupply.game.entity.effect.ExplosionProfileFactory;
import it.spaghettisource.tigersupply.game.weapon.Weapon;
import it.spaghettisource.tigersupply.game.weapon.enemy.StandardShot;

public class EnemyStandard extends Enemy {	
	
	public EnemyStandard(){
		super();
		life = 0;			
		hitProfile = ExplosionProfileFactory.standardHit();
		deathProfile = ExplosionProfileFactory.standardDeath();
		
		weapons = new Weapon[1];
		weapons[0] = new StandardShot(); 
		weapons[0].setOwner(this);
				
	}	
	
}
