package it.spaghettisource.tigersupply.engine.audio.repository;

import it.spaghettisource.tigersupply.engine.utils.StreamUtils;

import java.io.InputStream;


/**
 * 
 * utility to load audio file an convert in byte array
 * 
 * @author Alessandro D'Ottavio
 *
 */
public class FileAudioLoader {
	
	
	public byte[] loadAudioFile(String urlResource){

		byte[] buffer = null;
		
		try {
			InputStream stream = this.getClass().getClassLoader().getResourceAsStream(urlResource);			
			buffer = StreamUtils.getBytesFromInputStream(stream);
			stream.close();
		} catch (Exception e) {
			System.out.println("error loading resource:"+urlResource);
			e.printStackTrace();
			System.exit(1);
		}		
		
		return buffer;
		
	}

}
