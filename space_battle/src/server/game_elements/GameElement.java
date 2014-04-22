package server.game_elements;

public abstract class GameElement {
	
	private int coordX;
	private int coordY; 
	private HitBox hitBox;
	
	// Constructor
	public GameElement(int x, int y){
		coordX = x;
		coordY = y;
	}
	
	public abstract void autoMove();	//mozgató fv, playernél üres, ott manuális az irányítás
	
	// Getters, Setters
	// ------------------------------------------------------------------------------------------------------------------
	public int getCoordX() {
		return coordX;
	}

	public void setCoordX(int coordX) {
		this.coordX = coordX;
	}

	public int getCoordY() {
		return coordY;
	}

	public void setCoordY(int coordY) {
		this.coordY = coordY;
	}
		
	public HitBox getHitBox() {
		return hitBox;
	}

	public void setHitBox(HitBox hitBox) {
		this.hitBox = hitBox;
	}

}