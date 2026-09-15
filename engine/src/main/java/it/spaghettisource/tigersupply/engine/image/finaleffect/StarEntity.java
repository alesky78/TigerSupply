package it.spaghettisource.tigersupply.engine.image.finaleffect;



import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Random;

import it.spaghettisource.tigersupply.engine.entity.AbstractEntity;
import it.spaghettisource.tigersupply.engine.entity.Position;
import it.spaghettisource.tigersupply.engine.entity.Speed;
import it.spaghettisource.tigersupply.engine.entity.logic.UpdateAlgorithmFactoryWrapper;

public class StarEntity extends AbstractEntity {


	private static Random random = new Random();

	//a few subtle star tints, most stars stay white-ish
	private static final Color[] TINTS = {
		new Color(255, 255, 255),
		new Color(255, 255, 255),
		new Color(200, 220, 255),	//cold blue
		new Color(255, 240, 210),	//warm white
		new Color(255, 210, 190)	//faint red giant
	};

	private final int radius;			//half-size of the star in pixels
	private final Color tint;
	private final int baseBrightness;	//0..255 average alpha
	private final int twinkleRange;		//how much the brightness oscillates
	private final float twinkleSpeed;	//radians per second
	private float twinklePhase;

	public StarEntity(int screenWidth, int screenHeight){

		//size 0 -> tiny far star, 2 -> big close star
		radius = random.nextInt(3);

		//parallax: bigger stars are "closer" so they scroll faster
		int parallaxSpeed = -220 - radius * 150 - random.nextInt(80);
		speed = new Speed(parallaxSpeed, 0);

		position = new Position(screenWidth, random.nextInt(screenHeight), 0);

		tint = TINTS[random.nextInt(TINTS.length)];
		baseBrightness = 150 + random.nextInt(106);		//150..255
		twinkleRange = 40 + random.nextInt(60);			//40..99
		twinkleSpeed = 2f + random.nextFloat() * 4f;	//2..6 rad/s
		twinklePhase = random.nextFloat() * (float) (Math.PI * 2);

		try {
			updateAlgorithm = UpdateAlgorithmFactoryWrapper.newDefault();
		} catch (Exception e) {}

	}


	public void updateEntity(float deltaSeconds) throws Exception {
		super.updateEntity(deltaSeconds);
		twinklePhase += twinkleSpeed * deltaSeconds;
	}


	public void renderEntity(Graphics2D dbg) throws Exception {
		Color originalColor = dbg.getColor();

		int brightness = baseBrightness + (int) (Math.sin(twinklePhase) * twinkleRange);
		if(brightness < 0){
			brightness = 0;
		}else if(brightness > 255){
			brightness = 255;
		}

		dbg.setColor(new Color(tint.getRed(), tint.getGreen(), tint.getBlue(), brightness));

		int x = (int) position.getPosX();
		int y = (int) position.getPosY();

		if(radius == 0){
			dbg.drawLine(x, y, x, y);
		}else{
			int diameter = radius * 2 + 1;
			dbg.fillOval(x - radius, y - radius, diameter, diameter);
		}

		dbg.setColor(originalColor);

	}



}
