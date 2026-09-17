package it.spaghettisource.tigersupply.game.utils;

import it.spaghettisource.tigersupply.engine.control.GameContext;
import it.spaghettisource.tigersupply.engine.entity.Entity;
import it.spaghettisource.tigersupply.engine.entity.EntityFactory;
import it.spaghettisource.tigersupply.engine.entity.Position;
import it.spaghettisource.tigersupply.engine.entity.Size;
import it.spaghettisource.tigersupply.engine.entity.Speed;
import it.spaghettisource.tigersupply.engine.entity.logic.UpdateAlgorithm;
import it.spaghettisource.tigersupply.engine.entity.EntityGroupScreenBound;
import it.spaghettisource.tigersupply.game.entity.BaseEntity;
import it.spaghettisource.tigersupply.game.entity.effect.ParticleFadeSquare;
import it.spaghettisource.tigersupply.game.entity.enemy.EnergeticShield;
import it.spaghettisource.tigersupply.game.entity.effect.ParticleBurst;
import it.spaghettisource.tigersupply.game.entity.effect.Explosion;
import it.spaghettisource.tigersupply.game.entity.effect.ExplosionProfile;
import it.spaghettisource.tigersupply.game.entity.effect.ParticleColorScheme;
import it.spaghettisource.tigersupply.game.entity.effect.Smoke;
import it.spaghettisource.tigersupply.game.entity.player.Player;
import it.spaghettisource.tigersupply.game.entity.player.PlayerEngine;
import it.spaghettisource.tigersupply.game.entity.projectile.EnemyRocket;
import it.spaghettisource.tigersupply.game.entity.projectile.BallEnergy;
import it.spaghettisource.tigersupply.game.entity.projectile.BallFire;
import it.spaghettisource.tigersupply.game.entity.projectile.LightningBolt;
import it.spaghettisource.tigersupply.game.entity.projectile.PlayerBomb;
import it.spaghettisource.tigersupply.game.entity.projectile.PlayerRocket;
import it.spaghettisource.tigersupply.engine.entity.logic.UpdateAlgorithmFactoryWrapper;
import it.spaghettisource.tigersupply.engine.sprite.Sprite;
import it.spaghettisource.tigersupply.engine.sprite.SpriteFactory;


public class EntityFactoryWrapper {

	private EntityFactoryWrapper(){}


	public static Player newPlayer(int pHeight, float animationPeriod) throws Exception{
		Sprite sprite = SpriteFactory.getInstance().createImagePlayerCenterControllerSprite(animationPeriod, 800, 2, GameResources.PLAYER_SHIP_A);
		return EntityFactory.getInstance().createEntity(0, pHeight/2,GameResources.Z_PLAYER, 0, 0,1.0f, null, sprite, Player.class);
	}	

	public static Explosion newExplosion(ExplosionProfile profile,int posX,int posY,EntityGroupScreenBound<Entity> effectManager,GameContext context){
		Explosion explosion = new Explosion(profile, profile.getParticleNum(), -1f, -1f, null, effectManager, context);
		explosion.setPosition(new Position(posX, posY, GameResources.Z_EXPLOSION));
		return explosion;
	}

	public static Explosion newFollowingExplosion(ExplosionProfile profile,Entity owner,int perEmit,float interval,EntityGroupScreenBound<Entity> effectManager,GameContext context) throws Exception{
		Explosion explosion = new Explosion(profile, perEmit, interval, -1f, owner, effectManager, context);
		explosion.setPosition(new Position(owner.getXposition(), owner.getYposition(), GameResources.Z_EXPLOSION));
		explosion.setUpdateAlgorithm(UpdateAlgorithmFactoryWrapper.newCopyPosition(0, 0, owner.getPosition()));
		return explosion;
	}

	public static Explosion newTimedExplosion(ExplosionProfile profile,int posX,int posY,int perEmit,float interval,EntityGroupScreenBound<Entity> effectManager,GameContext context){
		Explosion explosion = new Explosion(profile, perEmit, interval, -1f, null, effectManager, context);
		explosion.setPosition(new Position(posX, posY, GameResources.Z_EXPLOSION));
		return explosion;
	}
	
