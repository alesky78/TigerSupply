package it.spaghettisource.tigersupply.game.entity;


import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;
import java.awt.RadialGradientPaint;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.util.Random;

import it.spaghettisource.tigersupply.engine.control.ApplicationContext;
import it.spaghettisource.tigersupply.engine.entity.Position;


/**
 * LithingBolt gun weapon
 * 
 * 
 * @author DOttavio
 *
 */
public class LithingBolt extends BaseEntity {

	private int statusWeapon; // 0 --> loading; 1 --> shot
	
	private float 	actualTickShot;	//number of update for this entity during shot phase	
	private float 	lifeTimeShot;	//life cycle in seconds of this entity during shot phase
	private float 	lifeTickShot;	//number of update for the life cycle of this entity during shot phase
	
	private float 	actualTickLoading;	//number of update for this entity during loading phase	
	private float 	lifeTimeLoading;	//life cycle in seconds of this entity during loading phase
	private float 	lifeTickLoading;	//number of update for the life cycle of this entity during loading phase	

	private float sizeLoadingBall = 1;
	private float sizeIncreasingLoadingBall = 0;	
	private float sizeMaxLoadingBall = 40;	
	
	private Random 	random;

	private int[] 	xPoint;
	private int[] 	yPoint;
	private int		points;
	private int		pointsFrequency;
	private int		pointsOffset;		

	public LithingBolt(ApplicationContext context,Position position,float shotTime,float loadingTime){

		lifeTimeShot = 2;
		lifeTickShot = lifeTimeShot/context.getPeriodSeconds();
		
		lifeTimeLoading = 4;
		actualTickLoading = lifeTimeLoading/context.getPeriodSeconds();
		sizeIncreasingLoadingBall = (sizeMaxLoadingBall/actualTickLoading);
		
		
		this.position = position;

		random = new Random();
		pointsFrequency = 25;
		points = (int) (position.getPosX()/pointsFrequency)+2;
		pointsOffset = 5;
		xPoint = new int[points]; 
		yPoint = new int[points];		

		statusWeapon = 0;	//preparation of the bolt

	}

	public void shotLaser(){
		statusWeapon = statusWeapon+1;
	}


	public void updateEntity(float deltaSeconds)  throws Exception  {

		if(statusWeapon == 0){
			actualTickLoading = actualTickLoading+1;
			sizeLoadingBall = sizeLoadingBall+sizeIncreasingLoadingBall;
			
			if(sizeLoadingBall <= 0)
				sizeLoadingBall = 1f;
			if(sizeLoadingBall >= sizeMaxLoadingBall)
				sizeLoadingBall = sizeMaxLoadingBall;

		}else{
			xPoint[0] = (int) position.getPosX();
			yPoint[0] = (int) position.getPosY();		
			for (int i = 1; i < points; i++) {
				xPoint[i] = xPoint[i-1]-pointsFrequency;
				yPoint[i] = yPoint[0] + genDeltaY();
			}

			actualTickShot = actualTickShot+1;
			if(actualTickShot>=lifeTickShot){
				remove = true;
			}	
		}

	}	

	public void collided(Entity other) {
		remove = false;	//remove only with time and not with collision
	}		

	public void renderEntity(Graphics2D dbg) throws Exception {

		if(statusWeapon == 0){
			
			int offset = 80;
			int size = (int) sizeLoadingBall;
			Paint original = dbg.getPaint();
			Point center = new Point((int)position.getPosX()-offset, (int)position.getPosY());   
		    float[] dist = {0.1f, 0.5f};
		    Color[] colors = {Color.WHITE, Color.BLUE};
			dbg.setPaint(new RadialGradientPaint(center,size,dist,colors));
			dbg.fillOval((int)position.getPosX()-offset-size/2, (int)position.getPosY()-size/2, size, size);
			
		}else{

			Color originalColor = dbg.getColor();
			Stroke originalStroke = dbg.getStroke();

			dbg.setStroke(new BasicStroke(2.0f));

			dbg.setPaint(new GradientPaint(xPoint[0], yPoint[0], Color.WHITE, xPoint[points-1], yPoint[0], Color.BLUE));
			dbg.drawPolyline(xPoint, yPoint, points);


			dbg.setColor(originalColor);
			dbg.setStroke(originalStroke);
		}
	}	


	public Rectangle[] getEntityRectangle(){
		
		if(statusWeapon == 0){
			return new Rectangle[]{};	
		}else{
			Rectangle rect = new Rectangle(0, (int)(position.getPosY()), (int)(position.getPosX()), 1);
			return new Rectangle[]{rect};
		}
		
		
		 
	}	

	private int genDeltaY(){
		return (int)  (pointsOffset * Math.sin(Math.toRadians(random.nextInt(361))));
	}


}
