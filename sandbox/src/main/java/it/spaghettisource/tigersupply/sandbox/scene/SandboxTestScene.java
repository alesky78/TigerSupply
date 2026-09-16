package it.spaghettisource.tigersupply.sandbox.scene;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import it.spaghettisource.tigersupply.engine.control.AbstractScene;
import it.spaghettisource.tigersupply.engine.control.GameContext;
import it.spaghettisource.tigersupply.engine.entity.Entity;
import it.spaghettisource.tigersupply.engine.entity.Size;
import it.spaghettisource.tigersupply.engine.entity.Speed;
import it.spaghettisource.tigersupply.engine.font.repository.FontRepositoryManager;
import it.spaghettisource.tigersupply.game.entity.enemy.Enemy;
import it.spaghettisource.tigersupply.game.utils.GameResources;
import it.spaghettisource.tigersupply.sandbox.catalog.SandboxCase;
import it.spaghettisource.tigersupply.sandbox.catalog.SandboxManagers;
import it.spaghettisource.tigersupply.sandbox.control.SandboxSceneHost;
import it.spaghettisource.tigersupply.sandbox.entity.SandboxTarget;

/**
 * Test scene: runs a single selected entity in isolation over the sandbox groups, with a movable
 * target and a debug overlay.
 *
 * <p>The entity (and anything it spawns, such as an enemy's shots or an effect's particles) lives in
 * the effect/shot/enemy groups. Arrows move the target; R respawns; Space pauses; comma/period step a
 * single frame while paused; Escape returns to the menu.</p>
 */
public class SandboxTestScene extends AbstractScene {

	private static final int MOVE_STEP = 12;

	private final GameContext context;
	private final SandboxSceneHost host;
	private final SandboxCase theCase;

	private SandboxManagers managers;
	private final SandboxTarget target;
	private Entity primary;

	private boolean paused = false;
	private boolean stepOnce = false;

	public SandboxTestScene(GameContext context, SandboxSceneHost host, SandboxCase theCase) throws Exception {
		this.context = context;
		this.host = host;
		this.theCase = theCase;
		this.pWidth = context.getScreenWidth();
		this.pHeight = context.getScreenHeight();
		this.target = new SandboxTarget(pWidth / 4, pHeight / 2, 26);
		spawn();
	}

	private void spawn() throws Exception {
		managers = new SandboxManagers(context);
		primary = theCase.build(context, managers, target);
		switch (theCase.getFamily()) {
			case PROJECTILE:
				managers.shot().addEntityToBeManaged(primary);
				break;
			case EFFECT:
				managers.effect().addEntityToBeManaged(primary);
				break;
			case ENEMY:
				managers.enemy().addEntityToBeManaged((Enemy) primary);
				break;
		}
	}

	private boolean needRespawn() {
		if (primary == null) {
			return true;
		}
		return primary.canBeRemoved() || primary.isOutOfScreen(pWidth, pHeight);
	}

	@Override
	public void update(float deltaTimeSeconds) throws Exception {
		if (context.isStop()) {
			return;
		}
		if (paused && !stepOnce) {
			return;
		}
		stepOnce = false;

		managers.effect().updateEntity(deltaTimeSeconds);
		managers.shot().updateEntity(deltaTimeSeconds);
		managers.enemy().updateEntity(deltaTimeSeconds);

		if (needRespawn()) {
			spawn();
		}
	}

	@Override
	public void internalRender(Graphics2D dbg) throws Exception {
		dbg.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		dbg.setColor(new Color(8, 8, 16));
		dbg.fillRect(0, 0, pWidth, pHeight);

		drawGrid(dbg);

		managers.enemy().renderEntity(dbg);
		managers.effect().renderEntity(dbg);
		managers.shot().renderEntity(dbg);

		target.renderEntity(dbg);

		drawOverlay(dbg);
	}

	private void drawGrid(Graphics2D dbg) {
		dbg.setColor(new Color(30, 30, 45));
		for (int x = 0; x <= pWidth; x += 100) {
			dbg.drawLine(x, 0, x, pHeight);
		}
		for (int y = 0; y <= pHeight; y += 100) {
			dbg.drawLine(0, y, pWidth, y);
		}
		dbg.setColor(new Color(60, 60, 90));
		dbg.drawLine(pWidth / 2, 0, pWidth / 2, pHeight);
		dbg.drawLine(0, pHeight / 2, pWidth, pHeight / 2);
	}

	private void drawOverlay(Graphics2D dbg) throws Exception {
		Font titleFont = FontRepositoryManager.getInstance().getFont(GameResources.FONT_C64, 22);
		dbg.setFont(titleFont);
		dbg.setColor(new Color(255, 80, 80));
		dbg.drawString(theCase.getLabel() + "  [" + theCase.getFamily().name() + "]", 12, 26);

		Font small = FontRepositoryManager.getInstance().getFont(GameResources.FONT_C64, 15);
		dbg.setFont(small);
		dbg.setColor(Color.LIGHT_GRAY);
		dbg.drawString(primaryInfo(), 12, 50);
		if (paused) {
			dbg.setColor(Color.YELLOW);
			dbg.drawString("PAUSED", 12, 70);
		}

		dbg.setColor(Color.GRAY);
		dbg.drawString("ARROWS move target   R respawn   SPACE pause   ,/. step   ESC menu", 12, pHeight - 14);
	}

	private String primaryInfo() {
		if (primary == null) {
			return "-";
		}
		StringBuilder sb = new StringBuilder();
		sb.append("pos(").append(primary.getXposition()).append(",").append(primary.getYposition()).append(")");
		Speed speed = primary.getSpeed();
		if (speed != null) {
			sb.append("  speed(").append((int) speed.getSpeedX()).append(",").append((int) speed.getSpeedY()).append(")");
		}
		Size size = primary.getsize();
		if (size != null) {
			sb.append("  size ").append(size.getWidth()).append("x").append(size.getHeight());
		}
		sb.append("  target(").append(target.getXposition()).append(",").append(target.getYposition()).append(")");
		return sb.toString();
	}

	@Override
	public void doFinalEffect(Graphics2D dbg) throws Exception {
	}

	@Override
	public void keyPressed(KeyEvent event) {
		int code = event.getKeyCode();
		try {
			switch (code) {
				case KeyEvent.VK_LEFT:
					target.moveBy(-MOVE_STEP, 0, pWidth, pHeight);
					break;
				case KeyEvent.VK_RIGHT:
					target.moveBy(MOVE_STEP, 0, pWidth, pHeight);
					break;
				case KeyEvent.VK_UP:
					target.moveBy(0, -MOVE_STEP, pWidth, pHeight);
					break;
				case KeyEvent.VK_DOWN:
					target.moveBy(0, MOVE_STEP, pWidth, pHeight);
					break;
				case KeyEvent.VK_R:
					spawn();
					break;
				case KeyEvent.VK_SPACE:
					paused = !paused;
					break;
				case KeyEvent.VK_COMMA:
				case KeyEvent.VK_PERIOD:
					if (paused) {
						stepOnce = true;
					}
					break;
				case KeyEvent.VK_ESCAPE:
					host.showMenu();
					break;
				default:
					break;
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
