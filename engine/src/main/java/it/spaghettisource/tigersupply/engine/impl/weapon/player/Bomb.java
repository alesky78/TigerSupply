package it.spaghettisource.tigersupply.engine.impl.weapon.player;

import it.spaghettisource.tigersupply.engine.entity.Entity;
import it.spaghettisource.tigersupply.engine.entity.Position;
import it.spaghettisource.tigersupply.engine.impl.entity.Player;
import it.spaghettisource.tigersupply.engine.impl.entity.PlayerBomb;
import it.spaghettisource.tigersupply.engine.impl.utils.EntityFactoryWrapper;
import it.spaghettisource.tigersupply.engine.impl.weapon.AbstractWeapon;
import it.spaghettisource.tigersupply.engine.impl.weapon.HangarWeapon;
import it.spaghettisource.tigersupply.engine.sprite.Sprite;
import it.spaghettisource.tigersupply.engine.sprite.SpriteFactory;
import it.spaghettisource.tigersupply.engine.utils.StaticResources;

;


public class Bomb extends AbstractWeapon<Player> implements HangarWeapon {

	public Bomb(){
		reloadingTime = 2f;	//0.5 reload tyme
	}

	protected void doFire(Entity target) throws Exception {

		Position sp1 = new Position(owner.getPosition());
		sp1.increaseX(-5);
		sp1.increaseY(6);
		Position sp2 = new Position(owner.getPosition());
		sp2.increaseX(-5);
		sp2.increaseY(-6);
		PlayerBomb rocketShotSprite1 = EntityFactoryWrapper.playerShotBomb(sp1,PlayerBomb.DIRECTION_DOWN);
		PlayerBomb rocketShotSprite2 = EntityFactoryWrapper.playerShotBomb(sp2,PlayerBomb.DIRECTION_UP);		
		owner.getShotManager().addRquest(rocketShotSprite2);
		owner.getShotManager().addRquest(rocketShotSprite1);
	}

	protected void doReload() {

	}

	public boolean targetInRange(Entity target) {
		return true;
	}
	
	public Sprite getSprite() throws Exception {
		return SpriteFactory.getInstance(). createImageSingleSprite(StaticResources.PLAYER_BOMB);
	}

	public String getDescription() {
		return "Bomb";
	}		

}
