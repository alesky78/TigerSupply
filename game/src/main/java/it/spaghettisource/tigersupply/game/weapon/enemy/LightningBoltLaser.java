package it.spaghettisource.tigersupply.game.weapon.enemy;

import it.spaghettisource.tigersupply.engine.entity.Entity;
import it.spaghettisource.tigersupply.game.entity.Enemy;
import it.spaghettisource.tigersupply.game.entity.LightningBolt;
import it.spaghettisource.tigersupply.game.utils.EntityFactoryWrapper;
import it.spaghettisource.tigersupply.game.weapon.AbstractWeapon;


public class LightningBoltLaser extends AbstractWeapon<Enemy> {

	LightningBolt gunShotSprite;
	
	public LightningBoltLaser(){
		reloadingTime = 4f;	//4 seconds and shot
		fireingTime = 3f;//;
	}
	
	
	protected void doFire(Entity target) throws Exception {
		gunShotSprite.shotLaser();
	}


	protected void doReload() {
		try {
			gunShotSprite = EntityFactoryWrapper.newEnemyShotLightningBolt(owner.getContext(),owner.getPosition(),fireingTime,reloadingTime);
		} catch (Exception e) {
			e.printStackTrace();
		}
		owner.getShotManager().addRquest(gunShotSprite);
		
	}

	public boolean targetInRange(Entity target) {
		if(target.getXposition() < owner.getXposition()){
			return true;
		}
		return false;
	}

}
