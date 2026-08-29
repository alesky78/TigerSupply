package it.spaghettisource.tigersupply.game.weapon.enemy;

import it.spaghettisource.tigersupply.engine.entity.Entity;
import it.spaghettisource.tigersupply.engine.entity.Position;
import it.spaghettisource.tigersupply.engine.entity.logic.UpdateAlgorithm;
import it.spaghettisource.tigersupply.game.entity.Enemy;
import it.spaghettisource.tigersupply.game.entity.EnemyRocket;
import it.spaghettisource.tigersupply.game.utils.EntityFactoryWrapper;
import it.spaghettisource.tigersupply.engine.entity.logic.UpdateAlgorithmFactoryWrapper;
import it.spaghettisource.tigersupply.game.weapon.AbstractWeapon;

public class RocketLauncer extends AbstractWeapon<Enemy> {

	public RocketLauncer(){
		reloadingTime = 1.8f;	//2 seconds and shot
	}
	
	
	protected void doFire(Entity target) throws Exception {
		Position shotPosition1 = new Position(owner.getPosition());
		Position shotPosition2 = new Position(owner.getPosition());			
		UpdateAlgorithm algorithm = UpdateAlgorithmFactoryWrapper.newGoToPointIncr(130, 40, new Position(target.getXposition(), target.getYposition(),0));
		EnemyRocket rocketShotSprite1=null;
		EnemyRocket rocketShotSprite2=null;
		shotPosition1.increaseY(20);
		shotPosition2.increaseY(-20);
		try {
			rocketShotSprite1 = EntityFactoryWrapper.newEnemyShot3(shotPosition1, algorithm);
			rocketShotSprite2 = EntityFactoryWrapper.newEnemyShot3(shotPosition2, algorithm);

		} catch (Exception e) {
			e.printStackTrace();
		}
		rocketShotSprite1.setEffectManager(owner.getEffectManager());		
		rocketShotSprite2.setEffectManager(owner.getEffectManager());			
		owner.getShotManager().addRquest(rocketShotSprite1);
		owner.getShotManager().addRquest(rocketShotSprite2);
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
