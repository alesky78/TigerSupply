package it.spaghettisource.tigersupply.game.weapon.enemy;

import it.spaghettisource.tigersupply.engine.entity.Entity;
import it.spaghettisource.tigersupply.engine.entity.Position;
import it.spaghettisource.tigersupply.game.entity.Enemy;
import it.spaghettisource.tigersupply.game.entity.EnemyRocket;
import it.spaghettisource.tigersupply.game.utils.EntityFactoryWrapper;
import it.spaghettisource.tigersupply.game.weapon.AbstractWeapon;

public class SeekerRocketLauncher extends AbstractWeapon<Enemy> {

	public SeekerRocketLauncher(){
		reloadingTime = 2.2f;
	}


	protected void doFire(Entity target) throws Exception {
		Position shotPosition = new Position(owner.getPosition());
		EnemyRocket rocketShot = EntityFactoryWrapper.newEnemyShotSeekerRocket(shotPosition, target);
		rocketShot.setEffectManager(owner.getEffectManager());
		owner.getShotManager().addRequest(rocketShot);
	}


	protected void doReload() {

	}

	public boolean targetInRange(Entity target) {
		if(target.getXposition() < owner.getXposition()){
			return true;
		}
		return false;
	}

}
