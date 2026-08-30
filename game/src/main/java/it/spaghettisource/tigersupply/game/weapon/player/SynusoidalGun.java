package it.spaghettisource.tigersupply.game.weapon.player;

import it.spaghettisource.tigersupply.engine.audio.AudioManager;
import it.spaghettisource.tigersupply.engine.entity.Entity;
import it.spaghettisource.tigersupply.game.entity.Player;
import it.spaghettisource.tigersupply.game.utils.EntityFactoryWrapper;
import it.spaghettisource.tigersupply.game.weapon.AbstractWeapon;
import it.spaghettisource.tigersupply.game.weapon.HangarWeapon;
import it.spaghettisource.tigersupply.engine.sprite.Sprite;
import it.spaghettisource.tigersupply.engine.sprite.SpriteFactory;
import it.spaghettisource.tigersupply.game.utils.GameResources;	



public class SynusoidalGun extends AbstractWeapon<Player>  implements HangarWeapon {

	public SynusoidalGun(){
		reloadingTime = 0.6f;	//0.5 reload tyme
	}
	
	protected void doFire(Entity target) throws Exception {
		Entity gunShotSprite = EntityFactoryWrapper.playerShotGunSynuisodal(owner.getPosition(),0);
		owner.getShotManager().addRequest(gunShotSprite);
		gunShotSprite = EntityFactoryWrapper.playerShotGunSynuisodal(owner.getPosition(),180);
		owner.getShotManager().addRequest(gunShotSprite);	
		
		AudioManager.getInstance().playFx("laser", false);
	}

	protected void doReload() {
		
	}

	public boolean targetInRange(Entity target) {
		return true;
	}

	public Sprite getSprite() throws Exception {
		return SpriteFactory.getInstance(). createImageSingleSprite(GameResources.PLAYER_GREEN);
	}	
	
	public String getDescription() {
		return "Sinusoidal";
	}	
}
