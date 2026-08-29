package it.spaghettisource.tigersupply.game.scene.statemachine;

import it.spaghettisource.tigersupply.engine.statemachine.AbstractState;

public abstract class StateAbstract extends AbstractState {

	protected EnemyBuilderDataModel dataModel;

	public void setDataModel(EnemyBuilderDataModel dataModel) {
		this.dataModel = dataModel;
	}
	
	
	
	
}
