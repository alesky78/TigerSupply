package it.spaghettisource.tigersupply.game.utils;

import it.spaghettisource.tigersupply.engine.control.GameContext;
import it.spaghettisource.tigersupply.engine.entity.Entity;
import it.spaghettisource.tigersupply.engine.entity.EntityFactory;
import it.spaghettisource.tigersupply.engine.entity.Position;
import it.spaghettisource.tigersupply.engine.entity.Speed;
import it.spaghettisource.tigersupply.engine.entity.logic.UpdateAlgorithm;
import it.spaghettisource.tigersupply.engine.entity.EntityGroupScreenBound;
import it.spaghettisource.tigersupply.game.entity.BaseEntity;
import it.spaghettisource.tigersupply.game.entity.EnemyRocket;
import it.spaghettisource.tigersupply.game.entity.EnergeticShield;
import it.spaghettisource.tigersupply.game.entity.ExplosionParticle;
import it.spaghettisource.tigersupply.game.entity.LithingBolt;
import it.spaghettisource.tigersupply.game.entity.Player;
import it.spaghettisource.tigersupply.game.entity.PlayerBomb;
import it.spaghettisource.tigersupply.game.entity.PlayerEngine;
import it.spaghettisource.tigersupply.game.entity.PlayerRocket;
import it.spaghettisource.tigersupply.game.entity.Smoke;
import it.spaghettisource.tigersupply.engine.entity.logic.UpdateAlgorithmFactoryWrapper;
import it.spaghettisource.tigersupply.engine.sprite.Sprite;
import it.spaghettisource.tigersupply.engine.sprite.SpriteFactory;
import it.spaghettisource.tigersupply.game.utils.GameResources;


public class EntityFactoryWrapper {

	private EntityFactoryWrapper(){}


	public static Player newPlayer(int pHeight, float animationPeriod) throws Exception{
		Sprite sprite = SpriteFactory.getInstance().createImagePlayerCenterControllerSprite(animationPeriod, 800, 2, GameResources.PLAYER_SHIP_A);
		return EntityFactory.getInstance().createEntity(0, pHeight/2,GameResources.Z_PLAYER, 0, 0,1.0f, null, sprite, Player.class);
	}	

	public static ExplosionParticle newExplosionParticleFire(int posX, int posY,int maxSize,int maxSpeed,float maxLifeTimeInSeconds,GameContext context){
		return new ExplosionParticle(ExplosionParticle.TYPE_FIRE,posX, posY,maxSize,maxSpeed,maxLifeTimeInSeconds,context);
	}	

	public static ExplosionParticle newExplosionParticleEnergetic(int posX, int posY,int maxSize,int maxSpeed,float maxLifeTimeInSeconds,GameContext context){
		return new ExplosionParticle(ExplosionParticle.TYPE_ENERGETIC,posX, posY,maxSize,maxSpeed,maxLifeTimeInSeconds,context);
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

	public static BaseEntity newEnemyShot1(Position shotPosition,UpdateAlgorithm algo) throws Exception{	
		Sprite sprite = SpriteFactory.getInstance(). createImageSingleSprite(GameResources.ENEMY_SHOT1); 
		return EntityFactory.getInstance().createEntity((int)shotPosition.getPosX(),(int)shotPosition.getPosY(),GameResources.Z_SHOT, -150, 0, 1.0f, algo, sprite, BaseEntity.class);		
	}

	public static BaseEntity newEnemyShot2(Position shotPosition,UpdateAlgorithm algo) throws Exception{
		Sprite sprite = SpriteFactory.getInstance(). createImageSingleSprite(GameResources.ENEMY_SHOT2);
		return EntityFactory.getInstance().createEntity((int)shotPosition.getPosX(),(int)shotPosition.getPosY(),GameResources.Z_SHOT, -350, 0, 1.0f, algo, sprite, BaseEntity.class);				
	}

	public static EnemyRocket newEnemyShot3(Position shotPosition,UpdateAlgorithm algo) throws Exception{	
		Sprite sprite = SpriteFactory.getInstance(). createImageSingleSprite(GameResources.ENEMY_SHOT3);
		return EntityFactory.getInstance().createEntity((int)shotPosition.getPosX(),(int)shotPosition.getPosY(),GameResources.Z_SHOT, -150, 0, 1.0f, algo, sprite, EnemyRocket.class);			
	}	
	
	public static LithingBolt newEnemyShotLightinBolt(GameContext context,Position shotPosition,float fireTime,float loadTime) throws Exception{	
		shotPosition.setPosZ(GameResources.Z_SHOT);
		LithingBolt shot = new LithingBolt(context,shotPosition,fireTime,loadTime);
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
