package it.spaghettisource.tigersupply.game.weapon;

import java.util.Random;

import it.spaghettisource.tigersupply.engine.entity.Entity;

public abstract class AbstractWeapon<T extends Entity> implements Weapon<T> {

	private static final Random RANDOM = new Random();

	protected static final int UNLOADED = 0;
	protected static final int READY = 1;	
	protected static final int RELOADING = 2;
	protected static final int FIREING = 3;	
	
	protected float reloadingTime = 0;		//in seconds
	protected float reloadingTimeJitter = 0;	//max extra seconds added on top of reloadingTime each reload cycle
	protected float currentReloadingTime;		//actual (jittered) reload duration for the ongoing cycle
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

	protected void doReload() {
		currentReloadingTime = reloadingTime + RANDOM.nextFloat() * reloadingTimeJitter;
	}

	public void updateWeapon(float deltaSeconds) throws Exception {
		elaspedTime+=deltaSeconds;
		if(status == RELOADING){
			if(elaspedTime>=currentReloadingTime){
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
