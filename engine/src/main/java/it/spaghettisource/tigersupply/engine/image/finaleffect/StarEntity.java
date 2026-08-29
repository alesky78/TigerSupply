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
	
	public StarEntity(int screenWidth, int screenHeight){
		
		speed = new Speed(-450,0);
		
		position = new Position(screenWidth, random.nextInt(screenHeight),0);
		
		try {
			updateAlgorithm = UpdateAlgorithmFactoryWrapper.newDefault();
		} catch (Exception e) {}

	}

	
	

	public void renderEntity(Graphics2D dbg) throws Exception {
		Color originalColor = dbg.getColor();
		
		dbg.setColor(Color.WHITE);
		dbg.drawLine((int)position.getPosX(), (int)position.getPosY(), (int)position.getPosX(), (int)position.getPosY());
		
		dbg.setColor(originalColor);

	}	



}
