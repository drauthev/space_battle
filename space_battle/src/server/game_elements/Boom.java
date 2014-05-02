package server.game_elements;

public class Boom extends Modifier{
	private static int verticalMoveQuantity = 1;
		
	public Boom(int x, int y){
		super(x,y);
	}
	
	@Override
	public void autoMove(){
		super.setCoordY(super.getCoordY() + verticalMoveQuantity);
	}
	
	// Getters, setters
	public static int getVerticalMoveQuantity() {
		return verticalMoveQuantity;
	}

	public static void setVerticalMoveQuantity(int verticalMoveQuantity) {
		Boom.verticalMoveQuantity = verticalMoveQuantity;
	}

}
