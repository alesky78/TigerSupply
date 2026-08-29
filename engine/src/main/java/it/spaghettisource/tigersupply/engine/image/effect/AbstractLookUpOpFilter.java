package it.spaghettisource.tigersupply.engine.image.effect;

import it.spaghettisource.tigersupply.engine.utils.StaticResources;

public abstract class AbstractLookUpOpFilter extends AbstractFilter {

	short[] noChange;			//used to channel that don't change see the hapha channel
	short[] saturation;			//used to channel that don't change see the hapha channel	
	short[] unsaturation;		//used to channel that don't change see the hapha channel	
	
	public AbstractLookUpOpFilter(){
		noChange = calculateArrayNoChange();
		saturation = calculateArrayByCostant(StaticResources.COLOR_SATURATION);
		unsaturation = calculateArrayByCostant(StaticResources.COLOR_UNSATURATION);		
	}
	
	
	protected short[] calculateArrayByPercentage(double percentage) {
		
		if(percentage == StaticResources.COLOR_UNSATURATION)
			return unsaturation;
		if(percentage == StaticResources.COLOR_ORIGINAL)
			return noChange;		
		if(percentage == StaticResources.COLOR_SATURATION)
			return saturation;
		
		short[] array = new short[256];
		short value;
		for (int i = 0; i < array.length; i++) {
			value = (short) (i*percentage);
			if(value>StaticResources.COLOR_SATURATION){
				value = StaticResources.COLOR_SATURATION;
			}
			if(value<StaticResources.COLOR_UNSATURATION){
				value = StaticResources.COLOR_UNSATURATION;
			}
			array[i] = value;
		}
		return array;
	}		

	private short[] calculateArrayNoChange() {
		short[] array = new short[256];
		for(int i=0; i < 256; i++) {
			array[i] = (short) i;
		}
		return array;
	}	
	
	private short[] calculateArrayByCostant(short constant) {
		short[] array = new short[256];
		for(int i=0; i < 256; i++) {
			array[i] = constant;
		}
		return array;
	}	


}
