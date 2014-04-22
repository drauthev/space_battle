package server;

public class Constants {
	public static int framePerSecond = 30;
    public static int gameFieldWidth = 480;
    public static int gameFieldHeigth = 640;
    
	//public static enum powerUpWhatIsInside {}
	
	// constants according to game difficulty -- DOUBLE KELLENE! 16 pixeleket ugrálni egy framen KURVA SZAR LENNE :D
	public final static int modifierSpeedSlowIfEasy = 1; // is this good practice?
	public final static int modifierSpeedSlowIfMedium = 2;
	public final static int modifierSpeedSlowIfHard = 3;
	public final static int modifierSpeedMediumIfEasy = 2;
	public final static int modifierSpeedMediumIfMedium = 4;
	public final static int modifierSpeedMediumIfHard = 6;
	public final static int modifierSpeedFastIfEasy = 4;
	public final static int modifierSpeedFastIfMedium = 8;
	public final static int modifierSpeedFastIfHard = 16;
	//
	public final static int hostileType1livesIfEasy = 1;
	public final static int hostileType1livesIfMedium = 2;
	public final static int hostileType1livesIfHard = 1;
	public final static int hostileType2livesIfEasy = 2;
	public final static int hostileType2livesIfMedium = 3;
	public final static int hostileType2livesIfHard = 1;
	public final static int hostileType3livesIfEasy = 3;
	public final static int hostileType3livesIfMedium = 3;
	public final static int hostileType3livesIfHard = 2;
	
	// constants used by modifiers to take effect 
}
