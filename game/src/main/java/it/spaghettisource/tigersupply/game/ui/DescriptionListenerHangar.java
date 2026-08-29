package it.spaghettisource.tigersupply.game.ui;



import java.awt.Graphics2D;
import java.awt.Paint;

import it.spaghettisource.tigersupply.engine.entity.Position;
import it.spaghettisource.tigersupply.engine.entity.Size;
import it.spaghettisource.tigersupply.engine.font.repository.FontRepositoryManager;
import it.spaghettisource.tigersupply.engine.sprite.Sprite;
import it.spaghettisource.tigersupply.engine.ui.RectangleButton;
import it.spaghettisource.tigersupply.engine.ui.listener.MouseOutListener;
import it.spaghettisource.tigersupply.engine.ui.listener.MouseOverListener;
import it.spaghettisource.tigersupply.game.utils.GameResources;

public class DescriptionListenerHangar extends RectangleButton implements MouseOverListener,MouseOutListener{

	private int cx,cy;	
	private Position position;
	private Size size;		

	private Sprite sprite;
	private String description;	

	private HangarDataModel model;



	public DescriptionListenerHangar(int x, int y, int w, int h, Paint paint,HangarDataModel model) {
		super(x, y, w, h, paint); 
		this.model = model;
		cx = x+w/2;
		cy = y+h/2;

		position = new Position(x+50, y+50, 0);

	}


	public void render(Graphics2D dbg) throws Exception {
		super.render(dbg);
		
		if(sprite!=null){	//render sprite and description
			size = new Size(sprite.getImageWidth(), sprite.getImageHeight());
			sprite.renderSprite(dbg, position, size);	
		}

		if(description!=null){
			dbg.setFont(FontRepositoryManager.getInstance().getFont(GameResources.FONT_TECHNO, 20));
			dbg.drawString(description, x+130, y+50);
		}
	}


	public void onMouseOver() {
		sprite = model.getSpriteInfo();
		description = model.getDescriptionInfo();
	}	

	public void onMouseOut() {
		sprite = null;
		description = null;
	}


}
