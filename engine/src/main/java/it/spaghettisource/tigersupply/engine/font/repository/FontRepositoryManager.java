package it.spaghettisource.tigersupply.engine.font.repository;

import java.awt.Font;

/**
 * Repository manger instance singleton
 * 
 * @author Alessandro D'Ottavio
 *
 */
public class FontRepositoryManager {

	private static FontRepositoryManager instance;	
	private FontRepository repository;

	private FontRepositoryManager() throws Exception{
		repository = new FontRepository();
		RepositoryLoader loader = new RepositoryLoader();
		loader.init(repository);
	}

	public static void init() throws Exception{
		if(instance==null){
			synchronized (FontRepositoryManager.class) {
				if(instance==null){
					instance = new FontRepositoryManager();
				}
			}
		}
	}

	public static FontRepositoryManager getInstance() throws Exception{
		if(instance==null){
			Exception ex = new Exception("RepositoryManager class must by initialized before to use it");
			throw ex;
		}
		return instance;
	}	 


	public Font getFont(String alias,int size){
		return repository.getFont(alias).deriveFont(Font.PLAIN, size);
	}
	
	public int getNumberOFManageImages(){
		return repository.getMangedFontByAlias();
	}
	
	
	
}
