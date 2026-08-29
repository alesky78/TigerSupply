package it.spaghettisource.tigersupply.engine.font.repository;

import java.awt.Font;
import java.io.InputStream;
import java.net.URL;


/**
 * 
 * utility to load image
 * 
 * @author Alessandro D'Ottavio
 *
 */
public class FontLoader {
	
	public Font loadFont(String urlResource){
		URL url;
		Font font = null;

		url = this.getClass().getClassLoader().getResource(urlResource);
		try {
			InputStream in = url.openStream();
			font = Font.createFont(Font.TRUETYPE_FONT, in);
			
		} catch (Exception e) {
			System.out.println("error loading resource:"+urlResource);
			e.printStackTrace();
			System.exit(1);
		}		
	
		return font;
		
	}

}
