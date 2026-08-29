package it.spaghettisource.tigersupply.engine.impl.scene.definition;

import java.util.List;

public class LevelDataRepository {

	private List<Horde> hordes;
	List<EnemyPrototype> prototypeEnemies;
	List<AlgorithmPrototype> algoithmPrototypes;


	public void setHordes(List<Horde> hordes) {
		this.hordes = hordes;
	}

	public void setEnemyPrototypes(List<EnemyPrototype> prototypeEnemies) {
		this.prototypeEnemies = prototypeEnemies;
	}

	public void setAlgoithmPrototypes(List<AlgorithmPrototype> algoithmPrototypes) {
		this.algoithmPrototypes = algoithmPrototypes;
	}

	public Horde getHordeByIndex(int i){
		return hordes.get(i);
	}
	
	public GenerateEvent getEventByIndex(int i){
		return hordes.get(i).getEvent();
	}	

	public EnemyPrototype getEnemyPrototypeByName(String name){
		for (EnemyPrototype proto : prototypeEnemies) {
			if(proto.getName().equals(name))
				return proto;
		}
		return null;
	}

	public AlgorithmPrototype getAlgorithmPrototypeByName(String name){
		for (AlgorithmPrototype proto : algoithmPrototypes) {
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
		for (EnemyPrototype single: prototypeEnemies) {
			buffer.append(single.toString());
		}	
		for (AlgorithmPrototype single: algoithmPrototypes) {
			buffer.append(single.toString());
		}
		
		return buffer.toString();
	}

}
