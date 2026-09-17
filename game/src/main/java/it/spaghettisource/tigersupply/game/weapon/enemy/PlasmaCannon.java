package it.spaghettisource.tigersupply.game.weapon.enemy;

import it.spaghettisource.tigersupply.engine.entity.Entity;
import it.spaghettisource.tigersupply.engine.entity.Position;
import it.spaghettisource.tigersupply.game.entity.enemy.Enemy;
import it.spaghettisource.tigersupply.game.utils.EntityFactoryWrapper;
import it.spaghettisource.tigersupply.game.weapon.AbstractWeapon;


public class PlasmaCannon extends AbstractWeapon<Enemy> {

	private boolean explosionInversion = true;

	public PlasmaCannon(){
		reloadingTime = 1f;	//2 seconds and shot
		reloadingTimeJitter = 0.5f;
	}


	protected void doFire(Entity target) throws Exception {

		Position shotPosition = new Position(owner.getPosition());

		Entity gunShotSprite = null;
		if(explosionInversion){
			shotPosition.increaseY(15);
			gunShotSprite = EntityFactoryWrapper.newEnemyShotPlasmaCannon(shotPosition, target);
			explosionInversion = !explosionInversion;
		}else{
			shotPosition.increaseY(-15);
			gunShotSprite = EntityFactoryWrapper.newEnemyShotPlasmaCannon(shotPosition, target);
			explosionInversion = !explosionInversion;
		}

		owner.getShotManager().addRequest(gunShotSprite);

	}


	protected void doReload() {
		super.doReload();
		explosionInversion = !explosionInversion;
	}

	public boolean targetInRange(Entity target) {
		if(target.getXposition() < owner.getXposition()){
			return true;
		}
		return false;
	}

}
