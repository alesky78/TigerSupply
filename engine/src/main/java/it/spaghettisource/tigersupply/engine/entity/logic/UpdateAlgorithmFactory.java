package it.spaghettisource.tigersupply.engine.entity.logic;

import it.spaghettisource.tigersupply.engine.utils.ClassFactory;
import it.spaghettisource.tigersupply.engine.utils.DynaProperties;

/**
 * Factory that instantiates {@link UpdateAlgorithm} implementations and configures them through
 * {@link UpdateAlgorithm#init(DynaProperties)}; algorithms should be created only from this class.
 *
 * @author Alessandro D'Ottavio
 *
 */
public class UpdateAlgorithmFactory {

	
	private UpdateAlgorithmFactory(){
	}

	/**
	 * Creates and initializes an algorithm of the given type.
	 *
	 * @param <S> the concrete {@link UpdateAlgorithm} type to build
	 * @param clazz the algorithm class to instantiate
	 * @param properties the configuration passed to {@link UpdateAlgorithm#init(DynaProperties)}
	 * @return the newly created and configured algorithm
	 * @throws Exception if the algorithm cannot be instantiated or configured
	 */
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
	
	/**
	 * Creates and initializes an algorithm, resolving its class from a fully-qualified class name.
	 *
	 * @param <S> the concrete {@link UpdateAlgorithm} type to build
	 * @param className the fully-qualified name of the algorithm class to instantiate
	 * @param properties the configuration passed to {@link UpdateAlgorithm#init(DynaProperties)}
	 * @return the newly created and configured algorithm
	 * @throws Exception if the class cannot be loaded, instantiated or configured
	 */
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
