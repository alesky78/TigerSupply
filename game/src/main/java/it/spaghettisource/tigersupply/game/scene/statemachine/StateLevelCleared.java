package it.spaghettisource.tigersupply.game.scene.statemachine;

import it.spaghettisource.tigersupply.engine.statemachine.AbstractState;
import it.spaghettisource.tigersupply.engine.statemachine.Event;

/**
 * Terminal state of the enemy-spawn machine: the boss is dead and the level is won. Reaching this
 * state is the single source of truth for "boss dead"; the machine halts here (see
 * {@code StateMachine.isInFinalState()}), so {@link #internalProcess(EnemySpawnContext)} is never run.
 *
 * @author Alessandro D'Ottavio
 */
public class StateLevelCleared extends AbstractState<EnemySpawnContext> {

	public StateLevelCleared(String stateName) {
		super(stateName);
	}

	@Override
	public boolean isFinal() {
		return true;
	}

	public Event internalProcess(EnemySpawnContext context) {
		//never invoked: the machine halts on a final state
		return EnemySpawnStateMachineFactory.PENDING;
	}

}
