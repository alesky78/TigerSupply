package it.spaghettisource.tigersupply.game.scene.action;

import it.spaghettisource.tigersupply.game.scene.builder.definition.ActionDefinition;
import it.spaghettisource.tigersupply.game.scene.builder.definition.ScriptDefinition;
import it.spaghettisource.tigersupply.game.scene.director.DirectorContext;

/**
 * Starts a named dialogue script as part of a {@code Step}. It reads its {@code script} attribute,
 * resolves the matching {@link ScriptDefinition} from the level data, and commands the shared
 * {@code DialogManager} to play it. Like every other step action it is fire-and-forget: the running
 * dialogue is owned by the dialogue subsystem, and the step's {@code dialogClosed} completion event
 * is what holds the level until the player dismisses it.
 */
public class ShowDialogAction implements LevelAction {

	private String script;

	@Override
	public void init(ActionDefinition definition) throws Exception {
		script = definition.getProperty("script");
		if (script == null || script.trim().isEmpty()) {
			throw new Exception("showDialog action requires a non-empty 'script' attribute");
		}
		script = script.trim();
	}

	@Override
	public void execute(DirectorContext context) throws Exception {
		ScriptDefinition definition = context.getLevelData().getScriptByName(script);
		if (definition == null) {
			throw new Exception("showDialog action references unknown dialogue script '" + script + "'");
		}
		context.getDialogManager().start(definition);
	}

}
