package server.game_elements;

public class HalfScores extends PowerDown{
	
	private static int timeItLasts = 15000; // 15 sec
	
	public HalfScores(int x, int y){
		super(x, y);
	}

	public static int getTimeItLasts() {
		return timeItLasts;
	}

}
