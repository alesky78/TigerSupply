package it.spaghettisource.tigersupply.game.scene.builder.definition;

import java.util.ArrayList;
import java.util.List;

/**
 * A single beat of the level timeline: an ordered list of {@link ActionDefinition}s to run, plus the
 * one {@link CompletionEvent} that decides how the game waits before the next step. Replaces the
 * former {@code Horde} (which fused "spawn these enemies" with "how the wave completes").
 *
 * @author Alessandro D'Ottavio
 */
public class Step {

	private List<ActionDefinition> actions;
	private CompletionEvent completion;

	public Step() {
		actions = new ArrayList<ActionDefinition>();
	}

	public List<ActionDefinition> getActions() {
		return actions;
	}

	public void setActions(List<ActionDefinition> actions) {
		this.actions = actions;
	}

	public void addAction(ActionDefinition action) {
		actions.add(action);
	}

	public CompletionEvent getCompletion() {
		return completion;
	}

	public void setCompletion(CompletionEvent completion) {
		this.completion = completion;
	}

	public String toString(){
		StringBuffer buffer = new StringBuffer();
		buffer.append("step\n");
		for(ActionDefinition action : actions){
			buffer.append(action+"\n");
		}
		buffer.append(completion+"\n");
		return buffer.toString();
	}

}
