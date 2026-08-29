package it.spaghettisource.tigersupply.engine.background;

import java.awt.Graphics2D;
import java.util.ArrayList;


/**
 * this is a basic implementation of a parallax background
 * @author Alessandro D'Ottavio
 *
 */
public class ParallaxBackGround implements BackGround {

	ArrayList<BackGround> backGrounds = new ArrayList<BackGround>();
	
	public void addBackGround(BackGround backGround){
		backGrounds.add(backGround);
	}
	
	
	public void updateBackground(float deltaSeconds) {
		for (BackGround backGround : backGrounds) {
			backGround.updateBackground(deltaSeconds);
		}
		
	}

	public void renderBackground(Graphics2D dbg) {
		for (BackGround backGround : backGrounds) {
			backGround.renderBackground(dbg);
		}
		
	}

}
