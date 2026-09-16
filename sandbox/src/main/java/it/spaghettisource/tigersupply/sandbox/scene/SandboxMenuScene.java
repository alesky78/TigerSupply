package it.spaghettisource.tigersupply.sandbox.scene;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.List;

import it.spaghettisource.tigersupply.engine.control.AbstractScene;
import it.spaghettisource.tigersupply.engine.control.GameContext;
import it.spaghettisource.tigersupply.engine.font.repository.FontRepositoryManager;
import it.spaghettisource.tigersupply.game.utils.GameResources;
import it.spaghettisource.tigersupply.sandbox.catalog.Family;
import it.spaghettisource.tigersupply.sandbox.catalog.SandboxCase;
import it.spaghettisource.tigersupply.sandbox.control.SandboxSceneHost;

/**
 * Menu scene: lists every catalogued entity grouped by family and launches the highlighted one.
 *
 * <p>Up/Down move the cursor, Enter launches the selected case in a {@link SandboxTestScene}, Escape
 * quits the sandbox.</p>
 */
public class SandboxMenuScene extends AbstractScene {

	private final GameContext context;
	private final SandboxSceneHost host;
	private final List<SandboxCase> cases;

	private int selectedIndex = 0;

	public SandboxMenuScene(GameContext context, SandboxSceneHost host, List<SandboxCase> cases) {
		this.context = context;
		this.host = host;
		this.cases = cases;
		this.pWidth = context.getScreenWidth();
		this.pHeight = context.getScreenHeight();
	}

	@Override
	public void update(float deltaTimeSeconds) throws Exception {
	}

	@Override
	public void internalRender(Graphics2D dbg) throws Exception {
		dbg.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		dbg.setColor(new Color(10, 10, 25));
		dbg.fillRect(0, 0, pWidth, pHeight);

		Font title = FontRepositoryManager.getInstance().getFont(GameResources.FONT_C64, 34);
		dbg.setFont(title);
		dbg.setColor(new Color(255, 60, 60));
		dbg.drawString("ENTITY SANDBOX", 40, 55);

		Font small = FontRepositoryManager.getInstance().getFont(GameResources.FONT_C64, 16);
		dbg.setFont(small);
		dbg.setColor(Color.LIGHT_GRAY);
		dbg.drawString("UP/DOWN select    ENTER launch    ESC quit", 40, 82);

		Font item = FontRepositoryManager.getInstance().getFont(GameResources.FONT_C64, 18);
		int y = 120;
		int lineHeight = 24;
		Family currentFamily = null;
		for (int i = 0; i < cases.size(); i++) {
			SandboxCase c = cases.get(i);
			if (c.getFamily() != currentFamily) {
				currentFamily = c.getFamily();
				y += 8;
				dbg.setFont(small);
				dbg.setColor(new Color(120, 200, 255));
				dbg.drawString(currentFamily.name(), 40, y);
				y += lineHeight;
			}
			dbg.setFont(item);
			boolean selected = (i == selectedIndex);
			dbg.setColor(selected ? Color.YELLOW : Color.WHITE);
			String prefix = selected ? "> " : "   ";
			dbg.drawString(prefix + c.getLabel(), 70, y);
			y += lineHeight;
		}
	}

	@Override
	public void doFinalEffect(Graphics2D dbg) throws Exception {
	}

	@Override
	public void keyPressed(KeyEvent event) {
		int code = event.getKeyCode();
		try {
			if (code == KeyEvent.VK_UP) {
				selectedIndex = (selectedIndex - 1 + cases.size()) % cases.size();
			} else if (code == KeyEvent.VK_DOWN) {
				selectedIndex = (selectedIndex + 1) % cases.size();
			} else if (code == KeyEvent.VK_ENTER) {
				host.showTest(cases.get(selectedIndex));
			} else if (code == KeyEvent.VK_ESCAPE) {
				context.requestStopGame();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void keyReleased(KeyEvent event) {
	}

	@Override
	public void mousePressed(int x, int y) {
	}

	@Override
	public void mouseMoved(MouseEvent event) {
	}
}
