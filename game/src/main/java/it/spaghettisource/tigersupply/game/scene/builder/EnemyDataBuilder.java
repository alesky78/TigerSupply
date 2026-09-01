package it.spaghettisource.tigersupply.game.scene.builder;



import java.util.List;

import it.spaghettisource.tigersupply.game.scene.builder.definition.AlgorithmPrototype;
import it.spaghettisource.tigersupply.game.scene.builder.definition.EnemyPrototype;
import it.spaghettisource.tigersupply.game.scene.builder.definition.Step;

public interface EnemyDataBuilder {

	public void parse() throws Exception;
	
	public List<Step> buildSteps();
	
	public List<EnemyPrototype> buildEnemyPrototypes();
	
	public List<AlgorithmPrototype> buildAlgorithmPrototypes();		
	
	
}
