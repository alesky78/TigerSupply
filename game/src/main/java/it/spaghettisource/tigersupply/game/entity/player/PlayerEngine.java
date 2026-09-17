package it.spaghettisource.tigersupply.game.entity.player;

import it.spaghettisource.tigersupply.engine.entity.Entity;
import it.spaghettisource.tigersupply.engine.entity.EntityGroupScreenBound;
import it.spaghettisource.tigersupply.engine.entity.Position;
import it.spaghettisource.tigersupply.game.entity.effect.Effect;
import it.spaghettisource.tigersupply.game.entity.effect.Smoke;
import it.spaghettisource.tigersupply.game.utils.EntityFactoryWrapper;


public class PlayerEngine extends Effect {

	private EntityGroupScreenBound<Entity> effectManager;
	private boolean thrustActive;
	private float smokeCounter = 0;
	
	public PlayerEngine(){
		spriteTimeDuration = -1;
	}

	public void setEffectManager(EntityGroupScreenBound<Entity> effectManager) {
		this.effectManager = effectManager;
	}

	public void setThrustActive(boolean thrustActive) {
		this.thrustActive = thrustActive;
	}

	public void updateEntity(float deltaSeconds) throws Exception {
		super.updateEntity(deltaSeconds);

		smokeCounter += deltaSeconds;
		if(smokeCounter > deltaSeconds * 3 && thrustActive){
			smokeCounter = 0;
			Position smokePosition = new Position(position);
			smokePosition.increaseX(-size.getHalfWidth() - 6);
			Smoke smokeEffect = EntityFactoryWrapper.newSmoke(smokePosition);
			effectManager.addRequest(smokeEffect);
		}
	}
	
	public boolean isOutOfScreen(int windowWidth, int windowHeight){
		return false;
	}

}
