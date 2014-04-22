package server.game_elements;


public class ProjectileGoingDown extends Projectile {

	ProjectileGoingDown(int x, int y) {
		super(x,y);
		//TODO: lehet, h a projectile-t nem is hitbox-szal kellene, hanem
		//mindegyikre külön isHit()-t írni, és a legalsó v legfelső stb koordinátáját vizsgálni
	}
	
	public void autoMove(){
		super.setCoordY(super.getCoordY() - verticalMoveQuantity);
	}

}
