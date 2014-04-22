package server.game_elements;

public abstract class PowerDown extends Modifier {
	
	private static int verticalMoveQuantity = 1;	// minden negatív mod. esetén gyors
	
	PowerDown(int x, int y){
		super(x, y);
	}
	
	public void autoMove(){
		super.setCoordY(super.getCoordY() - verticalMoveQuantity);
	}
	
}
