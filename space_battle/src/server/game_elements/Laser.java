package server.game_elements;

import server.Constants;

public class Laser extends Modifier{
	private static double verticalMoveQuantity = Constants.modifierSpeedMedium;
	private static int timeItLasts = 6000; // 6 sec
		
	public Laser(double x, double y){
		super(x,y);
	}
	
	@Override
	public void autoMove(){
		super.setCoordY(super.getCoordY() + verticalMoveQuantity);
	}
	
	// Getters, setters
	public static int getTimeItLasts() {
		return timeItLasts;
	}
}
