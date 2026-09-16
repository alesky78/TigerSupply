package it.spaghettisource.tigersupply.game.entity.effect;

import it.spaghettisource.tigersupply.game.entity.BaseEntity;

import java.awt.Color;
import java.awt.Graphics2D;

import it.spaghettisource.tigersupply.engine.control.GameContext;
import it.spaghettisource.tigersupply.engine.entity.Position;
import it.spaghettisource.tigersupply.engine.entity.Speed;
import it.spaghettisource.tigersupply.engine.entity.logic.UpdateAlgorithmFactoryWrapper;
import it.spaghettisource.tigersupply.game.utils.GameResources;

public class ExplosionParticle extends BaseEntity {

	private static final double PI2 = 2*Math.PI;

	private float colorAlteration;
	private int   size;			
	private ParticleColorScheme scheme;

	protected float lifeTime;
	private float lifeCounter; 		

		
	public ExplosionParticle(ParticleColorScheme scheme,int posX, int posY,int maxSize,int maxSpeed,float maxLifeTimeInSeconds,GameContext context){
		this.scheme = scheme;

		lifeTime = Math.max(0.01f, (float) (Math.random()*maxLifeTimeInSeconds));
		lifeCounter = 0;

		colorAlteration = 1f;
		size = Math.max(1, (int) (Math.random()*maxSize));

		//uniform random direction on the full circle at a speed within maxSpeed
		double angle = PI2*Math.random();
		double r = Math.random()*maxSpeed;
		double speedX = Math.cos(angle)*r;
		double speedY = Math.sin(angle)*r;

		speed = new Speed((int)speedX,(int)speedY);

		position = new Position(posX, posY,GameResources.Z_EXPLOSION);
		try {
			updateAlgorithm = UpdateAlgorithmFactoryWrapper.newDefault();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void updateEntity(float deltaSeconds)  throws Exception  {
		super.updateEntity(deltaSeconds);

		lifeCounter+=deltaSeconds;
		if(lifeCounter>=lifeTime){
			remove = true;
		}

		//fade based on elapsed lifetime, framerate independent
		colorAlteration = 1 - (lifeCounter / lifeTime);
		if(colorAlteration<0){
			colorAlteration = 0f;
		}

	}			


	public void renderEntity(Graphics2D dbg) throws Exception {
		Color originalColor = dbg.getColor();
		dbg.setColor(scheme.colorAt(colorAlteration));
		
		//dbg.fill3DRect(getXposition()-size/2,getYposition()-size/2,(int)(size*colorAlteration),(int)(size*colorAlteration),false);
		
		dbg.fillOval(getXposition()-size/2,getYposition()-size/2,(int)(size*colorAlteration),(int)(size*colorAlteration));
		
		dbg.setColor(originalColor);

	}	



}
