package it.spaghettisource.tigersupply.game.scene.builder.definition;

/**
 * Declares the event a {@link Step} emits once its actions have run: it names the completion event
 * that routes the level director's state machine, plus an optional {@code time} used only by the
 * {@code timed} completion. Replaces the former {@code GenerateEvent}.
 *
 * @author Alessandro D'Ottavio
 */
public class CompletionEvent {

	private String name;
	private String time;

	public CompletionEvent(String name, String time){
		this.name = name;
		this.time = time;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}

	public String toString(){
		StringBuffer buffer = new StringBuffer();
		buffer.append("completionEvent-> name:"+name+" time:"+time);
		return buffer.toString();
	}

}
