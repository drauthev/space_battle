package space_battle.server.game_elements;

import space_battle.enums.GameSkill;
import space_battle.server.Constants;
/**
 * Hostile type which goes straight down from the top of the screen.
 * Shoots one {@link ProjectileGoingDown} at a time with a period of {@link NPC#shootingFrequency}
 * @author daniel.szeifert
 * @version 1.0
 * @since 2014-05-17
 */
public class HostileType1 extends NPC {
	/**
	 * @param x Coordinate X of spawning.
	 * @param y Coordinate Y of spawning.
	 * @param difficulty Number of lives of the hostile according to difficulty stored in {@link space_battle.server.Server}.
	 */
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

	@Override
	public void autoMove(){
		super.setCoordY(super.getCoordY() + super.getVerticalMoveQuantity());
	}
	/**
	 * @see ShootingObject#shoot()
	 */
	@Override
	public Projectile shoot(){
		ProjectileGoingDown shot = new ProjectileGoingDown(this.getCoordX(), this.getCoordY() + Constants.hostile1Height/2 + Projectile.getProjectileheight()/2);
		return shot;
	}
		
}