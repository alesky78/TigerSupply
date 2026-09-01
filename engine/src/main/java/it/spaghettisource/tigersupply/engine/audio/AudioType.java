package it.spaghettisource.tigersupply.engine.audio;

/**
 * The kind of audio the engine can play.
 *
 * <p>The kind drives real playback behavior in {@code AudioManager}: {@link #MUSIC} tracks are
 * exclusive per alias (at most one playback of a given track at a time), while {@link #FX} sounds
 * are polyphonic (the same effect may overlap itself).
 *
 * @author Alessandro D'Ottavio
 */
public enum AudioType {

	/** Background music: exclusive per track (alias). */
	MUSIC,

	/** Short sound effect: polyphonic, fire-and-forget. */
	FX;

}
