package it.spaghettisource.tigersupply.game.builder;



import java.util.List;

import it.spaghettisource.tigersupply.game.scene.definition.AlgorithmPrototype;
import it.spaghettisource.tigersupply.game.scene.definition.EnemyPrototype;
import it.spaghettisource.tigersupply.game.scene.definition.Horde;

public interface EnemyDataBuilder {

	public void parse() throws Exception;
	
	public List<Horde> buildHordes();
	
	public List<EnemyPrototype> buildEnemyPrototypes();
	
	public List<AlgorithmPrototype> buildAlgorithmPrototypes();		
	
	
}
