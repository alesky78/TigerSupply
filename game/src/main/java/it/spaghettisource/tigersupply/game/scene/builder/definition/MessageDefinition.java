package it.spaghettisource.tigersupply.game.scene.builder.definition;

import java.util.ArrayList;
import java.util.List;

/**
 * Parsed definition of a single dialogue message: the speaker's name, the portrait alias to show
 * (image-catalog alias including the expression), and the message text pre-split into explicit
 * display lines. The lines are shown as authored — the dialogue system does not re-wrap text.
 */
public class MessageDefinition {

	private String speaker;
	private String portrait;
	private List<String> lines;

	public MessageDefinition(String speaker, String portrait) {
		this.speaker = speaker;
		this.portrait = portrait;
		this.lines = new ArrayList<String>();
	}

	public String getSpeaker() {
		return speaker;
	}

	public String getPortrait() {
		return portrait;
	}

	public List<String> getLines() {
		return lines;
	}

	public void addLine(String line) {
		lines.add(line);
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("message-> speaker:" + speaker + " portrait:" + portrait);
		for (String line : lines) {
			buffer.append("\n    " + line);
		}
		return buffer.toString();
	}

}
