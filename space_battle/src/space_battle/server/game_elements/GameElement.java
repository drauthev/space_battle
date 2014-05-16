package space_battle.server.game_elements;

public abstract class GameElement {
	
	private double coordX;
	private double coordY; 
	private HitBox hitBox;
	
	// Constructor
	public GameElement(double x, double y){
		coordX = x;
		coordY = y;
	}
	
	public abstract void autoMove();	//mozgató fv, playernél üres, ott manuális az irányítás
	
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