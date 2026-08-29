package it.spaghettisource.tigersupply.engine.impl.weapon.player;

import it.spaghettisource.tigersupply.engine.entity.Entity;
import it.spaghettisource.tigersupply.engine.entity.Position;
import it.spaghettisource.tigersupply.engine.impl.entity.Player;
import it.spaghettisource.tigersupply.engine.impl.utils.EntityFactoryWrapper;
import it.spaghettisource.tigersupply.engine.impl.weapon.AbstractWeapon;
import it.spaghettisource.tigersupply.engine.impl.weapon.HangarWeapon;
import it.spaghettisource.tigersupply.engine.sprite.Sprite;
import it.spaghettisource.tigersupply.engine.sprite.SpriteFactory;
import it.spaghettisource.tigersupply.engine.utils.StaticResources;

public class DoubleGun extends AbstractWeapon<Player> implements HangarWeapon  {

	private boolean shotUp = true;
	
	public DoubleGun(){
		reloadingTime = 0.3f;	//0.5 reload tyme
	}
	
	protected void doFire(Entity target) throws Exception {
		Position pos = null;
		if(shotUp){
			pos = new Position(owner.getPosition().getPosX(), owner.getPosition().getPosY()+3, 35);
		}else{
			pos = new Position(owner.getPosition().getPosX(), owner.getPosition().getPosY()-3, 35);
		}
		shotUp = !shotUp;		
		
		Entity gunShotSprite = EntityFactoryWrapper.playerShotGun(pos);
		owner.getShotManager().addRquest(gunShotSprite);
	}

	protected void doReload() {
		
	}

	public boolean targetInRange(Entity target) {
		return true;
	}
	
	public Sprite getSprite() throws Exception {
		return SpriteFactory.getInstance().createImageSingleSprite(StaticResources.PLAYER_GUN);
	}	
	
	public String getDescription() {
		return "Double Gun";
	}	

}
