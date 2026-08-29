package it.spaghettisource.tigersupply.game.weapon.enemy;

import it.spaghettisource.tigersupply.engine.entity.Entity;
import it.spaghettisource.tigersupply.engine.entity.Position;
import it.spaghettisource.tigersupply.engine.entity.logic.UpdateAlgorithm;
import it.spaghettisource.tigersupply.game.entity.Enemy;
import it.spaghettisource.tigersupply.game.utils.EntityFactoryWrapper;
import it.spaghettisource.tigersupply.engine.entity.logic.UpdateAlgorithmFactoryWrapper;
import it.spaghettisource.tigersupply.game.weapon.AbstractWeapon;


public class PlasmaCannon extends AbstractWeapon<Enemy> {

	private boolean explosionInversion = true;

	public PlasmaCannon(){
		reloadingTime = 1f;	//2 seconds and shot
	}


	protected void doFire(Entity target) throws Exception {

		Position shotPosition = new Position(owner.getPosition());
		UpdateAlgorithm algorithm = UpdateAlgorithmFactoryWrapper.newGoToPoint(350, 45, new Position(target.getXposition(), target.getYposition(),0));
		Entity gunShotSprite = null;
		if(explosionInversion){
			shotPosition.increaseY(15);
			gunShotSprite = EntityFactoryWrapper.newEnemyShot2(shotPosition, algorithm);
			explosionInversion = !explosionInversion;
		}else{
			shotPosition.increaseY(-15);
			gunShotSprite = EntityFactoryWrapper.newEnemyShot2(shotPosition, algorithm);
			explosionInversion = !explosionInversion;
		}

		owner.getShotManager().addRquest(gunShotSprite);

	}


	protected void doReload() {
		explosionInversion = !explosionInversion;
	}

	public boolean targetInRange(Entity target) {
		if(target.getXposition() < owner.getXposition()){
			return true;
		}
		return false;
	}

}
