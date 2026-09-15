package it.spaghettisource.tigersupply.game.entity.player;

import it.spaghettisource.tigersupply.game.entity.effect.Effect;


public class PlayerEngine extends Effect {

	
	public PlayerEngine(){
		spriteTimeDuration = -1;
	}
	
	public boolean isOutOfScreen(int windowWidth, int windowHeight){
		return false;
	}

}
