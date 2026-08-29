package it.spaghettisource.tigersupply.engine.impl.weapon.enemy;

import it.spaghettisource.tigersupply.engine.entity.Entity;
import it.spaghettisource.tigersupply.engine.impl.entity.Enemy;
import it.spaghettisource.tigersupply.engine.impl.entity.LithingBolt;
import it.spaghettisource.tigersupply.engine.impl.utils.EntityFactoryWrapper;
import it.spaghettisource.tigersupply.engine.impl.weapon.AbstractWeapon;


public class LightinBoltLaser extends AbstractWeapon<Enemy> {

	LithingBolt gunShotSprite;
	
	public LightinBoltLaser(){
		reloadingTime = 4f;	//4 seconds and shot
		fireingTime = 3f;//;
	}
	
	
	protected void doFire(Entity target) throws Exception {
		gunShotSprite.shotLaser();
	}


	protected void doReload() {
		try {
			gunShotSprite = EntityFactoryWrapper.newEnemyShotLightinBolt(owner.getContext(),owner.getPosition(),fireingTime,reloadingTime);
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
