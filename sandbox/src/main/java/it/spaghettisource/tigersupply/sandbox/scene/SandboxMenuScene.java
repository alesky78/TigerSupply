package it.spaghettisource.tigersupply.sandbox.scene;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import it.spaghettisource.tigersupply.engine.control.AbstractScene;
import it.spaghettisource.tigersupply.engine.control.GameContext;
import it.spaghettisource.tigersupply.engine.font.repository.FontRepositoryManager;
import it.spaghettisource.tigersupply.game.utils.GameResources;
import it.spaghettisource.tigersupply.sandbox.catalog.Family;
import it.spaghettisource.tigersupply.sandbox.catalog.SandboxCase;
import it.spaghettisource.tigersupply.sandbox.control.SandboxSceneHost;

/**
 * Menu scene: a two-pane master-detail catalog. The left pane lists the entity families; the right
 * pane lists the entities of the currently opened family as an independently scrolling list, so a
 * family of any length stays fully reachable within the fixed window.
 *
 * <p>In the family pane Up/Down move between families and Right/Enter open one (focus moves to the
 * item pane). In the item pane Up/Down move between entities, Enter launches the highlighted one in a
 * {@link SandboxTestScene}, and Left returns to the family pane. Escape quits from the family pane.</p>
 */
public class SandboxMenuScene extends AbstractScene {

	private static final int PANE_TOP = 120;
	private static final int LINE_HEIGHT = 24;
	private static final int BOTTOM_MARGIN = 40;
	private static final int FAMILY_X = 40;
	private static final int ITEM_X = 320;

	private final GameContext context;
	private final SandboxSceneHost host;

	private final List<Family> families = new ArrayList<Family>();
	private final Map<Family, List<SandboxCase>> casesByFamily = new LinkedHashMap<Family, List<SandboxCase>>();

	private int familyIndex = 0;
	private int itemIndex = 0;
	private boolean focusOnItems = false;
	private int scrollTop = 0;

	public SandboxMenuScene(GameContext context, SandboxSceneHost host, List<SandboxCase> cases) {
		this.context = context;
		this.host = host;
		this.pWidth = context.getScreenWidth();
		this.pHeight = context.getScreenHeight();
		groupByFamily(cases);
	}

	private void groupByFamily(List<SandboxCase> cases) {
		for (SandboxCase c : cases) {
			List<SandboxCase> group = casesByFamily.get(c.getFamily());
			if (group == null) {
				group = new ArrayList<SandboxCase>();
				casesByFamily.put(c.getFamily(), group);
				families.add(c.getFamily());
			}
			group.add(c);
		}
	}

	private List<SandboxCase> currentItems() {
		if (families.isEmpty()) {
			return new ArrayList<SandboxCase>();
		}
		return casesByFamily.get(families.get(familyIndex));
	}

	private int rowsPerPage() {
		return Math.max(1, (pHeight - BOTTOM_MARGIN - PANE_TOP) / LINE_HEIGHT);
	}

	private void clampScroll() {
		int rows = rowsPerPage();
		if (itemIndex < scrollTop) {
			scrollTop = itemIndex;
		} else if (itemIndex >= scrollTop + rows) {
			scrollTop = itemIndex - rows + 1;
		}
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
		String help = focusOnItems
				? "UP/DOWN select    ENTER launch    LEFT back"
				: "UP/DOWN family    RIGHT open    ESC quit";
		dbg.drawString(help, 40, 82);

		renderFamilyPane(dbg, small);
		renderItemPane(dbg, small);
	}

	private void renderFamilyPane(Graphics2D dbg, Font font) {
		dbg.setFont(font);
		int y = PANE_TOP;
		for (int i = 0; i < families.size(); i++) {
			boolean highlighted = (i == familyIndex);
			if (highlighted && !focusOnItems) {
				dbg.setColor(Color.YELLOW);
			} else if (highlighted) {
				dbg.setColor(new Color(120, 200, 255));
			} else {
				dbg.setColor(Color.WHITE);
			}
			String prefix = highlighted ? "> " : "   ";
			dbg.drawString(prefix + families.get(i).name(), FAMILY_X, y);
			y += LINE_HEIGHT;
		}
	}

	private void renderItemPane(Graphics2D dbg, Font small) throws Exception {
		List<SandboxCase> items = currentItems();
		if (items.isEmpty()) {
			return;
		}
		clampScroll();

		dbg.setFont(small);
		dbg.setColor(new Color(120, 200, 255));
		dbg.drawString(families.get(familyIndex).name() + "   " + (itemIndex + 1) + "/" + items.size(),
				ITEM_X, PANE_TOP - LINE_HEIGHT);

		Font itemFont = FontRepositoryManager.getInstance().getFont(GameResources.FONT_C64, 18);
		dbg.setFont(itemFont);
		int rows = rowsPerPage();
		int y = PANE_TOP;
		for (int i = scrollTop; i < items.size() && i < scrollTop + rows; i++) {
			boolean highlighted = (i == itemIndex);
			if (highlighted && focusOnItems) {
				dbg.setColor(Color.YELLOW);
			} else if (highlighted) {
				dbg.setColor(new Color(200, 200, 120));
			} else {
				dbg.setColor(Color.WHITE);
			}
			String prefix = highlighted ? "> " : "   ";
			dbg.drawString(prefix + items.get(i).getLabel(), ITEM_X, y);
			y += LINE_HEIGHT;
		}

		dbg.setColor(Color.GRAY);
		if (scrollTop > 0) {
			dbg.drawString("^", ITEM_X - 20, PANE_TOP);
		}
		if (scrollTop + rows < items.size()) {
			dbg.drawString("v", ITEM_X - 20, PANE_TOP + (rows - 1) * LINE_HEIGHT);
		}
	}

	@Override
	public void doFinalEffect(Graphics2D dbg) throws Exception {
	}

	@Override
	public void keyPressed(KeyEvent event) {
		int code = event.getKeyCode();
		try {
			if (focusOnItems) {
				handleItemPaneKey(code);
			} else {
				handleFamilyPaneKey(code);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void handleFamilyPaneKey(int code) {
		if (families.isEmpty()) {
			if (code == KeyEvent.VK_ESCAPE) {
				context.requestStopGame();
			}
			return;
		}
		if (code == KeyEvent.VK_UP) {
			familyIndex = (familyIndex - 1 + families.size()) % families.size();
			resetItemCursor();
		} else if (code == KeyEvent.VK_DOWN) {
			familyIndex = (familyIndex + 1) % families.size();
			resetItemCursor();
		} else if (code == KeyEvent.VK_RIGHT || code == KeyEvent.VK_ENTER) {
			if (!currentItems().isEmpty()) {
				focusOnItems = true;
			}
		} else if (code == KeyEvent.VK_ESCAPE) {
			context.requestStopGame();
		}
		// LEFT is a no-op in the family pane.
	}

	private void handleItemPaneKey(int code) throws Exception {
		List<SandboxCase> items = currentItems();
		if (items.isEmpty()) {
			focusOnItems = false;
			return;
		}
		if (code == KeyEvent.VK_UP) {
			itemIndex = (itemIndex - 1 + items.size()) % items.size();
		} else if (code == KeyEvent.VK_DOWN) {
			itemIndex = (itemIndex + 1) % items.size();
		} else if (code == KeyEvent.VK_ENTER) {
			host.showTest(items.get(itemIndex));
		} else if (code == KeyEvent.VK_LEFT || code == KeyEvent.VK_ESCAPE) {
			focusOnItems = false;
		}
	}

	private void resetItemCursor() {
		itemIndex = 0;
		scrollTop = 0;
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
