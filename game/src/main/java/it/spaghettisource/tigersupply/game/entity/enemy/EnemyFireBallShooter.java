package it.spaghettisource.tigersupply.game.entity.enemy;

import it.spaghettisource.tigersupply.game.weapon.Weapon;
import it.spaghettisource.tigersupply.game.weapon.enemy.FireBallCannon;

public class EnemyFireBallShooter extends Enemy {

	public EnemyFireBallShooter() {
		super();
		life = 12;
		particleNum = 100;
		particleMaxSize = 40;
		particleDeathMaxSize = 80;
		particleMaxSpeed = 130;
		particleDeathMaxSpeed = 130;
		particleMaxLifeTime = 0.3f;
		particleDeathMaxLifeTime = 0.4f;

		weapons = new Weapon[1];
		weapons[0] = new FireBallCannon();
		weapons[0].setOwner(this);
	}

}
