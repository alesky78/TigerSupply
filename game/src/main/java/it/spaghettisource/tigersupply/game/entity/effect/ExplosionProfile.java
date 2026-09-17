package it.spaghettisource.tigersupply.game.entity.effect;

/**
 * Immutable configuration of a single explosion burst: the colour scheme and the per-particle
 * maxima its {@link ParticleBurst}s are spawned with.
 *
 * @author Alessandro D'Ottavio
 */
public class ExplosionProfile {

	private final ParticleColorScheme scheme;
	private final int particleNum;
	private final int maxSize;
	private final int maxSpeed;
	private final float maxLifeTime;

	public ExplosionProfile(ParticleColorScheme scheme, int particleNum, int maxSize, int maxSpeed, float maxLifeTime) {
		this.scheme = scheme;
		this.particleNum = particleNum;
		this.maxSize = maxSize;
		this.maxSpeed = maxSpeed;
		this.maxLifeTime = maxLifeTime;
	}

	public ParticleColorScheme getScheme() {
		return scheme;
	}

	public int getParticleNum() {
		return particleNum;
	}

	public int getMaxSize() {
		return maxSize;
	}

	public int getMaxSpeed() {
		return maxSpeed;
	}

	public float getMaxLifeTime() {
		return maxLifeTime;
	}

}
