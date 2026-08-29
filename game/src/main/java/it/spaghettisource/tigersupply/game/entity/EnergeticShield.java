package it.spaghettisource.tigersupply.game.entity;


import java.awt.Color;
import java.awt.Graphics2D;

import it.spaghettisource.tigersupply.engine.control.GameContext;
import it.spaghettisource.tigersupply.engine.entity.Position;
import it.spaghettisource.tigersupply.engine.entity.Size;
import it.spaghettisource.tigersupply.engine.entity.Speed;
import it.spaghettisource.tigersupply.game.utils.GameResources;
import it.spaghettisource.tigersupply.engine.entity.Entity;


/**
 * energetic shield
 * 
 * 
 * @author DOttavio
 *
 */
public class EnergeticShield extends Enemy {
	
	private int   	maxSize;	
	private double  sizeShield;
	private float   color;		
	private double  increaseSizeForLoop;	
	private double  decreaseColorForLoop;		
	
	public EnergeticShield(int maxSize, float lifeTime,  GameContext context){
		
		this.maxSize = maxSize;
		increaseSizeForLoop = maxSize/(lifeTime/context.getPeriodSeconds()); //increment to reach maxSize
		decreaseColorForLoop = 1/(lifeTime/context.getPeriodSeconds());
		sizeShield = 1;
		color = 1;
		
		//create enemy variable
		life = 0;			
		particleNum = 20;
		particleMaxSize = 20;
		particleDeathMaxSize = 20;	
		particleMaxSpeed = 30;
		particleDeathMaxSpeed = 30 ;	
		particleMaxLifeTime = 0.3f;	
		particleDeathMaxLifeTime= 0.3f;				
		
		//create sprite variables
		size = new Size((int)sizeShield, (int)sizeShield);
		speed = new Speed();
		position = new Position(0, 0,GameResources.Z_EFFECT_UNDER);
	}
	
	
	public boolean canBeRemoved(){
		return (life<0 || remove);
	}		

	public void collided(Entity other) {
		life--;		
		createdHitExplosionParticleEnergy(other);			
	}	
	
	public void updateEntity(float deltaSeconds)  throws Exception  {
		super.updateEntity(deltaSeconds);

		sizeShield+=increaseSizeForLoop;
		color-=decreaseColorForLoop;
		
		//increse the size of the protection of the shield
		size.setHeigh((int)sizeShield);
		size.setWidth((int)sizeShield);
		
		if(sizeShield > maxSize || color <= 0){
			sizeShield = maxSize;
			color = 1;			
			remove = true;
		}
		
	}			

	public void renderEntity(Graphics2D dbg) throws Exception {
		Color originalColor = dbg.getColor();
		
		dbg.setColor(new Color(color, color, 1,color)); //blue
		dbg.fillOval(getXposition()-(int)(sizeShield/2),getYposition()-(int)(sizeShield/2),(int)sizeShield,(int)sizeShield);						
		
		dbg.setColor(originalColor);

	}	



}
