package server.game_elements;

public abstract class Projectile extends NonShootingObject {
	
	private static int verticalMoveQuantity = 5;
	private final static int projectileWidth = 5; //TODO
	private final static int projectileHeight = 12;
	
	Projectile(int x, int y){
		super(x,y);
	}
	
	public abstract boolean isHit(GameElement object);
	 
	// Getters
	public static int getProjectilewidth() {
		return projectileWidth;
	}

	public static int getProjectileheight() {
		return projectileHeight;
	}

	public static void setVerticalMoveQuantity(int verticalMoveQuantity) {
		Projectile.verticalMoveQuantity = verticalMoveQuantity;
	}

	public static int getVerticalMoveQuantity() {
		return verticalMoveQuantity;
	}
	 
}
