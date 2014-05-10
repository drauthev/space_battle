package server.game_elements;

public class Fastener extends Modifier {
	
	private static int verticalMoveQuantity = 1;
	private static int spawnFrequency;
	private static int timeItLasts = 10000; // 10 sec
		
	public Fastener(int x, int y){
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

	public static int getVerticalMoveQuantity() {
		return verticalMoveQuantity;
	}

	public static void setVerticalMoveQuantity(int verticalMoveQuantity) {
		Fastener.verticalMoveQuantity = verticalMoveQuantity;
	}

	public static int getTimeItLasts() {
		return timeItLasts;
	}
	
}
