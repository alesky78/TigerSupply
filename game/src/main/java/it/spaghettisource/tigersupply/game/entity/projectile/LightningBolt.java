package it.spaghettisource.tigersupply.game.entity.projectile;

import it.spaghettisource.tigersupply.engine.entity.Entity;
import it.spaghettisource.tigersupply.game.entity.BaseEntity;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RadialGradientPaint;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.util.Random;

import it.spaghettisource.tigersupply.engine.control.GameContext;
import it.spaghettisource.tigersupply.engine.entity.Position;


/**
 * LightningBolt gun weapon
 * 
 * 
 * @author DOttavio
 *
 */
public class LightningBolt extends BaseEntity {

	private static final Color ENERGY_CYAN = new Color(102, 230, 255);
	private static final Color ENERGY_BLUE = new Color(0, 90, 160);
	private static final Color ENERGY_GLOW = new Color(0, 90, 160, 80);
	private static final float LOADING_GLOW_FACTOR = 1.5f;

	private int statusWeapon; // 0 --> loading; 1 --> shot
	
	private float 	actualTickShot;	//number of update for this entity during shot phase	
	private float 	lifeTimeShot;	//life cycle in seconds of this entity during shot phase
	private float 	lifeTickShot;	//number of update for the life cycle of this entity during shot phase
	
	private float 	actualTickLoading;	//number of update for this entity during loading phase	
	private float 	lifeTimeLoading;	//life cycle in seconds of this entity during loading phase

	private float sizeLoadingBall = 1;
	private float sizeIncreasingLoadingBall = 0;	
	private float sizeMaxLoadingBall = 40;	
	
	private Random 	random;

	private int[] 	xPoint;
	private int[] 	yPoint;
	private int		maxPoints;		//array capacity, sized on screen width since the boss can still move before/while firing
	private int		points;			//points actually used this frame, recomputed so the bolt always reaches the screen edge
	private int		pointsFrequency;
	private int		pointsOffset;		

	public LightningBolt(GameContext context,Position position,float shotTime,float loadingTime){

		lifeTimeShot = 2;
		lifeTickShot = lifeTimeShot/context.getPeriodSeconds();
		
		lifeTimeLoading = 4;
		actualTickLoading = lifeTimeLoading/context.getPeriodSeconds();
		sizeIncreasingLoadingBall = (sizeMaxLoadingBall/actualTickLoading);
		
		
		this.position = position;

		random = new Random();
		pointsFrequency = 25;
		pointsOffset = 5;
		maxPoints = (int) (context.getScreenWidth()/pointsFrequency)+2;
		xPoint = new int[maxPoints]; 
		yPoint = new int[maxPoints];		

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
			//recomputed every tick: the boss may still be moving, so the reach must track its current X
			//clamped to [2, maxPoints] so a negative/off-screen X can never yield an empty polyline
			int reachX = (int) Math.max(0, position.getPosX());
			points = Math.max(2, Math.min(maxPoints, (reachX/pointsFrequency)+2));
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
			Point center = new Point((int)position.getPosX()-offset, (int)position.getPosY());   
			float glowSize = size * LOADING_GLOW_FACTOR;
			float[] dist = {0f, 0.45f, 0.75f, 1f};
			Color[] colors = {Color.WHITE, ENERGY_CYAN, new Color(0, 90, 160, 190), ENERGY_GLOW};
			dbg.setPaint(new RadialGradientPaint(center, glowSize / 2f, dist, colors));
			dbg.fillOval((int) (center.x - glowSize / 2f), (int) (center.y - glowSize / 2f),
					(int) glowSize, (int) glowSize);
			
		}else{

			//first render after the loading->shot transition can happen before updateEntity ever set points
			if(points < 2){
				return;
			}

			Color originalColor = dbg.getColor();
			Stroke originalStroke = dbg.getStroke();

			dbg.setStroke(new BasicStroke(5.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
			dbg.setPaint(new GradientPaint(xPoint[0], yPoint[0], ENERGY_CYAN, xPoint[points-1], yPoint[0], ENERGY_BLUE));
			dbg.drawPolyline(xPoint, yPoint, points);

			dbg.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
			dbg.setPaint(new GradientPaint(xPoint[0], yPoint[0], Color.WHITE, xPoint[points-1], yPoint[0], ENERGY_CYAN));
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
