package server;

public class Constants {
	public static int framePerSecond = 30;
    public static int gameFieldWidth = 480;
    public static int gameFieldHeigth = 640;
    
    public final static int timeBetweenShots = 500;
    
	//public static enum powerUpWhatIsInside {}
	
	// constants according to game difficulty --
	public final static int modifierSpeedSlowIfEasy = 1; // is this good practice?
	public final static int modifierSpeedSlowIfNormal = 2;
	public final static int modifierSpeedSlowIfHard = 3;
	public final static int modifierSpeedMediumIfEasy = 2;
	public final static int modifierSpeedMediumIfNormal = 4;
	public final static int modifierSpeedMediumIfHard = 6;
	public final static int modifierSpeedFastIfEasy = 4;
	public final static int modifierSpeedFastIfNormal = 8;
	public final static int modifierSpeedFastIfHard = 16;
	//
	public final static int hostileType1livesIfEasy = 1;
	public final static int hostileType1livesIfNormal = 2;
	public final static int hostileType1livesIfHard = 1;
	public final static int hostileType2livesIfEasy = 2;
	public final static int hostileType2livesIfNormal = 3;
	public final static int hostileType2livesIfHard = 1;
	public final static int hostileType3livesIfEasy = 3;
	public final static int hostileType3livesIfNormal = 3;
	public final static int hostileType3livesIfHard = 2;
	
	// constants used by modifiers to take effect 
}
