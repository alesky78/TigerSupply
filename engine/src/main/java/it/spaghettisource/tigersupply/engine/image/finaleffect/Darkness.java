package it.spaghettisource.tigersupply.engine.image.finaleffect;

import java.awt.Color;
import java.awt.Graphics2D;

public class Darkness extends AbstractFinalEffect {

	private int alphaChannel;	//1.0 means mantain original values
	private int RChannel;		//1.0 means mantain original values
	private int GChannel;		//1.0 means mantain original values
	private int BChannel;		//1.0 means mantain original values	
	
	private int alphaGrowth;	
	
	public Darkness(){
		RChannel = 0;
		GChannel = 0;
		BChannel = 0;
		alphaChannel = 0;
		active = false;
	}
	
	
	public void configAndStart(float secondToDark,float periodInSecond){
		alphaChannel = 0;
		int tick = (int) (secondToDark/periodInSecond);
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
