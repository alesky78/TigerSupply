package it.spaghettisource.tigersupply.game.utils;

/**
 * Constant keys for all TigerSupply game content: image/audio/font asset aliases, game state and
 * event names, level state-machine names, and render-layer Z coordinates.
 *
 * @author DOttavio
 *
 */
public class GameResources {

	private GameResources(){}

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

	//Z coordinate 
	public static final int Z_EXPLOSION  			= 5;
	public static final int Z_SHOT  				= 25;
	public static final int Z_PLAYER  				= 20;
	public static final int Z_ENEMY  				= 20;
	public static final int Z_EFFECT_UNDER  		= 30;	
	public static final int Z_ENEMY_BACKGROUND  	= 50;	
	
	
}
