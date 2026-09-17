package it.spaghettisource.tigersupply.game.entity.enemy;

import it.spaghettisource.tigersupply.game.entity.effect.ExplosionProfileFactory;
import it.spaghettisource.tigersupply.game.weapon.Weapon;
import it.spaghettisource.tigersupply.game.weapon.enemy.DoubleRocketLauncher;



public class EnemyShoterRocket extends Enemy {	

	public EnemyShoterRocket(){
		super();
		life = 10;
		hitProfile = ExplosionProfileFactory.shooterHit();
		deathProfile = ExplosionProfileFactory.shooterDeath();
		
		weapons = new Weapon[1];
		weapons[0] = new DoubleRocketLauncher(); 
		weapons[0].setOwner(this);
		
	}	


}
