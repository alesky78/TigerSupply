package it.spaghettisource.tigersupply.engine.utils;

/**
 * costant of all the resources used from the repository
 * 
 * @author DOttavio
 *
 */
public class StaticResources {

	private StaticResources(){}
	
	//font
	public static final String FONT_TECHNO = "techno";	
	public static final String FONT_C64 = "comodore64";	
	
	
	//background Images
	public static final String BCKGROUND_SPACE = "bcgr_space";
	public static final String BCKGROUND_PLANET = "bcgr_planet";
	public static final String BCKGROUND_HANGAR = "bcgr_hangar";	
	
	//sprites Images
	public static final String EFFECT_ENGINE = "engine";
	public static final String EFFECT_SMOKE = "smoke";
	public static final String EFFECT_EXPLOSION = "explosion";
	public static final String ENEMY_1 = "enemy1";
	public static final String ENEMY_2 = "enemy2";
	public static final String ENEMY_3 = "enemy3";
	public static final String ENEMY_4 = "enemy4";			
	public static final String ENEMY_BOSS = "boss";	
	public static final String ENEMY_SHOT1 = "enemyshot1";
	public static final String ENEMY_SHOT2 = "enemyshot2";
	public static final String ENEMY_SHOT3 = "enemyshot3";	
	public static final String PLAYER_SHIP_A = "playerA";
	public static final String PLAYER_SHIP_B = "playerB";	
	public static final String PLAYER_GUN  = "playershot1";
	public static final String PLAYER_ROCKET  = "playershot2";	
	public static final String PLAYER_GREEN  = "playershot3";	
	public static final String PLAYER_BOMB  = "playershot4";
	public static final String PLAYER_PASER  = "playershot5";
	public static final String ASTEROID_1  = "asteroid1";	
	public static final String ASTEROID_2  = "asteroid2";
	public static final String ASTEROID_3  = "asteroid3";
	public static final String ASTEROID_4  = "asteroid4";	

	//Game state used by the GameManager
	public static final String GAME_STATE_PRESENTATION = "presentation";
	public static final String GAME_STATE_PLAY 		   = "play";

//	public static final String GAME_STATE_GAME_OVER    = "gameover";
//	public static final String GAME_STATE_HANGAR 	   = "hangar";
//	public static final String GAME_STATE_GAME_END 	   = "gameend";	

	//State machine level enemy builder events
	public static final String GAME_EVENT_START  	 	= "start";
	public static final String GAME_EVENT_PRESENTATION  = "presentation";	
	public static final String GAME_EVENT_PLAYER_DEATH  = "playerdeath";
	public static final String GAME_EVENT_BOSS_DEATH    = "bossdeath";	
	public static final String GAME_EVENT_CONTINUE_LVL  = "contiuneLevel";	
	
	//State machine level enemy builder states
	public static final String STATE_GENERATE_HORDE  = "generateHorde";
	public static final String STATE_KILL_BOSS 	 	 = "killBoss";
	public static final String STATE_WAIT_KILL  	 = "waitKill";
	public static final String STATE_WAIT_TIME  	 = "waitTime";

	//State machine level enemy builder events
	public static final String EVENT_WAIT  	 	    = "wait";
	public static final String EVENT_NEW_HORDE      = "newHorde";
	public static final String EVENT_WAIT_KILL      = "waitKill";
	public static final String EVENT_WAIT_TIME      = "waitTime";
	public static final String EVENT_BOSS_GENERATED = "bossGenerated";	
	public static final String EVENT_BOSS_KILLED    = "bossKilled";		
	 
	//Filter registered by EffectManager
	public static final String FILTER_ROTATION  	= "rotate";
	public static final String FILTER_SCALE  	 	= "scale";
	public static final String FILTER_BRIGHTEN  	= "brighten";
	public static final String FILTER_TRANSPARENT  	= "transparent";	

	//Filter registered by EffectManager
	public static final short COLOR_SATURATION  	= 255;
	public static final short COLOR_UNSATURATION  	 = 0;
	public static final short COLOR_ORIGINAL  		= 1;	
	
	//Z coordinate 
	public static final int Z_EXPLOSION  			= 5;
	public static final int Z_SHOT  				= 25;
	public static final int Z_PLAYER  				= 20;
	public static final int Z_ENEMY  				= 20;
	public static final int Z_EFFECT_UNDER  		= 30;	
	public static final int Z_ENEMY_BACKGROUND  	= 50;	
	
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
