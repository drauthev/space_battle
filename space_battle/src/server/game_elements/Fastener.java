package server.game_elements;

public class Fastener extends Modifier {
	
	private static int verticalMoveQuantity = 1;
	private static int spawningFrequency;
	
	public Fastener(int x, int y){
		super(x,y);
	}
	
	public void autoMove(){
		super.setCoordY(super.getCoordY() - verticalMoveQuantity);
	}

}
