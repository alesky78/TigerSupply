package it.spaghettisource.tigersupply.game.scene.dialog;

import java.awt.Graphics2D;

import it.spaghettisource.tigersupply.game.scene.builder.definition.MessageDefinition;
import it.spaghettisource.tigersupply.game.scene.builder.definition.ScriptDefinition;
import it.spaghettisource.tigersupply.game.scene.builder.definition.WindowDefinition;

/**
 * Durative subsystem that plays a dialogue script: it reveals the current message one character at a
 * time, blinks a completion cursor, and moves through the messages as the player advances. It is
 * commanded fire-and-forget by the {@code showDialog} step action ({@link #start(ScriptDefinition)}),
 * ticked and rendered every frame by the level scene, and polled for {@link #isFinished()} by the
 * director's {@code awaitingDialog} state.
 *
 * <p>Advancement is edge-triggered by the caller: {@link #advance()} is expected to be invoked once
 * per physical fire-key press. The first advance on a still-typing message reveals it all; the next
 * moves to the following message; advancing past the last message finishes the dialogue.
 */
public class DialogManager {

	private static final float BLINK_INTERVAL_SECONDS = 0.4f;

	private final MessageBox messageBox = new MessageBox();

	private ScriptDefinition script;
	private WindowDefinition window;
	private float charDelay;

	private int messageIndex;
	private MessageDefinition currentMessage;
	private int totalChars;
	private int revealedChars;
	private float elapsed;
	private boolean finished;

	private float blinkTimer;
	private boolean blinkOn = true;

	/**
	 * Starts playing the given script from its first message. Any script with no messages finishes
	 * immediately.
	 *
	 * @param script the dialogue script to play
	 */
	public void start(ScriptDefinition script) {
		this.script = script;
		this.window = script.getWindow();
		this.charDelay = window.getCharDelay();
		this.messageIndex = 0;
		this.finished = false;
		this.blinkTimer = 0;
		this.blinkOn = true;
		if (script.getMessages().isEmpty()) {
			finished = true;
			currentMessage = null;
		} else {
			loadMessage(0);
		}
	}

	/**
	 * Advances the typewriter reveal and blink timer for the active message.
	 *
	 * @param deltaSeconds the elapsed frame time in seconds
	 */
	public void update(float deltaSeconds) {
		if (!isActive()) {
			return;
		}
		if (revealedChars < totalChars) {
			if (charDelay <= 0) {
				revealedChars = totalChars;	//no delay: reveal instantly
			} else {
				elapsed += deltaSeconds;
				revealedChars = Math.min(totalChars, (int) (elapsed / charDelay));
			}
		}
		blinkTimer += deltaSeconds;
		if (blinkTimer >= BLINK_INTERVAL_SECONDS) {
			blinkTimer = 0;
			blinkOn = !blinkOn;
		}
	}

	/**
	 * Handles one player advance request: reveal the rest of the current message while it is still
	 * typing, otherwise move to the next message (finishing the dialogue after the last one).
	 */
	public void advance() {
		if (!isActive()) {
			return;
		}
		if (revealedChars < totalChars) {
			revealedChars = totalChars;	//first press completes the current message
		} else {
			goToNextMessage();
		}
	}

	/**
	 * Draws the dialogue window when one is active; a no-op otherwise.
	 *
	 * @param dbg the graphics to draw into
	 * @throws Exception if the portrait or font cannot be resolved
	 */
	public void render(Graphics2D dbg) throws Exception {
		if (!isActive()) {
			return;
		}
		messageBox.render(dbg, window, currentMessage, revealedChars, revealedChars >= totalChars, blinkOn);
	}

	/**
	 * @return {@code true} while a dialogue is playing and not yet dismissed
	 */
	public boolean isActive() {
		return script != null && !finished;
	}

	/**
	 * @return {@code true} when no dialogue is playing or the active one has been dismissed
	 */
	public boolean isFinished() {
		return script == null || finished;
	}

	private void goToNextMessage() {
		messageIndex += 1;
		if (messageIndex >= script.getMessages().size()) {
			finished = true;
			currentMessage = null;
		} else {
			loadMessage(messageIndex);
		}
	}

	private void loadMessage(int index) {
		currentMessage = script.getMessages().get(index);
		totalChars = 0;
		for (String line : currentMessage.getLines()) {
			totalChars += line.length();
		}
		revealedChars = 0;
		elapsed = 0;
	}

}