	public static EnergeticShield newEnergeticShield(int shieldSize, float shieldLifeTimeInSeconds,EntityGroupScreenBound<Entity> effectManager,Position position,GameContext context) throws Exception{
		EnergeticShield shield = new EnergeticShield(shieldSize,shieldLifeTimeInSeconds, context);
		shield.setUpdateAlgorithm(UpdateAlgorithmFactoryWrapper.newCopyPosition(0, 0, position));
		shield.setContext(context);
		shield.setEffectManager(effectManager);
		return shield;
	}	

	public static Smoke newSmoke(Position smokePosition) throws Exception{
		Sprite sprite = SpriteFactory.getInstance(). createImageSingleSprite(GameResources.EFFECT_SMOKE); 
		return EntityFactory.getInstance().createEntity((int)smokePosition.getPosX(), (int)smokePosition.getPosY(),GameResources.Z_EFFECT_UNDER, 0, 0,1.0f, null, sprite, Smoke.class);
	}

	public static PlayerEngine newEnginePlayer(Position position,UpdateAlgorithm algo,float period) throws Exception{
		Sprite sprite = SpriteFactory.getInstance().createImagePlayerSprite(period, 200, true,GameResources.EFFECT_ENGINE); 
		return EntityFactory.getInstance().createEntity((int)position.getPosX(), (int)position.getPosY(),GameResources.Z_EFFECT_UNDER+1, 0, 0,1.0f, algo, sprite, PlayerEngine.class);		
	}

	public static BaseEntity newEnemyShotDefault(Position shotPosition, Entity target) throws Exception{
		Sprite sprite = SpriteFactory.getInstance(). createImageSingleSprite(GameResources.ENEMY_SHOT_DEFAULT); 
		UpdateAlgorithm algorithm = UpdateAlgorithmFactoryWrapper.newGoToPoint(new Position(target.getXposition(), target.getYposition(),0));		
		return EntityFactory.getInstance().createEntity((int)shotPosition.getPosX(),(int)shotPosition.getPosY(),GameResources.Z_SHOT, 250, 250, 1.0f, algorithm, sprite, BaseEntity.class);
	}

	public static BaseEntity newEnemyShotPlasmaCannon(Position shotPosition, Entity target) throws Exception{
		Sprite sprite = SpriteFactory.getInstance(). createImageSingleSprite(GameResources.ENEMY_SHOT_PLASMA_CANNON);
		UpdateAlgorithm algorithm = UpdateAlgorithmFactoryWrapper.newGoToPoint(new Position(target.getXposition(), target.getYposition(),0));
		return EntityFactory.getInstance().createEntity((int)shotPosition.getPosX(),(int)shotPosition.getPosY(),GameResources.Z_SHOT, -350, 0, 1.0f, algorithm, sprite, BaseEntity.class);				
	}

	public static EnemyRocket newEnemyShotRocket(Position shotPosition, Entity target) throws Exception{	
		Sprite sprite = SpriteFactory.getInstance(). createImageSingleSprite(GameResources.ENEMY_SHOT_ROCKET);
		UpdateAlgorithm algorithm = UpdateAlgorithmFactoryWrapper.newGoToPointIncr(130, 40, new Position(target.getXposition(), target.getYposition(),0));		
		return EntityFactory.getInstance().createEntity((int)shotPosition.getPosX(),(int)shotPosition.getPosY(),GameResources.Z_SHOT, -150, 0, 1.0f, algorithm, sprite, EnemyRocket.class);			
	}	

	public static EnemyRocket newEnemyShotSeekerRocket(Position shotPosition, Entity target) throws Exception{	
		Sprite sprite = SpriteFactory.getInstance(). createImageSingleSprite(GameResources.ENEMY_SHOT_ROCKET);
		UpdateAlgorithm algorithm = UpdateAlgorithmFactoryWrapper.newHoming(target, 200, 100, 3);
		return EntityFactory.getInstance().createEntity((int)shotPosition.getPosX(),(int)shotPosition.getPosY(),GameResources.Z_SHOT, -150, 0, 1.0f, algorithm, sprite, EnemyRocket.class);			
	}	
	
