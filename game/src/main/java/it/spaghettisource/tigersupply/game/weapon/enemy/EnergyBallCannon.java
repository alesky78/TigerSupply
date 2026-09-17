package it.spaghettisource.tigersupply.game.weapon.enemy;

import it.spaghettisource.tigersupply.engine.entity.Entity;
import it.spaghettisource.tigersupply.engine.entity.Position;
import it.spaghettisource.tigersupply.game.entity.enemy.Enemy;
import it.spaghettisource.tigersupply.game.entity.projectile.BallEnergy;
import it.spaghettisource.tigersupply.game.utils.EntityFactoryWrapper;
import it.spaghettisource.tigersupply.game.weapon.AbstractWeapon;

public class EnergyBallCannon extends AbstractWeapon<Enemy> {

	public EnergyBallCannon() {
		reloadingTime = 1.8f;
		reloadingTimeJitter = 0.9f;
	}

	protected void doFire(Entity target) throws Exception {
		Position shotPosition = new Position(owner.getPosition());
		BallEnergy ball = EntityFactoryWrapper.newEnemyShotEnergyBall(owner.getContext(), shotPosition,target);
		ball.setEffectManager(owner.getEffectManager());
		owner.getShotManager().addRequest(ball);
	}

	public boolean targetInRange(Entity target) {
		if (target.getXposition() < owner.getXposition()) {
			return true;
		}
		return false;
	}

}
