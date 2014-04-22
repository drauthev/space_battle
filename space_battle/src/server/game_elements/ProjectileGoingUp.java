package server.game_elements;

public class ProjectileGoingUp extends Projectile {
	
	ProjectileGoingUp(int x, int y) {
		super(x,y);
	}
	
	public void autoMove(){
		super.setCoordY(super.getCoordY() + verticalMoveQuantity);
	}

}
