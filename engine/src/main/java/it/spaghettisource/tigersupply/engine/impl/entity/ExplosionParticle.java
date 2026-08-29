package it.spaghettisource.tigersupply.engine.impl.entity;


import java.awt.Color;
import java.awt.Graphics2D;

import it.spaghettisource.tigersupply.engine.control.ApplicationContext;
import it.spaghettisource.tigersupply.engine.entity.Position;
import it.spaghettisource.tigersupply.engine.entity.Speed;
import it.spaghettisource.tigersupply.engine.impl.utils.UpdateAlgorithmFactoryWrapper;
import it.spaghettisource.tigersupply.engine.utils.StaticResources;

public class ExplosionParticle extends BaseEntity {

	public static final int TYPE_FIRE = 0;
	public static final int TYPE_ENERGETIC = 1;	
	private static double PI2 = 2*Math.PI;

	private float increaseForLoop;
	private float colorAlteration;
	private int   size;			
	private int   type;

	protected float spriteTimeDuration;
	private float spriteCounter; 		

		
	public ExplosionParticle(int type,int posX, int posY,int maxSize,int maxSpeed,float maxLifeTimeInSeconds,ApplicationContext context){
		this.type = type;
	
		spriteTimeDuration = (float) (Math.random()*maxLifeTimeInSeconds);
		spriteCounter = 0;

		float loops = (float) (spriteTimeDuration/context.getPeriodSeconds());

		colorAlteration = 1f;
		increaseForLoop = 1 / loops;
		size = (int) (Math.random()*maxSize);

		double rangle = (PI2)*Math.random();
		double sinX = Math.sin(rangle);
		double cosX = Math.cos(rangle);		
		double sinY = Math.sin((PI2)*Math.random());		
		double speedX = 0,speedY = 0;
		if(sinX>0){
			speedX=sinX*maxSpeed;
			speedY=sinY*maxSpeed;
			if(sinY>0){
				if(speedX+speedY > maxSpeed)
					speedY = Math.random()*maxSpeed*cosX;	
			}else if(sinY<0){
				if(speedX-speedY > maxSpeed)
					speedY = Math.random()*maxSpeed*cosX;
			}else{
				speedY = 0;
				speedX = maxSpeed;
			}
		}else if(sinX<0){
			speedX=sinX*maxSpeed;
			speedY=sinY*maxSpeed;
			if(sinY>0){
				if(-speedX+speedY > maxSpeed)
					speedY = Math.random()*maxSpeed*cosX;	
			}else if(sinY<0){
				if(-speedX-speedY > maxSpeed)
					speedY = Math.random()*maxSpeed*cosX;
			}else{
				speedY = 0;
				speedX = maxSpeed;				
			}
		}else{
			speedY = 0;
			speedY = maxSpeed;
		}


		speed = new Speed((int)speedX,(int)speedY);

		//speed che crea un quadrato
		//speed = new SpriteSpeed(Math.sin((PI2)*Math.random())*maxSpeed, Math.sin((PI2)*Math.random())*maxSpeed);
		position = new Position(posX, posY,StaticResources.Z_EXPLOSION);
		try {
			updateAlgorithm = UpdateAlgorithmFactoryWrapper.newDefault();
		} catch (Exception e) {}

	}

	public void updateEntity(float deltaSeconds)  throws Exception  {
		super.updateEntity(deltaSeconds);

		spriteCounter+=deltaSeconds;
		if(spriteCounter>=spriteTimeDuration){
			remove = true;
		}

		//manage the reduction of the color each cycle
		colorAlteration-=increaseForLoop;
		if(colorAlteration<0){
			colorAlteration = 0f;
		}

	}			


	public void renderEntity(Graphics2D dbg) throws Exception {
		Color originalColor = dbg.getColor();
		if(type==TYPE_FIRE){
			dbg.setColor(new Color(1, colorAlteration, 0, 1));  //red to yellow			
		}else if (type==TYPE_ENERGETIC){
			dbg.setColor(new Color(colorAlteration,colorAlteration,1, 1));  //white to blue			
		}
		
		//dbg.fill3DRect(getXposition()-size/2,getYposition()-size/2,(int)(size*colorAlteration),(int)(size*colorAlteration),false);
		
		dbg.fillOval(getXposition()-size/2,getYposition()-size/2,(int)(size*colorAlteration),(int)(size*colorAlteration));
		
		dbg.setColor(originalColor);

	}	



}
