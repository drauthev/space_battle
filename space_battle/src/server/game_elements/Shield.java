package server.game_elements;

public class Shield extends Modifier{
	private static int verticalMoveQuantity = 1;
	private static int timeItLasts = 10000; // 10 sec
		
	public Shield(int x, int y){
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
		Shield.verticalMoveQuantity = verticalMoveQuantity;
	}

	public static int getTimeItLasts() {
		return timeItLasts;
	}
}
