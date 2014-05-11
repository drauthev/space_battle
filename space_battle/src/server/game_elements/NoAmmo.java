package server.game_elements;

public class NoAmmo extends PowerDown{
	
	private static int timeItLasts = 10000; // 10 sec
	
	public NoAmmo(double x, double y){
		super(x, y);
	}

	public static int getTimeItLasts() {
		return timeItLasts;
	}

}
