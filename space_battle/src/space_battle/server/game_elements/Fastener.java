package space_battle.server.game_elements;

import space_battle.server.Constants;

public class Fastener extends Modifier {
	
	private static double verticalMoveQuantity = Constants.modifierSpeedSlow;
	private static int spawnFrequency;
	private static int timeItLasts = 10000; // 10 sec
		
	public Fastener(double x, double y){
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
		Fastener.spawnFrequency = spawnFrequency;
	}

	public static int getTimeItLasts() {
		return timeItLasts;
	}
	
}
