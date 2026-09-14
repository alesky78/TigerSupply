package it.spaghettisource.tigersupply.game.scene.builder.definition;

/**
 * Parsed geometry and timing of a dialogue window declared by a {@code <window>} element inside a
 * {@link ScriptDefinition}: the box position and size, the width reserved for the portrait, and the
 * per-character reveal delay of the typewriter effect. Values are stored as parsed strings and
 * exposed as numbers, mirroring the other level-definition POJOs.
 */
public class WindowDefinition {

	private String posX;
	private String posY;
	private String width;
	private String height;
	private String portraitWidth;
	private String charDelay;

	public WindowDefinition(String posX, String posY, String width, String height, String portraitWidth, String charDelay) {
		this.posX = posX;
		this.posY = posY;
		this.width = width;
		this.height = height;
		this.portraitWidth = portraitWidth;
		this.charDelay = charDelay;
	}

	public int getPosX() {
		return Integer.parseInt(posX.trim());
	}

	public int getPosY() {
		return Integer.parseInt(posY.trim());
	}

	public int getWidth() {
		return Integer.parseInt(width.trim());
	}

	public int getHeight() {
		return Integer.parseInt(height.trim());
	}

	public int getPortraitWidth() {
		return Integer.parseInt(portraitWidth.trim());
	}

	public float getCharDelay() {
		return Float.parseFloat(charDelay.trim());
	}

	public String toString() {
		return "window-> x:" + posX + " y:" + posY + " w:" + width + " h:" + height
				+ " portraitWidth:" + portraitWidth + " charDelay:" + charDelay;
	}

}
