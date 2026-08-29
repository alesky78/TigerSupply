package it.spaghettisource.tigersupply.engine.impl.builder;



import java.util.List;

import it.spaghettisource.tigersupply.engine.impl.scene.definition.AlgorithmPrototype;
import it.spaghettisource.tigersupply.engine.impl.scene.definition.EnemyPrototype;
import it.spaghettisource.tigersupply.engine.impl.scene.definition.Horde;

public interface EnemyDataBuilder {

	public void parse() throws Exception;
	
	public List<Horde> buildHordes();
	
	public List<EnemyPrototype> buildEnemyPrototypes();
	
	public List<AlgorithmPrototype> buildAlgorithmPrototypes();		
	
	
}
