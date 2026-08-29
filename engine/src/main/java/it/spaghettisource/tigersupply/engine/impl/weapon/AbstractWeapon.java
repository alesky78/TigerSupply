package it.spaghettisource.tigersupply.engine.impl.weapon;

import it.spaghettisource.tigersupply.engine.entity.Entity;

public abstract class AbstractWeapon<T extends Entity> implements Weapon<T> {

	protected static final int UNLOADED = 0;
	protected static final int READY = 1;	
	protected static final int RELOADING = 2;
	protected static final int FIREING = 3;	
	
	protected float reloadingTime = 0;		//in seconds
	protected float fireingTime = 0;		//in seconds	
	protected float elaspedTime = 0;	
	protected int status = UNLOADED;	//default unloaded
	protected T owner;
	
	public void setOwner(T owner) {
		this.owner = owner;
	}

	public void fire(Entity targer) throws Exception {
		status = FIREING;
		doFire(targer);
	}

	protected abstract void doFire(Entity target) throws Exception;

	public void reload() {
		status = RELOADING;
		doReload();
	}

	protected abstract void doReload();

	public void updateWeapon(float deltaSeconds) throws Exception {
		elaspedTime+=deltaSeconds;
		if(status == RELOADING){
			if(elaspedTime>=reloadingTime){
				status = READY;
				elaspedTime = 0;
			}
		}
		if(status == FIREING){
			if(elaspedTime>=fireingTime){
				status = UNLOADED;
				elaspedTime = 0;
			}
		}	
	}	
	
	public boolean isUnloaded() {
		return status == UNLOADED;
	}	
	
	public boolean isReloading() {
		return status == RELOADING;
	}

	public boolean isReady() {
		return status == READY;
	}

}
