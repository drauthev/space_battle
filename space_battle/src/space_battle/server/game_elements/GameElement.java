package space_battle.server.game_elements;

/**
 * <h1>Ancestor abstract class of all the elements.</h1>
 * @author daniel.szeifert
 * @version 1.0
 * @since 2014-05-17
 *
 */
public abstract class GameElement {
	
	private double coordX;
	private double coordY; 
	private HitBox hitBox;
	
	// Constructor
	public GameElement(double x, double y){
		coordX = x;
		coordY = y;
	}
	
	/**
	 * Moving function of the game elements, called periodically by the Server class.
	 */
	public abstract void autoMove();
	
	// Getters, Setters
	// ------------------------------------------------------------------------------------------------------------------
	public double getCoordX() {
		return coordX;
	}

	public void setCoordX(double coordX) {
		this.coordX = coordX;
	}

	public double getCoordY() {
		return coordY;
	}

	public void setCoordY(double coordY) {
		this.coordY = coordY;
	}
		
	public HitBox getHitBox() {
		return hitBox;
	}

	public void setHitBox(HitBox hitBox) {
		this.hitBox = hitBox;
	}

}