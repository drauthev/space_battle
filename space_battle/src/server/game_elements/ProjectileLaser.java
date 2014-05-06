package server.game_elements;

public class ProjectileLaser extends ProjectileGoingUp {
	
	// identical to ProjectileGoingUp, but goes through Hostiles, exploding them at first hit
	
	ProjectileLaser(int x, int y, Player whoShotThis) {
		super(x,y);
	}
	
	// autoMove() and isHit() are identical to ProjectileGoingUp

	
}
