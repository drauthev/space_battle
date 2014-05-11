package server.game_elements;

import server.Constants;

public abstract class PowerDown extends Modifier {
	
	private static double verticalMoveQuantity = Constants.modifierSpeedFast;	// all powerdowns move fast
	
	PowerDown(double x, double y){
		super(x, y);
	}
	
	public void autoMove(){
		super.setCoordY(super.getCoordY() + verticalMoveQuantity);
	}
	
}
