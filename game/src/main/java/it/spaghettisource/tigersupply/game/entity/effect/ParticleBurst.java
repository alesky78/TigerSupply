package it.spaghettisource.tigersupply.game.entity.effect;

import it.spaghettisource.tigersupply.game.entity.BaseEntity;

import java.awt.Color;
import java.awt.Graphics2D;

import it.spaghettisource.tigersupply.engine.control.GameContext;
import it.spaghettisource.tigersupply.engine.entity.Position;
import it.spaghettisource.tigersupply.engine.entity.Speed;
import it.spaghettisource.tigersupply.engine.entity.logic.UpdateAlgorithmFactoryWrapper;
import it.spaghettisource.tigersupply.game.utils.GameResources;

/**
 * A single fragment of an explosion burst that flies outward and fades away.
 *
 * <p>Rendered as a small filled oval, the particle is a visual-only effect: it lives in the effect
 * layer and never takes part in collision detection. Its purpose is to be spawned in groups to form
 * a radial spray of debris from an explosion.</p>
 *
 * <p>At construction each particle is given randomized parameters so that a group produces a natural,
 * irregular burst: a random lifetime up to the requested maximum, a random size up to the requested
 * maximum, and a velocity with a uniformly random direction over the full circle and a random
 * magnitude up to the requested maximum speed. This makes the fragments scatter outward in every
 * direction at varying speeds.</p>
 *
 * <p>Over its lifetime the particle moves in a straight line following its initial velocity while
 * {@link #colorAlteration} decreases proportionally to the elapsed time, so both its colour and its
 * drawn diameter shrink toward zero. When the lifetime elapses it flags itself for removal so the
 * game loop can discard it.</p>
 *
 * @author Alessandro D'Ottavio
 */
public class ParticleBurst extends BaseEntity {

	private static final double PI2 = 2*Math.PI;

	private float colorAlteration;
	private int   size;			
	private ParticleColorScheme scheme;

	protected float lifeTime;
	private float lifeCounter; 		

		
	public ParticleBurst(ParticleColorScheme scheme,int posX, int posY,int maxSize,int maxSpeed,float maxLifeTimeInSeconds,GameContext context){
		this.scheme = scheme;

		lifeTime = Math.max(0.01f, (float) (Math.random()*maxLifeTimeInSeconds));
		lifeCounter = 0;

		colorAlteration = 1f;
		size = Math.max(1, (int) (Math.random()*maxSize));

		//uniform random direction on the full circle at a speed within maxSpeed
		double angle = PI2*Math.random();
		double r = Math.random()*maxSpeed;
		double speedX = Math.cos(angle)*r;
		double speedY = Math.sin(angle)*r;

		speed = new Speed((int)speedX,(int)speedY);

		position = new Position(posX, posY,GameResources.Z_EXPLOSION);
		try {
			updateAlgorithm = UpdateAlgorithmFactoryWrapper.newDefault();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Advances the particle along its trajectory and fades it out over time.
	 *
	 * <p>The base entity update moves the particle according to its velocity, then the lifetime timer
	 * is increased by {@code deltaSeconds}. Once the elapsed time reaches {@link #lifeTime} the
	 * particle is flagged for removal. The fade factor {@link #colorAlteration} is recomputed from the
	 * elapsed fraction of the lifetime, making the fade framerate independent, and is clamped to zero.</p>
	 *
	 * @param deltaSeconds elapsed simulation time for this frame
	 * @throws Exception propagated from the parent entity update logic
	 */
	public void updateEntity(float deltaSeconds)  throws Exception  {
		super.updateEntity(deltaSeconds);

		lifeCounter+=deltaSeconds;
		if(lifeCounter>=lifeTime){
			remove = true;
		}

		//fade based on elapsed lifetime, framerate independent
		colorAlteration = 1 - (lifeCounter / lifeTime);
		if(colorAlteration<0){
			colorAlteration = 0f;
		}

	}			


	/**
	 * Draws the particle as a filled oval whose colour and diameter shrink as it fades.
	 *
	 * <p>The current drawing colour is saved and restored so the effect does not leak its state into
	 * later rendering. The oval is centred on the particle position and its drawn size is scaled by
	 * {@link #colorAlteration}, so the fragment visually contracts toward nothing as it dies.</p>
	 *
	 * @param dbg graphics context used to render the particle
	 * @throws Exception propagated from the underlying rendering contract
	 */
	public void renderEntity(Graphics2D dbg) throws Exception {
		Color originalColor = dbg.getColor();
		dbg.setColor(scheme.colorAt(colorAlteration));
		
		//dbg.fill3DRect(getXposition()-size/2,getYposition()-size/2,(int)(size*colorAlteration),(int)(size*colorAlteration),false);
		
		dbg.fillOval(getXposition()-size/2,getYposition()-size/2,(int)(size*colorAlteration),(int)(size*colorAlteration));
		
		dbg.setColor(originalColor);

	}	



}
