package it.spaghettisource.tigersupply.game.scene.builder;

import java.util.List;

import it.spaghettisource.tigersupply.game.scene.builder.definition.AlgorithmPrototype;
import it.spaghettisource.tigersupply.game.scene.builder.definition.EnemyPrototype;
import it.spaghettisource.tigersupply.game.scene.builder.definition.Step;

public class LevelDataRepository {

	private List<Step> steps;
	List<EnemyPrototype> enemyPrototypes;
	List<AlgorithmPrototype> algorithmPrototypes;


	public void setSteps(List<Step> steps) {
		this.steps = steps;
	}

	public void setEnemyPrototypes(List<EnemyPrototype> enemyPrototypes) {
		this.enemyPrototypes = enemyPrototypes;
	}

	public void setAlgorithmPrototypes(List<AlgorithmPrototype> algorithmPrototypes) {
		this.algorithmPrototypes = algorithmPrototypes;
	}

	public Step getStepByIndex(int i){
		return steps.get(i);
	}	

	public EnemyPrototype getEnemyPrototypeByName(String name){
		for (EnemyPrototype proto : enemyPrototypes) {
			if(proto.getName().equals(name))
				return proto;
		}
		return null;
	}

	public AlgorithmPrototype getAlgorithmPrototypeByName(String name){
		for (AlgorithmPrototype proto : algorithmPrototypes) {
			if(proto.getName().equals(name))
				return proto;
		}
		return null;
	}

	public String toString(){
		StringBuffer buffer = new StringBuffer();
		buffer.append("level data......");
		buffer.append("step size:"+steps.size()+"\n");
		for (Step single: steps) {
			buffer.append(single.toString());
		}
		for (EnemyPrototype single: enemyPrototypes) {
			buffer.append(single.toString());
		}	
		for (AlgorithmPrototype single: algorithmPrototypes) {
			buffer.append(single.toString());
		}
		
		return buffer.toString();
	}

}
