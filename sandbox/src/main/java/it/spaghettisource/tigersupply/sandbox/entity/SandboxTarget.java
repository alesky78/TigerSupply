package it.spaghettisource.tigersupply.sandbox.entity;

import java.awt.Color;
import java.awt.Graphics2D;

import it.spaghettisource.tigersupply.engine.entity.Position;
import it.spaghettisource.tigersupply.engine.entity.Size;
import it.spaghettisource.tigersupply.engine.entity.Speed;
import it.spaghettisource.tigersupply.game.entity.BaseEntity;

/**
 * A plain white square used inside the sandbox as a movable target.
 *
 * <p>It is a real {@link it.spaghettisource.tigersupply.engine.entity.Entity} so it can be handed to
 * the target-seeking factory methods (homing/aiming/following) as their {@code target}. The sandbox
 * test scene moves it with the arrow keys; it has no movement algorithm of its own.</p>
 */
public class SandboxTarget extends BaseEntity {

	public SandboxTarget(int posX, int posY, int sizePx) {
		this.position = new Position(posX, posY, Integer.MAX_VALUE);
		this.speed = new Speed(0, 0);
		this.size = new Size(sizePx, sizePx);
	}

	/** The target is driven by the keyboard, not by a movement algorithm. */
	@Override
	public void updateEntity(float deltaSeconds) throws Exception {
	}

	/**
	 * Moves the target by the given amount, clamped inside the playfield.
	 *
	 * @param dx horizontal displacement in pixels
	 * @param dy vertical displacement in pixels
	 * @param screenWidth playfield width used to clamp the position
	 * @param screenHeight playfield height used to clamp the position
	 */
	public void moveBy(int dx, int dy, int screenWidth, int screenHeight) {
		int x = Math.max(0, Math.min(screenWidth, getXposition() + dx));
		int y = Math.max(0, Math.min(screenHeight, getYposition() + dy));
		position.setPosX(x);
		position.setPosY(y);
	}

	@Override
	public void renderEntity(Graphics2D dbg) throws Exception {
		int w = size.getWidth();
		int h = size.getHeight();
		int x = getXposition() - w / 2;
		int y = getYposition() - h / 2;
		dbg.setColor(Color.WHITE);
		dbg.fillRect(x, y, w, h);
		dbg.setColor(Color.LIGHT_GRAY);
		dbg.drawRect(x, y, w, h);
	}

}
