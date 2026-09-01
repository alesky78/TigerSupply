package it.spaghettisource.tigersupply.game.scene.statemachine;

import it.spaghettisource.tigersupply.engine.statemachine.AbstractState;
import it.spaghettisource.tigersupply.engine.statemachine.Event;
import it.spaghettisource.tigersupply.game.scene.director.DirectorContext;

public class StateAwaitingTimer extends AbstractState<DirectorContext> {

	public StateAwaitingTimer(String stateName) {
		super(stateName);
	}

	@Override
	public void onEnter(DirectorContext context) {
		context.resetElapsedTime();
	}

	public Event internalProcess(DirectorContext context) {
		if (context.getElapsedTime() > context.getWaitTime())
			return LevelDirectorStateMachineFactory.READY;
		return LevelDirectorStateMachineFactory.PENDING;
	}

}
