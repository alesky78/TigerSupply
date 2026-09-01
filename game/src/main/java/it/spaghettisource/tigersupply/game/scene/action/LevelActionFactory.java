package it.spaghettisource.tigersupply.game.scene.action;

import java.util.HashMap;
import java.util.Map;

import it.spaghettisource.tigersupply.engine.utils.ClassFactory;
import it.spaghettisource.tigersupply.game.scene.builder.definition.ActionDefinition;

/**
 * Builds and configures {@link LevelAction} instances from a parsed {@link ActionDefinition}. The
 * action's {@code type} discriminator is resolved to a concrete class and instantiated by reflection
 * (mirroring {@code EntityFactory} / {@code UpdateAlgorithmFactory}); the new instance is then
 * configured via {@link LevelAction#init(ActionDefinition)}. Register a new action type by adding a
 * {@code type -> fully-qualified class name} entry here.
 *
 * @author Alessandro D'Ottavio
 */
public class LevelActionFactory {

	/** Maps the XML {@code <action type="...">} discriminator to the fully-qualified action class. */
	private static final Map<String, String> TYPE_TO_CLASS = new HashMap<String, String>();
	static {
		TYPE_TO_CLASS.put("spawnHorde", "it.spaghettisource.tigersupply.game.scene.action.SpawnHordeAction");
		TYPE_TO_CLASS.put("playMusic", "it.spaghettisource.tigersupply.game.scene.action.PlayMusicAction");
		TYPE_TO_CLASS.put("stopMusic", "it.spaghettisource.tigersupply.game.scene.action.StopMusicAction");
	}

	private LevelActionFactory() {
	}

	/**
	 * Creates and configures the {@link LevelAction} declared by the given definition.
	 *
	 * @param definition the parsed action definition, carrying the {@code type} and its data
	 * @return a ready-to-execute action instance
	 * @throws Exception if the type is unknown or the class cannot be instantiated/configured
	 */
	public static LevelAction create(ActionDefinition definition) throws Exception {
		String type = definition.getType();
		String className = TYPE_TO_CLASS.get(type);
		if (className == null) {
			throw new Exception("unknown level action type '" + type + "'; register it in LevelActionFactory");
		}
		LevelAction action = (LevelAction) ClassFactory.newIstance(className);
		action.init(definition);
		return action;
	}

}
