package server.game_elements;

public abstract class Projectile extends NonShootingObject {
	
	final static int verticalMoveQuantity = 1;
	final static int projectileWidth = 2;
	final static int projectileHeight = 10;
	
	Projectile(int x, int y){
		super(x,y);				// HITBOX ASDASDASD
	}
	
	 public abstract boolean isHit(GameElement object);
	 
}
//	Projectile(int x, int y){
//		this.coordX = x;
//		this.coordY = y;
//		this.elementHitbox = new HitBox(projectileWidth, projectileHeight);
//	}
