package server.game_elements;

public abstract class Projectile extends NonShootingObject {
	
	final static int verticalMoveQuantity = 5;
	final static int projectileWidth = 5; //TODO
	final static int projectileHeight = 12;
	
	Projectile(int x, int y){
		super(x,y);				// HITBOX ASDASDASD
	}
	
	public abstract boolean isHit(GameElement object);
	 
	// Getters
	public static int getProjectilewidth() {
		return projectileWidth;
	}

	public static int getProjectileheight() {
		return projectileHeight;
	}
	 
}
//	Projectile(int x, int y){
//		this.coordX = x;
//		this.coordY = y;
//		this.elementHitbox = new HitBox(projectileWidth, projectileHeight);
//	}
