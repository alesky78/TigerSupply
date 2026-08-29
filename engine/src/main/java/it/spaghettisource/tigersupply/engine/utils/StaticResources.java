package it.spaghettisource.tigersupply.engine.utils;

/**
 * Framework-level constant keys used across the engine: image-effect filter names, colour
 * saturation levels, and the property keys read by the {@code entity.logic} update algorithms.
 *
 * @author DOttavio
 *
 */
public class StaticResources {

	private StaticResources(){}

	//Filter registered by EffectManager
	public static final String FILTER_ROTATION  	= "rotate";
	public static final String FILTER_SCALE  	 	= "scale";
	public static final String FILTER_BRIGHTEN  	= "brighten";
	public static final String FILTER_TRANSPARENT  	= "transparent";	

	//Filter registered by EffectManager
	public static final short COLOR_SATURATION  	= 255;
	public static final short COLOR_UNSATURATION  	 = 0;
	public static final short COLOR_ORIGINAL  		= 1;	
	
	//Algotithm dynabean propeties
	public static final String ALGPRO_DELTA = "delta";
	public static final String ALGPRO_DELTAX = "deltax";	
	public static final String ALGPRO_DELTAY = "deltay";
	public static final String ALGPRO_SPEEDX = "speedx";	
	public static final String ALGPRO_SPEEDY = "speedy";	
	public static final String ALGPRO_POINT = "point";
	public static final String ALGPRO_SPRITE = "sprite";		
	public static final String ALGPRO_INCREMENT = "increment";
	public static final String ALGPRO_START 	= "start";	
	public static final String ALGPRO_LIST_POINTS = "listpoints";	
	
	
}
