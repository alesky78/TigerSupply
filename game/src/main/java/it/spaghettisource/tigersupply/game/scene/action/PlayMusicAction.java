package it.spaghettisource.tigersupply.game.scene.action;

import it.spaghettisource.tigersupply.engine.audio.AudioManager;
import it.spaghettisource.tigersupply.game.scene.builder.definition.ActionDefinition;
import it.spaghettisource.tigersupply.game.scene.director.DirectorContext;

/**
 * Starts a music track as part of a {@code Step}. Reads its {@code alias} (required) and {@code loop}
 * (optional, default {@code true}) from the action's XML attributes and commands the shared
 * {@link AudioManager} to play that track. Starting a track that is already playing is a no-op, so a
 * track is never layered on itself.
 *
 * @author Alessandro D'Ottavio
 */
public class PlayMusicAction implements LevelAction {

	private String alias;
	private boolean loop;

	@Override
	public void init(ActionDefinition definition) throws Exception {
		alias = definition.getProperty("alias");
		if (alias == null || alias.trim().isEmpty()) {
			throw new Exception("playMusic action requires a non-empty 'alias' attribute");
		}
		String loopValue = definition.getProperty("loop");
		loop = (loopValue == null) ? true : Boolean.parseBoolean(loopValue.trim());
	}

	@Override
	public void execute(DirectorContext context) throws Exception {
		AudioManager.getInstance().playMusic(alias, loop);
	}

}
