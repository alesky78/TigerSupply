package it.spaghettisource.tigersupply.engine.image.repository;

import java.awt.image.BufferedImage;

/**
 * Repository manger instance singleton
 * 
 * @author Alessandro D'Ottavio
 *
 */
public class ImageRepositoryManager {

	private static ImageRepositoryManager instance;	
	private ImageRepository repository;


	private ImageRepositoryManager() throws Exception{
		repository = new ImageRepository();
		RepositoryLoader loader = new RepositoryLoader();
		loader.init(repository);
	}

	public static void init() throws Exception{
		if(instance==null){
			synchronized (ImageRepositoryManager.class) {
				if(instance==null){
					instance = new ImageRepositoryManager();
				}
			}
		}
	}

	public static ImageRepositoryManager getInstance() throws Exception{
		if(instance==null){
			Exception ex = new Exception("RepositoryManager class must by initialized before to use it");
			throw ex;
		}
		return instance;
	}	 


	public BufferedImage getSingleImage(String alias){
		return repository.getSingleImage(alias);
	}

	public void addSingleImage(String alias,BufferedImage img) throws Exception{
		repository.addImage(alias, img);
	}

	public BufferedImage[] getLoopImage(String alias){
		return repository.getLoopImage(alias);
	}	
	
	public void addLoopImage(String alias,BufferedImage[] img) throws Exception{
		repository.addLoopImage(alias, img);
	}	

	public BufferedImage getVolatileImage(String alias){
		return repository.getVolatileImage(alias);
	}

	public void addVolatileImage(String alias,BufferedImage img) throws Exception{
		repository.addVolatileImage(alias, img);
	}	
	
	public void cleanVolatileImages(){
		repository.resetVolatileImageCache();
	}
	
	public int getNumberOFManageImages(){
		return repository.getMangedImagesByAlias();
	}
	
	
	
}