	public static LightningBolt newEnemyShotLightningBolt(GameContext context,Position shotPosition,float fireTime,float loadTime) throws Exception{	
		shotPosition.setPosZ(GameResources.Z_SHOT);
		LightningBolt shot = new LightningBolt(context,shotPosition,fireTime,loadTime);
		return shot;			
	}		

	public static BallEnergy newEnemyShotEnergyBall(GameContext context, Position shotPosition, Entity target) throws Exception{
		Position pos = new Position(shotPosition);
		pos.setPosZ(GameResources.Z_SHOT);
		BallEnergy shot = new BallEnergy();
		shot.setPosition(pos);
		shot.setSpeed(new Speed(-350, 0));
		shot.setSize(new Size(25, 25)); //square AABB used by the shot group for collision	
		shot.setUpdateAlgorithm(UpdateAlgorithmFactoryWrapper.newHoming(target, 350, 70, 1));
		shot.setContext(context);
		return shot;
	}

	public static BallFire newEnemyShotFireBall(GameContext context, Position shotPosition, Entity target) throws Exception{
		Position pos = new Position(shotPosition);
		pos.setPosZ(GameResources.Z_SHOT);
		BallFire shot = new BallFire();
		shot.setPosition(pos);
		shot.setSpeed(new Speed(-350, 0));
		shot.setSize(new Size(25, 25)); //square AABB used by the shot group for collision
		shot.setUpdateAlgorithm(UpdateAlgorithmFactoryWrapper.newDefault());
		shot.setContext(context);
		return shot;
	}

	public static BaseEntity playerShotGun(Position position) throws Exception{	
		Sprite sprite = SpriteFactory.getInstance(). createImageSingleSprite(GameResources.PLAYER_GUN);
		return EntityFactory.getInstance().createEntity((int)position.getPosX(),(int)position.getPosY(),GameResources.Z_SHOT, 400, 0, 1.0f, null, sprite, BaseEntity.class);			
	}
	
	public static BaseEntity playerShotPaser(Position position) throws Exception{	
		Sprite sprite = SpriteFactory.getInstance(). createImageSingleSprite(GameResources.PLAYER_PASER);
		return EntityFactory.getInstance().createEntity((int)position.getPosX(),(int)position.getPosY(),GameResources.Z_SHOT, 450, 0, 1.0f, null, sprite, BaseEntity.class);			
	}	

	public static PlayerRocket playerShotRocket(Position position) throws Exception{	
		Sprite sprite = SpriteFactory.getInstance(). createImageSingleSprite(GameResources.PLAYER_ROCKET);
		return EntityFactory.getInstance().createEntity((int)position.getPosX(),(int)position.getPosY(),GameResources.Z_SHOT, 90, 0, 1.0f, null, sprite, PlayerRocket.class);		
	}
	
	public static PlayerBomb playerShotBomb(Position position,int direction) throws Exception{	
		Sprite sprite = SpriteFactory.getInstance(). createImageSingleSprite(GameResources.PLAYER_BOMB);
		PlayerBomb bomb = EntityFactory.getInstance().createEntity((int)position.getPosX(),(int)position.getPosY(),GameResources.Z_SHOT, 70, direction*40, 1.0f, null, sprite, PlayerBomb.class);
		bomb.setDirection(direction);
		return bomb;
	}	
	
	public static BaseEntity playerShotGunSynuisodal(Position position,float startAngle) throws Exception{	
		Sprite sprite = SpriteFactory.getInstance(). createImageSingleSprite(GameResources.PLAYER_GREEN);
		return EntityFactory.getInstance().createEntity((int)position.getPosX(),(int)position.getPosY(),GameResources.Z_SHOT, 250, 0, 1.0f, 
														UpdateAlgorithmFactoryWrapper.newSinusoidal(15, 360, startAngle), sprite, BaseEntity.class);			
	}	





}
