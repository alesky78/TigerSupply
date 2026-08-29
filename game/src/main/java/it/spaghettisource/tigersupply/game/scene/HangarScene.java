package it.spaghettisource.tigersupply.game.scene;

import it.spaghettisource.tigersupply.engine.background.BackGround;
import it.spaghettisource.tigersupply.engine.background.StaticBackGroundFitImage;
import it.spaghettisource.tigersupply.engine.control.AbstractGameJPanel;
import it.spaghettisource.tigersupply.engine.control.ApplicationContext;
import it.spaghettisource.tigersupply.engine.entity.Entity;
import it.spaghettisource.tigersupply.engine.entity.Speed;
import it.spaghettisource.tigersupply.engine.entity.manager.EntityManagerEntityRequest;
import it.spaghettisource.tigersupply.engine.font.repository.FontRepositoryManager;
import it.spaghettisource.tigersupply.engine.image.repository.ImageRepositoryManager;
import it.spaghettisource.tigersupply.game.entity.Player;
import it.spaghettisource.tigersupply.game.ui.DescriptionListenerHangar;
import it.spaghettisource.tigersupply.game.ui.HangarDataModel;
import it.spaghettisource.tigersupply.game.ui.ShipButtonHangar;
import it.spaghettisource.tigersupply.game.ui.StartButtonHangar;
import it.spaghettisource.tigersupply.game.ui.WeaponButtonHangar;
import it.spaghettisource.tigersupply.game.utils.EntityFactoryWrapper;
import it.spaghettisource.tigersupply.game.weapon.Weapon;
import it.spaghettisource.tigersupply.engine.sprite.ImagePlayerCenterControllerSprite;
import it.spaghettisource.tigersupply.engine.sprite.SpriteFactory;
import it.spaghettisource.tigersupply.engine.ui.RectangleInterfaceComposition;
import it.spaghettisource.tigersupply.engine.ui.UserInterfaceManager;
import it.spaghettisource.tigersupply.game.utils.GameResources;
import it.spaghettisource.tigersupply.game.weapon.player.Paser;
import it.spaghettisource.tigersupply.game.weapon.player.DoubleGun;
import it.spaghettisource.tigersupply.game.weapon.player.SynusoidalGun;
import it.spaghettisource.tigersupply.game.weapon.player.RocketLauncer;
import it.spaghettisource.tigersupply.game.weapon.player.Bomb;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class HangarScene extends AbstractGameJPanel {

	private ApplicationContext context;
	private BackGround backGround;	

	//menu variables
	private UserInterfaceManager uiManager;


	public HangarScene(ApplicationContext context,Player player) throws Exception{
		this.context = context;
		this.pWidth = context.getScreenWidth();
		this.pHeight = context.getScreenHeight();

		player.clearWeapons();	//remove previous weapon configuration before to configure
		
		backGround = new StaticBackGroundFitImage(ImageRepositoryManager.getInstance().getSingleImage(GameResources.BCKGROUND_HANGAR),  pWidth, pHeight);

		HangarDataModel model = new HangarDataModel();
		//draw the menu
		Paint paint;
		paint = new GradientPaint(100, 100, new Color(0,0,0,150), pWidth-200,pHeight-200, new Color(0,50,50,220));
		RectangleInterfaceComposition composition = new RectangleInterfaceComposition(100,100,pWidth-200,pHeight-200, paint);

		
		paint = new Color(0, 150, 190, 80);//paint for all the buttons
		
		//INFO LISTENER
		DescriptionListenerHangar listener = new DescriptionListenerHangar(900, 125, 300, 425, paint, model);
		composition.addInterface(listener);
		
		//SHIP SPRITE AND SPEED SELECTION
		ImagePlayerCenterControllerSprite sprite = null;
		Speed speed;
		
		//ship button A
		sprite = SpriteFactory.getInstance().createImagePlayerCenterControllerSprite(context.getPeriodMilliseconds(), 800, 2, GameResources.PLAYER_SHIP_A);
		speed = new Speed(150, 150);
		ShipButtonHangar a = new ShipButtonHangar(150, 125, 100, 100, paint,sprite,speed,model);
		a.addMouseOverListener(listener);
		a.addMouseOutListener(listener);
		
		//ship button B
		sprite = SpriteFactory.getInstance().createImagePlayerCenterControllerSprite(context.getPeriodMilliseconds(), 800, 2, GameResources.PLAYER_SHIP_B);
		speed = new Speed(95, 95);		
		ShipButtonHangar b = new ShipButtonHangar(300, 125, 100, 100, paint,sprite,speed,model);		
		b.addMouseOverListener(listener);
		b.addMouseOutListener(listener);
		
		composition.addInterface(a);
		composition.addInterface(b);

		//PRIMARY WEAPON SELECTION		
		WeaponButtonHangar wbutton = null;
		Weapon<Player> weapon = null;
		
		weapon = new Paser();
		wbutton = new WeaponButtonHangar(150, 250, 100, 100, paint, weapon, model,WeaponButtonHangar.PRIMARY_WEAPON);
		wbutton.addMouseOverListener(listener);
		wbutton.addMouseOutListener(listener);
		composition.addInterface(wbutton);
		
		weapon = new DoubleGun();
		wbutton = new WeaponButtonHangar(300, 250, 100, 100, paint, weapon, model,WeaponButtonHangar.PRIMARY_WEAPON);
		wbutton.addMouseOverListener(listener);
		wbutton.addMouseOutListener(listener);
		composition.addInterface(wbutton);		
		
		weapon = new SynusoidalGun();
		wbutton = new WeaponButtonHangar(450, 250, 100, 100, paint, weapon, model,WeaponButtonHangar.PRIMARY_WEAPON);
		wbutton.addMouseOverListener(listener);
		wbutton.addMouseOutListener(listener);
		composition.addInterface(wbutton);		
		
		//SECONDARY WEAPON SELECTION				
		weapon = new RocketLauncer();
		wbutton = new WeaponButtonHangar(150, 375, 100, 100, paint, weapon, model,WeaponButtonHangar.SECONDARY_WEAPON);
		wbutton.addMouseOverListener(listener);
		wbutton.addMouseOutListener(listener);
		composition.addInterface(wbutton);
		
		weapon = new Bomb();
		wbutton = new WeaponButtonHangar(300, 375, 100, 100, paint, weapon, model,WeaponButtonHangar.SECONDARY_WEAPON);
		wbutton.addMouseOverListener(listener);
		wbutton.addMouseOutListener(listener);
		composition.addInterface(wbutton);		
		
		//START BUTTON	
		StartButtonHangar start = new StartButtonHangar(150, 500, 100, 50, paint, model, player);
		composition.addInterface(start);
		
		uiManager = new UserInterfaceManager();
		uiManager.setComposition(composition);	
	}

	public void updateGame(float deltaTimeSeconds) throws Exception {
		if (!context.isPaused() && !context.isStop()){
			backGround.updateBackground(deltaTimeSeconds);
			uiManager.updateUserInterface(deltaTimeSeconds);
		}
	}


	public void internalRenderGame(Graphics2D dbg) throws Exception {
		backGround.renderBackground(dbg);
		uiManager.renderUserInterface(dbg);
	}

	public void doFinalEffect(Graphics2D dbg) throws Exception {
	}		
	

	public void mousePress(int x, int y) {
		if (!context.isPaused() && !context.isStop()){
			uiManager.mousePress(x, y);
		}
	}

	public void mouseMove(MouseEvent event) {
		if (!context.isPaused() && !context.isStop()){
			uiManager.mouseMove(event);
		}
	}	

	public void keyPressed(KeyEvent event) {
	}

	public void keyReleased(KeyEvent event) {
	}

}
