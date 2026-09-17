package it.spaghettisource.tigersupply.game.entity.enemy;

import it.spaghettisource.tigersupply.engine.audio.AudioManager;
import it.spaghettisource.tigersupply.engine.control.GameContext;
import it.spaghettisource.tigersupply.game.entity.BaseEntity;
import it.spaghettisource.tigersupply.game.entity.effect.ExplosionProfile;
import it.spaghettisource.tigersupply.engine.entity.Entity;
import it.spaghettisource.tigersupply.engine.entity.EntityGroupScreenBound;
import it.spaghettisource.tigersupply.game.utils.EntityFactoryWrapper;
import it.spaghettisource.tigersupply.game.weapon.Weapon;

public class Enemy extends BaseEntity {

	protected int life = 0;

	protected EntityGroupScreenBound<Entity> shotManager;
	protected EntityGroupScreenBound<Entity> effectManager;
	protected EntityGroupScreenBound<Enemy> enemyManager;		
	protected Entity target;

	protected GameContext context;

	//enemy weapon
	protected Weapon<Enemy>[] weapons = new Weapon[0];	//deafult 0 fix otherway null poin exception if there is enemy wihtout weapon (background)

	//explosion management
	protected ExplosionProfile hitProfile;
	protected ExplosionProfile deathProfile;

	public void setContext(GameContext context) {
		this.context = context;
	}

	public GameContext getContext(){
		return context;
	}

	public void setShotManager(EntityGroupScreenBound<Entity> shotManager) {
		this.shotManager = shotManager;
	}

	public EntityGroupScreenBound<Entity> getShotManager() {
		return shotManager;
	}	
	
	public void setEffectManager(EntityGroupScreenBound<Entity> effectManager) {
		this.effectManager = effectManager;
	}
	
	public EntityGroupScreenBound<Entity> getEffectManager() {
		return effectManager;
	}

	public void setEnemyManager(EntityGroupScreenBound<Enemy> enemyManager) {
		this.enemyManager = enemyManager;
	}

	public void setTarget(Entity target) {
		this.target = target;
	}

	public void collided(Entity other) {
		life--;		
		if(life>=0){
			createdHitExplosionParticle(other);			
		}else{
			createdDeadExplosionParticle();
		}
	}

	public boolean canBeRemoved(){
		return (life<0);
	}	


	public void updateEntity(float deltaSeconds) throws Exception {
		super.updateEntity(deltaSeconds);
		
		for (int i = 0; i < weapons.length; i++) {
			weapons[i].updateWeapon(deltaSeconds);
		}
		
		scanTargetInRange();
	}	

	/**
	 * verify if the target is in range for this weapon and shot or reload if weapon required reload
	 * 
	 * @throws Exception
	 */
	protected void scanTargetInRange() throws Exception{
		for (int i = 0; i < weapons.length; i++) {
			if(weapons[i].isUnloaded()){
				weapons[i].reload();
			}
			if(weapons[i].isReady() && weapons[i].targetInRange(target)){
				weapons[i].fire(target);
			}
		}	
	}


	/**
	 * used when die
	 */
	protected void createdDeadExplosionParticle(){
		try {
			AudioManager.getInstance().playFx("explosion-little", false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		effectManager.addRequest(EntityFactoryWrapper.newExplosion(deathProfile, getXposition(), getYposition(), effectManager, context));
	}

	/**
	 * used when hit by another sprite
	 * @param other
	 */
	protected void createdHitExplosionParticle(Entity other){
		try {
			AudioManager.getInstance().playFx("explosion-little", false);
		} catch (Exception e) {
			e.printStackTrace();
		}		
		effectManager.addRequest(EntityFactoryWrapper.newExplosion(hitProfile, other.getXposition(), other.getYposition(), effectManager, context));
	}	

	/**
	 * used when hit by another sprite
	 * @param other
	 */
	protected void createdHitExplosionParticleEnergy(Entity other){
		effectManager.addRequest(EntityFactoryWrapper.newExplosion(hitProfile, other.getXposition(), other.getYposition(), effectManager, context));
	}	



	/**
	 * generate a new shield that follow this enemy
	 * 
	 * @param size
	 * @param lifeInSeconds
	 * @throws Exception
	 */
	protected void generateShield(int size, float lifeInSeconds) throws Exception {
		enemyManager.addRequest(EntityFactoryWrapper.newEnergeticShield(size, lifeInSeconds, effectManager, position, context));
	}	

}