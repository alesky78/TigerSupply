package it.spaghettisource.tigersupply.game.scene.builder.definition;

import java.util.ArrayList;
import java.util.List;

/**
 * Parsed definition of a reusable dialogue script declared in the level's {@code <scripts>} section
 * and referenced by name from a {@code showDialog} step action. It carries the window geometry and
 * reveal speed ({@link WindowDefinition}) plus the ordered list of {@link MessageDefinition}s shown,
 * one after another, as the player advances the dialogue.
 */
public class ScriptDefinition {

	private String name;
	private WindowDefinition window;
	private List<MessageDefinition> messages;

	public ScriptDefinition(String name) {
		this.name = name;
		this.messages = new ArrayList<MessageDefinition>();
	}

	public String getName() {
		return name;
	}

	public WindowDefinition getWindow() {
		return window;
	}

	public void setWindow(WindowDefinition window) {
		this.window = window;
	}

	public List<MessageDefinition> getMessages() {
		return messages;
	}

	public void addMessage(MessageDefinition message) {
		messages.add(message);
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("script-> name:" + name + "\n  " + window);
		for (MessageDefinition message : messages) {
			buffer.append("\n  " + message);
		}
		return buffer.toString();
	}

}
