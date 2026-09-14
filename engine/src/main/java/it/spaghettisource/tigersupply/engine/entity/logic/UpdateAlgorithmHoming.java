package it.spaghettisource.tigersupply.engine.entity.logic;

import static it.spaghettisource.tigersupply.engine.utils.StaticResources.ALGPRO_SEEK_TIME;
import static it.spaghettisource.tigersupply.engine.utils.StaticResources.ALGPRO_SPEED;
import static it.spaghettisource.tigersupply.engine.utils.StaticResources.ALGPRO_SPRITE;
import static it.spaghettisource.tigersupply.engine.utils.StaticResources.ALGPRO_TURN_RATE;

import it.spaghettisource.tigersupply.engine.entity.Entity;
import it.spaghettisource.tigersupply.engine.entity.Position;
import it.spaghettisource.tigersupply.engine.entity.Speed;
import it.spaghettisource.tigersupply.engine.utils.DynaProperties;

/**
 * {@link UpdateAlgorithm} that seeks a live target {@link Entity} like a homing missile: it travels at
 * a constant speed and, on every frame, rotates its heading toward the target's current position by at
 * most a maximum turn rate, so it converges on the target instead of oscillating around it like
 * {@link UpdateAlgoritmFollowSprite}.
 *
 * <p>The heading is seeded on the first frame from the entity's launch {@link Speed} (or aimed at the
 * target when the launch speed is zero); afterwards only the heading rotates while the speed magnitude
 * stays constant. It homes only for a bounded time; once that elapses it stops steering and flies
 * straight, so a dodged missile eventually leaves the screen instead of orbiting the target forever.
 * Both the turn ({@code maxTurnDegPerSec * deltaSeconds}) and the translation
 * ({@code speed * deltaSeconds}) are scaled by the elapsed time, so the motion is frame-rate
 * independent. Configuration keys (from {@code StaticResources}): {@code ALGPRO_SPRITE} (the target
 * {@link Entity}), {@code ALGPRO_SPEED} (travel speed in pixel/second), {@code ALGPRO_TURN_RATE}
 * (maximum turn rate in degree/second) and {@code ALGPRO_SEEK_TIME} (seconds to home before losing
 * the lock).</p>
 */
public class UpdateAlgorithmHoming extends AbstractUpdateAlgorithm {

	private Entity target;
	private float travelSpeed;
	private float maxTurnDegPerSec;
	private float seekSeconds;

	private float headingDeg;
	private float elapsedSeconds = 0;
	private boolean initialized = false;

	/**
	 * {@inheritDoc}
	 *
	 * <p>Seeds the heading from the launch speed on the first frame, then rotates it toward the
	 * target's current position by at most {@code maxTurnDegPerSec * deltaSeconds} and advances the
	 * position at the constant travel speed along the resulting heading. When the target is absent, or
	 * the seek time has elapsed, the heading is left unchanged, so the entity keeps flying straight.</p>
	 */
	@Override
	public void updateLogic(Position position, Speed speed, float deltaSeconds) {

		if(!initialized){
			if(speed.getSpeedX() != 0 || speed.getSpeedY() != 0){
				headingDeg = (float) Math.toDegrees(Math.atan2(speed.getSpeedY(), speed.getSpeedX()));
			}else if(target != null){
				headingDeg = (float) Math.toDegrees(Math.atan2(target.getYposition() - position.getPosY(), target.getXposition() - position.getPosX()));
			}
			initialized = true;
		}

		elapsedSeconds += deltaSeconds;

		//home only for a bounded time, then fly straight so a dodged missile eventually leaves the screen
		if(target != null && elapsedSeconds < seekSeconds){
			double desired = Math.toDegrees(Math.atan2(target.getYposition() - position.getPosY(), target.getXposition() - position.getPosX()));
			float diff = normalizeDegrees((float) desired - headingDeg);
			float maxStep = maxTurnDegPerSec * deltaSeconds;
			if(diff > maxStep){
				diff = maxStep;
			}else if(diff < -maxStep){
				diff = -maxStep;
			}
			headingDeg += diff;
		}

		double headingRad = Math.toRadians(headingDeg);
		position.increaseX((float) (travelSpeed * Math.cos(headingRad) * deltaSeconds));
		position.increaseY((float) (travelSpeed * Math.sin(headingRad) * deltaSeconds));
	}

	/**
	 * Normalises a signed angle difference to the range {@code [-180, 180)} degrees.
	 *
	 * @param angle the raw angle difference in degrees
	 * @return the equivalent difference in {@code [-180, 180)}
	 */
	private float normalizeDegrees(float angle){
		angle = angle % 360;
		if(angle < -180){
			angle += 360;
		}else if(angle >= 180){
			angle -= 360;
		}
		return angle;
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>Reads the {@code ALGPRO_SPRITE} target entity, the {@code ALGPRO_SPEED} travel speed, the
	 * {@code ALGPRO_TURN_RATE} maximum turn rate and the {@code ALGPRO_SEEK_TIME} seek duration.</p>
	 */
	public void init(DynaProperties properties) {
		target = (Entity) properties.getObject(ALGPRO_SPRITE);
		travelSpeed = getFloat(properties.getString(ALGPRO_SPEED));
		maxTurnDegPerSec = getFloat(properties.getString(ALGPRO_TURN_RATE));
		seekSeconds = getFloat(properties.getString(ALGPRO_SEEK_TIME));
	}

}
