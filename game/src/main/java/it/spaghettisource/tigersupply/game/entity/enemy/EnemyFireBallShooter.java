package it.spaghettisource.tigersupply.game.entity.enemy;

import it.spaghettisource.tigersupply.game.entity.effect.ExplosionProfileFactory;
import it.spaghettisource.tigersupply.game.weapon.Weapon;
import it.spaghettisource.tigersupply.game.weapon.enemy.FireBallCannon;

public class EnemyFireBallShooter extends Enemy {

	public EnemyFireBallShooter() {
		super();
		life = 12;
		hitProfile = ExplosionProfileFactory.shooterHit();
		deathProfile = ExplosionProfileFactory.shooterDeath();

		weapons = new Weapon[1];
		weapons[0] = new FireBallCannon();
		weapons[0].setOwner(this);
	}

}
