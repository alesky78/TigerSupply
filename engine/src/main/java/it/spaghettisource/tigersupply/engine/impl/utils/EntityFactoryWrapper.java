package it.spaghettisource.tigersupply.engine.impl.utils;

import it.spaghettisource.tigersupply.engine.control.ApplicationContext;
import it.spaghettisource.tigersupply.engine.entity.Entity;
import it.spaghettisource.tigersupply.engine.entity.EntityFactory;
import it.spaghettisource.tigersupply.engine.entity.Position;
import it.spaghettisource.tigersupply.engine.entity.Speed;
import it.spaghettisource.tigersupply.engine.entity.logic.UpdateAlgorithm;
import it.spaghettisource.tigersupply.engine.entity.manager.EntityManagerEntityRequest;
import it.spaghettisource.tigersupply.engine.impl.entity.BaseEntity;
import it.spaghettisource.tigersupply.engine.impl.entity.EnemyRocket;
import it.spaghettisource.tigersupply.engine.impl.entity.EnergeticShield;
import it.spaghettisource.tigersupply.engine.impl.entity.ExplosionParticle;
import it.spaghettisource.tigersupply.engine.impl.entity.LithingBolt;
import it.spaghettisource.tigersupply.engine.impl.entity.Player;
import it.spaghettisource.tigersupply.engine.impl.entity.PlayerBomb;
import it.spaghettisource.tigersupply.engine.impl.entity.PlayerEngine;
import it.spaghettisource.tigersupply.engine.impl.entity.PlayerRocket;
import it.spaghettisource.tigersupply.engine.impl.entity.Smoke;
import it.spaghettisource.tigersupply.engine.sprite.Sprite;
import it.spaghettisource.tigersupply.engine.sprite.SpriteFactory;
import it.spaghettisource.tigersupply.engine.utils.StaticResources;


public class EntityFactoryWrapper {

	private EntityFactoryWrapper(){}


	public static Player newPlayer(int pHeight, float animationPeriod) throws Exception{
		Sprite sprite = SpriteFactory.getInstance().createImagePlayerCenterControllerSprite(animationPeriod, 800, 2, StaticResources.PLAYER_SHIP_A);
		return EntityFactory.getInstance().createEntity(0, pHeight/2,StaticResources.Z_PLAYER, 0, 0,1.0f, null, sprite, Player.class);
	}	

	public static ExplosionParticle newExplosionParticleFire(int posX, int posY,int maxSize,int maxSpeed,float maxLifeTimeInSeconds,ApplicationContext context){
		return new ExplosionParticle(ExplosionParticle.TYPE_FIRE,posX, posY,maxSize,maxSpeed,maxLifeTimeInSeconds,context);
	}	

	public static ExplosionParticle newExplosionParticleEnergetic(int posX, int posY,int maxSize,int maxSpeed,float maxLifeTimeInSeconds,ApplicationContext context){
		return new ExplosionParticle(ExplosionParticle.TYPE_ENERGETIC,posX, posY,maxSize,maxSpeed,maxLifeTimeInSeconds,context);
	}		
	
	public static EnergeticShield newEnergeticShield(int shieldSize, float shieldLifeTimeInSeconds,EntityManagerEntityRequest<Entity> effectManager,Position position,ApplicationContext context) throws Exception{
		EnergeticShield shield = new EnergeticShield(shieldSize,shieldLifeTimeInSeconds, context);
		shield.setUpdateAlgorithm(UpdateAlgorithmFactoryWrapper.newCopyPosition(0, 0, position));
		shield.setContext(context);
		shield.setEffectManager(effectManager);
		return shield;
	}	

	public static Smoke newSmoke(Position smokePosition) throws Exception{
		Sprite sprite = SpriteFactory.getInstance(). createImageSingleSprite(StaticResources.EFFECT_SMOKE); 
		return EntityFactory.getInstance().createEntity((int)smokePosition.getPosX(), (int)smokePosition.getPosY(),StaticResources.Z_EFFECT_UNDER, 0, 0,1.0f, null, sprite, Smoke.class);
	}

	public static PlayerEngine newEnginePlayer(Position position,UpdateAlgorithm algo,float period) throws Exception{
		Sprite sprite = SpriteFactory.getInstance().createImagePlayerSprite(period, 200, true,StaticResources.EFFECT_ENGINE); 
		return EntityFactory.getInstance().createEntity((int)position.getPosX(), (int)position.getPosY(),StaticResources.Z_EFFECT_UNDER+1, 0, 0,1.0f, algo, sprite, PlayerEngine.class);		
	}

