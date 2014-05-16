package space_battle.server.game_elements;

public class HalfScores extends PowerDown{
	
	private static int timeItLasts = 15000; // 15 sec
	
	public HalfScores(double x, double y){
		super(x, y);
	}

	public static int getTimeItLasts() {
		return timeItLasts;
	}

}
