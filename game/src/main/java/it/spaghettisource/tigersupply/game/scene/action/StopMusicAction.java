package it.spaghettisource.tigersupply.game.scene.action;

import it.spaghettisource.tigersupply.engine.audio.AudioManager;
import it.spaghettisource.tigersupply.game.scene.builder.definition.ActionDefinition;
import it.spaghettisource.tigersupply.game.scene.director.DirectorContext;

/**
 * Stops a music track as part of a {@code Step}. Reads an optional {@code alias} from the action's
 * XML attributes: when present it stops that single track through the shared {@link AudioManager};
 * when absent it stops every music track. Stopping a track that is not playing is a no-op.
 *
 * @author Alessandro D'Ottavio
 */
public class StopMusicAction implements LevelAction {

	private String alias;

	@Override
	public void init(ActionDefinition definition) throws Exception {
		alias = definition.getProperty("alias");
		if (alias != null && alias.trim().isEmpty()) {
			alias = null;
		}
	}

	@Override
	public void execute(DirectorContext context) throws Exception {
		if (alias != null) {
			AudioManager.getInstance().stopMusic(alias);
		} else {
			AudioManager.getInstance().stopMusic();
		}
	}

}