	public static BaseEntity newEnemyShot1(Position shotPosition,UpdateAlgorithm algo) throws Exception{	
		Sprite sprite = SpriteFactory.getInstance(). createImageSingleSprite(StaticResources.ENEMY_SHOT1); 
		return EntityFactory.getInstance().createEntity((int)shotPosition.getPosX(),(int)shotPosition.getPosY(),StaticResources.Z_SHOT, -150, 0, 1.0f, algo, sprite, BaseEntity.class);		
	}

	public static BaseEntity newEnemyShot2(Position shotPosition,UpdateAlgorithm algo) throws Exception{
		Sprite sprite = SpriteFactory.getInstance(). createImageSingleSprite(StaticResources.ENEMY_SHOT2);
		return EntityFactory.getInstance().createEntity((int)shotPosition.getPosX(),(int)shotPosition.getPosY(),StaticResources.Z_SHOT, -350, 0, 1.0f, algo, sprite, BaseEntity.class);				
	}

	public static EnemyRocket newEnemyShot3(Position shotPosition,UpdateAlgorithm algo) throws Exception{	
		Sprite sprite = SpriteFactory.getInstance(). createImageSingleSprite(StaticResources.ENEMY_SHOT3);
		return EntityFactory.getInstance().createEntity((int)shotPosition.getPosX(),(int)shotPosition.getPosY(),StaticResources.Z_SHOT, -150, 0, 1.0f, algo, sprite, EnemyRocket.class);			
	}	
	
	public static LithingBolt newEnemyShotLightinBolt(ApplicationContext context,Position shotPosition,float fireTime,float loadTime) throws Exception{	
		shotPosition.setPosZ(StaticResources.Z_SHOT);
		LithingBolt shot = new LithingBolt(context,shotPosition,fireTime,loadTime);
		return shot;			
	}		

	public static BaseEntity playerShotGun(Position position) throws Exception{	
		Sprite sprite = SpriteFactory.getInstance(). createImageSingleSprite(StaticResources.PLAYER_GUN);
		return EntityFactory.getInstance().createEntity((int)position.getPosX(),(int)position.getPosY(),StaticResources.Z_SHOT, 400, 0, 1.0f, null, sprite, BaseEntity.class);			
	}
	
	public static BaseEntity playerShotPaser(Position position) throws Exception{	
		Sprite sprite = SpriteFactory.getInstance(). createImageSingleSprite(StaticResources.PLAYER_PASER);
		return EntityFactory.getInstance().createEntity((int)position.getPosX(),(int)position.getPosY(),StaticResources.Z_SHOT, 450, 0, 1.0f, null, sprite, BaseEntity.class);			
	}	

	public static PlayerRocket playerShotRocket(Position position) throws Exception{	
		Sprite sprite = SpriteFactory.getInstance(). createImageSingleSprite(StaticResources.PLAYER_ROCKET);
		return EntityFactory.getInstance().createEntity((int)position.getPosX(),(int)position.getPosY(),StaticResources.Z_SHOT, 90, 0, 1.0f, null, sprite, PlayerRocket.class);		
	}
	
	public static PlayerBomb playerShotBomb(Position position,int direction) throws Exception{	
		Sprite sprite = SpriteFactory.getInstance(). createImageSingleSprite(StaticResources.PLAYER_BOMB);
		PlayerBomb bomb = EntityFactory.getInstance().createEntity((int)position.getPosX(),(int)position.getPosY(),StaticResources.Z_SHOT, 70, direction*40, 1.0f, null, sprite, PlayerBomb.class);
		bomb.setDirection(direction);
		return bomb;
	}	
	
	public static BaseEntity playerShotGunSynuisodal(Position position,float startAngle) throws Exception{	
		Sprite sprite = SpriteFactory.getInstance(). createImageSingleSprite(StaticResources.PLAYER_GREEN);
		return EntityFactory.getInstance().createEntity((int)position.getPosX(),(int)position.getPosY(),StaticResources.Z_SHOT, 250, 0, 1.0f, 
														UpdateAlgorithmFactoryWrapper.newSinusoidal(15, 360, startAngle), sprite, BaseEntity.class);			
	}	





}
