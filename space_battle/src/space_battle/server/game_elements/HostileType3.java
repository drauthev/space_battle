package space_battle.server.game_elements;

import space_battle.enums.GameSkill;
import space_battle.server.Constants;
import space_battle.server.Server;
/**
 * Hostile type which goes straight down in the screen and periodically teleports to a spot with a random X coordinate.
 * It shoots three {@link ProjectileGoingDown} fast in a row, and then doesn't shoot for a period of time.
 * @author daniel.szeifert
 * @version 1.0
 * @since 2014-05-17
 */
public class HostileType3 extends NPC {
	/**
	 * Sent to the GUI for animation purposes.
	 */
	private long teleportTime = 0;
	/**
	 * The {@link space_battle.server.Server} calls {@link #shoot()} periodically, but it only returns not null if this flag is true.
	 * This is because the shooting frequency of {@link HostileType3} is high and constant, but the hostile should shoot during shooting periods only.
	 */
	private boolean shootingIsEnabled = false;
	/**
	 * Game tick when a shooting period ended. {@link space_battle.server.Server} sets {@link #shootingIsEnabled} true again, if enough time lasted.
	 */
	private long shootingWasEnabled = 0;
	
	/**
	 * @param x Coordinate X of spawning.
	 * @param y Coordinate Y of spawning.
	 * @param difficulty Number of lives of the hostile according to difficulty stored in {@link Server}.
	 */
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

	@Override
	public void autoMove(){
		super.setCoordY(super.getCoordY() + super.getVerticalMoveQuantity());
	}
	/**
	 * Sets coordX of the object to a random number between the boundaries of the game field.
	 * Also increases coordY with a quite large quantity.
	 */
	public void teleport(){
		teleportTime = java.lang.System.currentTimeMillis();
		int x = (int)(Math.random()*(Constants.gameFieldWidth - Constants.hostile3Width));
		x += Constants.hostile3Width/2;
		super.setCoordX(x);
		super.setCoordY(super.getCoordY() + 10*super.getVerticalMoveQuantity());
	}
	/**
	 * 
	 * Returns null if the hostile is not in a "shooting period", that is {@link #shootingIsEnabled} is false.
	 */
	@Override
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