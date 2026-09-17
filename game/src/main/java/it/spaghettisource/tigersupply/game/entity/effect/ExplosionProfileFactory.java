package it.spaghettisource.tigersupply.game.entity.effect;

/**
 * Named explosion presets, centralising the burst tuning that used to live as scattered
 * {@code particle*} fields on the game entities.
 *
 * @author Alessandro D'Ottavio
 */
public class ExplosionProfileFactory {

	private ExplosionProfileFactory() {}

	// --- standard enemies (EnemyStandard, EnemyShield) ---

	public static ExplosionProfile standardHit() {
		return new ExplosionProfile(ParticleColorScheme.FIRE, 50, 35, 60, 0.3f);
	}

	public static ExplosionProfile standardDeath() {
		return new ExplosionProfile(ParticleColorScheme.FIRE, 50, 35, 60, 0.5f);
	}

	// --- asteroid ---

	public static ExplosionProfile asteroidHit() {
		return new ExplosionProfile(ParticleColorScheme.FIRE, 120, 40, 90, 0.3f);
	}

	public static ExplosionProfile asteroidDeath() {
		return new ExplosionProfile(ParticleColorScheme.FIRE, 120, 60, 100, 0.5f);
	}

	// --- shooter enemies (EnemyFireBallShooter, EnemyShoterRocket) ---

	public static ExplosionProfile shooterHit() {
		return new ExplosionProfile(ParticleColorScheme.FIRE, 100, 40, 130, 0.3f);
	}

	public static ExplosionProfile shooterDeath() {
		return new ExplosionProfile(ParticleColorScheme.FIRE, 100, 80, 130, 0.4f);
	}

	// --- boss ---

	public static ExplosionProfile bossHit() {
		return new ExplosionProfile(ParticleColorScheme.FIRE, 200, 40, 180, 0.3f);
	}

	public static ExplosionProfile bossDeath() {
		return new ExplosionProfile(ParticleColorScheme.FIRE, 200, 90, 150, 0.9f);
	}

	/** Per-burst configuration of the boss agony emitter (emitted repeatedly while the boss dies). */
	public static ExplosionProfile bossAgony() {
		return new ExplosionProfile(ParticleColorScheme.FIRE, 200, 60, 150, 0.5f);
	}

	// --- energetic shield hit ---

	public static ExplosionProfile shieldEnergeticHit() {
		return new ExplosionProfile(ParticleColorScheme.ENERGETIC, 20, 20, 30, 0.3f);
	}

	// --- player ---

	public static ExplosionProfile playerDeath() {
		return new ExplosionProfile(ParticleColorScheme.FIRE, 100, 40, 100, 0.5f);
	}

}
