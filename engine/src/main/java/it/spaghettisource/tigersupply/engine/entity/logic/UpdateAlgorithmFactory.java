package it.spaghettisource.tigersupply.engine.entity.logic;

import it.spaghettisource.tigersupply.engine.utils.ClassFactory;
import it.spaghettisource.tigersupply.engine.utils.DynaProperties;

/**
 * Factory for any kind of Sprite, the sprite should be created only from this class
 * 
 * @author Alessandro D'Ottavio
 *
 */
public class UpdateAlgorithmFactory {

	
	private UpdateAlgorithmFactory(){
	}

	public static <S extends UpdateAlgorithm> S newInstance(Class<S> clazz,DynaProperties properties) throws Exception{
		try {
			S algo = ClassFactory.newIstance(clazz);
			algo.init(properties);
			return algo;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	@SuppressWarnings("unchecked")
	public static <S extends UpdateAlgorithm> S newInstance(String className,DynaProperties properties) throws Exception{
		try {
			S algo = (S) ClassFactory.newIstance(className);
			algo.init(properties);
			return algo;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}	
	
	

}
