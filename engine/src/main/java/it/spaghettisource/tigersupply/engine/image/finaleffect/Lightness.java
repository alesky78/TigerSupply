package it.spaghettisource.tigersupply.engine.image.finaleffect;

import java.awt.Color;
import java.awt.Graphics2D;

/**
 * Full-screen overlay that fades the image toward white, the inverse of
 * {@link Darkness}. Useful for explosions, lightning strikes or a "blinded"
 * moment.
 *
 * @author Alessandro D'Ottavio
 */
public class Lightness extends AbstractFinalEffect {

	private int alphaChannel;	//0 means the original image is untouched
	private int RChannel;
	private int GChannel;
	private int BChannel;

	private int alphaGrowth;

	public Lightness(){
		RChannel = 255;
		GChannel = 255;
		BChannel = 255;
		alphaChannel = 0;
		active = false;
	}


	public void configAndStart(float secondToLight,float periodInSecond){
		alphaChannel = 0;
		int tick = (int) (secondToLight/periodInSecond);
		alphaGrowth = 255/tick;
		active = true;
	}

	public void reset(){
		alphaChannel = 0;
		active = false;
	}

	public boolean isFinish(){
		return active && alphaChannel == 255;
	}


	public void updateEffect(float deltaSeconds) throws Exception {

		if(alphaChannel<255){
			alphaChannel = alphaChannel+alphaGrowth;
			if(alphaChannel>=255){
				alphaChannel = 255;
			}
		}
	}


	public void renderEffect(Graphics2D dbg,int screenWidth,int screenHeight) throws Exception {

		Color original = dbg.getColor();
		dbg.setColor(new Color(RChannel, GChannel, BChannel, alphaChannel) );
		dbg.fillRect(0, 0, screenWidth, screenHeight);
		dbg.setColor(original);
	}


}
