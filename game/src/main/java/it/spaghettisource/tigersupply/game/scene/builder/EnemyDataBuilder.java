package it.spaghettisource.tigersupply.game.scene.builder;



import java.util.List;

import it.spaghettisource.tigersupply.game.scene.builder.definition.AlgorithmPrototype;
import it.spaghettisource.tigersupply.game.scene.builder.definition.EnemyPrototype;
import it.spaghettisource.tigersupply.game.scene.builder.definition.Horde;

public interface EnemyDataBuilder {

	public void parse() throws Exception;
	
	public List<Horde> buildHordes();
	
	public List<EnemyPrototype> buildEnemyPrototypes();
	
	public List<AlgorithmPrototype> buildAlgorithmPrototypes();		
	
	
}
