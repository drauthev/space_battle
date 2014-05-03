package server.game_elements;

public class Laser extends Modifier{
	private static int verticalMoveQuantity = 1;
	private static int timeItLasts = 6000; // 5 sec
		
	public Laser(int x, int y){
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
		Laser.verticalMoveQuantity = verticalMoveQuantity;
	}

	public static int getTimeItLasts() {
		return timeItLasts;
	}
}
