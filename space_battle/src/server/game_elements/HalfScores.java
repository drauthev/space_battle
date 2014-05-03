package server.game_elements;

public class HalfScores extends PowerDown{
	
	private static int timeItLasts = 10000; // 10 sec
	
	public HalfScores(int x, int y){
		super(x, y);
	}

	public static int getTimeItLasts() {
		return timeItLasts;
	}

}
