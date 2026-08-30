package it.spaghettisource.tigersupply.game.weapon.player;

import it.spaghettisource.tigersupply.engine.audio.AudioManager;
import it.spaghettisource.tigersupply.engine.entity.Entity;
import it.spaghettisource.tigersupply.engine.entity.Position;
import it.spaghettisource.tigersupply.game.entity.Player;
import it.spaghettisource.tigersupply.game.entity.PlayerRocket;
import it.spaghettisource.tigersupply.game.utils.EntityFactoryWrapper;
import it.spaghettisource.tigersupply.game.weapon.AbstractWeapon;
import it.spaghettisource.tigersupply.game.weapon.HangarWeapon;
import it.spaghettisource.tigersupply.engine.sprite.Sprite;
import it.spaghettisource.tigersupply.engine.sprite.SpriteFactory;
import it.spaghettisource.tigersupply.game.utils.GameResources;


public class RocketLauncher extends AbstractWeapon<Player> implements HangarWeapon {

	public RocketLauncher(){
		reloadingTime = 2f;	//0.5 reload tyme
	}

	protected void doFire(Entity target) throws Exception {

		Position sp1 = new Position(owner.getPosition());
		sp1.increaseX(-5);
		sp1.increaseY(6);
		Position sp2 = new Position(owner.getPosition());
		sp2.increaseX(-5);
		sp2.increaseY(-6);
		PlayerRocket rocketShotSprite1 = EntityFactoryWrapper.playerShotRocket(sp1);
		PlayerRocket rocketShotSprite2 = EntityFactoryWrapper.playerShotRocket(sp2);			
		rocketShotSprite1.setEffectManager(owner.getEffectManager());
		rocketShotSprite2.setEffectManager(owner.getEffectManager());
		owner.getShotManager().addRequest(rocketShotSprite1);
		owner.getShotManager().addRequest(rocketShotSprite2);
		
		AudioManager.getInstance().playFx("rocket", false);		
	}

	protected void doReload() {

	}

	public boolean targetInRange(Entity target) {
		return true;
	}
	
	public Sprite getSprite() throws Exception {
		return SpriteFactory.getInstance(). createImageSingleSprite(GameResources.PLAYER_ROCKET);
	}	

	public String getDescription() {
		return "Rocket";
	}	
	
}
