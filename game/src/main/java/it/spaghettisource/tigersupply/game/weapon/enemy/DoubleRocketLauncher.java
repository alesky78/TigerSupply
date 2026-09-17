package it.spaghettisource.tigersupply.game.weapon.enemy;

import it.spaghettisource.tigersupply.engine.entity.Entity;
import it.spaghettisource.tigersupply.engine.entity.Position;
import it.spaghettisource.tigersupply.game.entity.enemy.Enemy;
import it.spaghettisource.tigersupply.game.entity.projectile.EnemyRocket;
import it.spaghettisource.tigersupply.game.utils.EntityFactoryWrapper;
import it.spaghettisource.tigersupply.game.weapon.AbstractWeapon;

public class DoubleRocketLauncher extends AbstractWeapon<Enemy> {

	public DoubleRocketLauncher(){
		reloadingTime = 1.8f;	//2 seconds and shot
		reloadingTimeJitter = 0.9f;
	}
	
	
	protected void doFire(Entity target) throws Exception {
		Position shotPosition1 = new Position(owner.getPosition());
		Position shotPosition2 = new Position(owner.getPosition());			

		EnemyRocket rocketShotSprite1=null;
		EnemyRocket rocketShotSprite2=null;
		shotPosition1.increaseY(20);
		shotPosition2.increaseY(-20);
		try {
			rocketShotSprite1 = EntityFactoryWrapper.newEnemyShotRocket(shotPosition1, target);
			rocketShotSprite2 = EntityFactoryWrapper.newEnemyShotRocket(shotPosition2, target);

		} catch (Exception e) {
			e.printStackTrace();
		}
		rocketShotSprite1.setEffectManager(owner.getEffectManager());		
		rocketShotSprite2.setEffectManager(owner.getEffectManager());			
		owner.getShotManager().addRequest(rocketShotSprite1);
		owner.getShotManager().addRequest(rocketShotSprite2);
	}


	public boolean targetInRange(Entity target) {
		if(target.getXposition() < owner.getXposition()){
			return true;
		}
		return false;
	}

}
