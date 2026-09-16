package it.spaghettisource.tigersupply.sandbox.catalog;

import it.spaghettisource.tigersupply.engine.control.GameContext;
import it.spaghettisource.tigersupply.engine.entity.Entity;

/**
 * A single testable entity in the sandbox: a label, the {@link Family} it belongs to (which group it
 * is inserted into) and a build step that constructs the entity and wires it to the collaborators it
 * needs.
 */
public class SandboxCase {

	/**
	 * Builds and wires the entity under test.
	 */
	public interface Builder {
		/**
		 * @param context the shared game context
		 * @param managers the sandbox groups (effect/shot/enemy) to wire and spawn children into
		 * @param target the movable target for aiming/homing/following entities
		 * @return the primary entity to run, never {@code null}
		 * @throws Exception if construction fails
		 */
		Entity build(GameContext context, SandboxManagers managers, Entity target) throws Exception;
	}

	private final String label;
	private final Family family;
	private final Builder builder;

	public SandboxCase(String label, Family family, Builder builder) {
		this.label = label;
		this.family = family;
		this.builder = builder;
	}

	public String getLabel() {
		return label;
	}

	public Family getFamily() {
		return family;
	}

	public Entity build(GameContext context, SandboxManagers managers, Entity target) throws Exception {
		return builder.build(context, managers, target);
	}
}
