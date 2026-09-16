package it.spaghettisource.tigersupply.game.weapon.enemy;

import it.spaghettisource.tigersupply.engine.entity.Entity;
import it.spaghettisource.tigersupply.engine.entity.Position;
import it.spaghettisource.tigersupply.game.entity.enemy.Enemy;
import it.spaghettisource.tigersupply.game.entity.projectile.EnergyBall;
import it.spaghettisource.tigersupply.game.utils.EntityFactoryWrapper;
import it.spaghettisource.tigersupply.game.weapon.AbstractWeapon;

public class EnergyBallCannon extends AbstractWeapon<Enemy> {

	public EnergyBallCannon() {
		reloadingTime = 1.5f;
	}

	protected void doFire(Entity target) throws Exception {
		Position shotPosition = new Position(owner.getPosition());
		EnergyBall ball = EntityFactoryWrapper.newEnemyShotEnergyBall(owner.getContext(), shotPosition);
		ball.setEffectManager(owner.getEffectManager());
		owner.getShotManager().addRequest(ball);
	}

	protected void doReload() {

	}

	public boolean targetInRange(Entity target) {
		if (target.getXposition() < owner.getXposition()) {
			return true;
		}
		return false;
	}

}
