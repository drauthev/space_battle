package server.game_elements;

public class OneUp extends Modifier {
	private static int verticalMoveQuantity = 1;
	private static int spawnFrequency;
		
	public OneUp(int x, int y){
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

	public static int getVerticalMoveQuantity() {
		return verticalMoveQuantity;
	}

	public static void setVerticalMoveQuantity(int verticalMoveQuantity) {
		OneUp.verticalMoveQuantity = verticalMoveQuantity;
	}

}
