package it.spaghettisource.tigersupply.engine.font.repository;

import java.awt.Font;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class FontRepository {

	private List<String> fontAlias = new ArrayList<String>();
	private List<Font> fontContainer = new ArrayList<Font>();	
	
	
	public void addFont(String name, Font font) throws Exception{
		storeSingleFont(name,font);
	}
	
	public int getMangedFontByAlias(){
		return fontAlias.size();
	}
	
	
	public Font getFont(String alias){
		int position = fontAlias.indexOf(alias);
		if(position == -1)
			return null;
		return fontContainer.get(position);
	}

	private void storeSingleFont(String alias,Font img) throws Exception{
		if(fontAlias.contains(alias)){
			throw new Exception("font "+alias+" already loaded in the repository use different alias");
		}
		fontAlias.add(alias);
		fontContainer.add(img);
	}	
	
	
	
}
