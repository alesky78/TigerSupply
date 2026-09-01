package it.spaghettisource.tigersupply.game.scene.statemachine;

import it.spaghettisource.tigersupply.engine.statemachine.AbstractState;
import it.spaghettisource.tigersupply.engine.statemachine.Event;
import it.spaghettisource.tigersupply.game.scene.director.DirectorContext;

public class StateAwaitingClear extends AbstractState<DirectorContext> {

	public StateAwaitingClear(String stateName) {
		super(stateName);
	}

	@Override
	public Event internalProcess(DirectorContext context) {
		if (context.areAllEnemiesKilled())
			return LevelDirectorStateMachineFactory.READY;

		return LevelDirectorStateMachineFactory.PENDING;
	}

}
