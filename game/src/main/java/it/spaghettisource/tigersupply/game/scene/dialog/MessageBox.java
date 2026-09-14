package it.spaghettisource.tigersupply.game.scene.dialog;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.image.BufferedImage;

import it.spaghettisource.tigersupply.engine.font.repository.FontRepositoryManager;
import it.spaghettisource.tigersupply.engine.image.repository.ImageRepositoryManager;
import it.spaghettisource.tigersupply.game.scene.builder.definition.MessageDefinition;
import it.spaghettisource.tigersupply.game.scene.builder.definition.WindowDefinition;
import it.spaghettisource.tigersupply.game.utils.GameResources;

/**
 * Stateless renderer of the 90s-arcade radio communication window: a black box with a thin white
 * border, the speaker's portrait on the left, and the speaker's name plus the progressively revealed
 * message lines in the bitmap font on the right, with a blinking cursor once the message is fully
 * revealed. It owns no dialogue state — the {@link DialogManager} passes the window geometry, the
 * message, and how many characters are currently revealed.
 */
public class MessageBox {

	private static final int PADDING = 12;
	private static final int NAME_SIZE = 22;
	private static final int TEXT_SIZE = 20;
	private static final int LINE_HEIGHT = TEXT_SIZE + 10;

	/**
	 * Draws the dialogue window for the given message.
	 *
	 * @param dbg           the graphics to draw into
	 * @param window        the window geometry (position, size, portrait width)
	 * @param message       the message being shown (speaker, portrait alias, lines)
	 * @param revealedChars how many characters of the message are currently revealed
	 * @param fullyRevealed whether the whole message text has been revealed
	 * @param blinkOn       whether the completion cursor is currently in its visible blink phase
	 * @throws Exception if the portrait image or the font cannot be resolved
	 */
	public void render(Graphics2D dbg, WindowDefinition window, MessageDefinition message,
			int revealedChars, boolean fullyRevealed, boolean blinkOn) throws Exception {

		int x = window.getPosX();
		int y = window.getPosY();
		int w = window.getWidth();
		int h = window.getHeight();
		int portraitWidth = window.getPortraitWidth();

		//black background + thin white border
		dbg.setColor(Color.BLACK);
		dbg.fillRect(x, y, w, h);
		dbg.setColor(Color.WHITE);
		Stroke previousStroke = dbg.getStroke();
		dbg.setStroke(new BasicStroke(2f));
		dbg.drawRect(x + 1, y + 1, w - 2, h - 2);
		dbg.setStroke(previousStroke);

		//portrait on the left, scaled into the reserved column
		BufferedImage portrait = ImageRepositoryManager.getInstance().getSingleImage(message.getPortrait());
		if (portrait != null) {
			dbg.drawImage(portrait, x + PADDING, y + PADDING, portraitWidth, h - 2 * PADDING, null);
		}

		//speaker name and message text to the right of the portrait
		int textX = x + PADDING + portraitWidth + PADDING;
		Font nameFont = FontRepositoryManager.getInstance().getFont(GameResources.FONT_C64, NAME_SIZE);
		Font textFont = FontRepositoryManager.getInstance().getFont(GameResources.FONT_C64, TEXT_SIZE);

		dbg.setColor(Color.WHITE);
		dbg.setFont(nameFont);
		int nameY = y + PADDING + NAME_SIZE;
		dbg.drawString(message.getSpeaker(), textX, nameY);

		dbg.setFont(textFont);
		int remaining = revealedChars;
		int lineY = nameY + LINE_HEIGHT;
		int cursorX = textX;
		int cursorY = lineY;
		for (String line : message.getLines()) {
			int show = Math.min(remaining, line.length());
			String visible = line.substring(0, Math.max(0, show));
			dbg.drawString(visible, textX, lineY);
			cursorX = textX + dbg.getFontMetrics().stringWidth(visible);
			cursorY = lineY;
			remaining -= show;
			lineY += LINE_HEIGHT;
			if (remaining <= 0 && !fullyRevealed) {
				break;	//stop at the first not-yet-revealed line while still typing
			}
		}

		//blinking cursor after the last revealed text once the message is complete
		if (fullyRevealed && blinkOn) {
			dbg.fillRect(cursorX + 6, cursorY - TEXT_SIZE + 4, 10, TEXT_SIZE);
		}
	}

}
