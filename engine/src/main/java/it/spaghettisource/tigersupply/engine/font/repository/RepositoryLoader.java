package it.spaghettisource.tigersupply.engine.font.repository;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Scanner;

/**
 * Implementation of a repository loader for Font from text file
 * 
 * 
 * @author Alessando D'Ottavio
 *
 */
public class RepositoryLoader {

	private final String CATAOLOG_FILE = "font/font-catalog.txt";

	private FontLoader fontLoader;	
	private String alias;
	private String resource;

	public RepositoryLoader(){
		fontLoader = new FontLoader();
	}

	public void init(FontRepository repository) throws Exception{

		System.out.println("create the images repository from: "+CATAOLOG_FILE);

		InputStream is =  this.getClass().getClassLoader().getResourceAsStream(CATAOLOG_FILE);
		BufferedReader bfr = new BufferedReader(new InputStreamReader(is));

		String line=null;
		Scanner scanner = null;
		String  chunk = null;
		while((line = bfr.readLine()) != null ){
			if (!line.startsWith("//") && !line.isEmpty()){ //if line is not a comment or empty
				scanner = new Scanner(line);
				loadFont(scanner, repository);
			}

		}
	}



	/**
	 * s <image alias> <imagePath/imageName> 
	 * @throws Exception 
	 */
	private void loadFont(Scanner scanner,FontRepository repository) throws Exception{
		alias = scanner.next();
		resource = scanner.next();
		repository.addFont(alias, fontLoader.loadFont(resource));
		System.out.println("loaded font:"+alias+" from:"+resource);
	}




//	public static void main(String[] args) throws Exception{
//		FontRepository repository = new FontRepository();
//		RepositoryLoader loader = new RepositoryLoader();
//		loader.init(repository);
//	}

}
