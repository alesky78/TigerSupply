package it.spaghettisource.tigersupply.engine.image.finaleffect;



import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Random;

import it.spaghettisource.tigersupply.engine.entity.AbstractEntity;
import it.spaghettisource.tigersupply.engine.entity.Position;
import it.spaghettisource.tigersupply.engine.entity.Speed;
import it.spaghettisource.tigersupply.engine.entity.logic.UpdateAlgorithmFactoryWrapper;

/**
 * A single rain drop rendered as a short, slightly slanted vertical streak that
 * falls from the top of the screen. Companion particle of {@link Rain}, mirroring
 * {@link StarEntity}.
 *
 * @author Alessandro D'Ottavio
 */
public class RainEntity extends AbstractEntity {


	private static Random random = new Random();

	private static final Color RAIN_COLOR = new Color(170, 190, 230);
	private static final int STREAK_LENGTH = 12;

	public RainEntity(int screenWidth, int screenHeight){

		speed = new Speed(-120,900);

		position = new Position(random.nextInt(screenWidth), -STREAK_LENGTH, 0);

		try {
			updateAlgorithm = UpdateAlgorithmFactoryWrapper.newDefault();
		} catch (Exception e) {}

	}




	public void renderEntity(Graphics2D dbg) throws Exception {
		Color originalColor = dbg.getColor();

		dbg.setColor(RAIN_COLOR);
		int x = (int)position.getPosX();
		int y = (int)position.getPosY();
		dbg.drawLine(x, y, x - 2, y + STREAK_LENGTH);

		dbg.setColor(originalColor);

	}



}
