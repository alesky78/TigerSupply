package it.spaghettisource.tigersupply.engine.audio;

/**
 * 
 * 
 * @author Alessandro D'Ottavio
 *
 */
public class AudioPlayerThread implements Runnable {

	private byte[] buffer;
	private  int audioType;
	private boolean loop;
	private AudioPlayer player;		
		

	public AudioPlayerThread(byte[] buffer, int audioType,boolean loop){
		this.buffer = buffer;
		this.audioType = audioType;
		this.loop = loop;
		player = new AudioPlayer();
	}
	
	/**
	 * 
	 * @return FX or MUSIC
	 */
	public int getAudioType(){
		return audioType;
	}	

	
	public void stopPlayer(){
		player.forceStop();
	}
	
	
	public void run() {
		try {
			player.play(buffer,loop);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}



}
