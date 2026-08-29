package it.spaghettisource.tigersupply.engine.utils;

import java.lang.reflect.Constructor;


/**
 * generic utils that is able to create instances of object or load class
 * 
 * @author Alessandro D'Ottavio
 *
 */
public class ClassFactory {

	private ClassFactory(){

	}


	public static Object newIstance(String className) throws Exception{
		try{
			Class<?> clazz = Class.forName(className);
			Constructor<?> cst = clazz.getConstructor();
			return cst.newInstance();
		}catch(Exception e){
			Exception ex = new Exception("class factory can not create istance of class "+className,e);
			throw ex;
		}
	}

	public static <S extends Object> S newIstance(Class<S> clazz) throws Exception{
		try{
			Constructor<S> cst = clazz.getConstructor();
			return cst.newInstance();
		}catch(Exception e){
			Exception ex = new Exception("class factory can not create istance of class "+clazz.getName(),e);
			throw ex;
		}
	}	

	public static Class<?> loadClass(String className) throws Exception{
		try{
			return Class.forName(className);
		}catch(Exception e){
			e.printStackTrace();
			throw e;
		}
	}


}
