package it.spaghettisource.tigersupply.engine.image.repository;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.util.StringTokenizer;

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
	
	private final String CATAOLOG_FILE = "image/image-catalog.txt";
	
	private ImageLoader imageLoader;	
	private String alias;
	private String resource;
	private BufferedImage image;
	
	private BufferedImage[] images;
	private String prefix;
	private String extension;		
	private String imageNumber;
	
	
	public RepositoryLoader(){
		imageLoader = new ImageLoader();
	}
	
	public void init(ImageRepository repository) throws Exception{
		
		System.out.println("create the images repository from: "+CATAOLOG_FILE);
		
		InputStream is =  this.getClass().getClassLoader().getResourceAsStream(CATAOLOG_FILE);
		BufferedReader bfr = new BufferedReader(new InputStreamReader(is));
		
		String line=null;
		Scanner scanner = null;
		String  chunk = null;
		while((line = bfr.readLine()) != null ){
			 if (!line.startsWith("//") && !line.isEmpty()){ //if line is not a comment or empty
				 scanner = new Scanner(line);
				 chunk = scanner.next();
				 if(chunk.equals("s")){
					 loadSingleImage(scanner, repository);
				 }else if(chunk.equals("l")){
					 loadLoopImage(scanner,repository); 
				 }
				 
			 }
		}
		
	}
	
	
	/**
	 * s <image alias> <imagePath/imageName> 
	 * @throws Exception 
	 */
	private void loadSingleImage(Scanner scanner,ImageRepository repository) throws Exception{
		alias = scanner.next();
		resource = scanner.next();
		image = imageLoader.loadImage(resource);
		repository.storeSingleImage(alias, image);
		System.out.println("loaded single:"+alias+" from:"+resource);
	}
	
	/**
	 * l <image alias> <imagePath/imageName> <image number>
	 * @throws Exception 
	 */
	private void loadLoopImage(Scanner scanner,ImageRepository repository) throws Exception{
		alias = scanner.next();
		resource = scanner.next();
		imageNumber = scanner.next();
		int num = Integer.parseInt(imageNumber);
		images = new BufferedImage[num];
		
		StringTokenizer buffer = new StringTokenizer(resource, "*");
		prefix = buffer.nextToken();
		extension = buffer.nextToken();
		
		for (int i = 0; i < num; i++) {
			images[i] = imageLoader.loadImage(prefix+i+extension);
		}
		repository.storeLoopImage(alias, images);
		
		System.out.println("loaded loop:"+alias+" images:"+num+" from:"+resource);
	}
	
	
//	public static void main(String[] args) throws Exception{
//		ImageRepository repository = new ImageRepository();
//		RepositoryLoader loader = new RepositoryLoader();
//		loader.init(repository);
//	}
	
}
