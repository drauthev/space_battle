package space_battle.server.game_elements;

public class HostileFrenzy extends PowerDown{

	private static int timeItLasts = 10000; // 10 sec
	
	public HostileFrenzy(double x, double y){
		super(x, y);
	}

	public static int getTimeItLasts() {
		return timeItLasts;
	}
	
}
