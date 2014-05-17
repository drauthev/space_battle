package space_battle.server.game_elements;
/**
 * Abstract ancestor class of {@link ProjectileGoingDown}, {@link ProjectileGoingUp}, {@link ProjectileGoingDiagonallyLeft} and {@link ProjectileGoingDiagonallyRight}.
 * @author daniel.szeifert
 * @version 1.0
 * @since 2014-05-17
 */
public abstract class Projectile extends GameElement {
	
	private static int verticalMoveQuantity = 5;
	private final static int projectileWidth = 5;
	private final static int projectileHeight = 12;
	
	/**
	 * @param x Coordinate X of spawning.
	 * @param y Coordinate Y of spawning.
	 */
	Projectile(double x, double y){
		super(x,y);
	}
	/**
	 * 
	 * @param object {@link space_battle.server.game_elements.GameElement} with which the overlapping is checked.
	 * @return true if the projectile is overlapping with object, that is there is a hit. false otherwise.
	 */
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
