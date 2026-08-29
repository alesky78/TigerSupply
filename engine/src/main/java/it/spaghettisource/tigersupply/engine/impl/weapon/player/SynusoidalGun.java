package it.spaghettisource.tigersupply.engine.impl.weapon.player;

import it.spaghettisource.tigersupply.engine.audio.AudioManager;
import it.spaghettisource.tigersupply.engine.entity.Entity;
import it.spaghettisource.tigersupply.engine.impl.entity.Player;
import it.spaghettisource.tigersupply.engine.impl.utils.EntityFactoryWrapper;
import it.spaghettisource.tigersupply.engine.impl.weapon.AbstractWeapon;
import it.spaghettisource.tigersupply.engine.impl.weapon.HangarWeapon;
import it.spaghettisource.tigersupply.engine.sprite.Sprite;
import it.spaghettisource.tigersupply.engine.sprite.SpriteFactory;
import it.spaghettisource.tigersupply.engine.utils.StaticResources;	



public class SynusoidalGun extends AbstractWeapon<Player>  implements HangarWeapon {

	public SynusoidalGun(){
		reloadingTime = 0.6f;	//0.5 reload tyme
	}
	
	protected void doFire(Entity target) throws Exception {
		Entity gunShotSprite = EntityFactoryWrapper.playerShotGunSynuisodal(owner.getPosition(),0);
		owner.getShotManager().addRquest(gunShotSprite);
		gunShotSprite = EntityFactoryWrapper.playerShotGunSynuisodal(owner.getPosition(),180);
		owner.getShotManager().addRquest(gunShotSprite);	
		
		AudioManager.getInstance().playFx("laser", false);
	}

	protected void doReload() {
		
	}

	public boolean targetInRange(Entity target) {
		return true;
	}

	public Sprite getSprite() throws Exception {
		return SpriteFactory.getInstance(). createImageSingleSprite(StaticResources.PLAYER_GREEN);
	}	
	
	public String getDescription() {
		return "Sinusoidal";
	}	
}
