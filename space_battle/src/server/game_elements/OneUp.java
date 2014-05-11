package server.game_elements;

import server.Constants;

public class OneUp extends Modifier {
	private static double verticalMoveQuantity = Constants.modifierSpeedFast;
	private static int spawnFrequency;
		
	public OneUp(double x, double y){
		super(x,y);
	}
	
	@Override
	public void autoMove(){
		super.setCoordY(super.getCoordY() + verticalMoveQuantity);
	}
	
	// Getters, setters
	public static int getSpawnFrequency() {
		return spawnFrequency;
	}

	public static void setSpawnFrequency(int spawnFrequency) {
		OneUp.spawnFrequency = spawnFrequency;
	}

}
