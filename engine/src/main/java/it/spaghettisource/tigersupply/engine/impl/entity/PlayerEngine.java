package it.spaghettisource.tigersupply.engine.impl.entity;


public class PlayerEngine extends Effect {

	
	public PlayerEngine(){
		spriteTimeDuration = -1;
	}
	
	public boolean isOutOfScreen(int windowWidth, int windowHeight){
		return false;
	}

}
