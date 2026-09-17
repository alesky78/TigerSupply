package it.spaghettisource.tigersupply.game.entity.enemy;

import it.spaghettisource.tigersupply.game.entity.effect.ExplosionProfileFactory;
import it.spaghettisource.tigersupply.game.utils.EntityFactoryWrapper;
import it.spaghettisource.tigersupply.game.weapon.Weapon;
import it.spaghettisource.tigersupply.game.weapon.enemy.EnergyBallCannon;
import it.spaghettisource.tigersupply.game.weapon.enemy.LightningBoltLaser;
import it.spaghettisource.tigersupply.game.weapon.enemy.PlasmaCannon;

public class EnemyBoss extends Enemy {

	
	private boolean agonyStarted = false;
	
	public EnemyBoss(){
		super();
		life = 100;	
		hitProfile = ExplosionProfileFactory.bossHit();
		deathProfile = ExplosionProfileFactory.bossDeath();
		
		weapons = new Weapon[2];
		weapons[0] = new EnergyBallCannon();
		weapons[0].setOwner(this);		
		weapons[1] = new LightningBoltLaser(); 
		weapons[1].setOwner(this);				
		
	}

	public void updateEntity(float deltaSeconds) throws Exception  {
		
		//Y: the boss try to hit player with the laser beam
		if(target.getPosition().getPosY() > position.getPosY()){
			speed.setSpeedY(Math.abs(speed.getSpeedY()));
		}else if(target.getPosition().getPosY() < position.getPosY()){
			speed.setSpeedY(-Math.abs(speed.getSpeedY()));
		}
		
		//X: the boss move from right to screen center and then back (90% screen width)
		if(position.getPosX() < (0.5 * context.getScreenWidth())){
			speed.setSpeedX(Math.abs(speed.getSpeedX()));
		}else if(position.getPosX() > (0.9 * context.getScreenWidth()) ){
			speed.setSpeedX(- Math.abs(speed.getSpeedX()));
		}
		
		
		
		super.updateEntity(deltaSeconds);
		
		if(life<15 && !agonyStarted){
			agonyStarted = true;
			effectManager.addRequest(EntityFactoryWrapper.newFollowingExplosion(
					ExplosionProfileFactory.bossAgony(), this, 200, 2f, effectManager, context));
		}
	}
	
	public boolean isOutOfScreen(int windowWidth, int windowHeight){
			return false;
	}	

}
