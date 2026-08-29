package it.spaghettisource.tigersupply.engine.sprite;

import it.spaghettisource.tigersupply.engine.utils.StaticResources;

/**
 * sprite color informations
 * 
 * each value must be contained in range between 0 and 255
 * 
 * 
 * @author Alessandro D'Ottavio
 *
 */
public class SpriteColor {

	private double alphaChannel;	//1.0 means mantain original values
	private double RChannel;		//1.0 means mantain original values
	private double GChannel;		//1.0 means mantain original values
	private double BChannel;		//1.0 means mantain original values	

	public SpriteColor(){
		alphaChannel = 1.0;
		RChannel =  1.0;
		GChannel =  1.0;
		BChannel =  1.0;
	}

	public double getAlphaChannel() {
		return alphaChannel;
	}

	public void setAlphaChannel(double value) {
		this.alphaChannel = verifyValue(value);
	}

	public double getRChannel() {
		return RChannel;
	}

	public void setRChannel(double value) {
		RChannel = verifyValue(value);
	}

	public double getGChannel() {
		return GChannel;
	}

	public void setGChannel(double value) {
		GChannel = verifyValue(value);
	}	

	public double getBChannel() {
		return BChannel;
	}

	public void setBChannel(double value) {
		BChannel = verifyValue(value);
	}	

	public void setRGBChannel(double value) {
		double allValue =verifyValue(value); 
		RChannel = allValue;
		GChannel = allValue;
		BChannel = allValue;
	}
	
	
	private double verifyValue(double value){
		if(value <StaticResources.COLOR_UNSATURATION){
			return StaticResources.COLOR_UNSATURATION;
		}else if(value >StaticResources.COLOR_SATURATION){
			return StaticResources.COLOR_SATURATION;
		}
		return value;
	}

}
