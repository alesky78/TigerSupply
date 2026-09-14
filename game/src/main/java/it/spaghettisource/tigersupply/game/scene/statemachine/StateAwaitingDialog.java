package it.spaghettisource.tigersupply.game.scene.statemachine;

import it.spaghettisource.tigersupply.engine.statemachine.AbstractState;
import it.spaghettisource.tigersupply.engine.statemachine.Event;
import it.spaghettisource.tigersupply.game.scene.director.DirectorContext;

/**
 * Wait state entered after a {@code dialogClosed} step: it self-loops while a dialogue is on screen
 * and releases to the next step once the dialogue subsystem reports the dialogue dismissed. Mirrors
 * {@link StateAwaitingClear}, polling the dialogue manager instead of the enemy count.
 */
public class StateAwaitingDialog extends AbstractState<DirectorContext> {

	public StateAwaitingDialog(String stateName) {
		super(stateName);
	}

	@Override
	public Event internalProcess(DirectorContext context) {
		if (context.getDialogManager().isFinished())
			return LevelDirectorStateMachineFactory.READY;

		return LevelDirectorStateMachineFactory.PENDING;
	}

}
