package server.game_elements;

import server.Constants;
import enums.GameSkill;

public class HostileType3 extends NPC {
		
	HostileType3(int x, int y, GameSkill difficulty){
		super(x,y, Constants.hostile3scoreIfDestroyed, Constants.hostile3verticalMoveQuantity, Constants.hostile3horizontalMoveQuantity, Constants.hostile3shootingFrequency);
		super.setHitBox(new HitBox(Constants.hostile3Width, Constants.hostile3Height, this));
		// setting NPC's lives
		if(difficulty == GameSkill.EASY){
			super.setLives(Constants.hostileType3livesIfEasy);
		}
		else if(difficulty == GameSkill.NORMAL){
			super.setLives(Constants.hostileType3livesIfNormal);
		}
		else{
			super.setLives(Constants.hostileType3livesIfHard);
		}
	}
	
	public void autoMove(){
		//TODO
	}
	
	public Projectile shoot(){
		ProjectileGoingDown dummy = new ProjectileGoingDown(0,0);
		return dummy;
	}
	
	
}