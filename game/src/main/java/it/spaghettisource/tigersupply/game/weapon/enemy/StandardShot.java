package it.spaghettisource.tigersupply.game.weapon.enemy;

import it.spaghettisource.tigersupply.engine.entity.Entity;
import it.spaghettisource.tigersupply.engine.entity.Position;
import it.spaghettisource.tigersupply.game.entity.enemy.Enemy;
import it.spaghettisource.tigersupply.game.utils.EntityFactoryWrapper;
import it.spaghettisource.tigersupply.game.weapon.AbstractWeapon;


public class StandardShot extends AbstractWeapon<Enemy> {

	public StandardShot(){
		reloadingTime = 3f;	//2 seconds and shot
		reloadingTimeJitter = 1.5f;
	}
	
	
	protected void doFire(Entity target) throws Exception {
		Position shotPosition = new Position(owner.getPosition());
		Entity gunShotSprite = EntityFactoryWrapper.newEnemyShotDefault(shotPosition, target);
		owner.getShotManager().addRequest(gunShotSprite);
	}

	public boolean targetInRange(Entity target) {
		if(target.getXposition() < owner.getXposition()){
			return true;
		}
		return false;
	}

}
