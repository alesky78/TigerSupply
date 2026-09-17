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

		//parallax: bigger stars are "closer" so they scroll faster, but must stay well below the
		//slowest enemy speed (level-1.xml enemies range ~30..200) or the background reads as
		//foreground and enemies look like they are moving in slow motion
		int parallaxSpeed = -15 - radius * 20 - random.nextInt(15);
		speed = new Speed(parallaxSpeed, 0);

		position = new Position(screenWidth, random.nextInt(screenHeight), 0);

		tint = TINTS[random.nextInt(TINTS.length)];
		//kept dim/subtle so stars don't compete with bullets/enemies for the player's attention
		baseBrightness = 70 + random.nextInt(70);			//70..139
		twinkleRange = 20 + random.nextInt(30);			//20..49
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
