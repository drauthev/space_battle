package server.game_elements;

import server.Constants;
import enums.GameSkill;

public class HostileType3 extends NPC {
	
	private long teleportTime = 0;
	private boolean shootingIsEnabled = false;
	private long shootingWasEnabled = 0;
		
	public HostileType3(int x, int y, GameSkill difficulty){
		super(x,y, Constants.hostile3scoreIfDestroyed, Constants.hostile3verticalMoveQuantity, Constants.hostile3horizontalMoveQuantity, Constants.hostile3burstShootingFrequency);
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
		super.setCoordY(super.getCoordY() + verticalMoveQuantity);
	}
	
	public void teleport(){
		teleportTime = java.lang.System.currentTimeMillis();
		int x = (int)(Math.random()*(Constants.gameFieldWidth - Constants.hostile3Width));
		x += Constants.hostile3Width/2;
		super.setCoordX(x);
		super.setCoordY(super.getCoordY() + 10);
	}
	
	public Projectile shoot(){
		if(shootingIsEnabled){
			ProjectileGoingDown shot = new ProjectileGoingDown(this.getCoordX(), this.getCoordY() + Constants.hostile3Height/2 + Projectile.getProjectileheight()/2);
			return shot;
		}
		else
			return null;
		
	}

	// Getters, setters
	public long getTeleportTime() {
		return teleportTime;
	}

	public void setTeleportTime(long teleportTime) {
		this.teleportTime = teleportTime;
	}

	public long getShootingWasEnabled() {
		return shootingWasEnabled;
	}

	public void setShootingWasEnabled(long shootingWasEnabled) {
		this.shootingWasEnabled = shootingWasEnabled;
	}

	public boolean isShootingIsEnabled() {
		return shootingIsEnabled;
	}

	public void setShootingIsEnabled(boolean shootingIsEnabled) {
		this.shootingIsEnabled = shootingIsEnabled;
	}
	
	
}