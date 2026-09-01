package it.spaghettisource.tigersupply.game.scene.action;

import it.spaghettisource.tigersupply.game.scene.builder.definition.ActionDefinition;
import it.spaghettisource.tigersupply.game.scene.director.DirectorContext;

/**
 * A single, fire-and-forget command executed as part of a {@code Step}. An action is configured once
 * from its parsed {@link ActionDefinition} via {@link #init(ActionDefinition)} and then performed via
 * {@link #execute(DirectorContext)}, which imperatively commands a game subsystem through the shared
 * context. Any ongoing behavior an action starts is owned by the subsystem it commands, not by the
 * action itself.
 *
 * @author Alessandro D'Ottavio
 */
public interface LevelAction {

	/**
	 * Configures this action from its parsed definition. Called by {@code LevelActionFactory} before
	 * {@link #execute(DirectorContext)}.
	 *
	 * @param definition the parsed action data (nested enemies and/or a property bag)
	 * @throws Exception if the definition is invalid for this action type
	 */
	void init(ActionDefinition definition) throws Exception;

	/**
	 * Performs the action's side effect against the shared director context.
	 *
	 * @param context the shared level-director context exposing the commandable subsystems
	 * @throws Exception if the action cannot be carried out
	 */
	void execute(DirectorContext context) throws Exception;

}
