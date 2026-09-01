package it.spaghettisource.tigersupply.game.scene.statemachine;

import it.spaghettisource.tigersupply.engine.statemachine.AbstractState;
import it.spaghettisource.tigersupply.engine.statemachine.Event;
import it.spaghettisource.tigersupply.game.scene.action.LevelAction;
import it.spaghettisource.tigersupply.game.scene.action.LevelActionFactory;
import it.spaghettisource.tigersupply.game.scene.builder.definition.ActionDefinition;
import it.spaghettisource.tigersupply.game.scene.builder.definition.CompletionEvent;
import it.spaghettisource.tigersupply.game.scene.builder.definition.Step;
import it.spaghettisource.tigersupply.game.scene.director.DirectorContext;

/**
 * Action state of the level-director machine: it runs every action of the current step, in
 * declaration order, then emits the step's completion event (which routes the machine to the next
 * wait state). The generalization of the former {@code StateSpawningHorde}, whose single hard-wired
 * action was "spawn a horde".
 *
 * @author Alessandro D'Ottavio
 */
public class StateExecutingStep extends AbstractState<DirectorContext> {

	public StateExecutingStep(String stateName) {
		super(stateName);
	}

	/**
	 * Runs the current step's actions in order, then returns the step's completion event.
	 *
	 * @throws Exception if an action fails to execute
	 */
	public Event internalProcess(DirectorContext context) throws Exception {

		Step step = context.getCurrentStep();

		for (ActionDefinition definition : step.getActions()) {
			LevelAction action = LevelActionFactory.create(definition);
			action.execute(context);
		}

		CompletionEvent completion = step.getCompletion();
		context.honorCompletion(completion);
		context.advanceStep();

		return new Event(completion.getName());
	}

}
