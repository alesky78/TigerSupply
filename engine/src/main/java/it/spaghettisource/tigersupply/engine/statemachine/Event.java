package it.spaghettisource.tigersupply.engine.statemachine;

/**
 * An immutable, named signal produced by a {@link State} during a {@link StateMachine#tick() tick} and used,
 * together with the current state, as the key that the {@link TransitionTable} resolves into the next state.
 * <p>
 * The name is opaque to the engine: it only compares it for equality when looking up transitions, so its
 * meaning is defined entirely by the concrete states and the transition table that declare it.
 */
public class Event {

	private String name;

	/**
	 * @param name the event name used as part of the transition-table lookup key; expected to be non-{@code null}
	 */
	public Event(String name){
		this.name = name;
	}

	/**
	 * @return the event name used, with the current state, to resolve the next state in the transition table
	 */
	public String getName() {
		return name;
	}
	
}
