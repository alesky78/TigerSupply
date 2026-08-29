package it.spaghettisource.tigersupply.game.weapon.player;

import it.spaghettisource.tigersupply.engine.entity.Entity;
import it.spaghettisource.tigersupply.game.entity.Player;
import it.spaghettisource.tigersupply.game.utils.EntityFactoryWrapper;
import it.spaghettisource.tigersupply.game.weapon.AbstractWeapon;
import it.spaghettisource.tigersupply.game.weapon.HangarWeapon;
import it.spaghettisource.tigersupply.engine.sprite.Sprite;
import it.spaghettisource.tigersupply.engine.sprite.SpriteFactory;
import it.spaghettisource.tigersupply.game.utils.GameResources;

public class Paser extends AbstractWeapon<Player> implements HangarWeapon {

	public Paser(){
		reloadingTime = 0.3f;	//0.5 reload tyme
	}
	
	protected void doFire(Entity target) throws Exception {
		Entity gunShotSprite = EntityFactoryWrapper.playerShotPaser(owner.getPosition());
		owner.getShotManager().addRquest(gunShotSprite);
	}

	protected void doReload() {
		
	}

	public boolean targetInRange(Entity target) {
		return true;
	}

	public Sprite getSprite() throws Exception {
		return SpriteFactory.getInstance().createImageSingleSprite(GameResources.PLAYER_PASER);
	}
	
	public String getDescription() {
		return "Paser";
	}	

}
