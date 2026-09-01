package it.spaghettisource.tigersupply.game.scene.statemachine;

import it.spaghettisource.tigersupply.engine.statemachine.AbstractState;
import it.spaghettisource.tigersupply.engine.statemachine.Event;
import it.spaghettisource.tigersupply.game.scene.director.DirectorContext;

/**
 * Terminal state of the level-director machine: the boss is dead and the level is won. Reaching this
 * state is the single source of truth for "level cleared"; the machine halts here (see
 * {@code StateMachine.isInFinalState()}), so {@link #internalProcess(DirectorContext)} is never run.
 *
 * @author Alessandro D'Ottavio
 */
public class StateLevelCleared extends AbstractState<DirectorContext> {

	public StateLevelCleared(String stateName) {
		super(stateName);
	}

	@Override
	public boolean isFinal() {
		return true;
	}

	public Event internalProcess(DirectorContext context) {
		//never invoked: the machine halts on a final state
		return LevelDirectorStateMachineFactory.PENDING;
	}

}
