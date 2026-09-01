package it.spaghettisource.tigersupply.engine.audio;

import java.util.function.Consumer;

/**
 * 
 * 
 * @author Alessandro D'Ottavio
 *
 */
public class AudioPlayerThread implements Runnable {

	private byte[] buffer;
	private AudioType audioType;
	private boolean loop;
	private float volume;
	private Consumer<AudioPlayerThread> onFinished;
	private AudioPlayer player;		
		

	public AudioPlayerThread(byte[] buffer, AudioType audioType, boolean loop, float volume, Consumer<AudioPlayerThread> onFinished){
		this.buffer = buffer;
		this.audioType = audioType;
		this.loop = loop;
		this.volume = volume;
		this.onFinished = onFinished;
		player = new AudioPlayer();
	}
	
	/**
	 * 
	 * @return FX or MUSIC
	 */
	public AudioType getAudioType(){
		return audioType;
	}	

	
	public void stopPlayer(){
		player.forceStop();
	}
	
	
	public void run() {
		try {
			player.play(buffer,loop,volume);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(onFinished != null){
				onFinished.accept(this);
			}
		}
		
	}



}
