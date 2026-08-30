package it.spaghettisource.tigersupply.game.entity;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import it.spaghettisource.tigersupply.engine.entity.Position;
import it.spaghettisource.tigersupply.engine.entity.Speed;
import it.spaghettisource.tigersupply.engine.entity.logic.UpdateAlgorithm;
import it.spaghettisource.tigersupply.engine.entity.Entity;
import it.spaghettisource.tigersupply.engine.entity.EntityGroupScreenBound;
import it.spaghettisource.tigersupply.game.utils.EntityFactoryWrapper;
import it.spaghettisource.tigersupply.engine.entity.logic.UpdateAlgorithmFactoryWrapper;
import it.spaghettisource.tigersupply.game.weapon.Weapon;
import it.spaghettisource.tigersupply.engine.sprite.ImagePlayerCenterControllerSprite;

public class Player extends BaseEntity {

	private float maxSpeedX;
	private float maxSpeedY;	

	private boolean initAnimation;
	private int startGameXPoisition;

	//external sprites management
	private EntityGroupScreenBound<Entity> shotManager;
	private EntityGroupScreenBound<Entity> effectManager;	

	private boolean shotRequest = false;	
	private float smokeCounter = 0;	

	//bug left right, don't set 0 speed if one is pressed
	private boolean left  	=false;
	private boolean right 	=false;	
	private boolean up  	=false;	
	private boolean down  	=false;	

	//used to create the first time the engine sprite
	boolean engineCreated = false;


	protected List<Weapon<Player>> weapons = new ArrayList<Weapon<Player>>(); 
	
	//explosion management
	protected int particleNum;
	protected int particleMaxSize;	
	protected int particleMaxSpeed;	
	protected float particleMaxLifeTime;		
	
	
	int life = 4;	//life of the player, how many time they can distroy you!!!!!!

	//post-spawn invulnerability: a grace window with a retro blink of the ship sprite
	private static final float INVULNERABILITY_SECONDS = 3f;
	private static final float BLINK_INTERVAL_SECONDS = 0.1f;
	private static final double INVULNERABLE_ALPHA = 0.25;
	private static final double NORMAL_ALPHA = 1.0;
	private float invulnerableTimer = 0f;	//seconds left of the grace window, invulnerable while > 0
	private float blinkCounter = 0f;		//accumulates time between blink toggles
	private boolean visible = true;			//current blink phase of the ship sprite
	
	public Player(){
		particleNum = 100;
		particleMaxSize = 40;	
		particleMaxSpeed = 100;	
		particleMaxLifeTime = 0.5f;			
	}
	
	
	public void addWeapon(Weapon<Player> weapon){
		weapon.setOwner(this);
		weapons.add(weapon);
	}
	
	public void clearWeapons(){
		weapons.clear();
	}	
	
	
	public void reset(){
		life = 3;
	}
	
	public boolean isLive(){
		return life > 0;
	}
	
	public int numberLive(){
		return life;
	}	
	
	public boolean isInvulnerable(){
		return invulnerableTimer > 0;
	}
	
	/**
	 * arm the post-spawn grace window and (re)start the blink from a visible phase
	 */
	private void startInvulnerability(){
		invulnerableTimer = INVULNERABILITY_SECONDS;
		blinkCounter = 0f;
		visible = true;
	}
	

	public void setSpeed(Speed speed) {
		super.setSpeed(speed);
		this.maxSpeedX = speed.getSpeedX();
		this.maxSpeedY = speed.getSpeedY();	
	}
	
	
	public void startPosition(){
		startGameXPoisition = 60;
		position.setPosX(-80);
		initAnimation = true;		
		startInvulnerability();
	}

