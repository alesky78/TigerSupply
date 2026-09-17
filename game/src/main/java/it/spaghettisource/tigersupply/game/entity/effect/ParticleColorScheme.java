package it.spaghettisource.tigersupply.game.entity.effect;

import java.awt.Color;

/**
 * Named color palette for a procedural fade particle.
 *
 * <p>Each constant maps a fade-progress value {@code t} in {@code [0,1]} (full intensity to fully
 * faded) to the render {@link Color} used at that moment. A constant also decides how the fade is
 * expressed across the color channels: {@link #FIRE} and {@link #ENERGETIC} stay fully opaque (their
 * fade is carried by the particle shrinking), while {@link #ENERGY_TRAIL} fades its alpha to zero.
 * Adding a new particle appearance is a matter of adding a new constant.</p>
 */
public enum ParticleColorScheme {

	/** Red to yellow, fully opaque. */
	FIRE {
		public Color colorAt(float t) {
			return new Color(1, t, 0, 1);
		}
	},

	/** White to blue, fully opaque. */
	ENERGETIC {
		public Color colorAt(float t) {
			return new Color(t, t, 1, 1);
		}
	},

	/** Cyan whose alpha fades to zero. */
	ENERGY_TRAIL {
		public Color colorAt(float t) {
			return new Color(0.4f, 0.9f, 1f, t);
		}
	},

	/** Fire orange whose alpha fades to zero. */
	FIRE_TRAIL {
		public Color colorAt(float t) {
			return new Color(1f, 0.5f, 0.1f, t);
		}
	},

	/**
	 * Glowing ember cooling into ash: hot red at full intensity, decaying toward grey while its
	 * alpha fades to zero. Suited to a smoky exhaust trail that starts hot and cools as it dissipates.
	 */
	EMBER {
		public Color colorAt(float t) {
			float r = 0.5f + 0.5f * t;   //1.0 (hot red) -> 0.5 (grey)
			float g = 0.5f - 0.2f * t;   //0.3 -> 0.5
			float b = 0.5f - 0.5f * t;   //0.0 -> 0.5
			return new Color(r, g, b, t);
		}
	};

	/**
	 * Returns the render color for the given fade progress.
	 *
	 * @param t the fade progress in {@code [0,1]}, from full intensity to fully faded
	 * @return the color for that progress, with all four RGBA channels specified
	 */
	public abstract Color colorAt(float t);

}
