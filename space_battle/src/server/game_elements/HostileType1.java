package server.game_elements;

import server.Constants;
import enums.GameSkill;

public class HostileType1 extends NPC {
		
	public HostileType1(int x, int y, GameSkill difficulty){
		super(x,y, Constants.hostile1scoreIfDestroyed, Constants.hostile1verticalMoveQuantity, Constants.hostile1horizontalMoveQuantity,Constants.hostile1shootingFrequency);
		super.setHitBox(new HitBox(Constants.hostile1Width, Constants.hostile1Height, this));
		// setting NPC's lives
		if(difficulty == GameSkill.EASY){
			super.setLives(Constants.hostileType1livesIfEasy);
		}
		else if(difficulty == GameSkill.NORMAL){
			super.setLives(Constants.hostileType1livesIfNormal);
		}
		else{
			super.setLives(Constants.hostileType1livesIfHard);
		}
	}
	
	public void autoMove(){
		super.setCoordY(super.getCoordY() + verticalMoveQuantity);
	}
	
	public Projectile shoot(){
		ProjectileGoingDown shot = new ProjectileGoingDown(this.getCoordX(), this.getCoordY() + Constants.hostile1Height/2 + Projectile.getProjectileheight()/2);
		return shot;
	}
		
}