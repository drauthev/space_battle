package server.game_elements;

public class LeftRightSwitcher extends PowerDown{
	
	private static int timeItLasts = 20000; // 20 sec
	
	public LeftRightSwitcher(int x, int y){
		super(x, y);
	}

	public static int getTimeItLasts() {
		return timeItLasts;
	}
}
