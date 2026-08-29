package it.spaghettisource.tigersupply.engine.audio.repository;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Scanner;

/**
 * Implementation of a repository loader from text file
 * 
 * for single image: s <image alias> <imagePath/imageName> 
 * loop images:		 l <image alias> <imagePath/imageName> <image number>
 * 
 * @author Alessando D'Ottavio
 *
 */
public class RepositoryLoader {

	private final String CATAOLOG_FILE = "audio/audio-catalog.txt";

	private FileAudioLoader audioFileLoader;	
	private String alias;
	private String resource;


	public RepositoryLoader(){
		audioFileLoader = new FileAudioLoader();
	}

	public void init(AudioRepository repository) throws Exception{

		System.out.println("create the audio repository from: "+CATAOLOG_FILE);

		InputStream is =  this.getClass().getClassLoader().getResourceAsStream(CATAOLOG_FILE);
		BufferedReader bfr = new BufferedReader(new InputStreamReader(is));

		String line=null;
		Scanner scanner = null;
		while((line = bfr.readLine()) != null ){
			if (!line.startsWith("//") && !line.isEmpty()){ //if line is not a comment or empty
				scanner = new Scanner(line);
				loadAudio(scanner, repository);
			}

		}
	}

	private void loadAudio(Scanner scanner, AudioRepository repository) throws Exception {
		alias = scanner.next();
		resource = scanner.next();
		repository.storeAudioBuffer(alias, audioFileLoader.loadAudioFile(resource));     
		System.out.println("loaded audio:"+alias+" from file:"+resource);
	}

}
