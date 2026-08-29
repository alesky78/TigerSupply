package it.spaghettisource.tigersupply.engine.audio.repository;

import java.util.ArrayList;
import java.util.List;

public class AudioRepository {

	private List<String> audioBufferAlias = new ArrayList<String>();
	private List<byte[]> audioBufferContainer = new ArrayList<byte[]>();	
	
	
	public byte[] getAudioBuffer(String alias){
		int position = audioBufferAlias.indexOf(alias);
		if(position == -1)
			return null;
		return audioBufferContainer.get(position);
	}

	public void storeAudioBuffer(String alias,byte[] buffer) throws Exception{
		if(audioBufferAlias.contains(alias)){
			throw new Exception("audio buffer "+alias+" already loaded in the repository use different alias");
		}
		audioBufferAlias.add(alias);
		audioBufferContainer.add(buffer);
	}	
	
	

}
