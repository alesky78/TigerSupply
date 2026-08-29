package it.spaghettisource.tigersupply.engine.audio;

import it.spaghettisource.tigersupply.engine.audio.repository.AudioRepository;
import it.spaghettisource.tigersupply.engine.audio.repository.RepositoryLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Alessandro D'Ottavio
 *
 */
public class AudioManager {

	private static AudioManager instance;
	
	private List<AudioPlayerThread> threadFX;
	private List<AudioPlayerThread> threadMUSIC;	
	private AudioRepository repository;
	
	
	private AudioManager() throws Exception{
		repository = new AudioRepository();
		threadFX = new ArrayList<AudioPlayerThread>();
		threadMUSIC = new ArrayList<AudioPlayerThread>();
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
		
	
	public void playMusic(String alias,boolean loop){
		play(repository.getAudioBuffer(alias), loop, AudioType.MUSIC);
	}
	

	public void playFx(String alias,boolean loop){
		play(repository.getAudioBuffer(alias), loop, AudioType.FX);
	}	
	
	public void stopAllAudio(){
		for (AudioPlayerThread thread : threadFX) {
			thread.stopPlayer();
		}
		for (AudioPlayerThread thread : threadMUSIC) {
			thread.stopPlayer();
		}		
	}
	
	private void play(byte[] buffer,boolean loop,int audioType){
		
		AudioPlayerThread thread = new AudioPlayerThread(buffer, audioType, loop);

		if(audioType == AudioType.FX){
			threadFX.add(thread);
		}else if(audioType ==  AudioType.MUSIC){
			threadMUSIC.add(thread);
		}
		
		new Thread(thread).start();
		
	}
	
	
	

}
