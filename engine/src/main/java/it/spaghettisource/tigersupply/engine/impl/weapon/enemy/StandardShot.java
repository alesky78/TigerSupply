package it.spaghettisource.tigersupply.engine.impl.weapon.enemy;

import it.spaghettisource.tigersupply.engine.entity.Entity;
import it.spaghettisource.tigersupply.engine.entity.Position;
import it.spaghettisource.tigersupply.engine.impl.entity.Enemy;
import it.spaghettisource.tigersupply.engine.impl.utils.EntityFactoryWrapper;
import it.spaghettisource.tigersupply.engine.impl.utils.UpdateAlgorithmFactoryWrapper;
import it.spaghettisource.tigersupply.engine.impl.weapon.AbstractWeapon;
import it.spaghettisource.tigersupply.engine.entity.logic.UpdateAlgorithm;


public class StandardShot extends AbstractWeapon<Enemy> {

	public StandardShot(){
		reloadingTime = 3f;	//2 seconds and shot
	}
	
	
	protected void doFire(Entity target) throws Exception {
		Position shotPosition = new Position(owner.getPosition());
		UpdateAlgorithm algorithm = UpdateAlgorithmFactoryWrapper.newGoToPoint(180, 100, new Position(target.getXposition(), target.getYposition(),0));
		Entity gunShotSprite = EntityFactoryWrapper.newEnemyShot1(shotPosition, algorithm);
		owner.getShotManager().addRquest(gunShotSprite);
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
