package space_battle.server.game_elements;

/**
 * Class of projectiles shot by the {@link space_battle.server.game_elements.Player} class if it picked up a {@link Laser} power up.
 * It's ancestor class is {@link ProjectileGoingUp}.
 * @author daniel.szeifert
 * @version 1.0
 * @since 2014-05-17
 */
public class ProjectileLaser extends ProjectileGoingUp {
	
	// identical to ProjectileGoingUp, but goes through Hostiles, exploding them at first hit
	
	ProjectileLaser(double x, double y, Player whoShotThis) {
		super(x,y);
	}
	
	// autoMove() and isHit() are identical to ProjectileGoingUp

	
}
