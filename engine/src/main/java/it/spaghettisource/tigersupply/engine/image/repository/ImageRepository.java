package it.spaghettisource.tigersupply.engine.image.repository;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class ImageRepository {

	private List<String> singleImageAlias = new ArrayList<String>();
	private List<BufferedImage> singleImageConatiner = new ArrayList<BufferedImage>();	
	
	private List<String> loopImageAlias = new ArrayList<String>();
	private List<BufferedImage[]> loopImageConatiner = new ArrayList<BufferedImage[]>();	

	private List<String> vaolatileImageAlias = new ArrayList<String>();
	private List<BufferedImage> vaolatileImageConatiner = new ArrayList<BufferedImage>();	
	
	
	public void storeSingleImage(String name, BufferedImage img) throws Exception{
		addImage(name,img);
	}
	
	public void storeLoopImage(String name, BufferedImage[] img) throws Exception{
		addLoopImage(name,img);		
	}
	
	public int getMangedImagesByAlias(){
		return singleImageAlias.size() + loopImageAlias.size() + vaolatileImageAlias.size();
	}
	
	
	public BufferedImage getSingleImage(String alias){
		int position = singleImageAlias.indexOf(alias);
		if(position == -1)
			return null;
		return singleImageConatiner.get(position);
	}

	public void addImage(String alias,BufferedImage img) throws Exception{
		if(singleImageAlias.contains(alias)){
			throw new Exception("image "+alias+" already loaded in the repository use different alias");
		}
		singleImageAlias.add(alias);
		singleImageConatiner.add(img);
	}	
	
	public BufferedImage[] getLoopImage(String alias){
		int position = loopImageAlias.indexOf(alias);
		if(position == -1)
			return null;		
		return loopImageConatiner.get(position);
	}
	
	public void addLoopImage(String alias,BufferedImage[] img) throws Exception{
		if(loopImageAlias.contains(alias)){
			throw new Exception("loop image "+alias+" already loaded in the repository use different alias");
		}		
		loopImageAlias.add(alias);
		loopImageConatiner.add(img);
	}		
	
	public BufferedImage getVolatileImage(String alias){
		int position = vaolatileImageAlias.indexOf(alias);
		if(position == -1)
			return null;
		return vaolatileImageConatiner.get(position);
	}

	public void addVolatileImage(String alias,BufferedImage img) throws Exception{
		if(vaolatileImageAlias.contains(alias)){
			throw new Exception("image "+alias+" already loaded in the repository use different alias");
		}
		vaolatileImageAlias.add(alias);
		vaolatileImageConatiner.add(img);
	}		
	
	public void resetVolatileImageCache(){
		vaolatileImageAlias.clear();
		vaolatileImageConatiner.clear();
	}		
	
}
