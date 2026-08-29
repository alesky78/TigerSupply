package it.spaghettisource.tigersupply.game.weapon;

import it.spaghettisource.tigersupply.engine.entity.Entity;

public interface Weapon<T extends Entity> {

	public void setOwner(T owner);
	
	public boolean targetInRange(Entity target);
	
	public void fire(Entity target) throws Exception;
	
	public void reload();
	
	public boolean isUnloaded();	
	
	public boolean isReloading();
	
	public boolean isReady();
	
	public void updateWeapon(float deltaSeconds) throws Exception;
		
	
}
