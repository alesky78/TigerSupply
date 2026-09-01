package it.spaghettisource.tigersupply.engine.audio;

import it.spaghettisource.tigersupply.engine.audio.repository.AudioRepository;
import it.spaghettisource.tigersupply.engine.audio.repository.RepositoryLoader;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author Alessandro D'Ottavio
 *
 */
public class AudioManager {

	private static AudioManager instance;

	/** music is exclusive per track: at most one playback per alias */
	private Map<String, AudioPlayerThread> musicTracks;
	/** effects are polyphonic: the same alias may overlap itself */
	private List<AudioPlayerThread> fxThreads;

	private EnumMap<AudioType, Float> volume;
	private EnumMap<AudioType, Boolean> muted;

	private AudioRepository repository;


	private AudioManager() throws Exception{
		repository = new AudioRepository();
		musicTracks = new HashMap<String, AudioPlayerThread>();
		fxThreads = new ArrayList<AudioPlayerThread>();

		volume = new EnumMap<AudioType, Float>(AudioType.class);
		volume.put(AudioType.MUSIC, 1.0f);
		volume.put(AudioType.FX, 1.0f);

		muted = new EnumMap<AudioType, Boolean>(AudioType.class);
		muted.put(AudioType.MUSIC, Boolean.FALSE);
		muted.put(AudioType.FX, Boolean.FALSE);

		RepositoryLoader loader = new RepositoryLoader();
		loader.init(repository);
	}


	public static void init() throws Exception{
		if(instance==null){
			synchronized (AudioManager.class) {
				if(instance==null){
					instance = new AudioManager();
				}
			}
		}
	}

	public static AudioManager getInstance() throws Exception{
		if(instance==null){
			Exception ex = new Exception("AudioManager class must by initialized before to use it");
			throw ex;
		}
		return instance;
	}


	/**
	 * Play a music track. Music is exclusive per track: if the same {@code alias} is already
	 * playing this call is a no-op, so a track is never layered on itself. Different aliases can
	 * play concurrently.
	 *
	 * @param alias the catalog alias of the music track
	 * @param loop  whether the track loops until stopped
	 */
	public void playMusic(String alias,boolean loop){
		byte[] buffer = repository.getAudioBuffer(alias);
		synchronized (musicTracks) {
			if(musicTracks.containsKey(alias)){
				return;	//already playing: never layer a track on itself
			}
			float effectiveVolume = effectiveVolume(AudioType.MUSIC);
			AudioPlayerThread thread = new AudioPlayerThread(buffer, AudioType.MUSIC, loop, effectiveVolume,
					finished -> { synchronized (musicTracks) { musicTracks.remove(alias, finished); } });
			musicTracks.put(alias, thread);
			new Thread(thread).start();
		}
	}


	/**
	 * Play a sound effect. Effects are polyphonic: the same {@code alias} may overlap itself.
	 *
	 * @param alias the catalog alias of the effect
	 * @param loop  whether the effect loops until stopped
	 */
	public void playFx(String alias,boolean loop){
		byte[] buffer = repository.getAudioBuffer(alias);
		float effectiveVolume = effectiveVolume(AudioType.FX);
		synchronized (fxThreads) {
			AudioPlayerThread thread = new AudioPlayerThread(buffer, AudioType.FX, loop, effectiveVolume,
					finished -> { synchronized (fxThreads) { fxThreads.remove(finished); } });
			fxThreads.add(thread);
			new Thread(thread).start();
		}
	}


	/**
	 * Set the playback volume for an audio kind. The value is clamped to {@code [0,1]} and applies
	 * to sounds of that kind started after this call.
	 *
	 * @param type  the audio kind
	 * @param value linear volume; clamped to {@code [0,1]}
	 */
	public void setVolume(AudioType type, float value){
		float clamped = Math.max(0.0f, Math.min(1.0f, value));
		volume.put(type, clamped);
	}

	/**
	 * Mute or un-mute an audio kind. Applies to sounds of that kind started after this call.
	 *
	 * @param type  the audio kind
	 * @param value {@code true} to mute, {@code false} to un-mute
	 */
	public void setMuted(AudioType type, boolean value){
		muted.put(type, value);
	}


	/**
	 * Stop a single music track by its alias. Other music tracks and all effects are unaffected.
	 *
	 * @param alias the catalog alias of the music track to stop
	 */
	public void stopMusic(String alias){
		synchronized (musicTracks) {
			AudioPlayerThread thread = musicTracks.get(alias);
			if(thread != null){
				thread.stopPlayer();
			}
		}
	}

	/** Stop every music track. Effects are unaffected. */
	public void stopMusic(){
		synchronized (musicTracks) {
			for (AudioPlayerThread thread : musicTracks.values()) {
				thread.stopPlayer();
			}
		}
	}

	/** Stop every sound effect. Music is unaffected. */
	public void stopFx(){
		synchronized (fxThreads) {
			for (AudioPlayerThread thread : fxThreads) {
				thread.stopPlayer();
			}
		}
	}

	public void stopAllAudio(){
		stopMusic();
		stopFx();
	}


	private float effectiveVolume(AudioType type){
		if(Boolean.TRUE.equals(muted.get(type))){
			return 0.0f;
		}
		return volume.get(type);
	}

}
