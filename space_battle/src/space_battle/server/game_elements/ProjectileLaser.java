package space_battle.server.game_elements;

public class ProjectileLaser extends ProjectileGoingUp {
	
	// identical to ProjectileGoingUp, but goes through Hostiles, exploding them at first hit
	
	ProjectileLaser(double x, double y, Player whoShotThis) {
		super(x,y);
	}
	
	// autoMove() and isHit() are identical to ProjectileGoingUp

	
}
