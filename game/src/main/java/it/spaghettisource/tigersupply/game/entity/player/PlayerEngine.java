package it.spaghettisource.tigersupply.game.entity.player;

import java.util.Random;

import it.spaghettisource.tigersupply.engine.entity.Entity;
import it.spaghettisource.tigersupply.engine.entity.EntityGroupScreenBound;
import it.spaghettisource.tigersupply.game.entity.effect.Effect;
import it.spaghettisource.tigersupply.game.entity.effect.ParticleColorScheme;
import it.spaghettisource.tigersupply.game.entity.effect.ParticleTrail;


public class PlayerEngine extends Effect {

	private static final float TRAIL_INTERVAL = 0.02f;
	private static final int TRAIL_PARTICLES = 2;
	private static final float TRAIL_LIFETIME = 0.2f;
	private static final float TRAIL_DRIFT_SPEED = 120f;

	private static final Random random = new Random();

	private EntityGroupScreenBound<Entity> effectManager;
	private boolean thrustActive;
	private float trailCounter = 0;
	
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

		trailCounter += deltaSeconds;
		if(trailCounter >= TRAIL_INTERVAL && thrustActive){
			trailCounter = 0;
			int emitX = getXposition() - size.getHalfWidth() - 6;
			int emitY = getYposition();
			for(int i = 0; i < TRAIL_PARTICLES; i++){
				int jitterY = random.nextInt(7) - 3;
				float driftX = -TRAIL_DRIFT_SPEED + (random.nextInt(60) - 30);
				float driftY = random.nextInt(80) - 40;
				int pixelSize = 8 + random.nextInt(6);
				ParticleTrail trail = new ParticleTrail(ParticleColorScheme.EMBER,
						emitX, emitY + jitterY, pixelSize, driftX, driftY, TRAIL_LIFETIME, context);
				effectManager.addRequest(trail);
			}
		}
	}
	
	public boolean isOutOfScreen(int windowWidth, int windowHeight){
		return false;
	}

}
