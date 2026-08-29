package it.spaghettisource.tigersupply.engine.utils;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;


/**
 * Utility to manage the InputStream
 * 
 * 
 * @author DOttavio
 *
 */
public class StreamUtils {

	
	/**
	 * generate a byte[] reading an InputStream, this method don't close the InputStream,
	 * it is duty of the code that open the stream
	 * 
	 * @param is InputStream to analyze
	 * @return array of byte
	 * @throws IOException
	 */
	public static byte[] getBytesFromInputStream(InputStream is) throws IOException {
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		BufferedInputStream fin = new BufferedInputStream(is);
		byte buf[] = new byte[8192];
		int ret = 0;
		while ((ret = fin.read(buf)) != -1) {
			bout.write(buf, 0, ret);
		}
		fin.close();
		return bout.toByteArray();
	}	


}
