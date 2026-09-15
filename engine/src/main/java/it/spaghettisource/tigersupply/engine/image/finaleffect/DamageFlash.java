package it.spaghettisource.tigersupply.engine.image.finaleffect;

import java.awt.Color;
import java.awt.Graphics2D;

/**
 * Full-screen red overlay that flashes at a configurable opacity and then fades
 * out to transparent, giving the player quick "you were hit" feedback. Unlike
 * {@link Darkness}/{@link Lightness} the alpha shrinks over time and the effect
 * deactivates itself once fully faded.
 *
 * @author Alessandro D'Ottavio
 */
public class DamageFlash extends AbstractFinalEffect {

	private int alphaChannel;
	private int RChannel;
	private int GChannel;
	private int BChannel;

	private int alphaDecay;

	public DamageFlash(){
		RChannel = 255;
		GChannel = 0;
		BChannel = 0;
		alphaChannel = 0;
		active = false;
	}

	/**
	 * Start the flash at {@code startAlpha} opacity and fade it out linearly.
	 *
	 * @param startAlpha     initial opacity in the 0..255 range
	 * @param secondToFade   time, in seconds, to fade back to transparent
	 * @param periodInSecond duration of one update tick, in seconds
	 */
	public void configAndStart(int startAlpha,float secondToFade,float periodInSecond){
		alphaChannel = startAlpha;
		int tick = (int) (secondToFade/periodInSecond);
		alphaDecay = startAlpha/tick;
		active = true;
	}

	public void reset(){
		alphaChannel = 0;
		active = false;
	}


	public void updateEffect(float deltaSeconds) throws Exception {

		if(alphaChannel>0){
			alphaChannel = alphaChannel-alphaDecay;
			if(alphaChannel<=0){
				alphaChannel = 0;
				active = false;
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
