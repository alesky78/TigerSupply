package it.spaghettisource.tigersupply.engine.image.effect;

import it.spaghettisource.tigersupply.engine.entity.Position;
import it.spaghettisource.tigersupply.engine.entity.Size;
import it.spaghettisource.tigersupply.engine.sprite.SpriteColor;
import it.spaghettisource.tigersupply.engine.utils.StaticResources;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class EffectManager {

	private static EffectManager instance;

	Map<String,Filter> registeredFilter; 
	
	private EffectManager(){
		registeredFilter = new HashMap<String, Filter>();
		
		//register hire the filter to use
		registeredFilter.put(StaticResources.FILTER_ROTATION, new Rotation());
		registeredFilter.put(StaticResources.FILTER_SCALE, new Scale());	
		registeredFilter.put(StaticResources.FILTER_BRIGHTEN, new Brighten());	
		registeredFilter.put(StaticResources.FILTER_TRANSPARENT, new Transparent());		
	}
	
	public static EffectManager getInstance(){
		if(instance==null){
			synchronized (EffectManager.class) {
				if(instance==null){
					instance = new EffectManager();
				}
			}
		}
		return instance;
	}	

	public List<String> getAllRegisteredFilter(){
		List<String> list = new ArrayList<String>(); 
		list.addAll(registeredFilter.keySet());
		return list;
	}
	
	public BufferedImage chain(List<String> filters,BufferedImage source,Position position,Size size,SpriteColor color ) throws Exception{
		if(filters.size() == 0)
			return source;
	
		BufferedImage newImage = source;
		for (String name : filters) {
			Filter filter = registeredFilter.get(name);
			if(filter == null){
				Exception ex = new Exception("the filter with name "+name+" is not registered");
				throw ex;
			}
			
			newImage = filter.filterImage(newImage, position, size, color);
		}
					
		return newImage;
		
	}
	

}
