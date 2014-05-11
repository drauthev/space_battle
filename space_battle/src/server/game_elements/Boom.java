package server.game_elements;

import server.Constants;

public class Boom extends Modifier{
	private static double verticalMoveQuantity = Constants.modifierSpeedFast;
		
	public Boom(double x, double y){
		super(x,y);
	}
	
	@Override
	public void autoMove(){
		super.setCoordY(super.getCoordY() + verticalMoveQuantity);
	}
	
	// Getters, setters

}
