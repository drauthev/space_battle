package space_battle.server.game_elements;

import space_battle.server.Constants;

public class Shield extends Modifier{
	private static double verticalMoveQuantity = Constants.modifierSpeedFast;
	private static int timeItLasts = 20000; // 10 sec
		
	public Shield(double x, double y){
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
