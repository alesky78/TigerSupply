package it.spaghettisource.tigersupply.game.scene.builder;

import java.util.List;

import it.spaghettisource.tigersupply.game.scene.builder.definition.AlgorithmPrototype;
import it.spaghettisource.tigersupply.game.scene.builder.definition.EnemyPrototype;
import it.spaghettisource.tigersupply.game.scene.builder.definition.GenerateEvent;
import it.spaghettisource.tigersupply.game.scene.builder.definition.Horde;

public class LevelDataRepository {

	private List<Horde> hordes;
	List<EnemyPrototype> enemyPrototypes;
	List<AlgorithmPrototype> algorithmPrototypes;


	public void setHordes(List<Horde> hordes) {
		this.hordes = hordes;
	}

	public void setEnemyPrototypes(List<EnemyPrototype> enemyPrototypes) {
		this.enemyPrototypes = enemyPrototypes;
	}

	public void setAlgorithmPrototypes(List<AlgorithmPrototype> algorithmPrototypes) {
		this.algorithmPrototypes = algorithmPrototypes;
	}

	public Horde getHordeByIndex(int i){
		return hordes.get(i);
	}
	
	public GenerateEvent getEventByIndex(int i){
		return hordes.get(i).getEvent();
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
		buffer.append("horde size:"+hordes.size()+"\n");
		for (Horde single: hordes) {
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
