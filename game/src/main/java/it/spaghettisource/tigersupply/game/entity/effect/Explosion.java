package it.spaghettisource.tigersupply.game.entity.effect;

import java.awt.Graphics2D;

import it.spaghettisource.tigersupply.engine.control.GameContext;
import it.spaghettisource.tigersupply.engine.entity.Entity;
import it.spaghettisource.tigersupply.engine.entity.EntityGroupScreenBound;
import it.spaghettisource.tigersupply.engine.entity.Position;
import it.spaghettisource.tigersupply.engine.entity.Size;
import it.spaghettisource.tigersupply.engine.entity.Speed;
import it.spaghettisource.tigersupply.engine.entity.logic.UpdateAlgorithmFactoryWrapper;
import it.spaghettisource.tigersupply.game.entity.BaseEntity;
import it.spaghettisource.tigersupply.game.utils.GameResources;

/**
 * Explosion emitter: an invisible effect entity that spawns {@link ParticleBurst}s into the
 * effect group from an {@link ExplosionProfile}.
 *
 * <p>With a non-positive {@code interval} it is a one-shot burst (emit once, then remove itself).
 * With a positive {@code interval} it is a timed emitter that emits repeatedly. When an {@code owner}
 * is attached the emitter tracks the owner's position and jitters each emission within the owner's
 * body; its lifetime is then bound to the owner (it removes itself once the owner is removed).</p>
 *
 * @author Alessandro D'Ottavio
 */
public class Explosion extends BaseEntity {

	private final ExplosionProfile profile;
	private final EntityGroupScreenBound<Entity> effectManager;
	private final int perEmit;

	private final float interval;	//<=0 means one-shot
	private final float duration;	//>0 caps the lifetime; <=0 relies on owner or one-shot
	private final Entity owner;		//nullable; when set, emission jitters within its size

	private float elapsed = 0f;
	private float emitCounter = 0f;

	public Explosion(ExplosionProfile profile, int perEmit, float interval, float duration,
			Entity owner, EntityGroupScreenBound<Entity> effectManager, GameContext context) {
		this.profile = profile;
		this.perEmit = perEmit;
		this.interval = interval;
		this.duration = duration;
		this.owner = owner;
		this.effectManager = effectManager;
		this.context = context;
		this.speed = new Speed(0, 0);
		this.size = new Size(1, 1);
		if (position == null) {
			this.position = new Position(0, 0, GameResources.Z_EXPLOSION);
		}
		try {
			this.updateAlgorithm = UpdateAlgorithmFactoryWrapper.newDefault();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void updateEntity(float deltaSeconds) throws Exception {
		super.updateEntity(deltaSeconds);

		if (interval <= 0) {			//one-shot
			emit();
			remove = true;
			return;
		}

		elapsed += deltaSeconds;
		emitCounter += deltaSeconds;
		while (emitCounter >= interval) {
			emitCounter -= interval;
			emit();
		}

		boolean durationDone = duration > 0 && elapsed >= duration;
		boolean ownerGone = owner != null && owner.canBeRemoved();
		if (durationDone || ownerGone) {
			remove = true;
		}
	}

	private void emit() {
		int originX = getXposition();
		int originY = getYposition();
		if (owner != null) {
			int jitterX = owner.getsize().getHalfWidth();
			int jitterY = owner.getsize().getHalfHeight();
			originX += (int) (Math.random() * jitterX);
			originY += (int) (Math.random() * jitterY);
		}
		for (int i = 0; i < perEmit; i++) {
			effectManager.addRequest(new ParticleBurst(
					profile.getScheme(), originX, originY,
					profile.getMaxSize(), profile.getMaxSpeed(), profile.getMaxLifeTime(), context));
		}
	}

	/** The emitter is inert: it never collides, it only spawns particles. */
	public boolean collidedWith(Entity other) {
		return false;
	}

	public void renderEntity(Graphics2D dbg) throws Exception {
		//invisible: the emitter only spawns particles
	}

}