	public void setShootManager(EntityGroupScreenBound<Entity> shotManager) {
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

	public void collided(Entity other){
		
		if(isInvulnerable()){	//ignore damage during the post-spawn grace window
			return;
		}
		
		createdDeadParticle();
		
		life-=1;	//remove one life
		
		shotRequest = false;
		position.setPosX(-80);
		initAnimation = true;
		startInvulnerability();
	}

	public void tryToShot(float deltaSeconds) throws Exception{
		for (Weapon<Player> weapon : weapons) {
			weapon.updateWeapon(deltaSeconds);
			if(weapon.isReady()){
				if(shotRequest){
					weapon.fire(null);
				}
			}else if(weapon.isUnloaded()){
				weapon.reload();
			}
		}
		
	}


	public boolean collidedWith(Entity other) {
		if(initAnimation)
			return false;
		return super.collidedWith(other);
	}	

	public void updateEntity(float deltaSeconds) throws Exception {
		
		if(!engineCreated){	//only first time
			createEngineSpriteAndLinkToPlayer();
			engineCreated = true;
		}

		if(initAnimation){	//only to enter in the level and when destroyed
			speed.setSpeedX(maxSpeedX);
			speed.setSpeedY(0);
			if(position.getPosX() >= startGameXPoisition ){
				initAnimation = false;
				speed.setSpeedX(0);
				speed.setSpeedY(0);
			}		
		}
		
		super.updateEntity(deltaSeconds);	
		
		updateInvulnerability(deltaSeconds);
		
		adjustSpeed(deltaSeconds);
		
		tryToShot(deltaSeconds);				
		
		smokeCounter+=deltaSeconds;
		if(smokeCounter >deltaSeconds*3 && right){	//smoke only if goes front
			smokeCounter = 0;
			Position smokePosition = new Position(position);
			smokePosition.increaseX(-size.getHalfWidth()-6);
			Smoke smokeEffect = EntityFactoryWrapper.newSmoke(smokePosition);
			effectManager.addRequest(smokeEffect);	
		}
	}	

	/**
	 * advance the post-spawn grace window and blink the ship sprite between opaque and
	 * semi-transparent; restores full opacity exactly once when the window ends.
	 * 
	 * @param deltaSeconds elapsed time for this frame
	 */
	private void updateInvulnerability(float deltaSeconds) {
		if(invulnerableTimer <= 0){
			return;
		}
		
		invulnerableTimer -= deltaSeconds;
		if(invulnerableTimer <= 0){	//window ended: leave the ship fully opaque
			invulnerableTimer = 0;
			visible = true;
			sprite.setAlpha(NORMAL_ALPHA);
			return;
		}
		
		blinkCounter += deltaSeconds;
		if(blinkCounter >= BLINK_INTERVAL_SECONDS){
			blinkCounter = 0;
			visible = !visible;
			sprite.setAlpha(visible ? NORMAL_ALPHA : INVULNERABLE_ALPHA);
		}
	}

	/**
	 * reduce speed to create the float effect, when the key are not typed
	 * 
	 * @param deltaSeconds
	 */
	private void adjustSpeed(float deltaSeconds) {
		int xSpeed = (int) speed.getSpeedX();
		int ySpeed = (int) speed.getSpeedY();		
		
		if(xSpeed!=0){
			if(!right && !left){
				speed.setSpeedX(xSpeed*0.9f);	
			}
		}
		
		if(ySpeed!=0){
			if(!up && !down){
				speed.setSpeedY(ySpeed*0.9f);	
			}
		}
		
		
		
	}


	private void createEngineSpriteAndLinkToPlayer() {
		Entity engineSprite = null;
		try {
			UpdateAlgorithm algo = UpdateAlgorithmFactoryWrapper.newCopyPosition((int)(-1-size.getHalfWidth()), 0, position);
			engineSprite = EntityFactoryWrapper.newEnginePlayer(position, algo, context.getPeriodMilliseconds());
		} catch (Exception e) {
			e.printStackTrace();
		}
		effectManager.addRequest(engineSprite);
	}	
	
	
	private boolean isMoving(){
		return up || down || left || right;
	}
	

	public void KeyboardPressed(KeyEvent event){
		if(event.getKeyCode() == KeyEvent.VK_I){//up
			speed.setSpeedY(-maxSpeedY);
			up = true;
			((ImagePlayerCenterControllerSprite)sprite).goToUpAnimation();			
		}
		if(event.getKeyCode() == KeyEvent.VK_K){//down
			speed.setSpeedY(maxSpeedY);
			down = true;
			((ImagePlayerCenterControllerSprite)sprite).goToDownAnimation();			
		}	
		if(!initAnimation){	
			if(event.getKeyCode() == KeyEvent.VK_J){//left
				speed.setSpeedX(-maxSpeedX);
				left = true;
			}
			if(event.getKeyCode() == KeyEvent.VK_L){//right
				speed.setSpeedX(maxSpeedX);
				right = true;
			}
			if(event.getKeyCode() == KeyEvent.VK_SPACE ){
				shotRequest =true;
			}
		}
	}


	public void KeyboardReleased(KeyEvent event){
		
		if(event.getKeyCode() == KeyEvent.VK_I){//up
			up = false;
			((ImagePlayerCenterControllerSprite)sprite).goToCentralAnimation();			
		}
		if(event.getKeyCode() == KeyEvent.VK_K){//down
			down = false;
			((ImagePlayerCenterControllerSprite)sprite).goToCentralAnimation();			
		}

		if(!initAnimation){  // it block only left and right
			if(event.getKeyCode() == KeyEvent.VK_J){//left
				left = false;			
			}
			if(event.getKeyCode() == KeyEvent.VK_L){//right
				right = false;
			}
			if(event.getKeyCode() == KeyEvent.VK_SPACE ){
				shotRequest = false;
			}			
		}
	}	

	
	private void createdDeadParticle(){
		createExplosionParticleFire(particleNum, getXposition(),getYposition(), particleMaxSize, particleMaxSpeed, particleMaxLifeTime);
	}	

	protected void createExplosionParticleFire(int particleNum,int posX, int posY, int maxSize,int maxSpeed,float maxLifeTimeInSeconds){
		for (int i = 0; i < particleNum; i++) {					
			effectManager.addRequest(EntityFactoryWrapper.newExplosionParticleFire(posX, posY, maxSize, maxSpeed, maxLifeTimeInSeconds,context));			
		}
	}
	
	protected void createExplosionParticleEnergy(int particleNum,int posX, int posY, int maxSize,int maxSpeed,float maxLifeTimeInSeconds){
		for (int i = 0; i < particleNum; i++) {					
			effectManager.addRequest(EntityFactoryWrapper.newExplosionParticleEnergetic(posX, posY, maxSize, maxSpeed, maxLifeTimeInSeconds,context));			
		}
	}

}
