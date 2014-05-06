package server;

public class Constants {
	public static int framePerSecond = 30;
    public static int gameFieldWidth = 480;
    public static int gameFieldHeigth = 640;
    
    public final static int timeBetweenShots = 600; //1000-rel sztem nehez
    public final static int timeBetweenShotsIfFastened = 300;
    
    // Hostile-related constants
    public final static int hostile3TeleportFrequency = 3000;
    public final static int hostile3timeBetweenBurstShots = 8000;
    public final static int hostile3burstShootingFrequency = 500;
    //
    public final static int hostile1shootingFrequency = 2000;
    public final static int hostile2shootingFrequency = 2000;
    //
    public final static int hostile1spawningFrequency = 7000;
    public final static int hostile2spawningFrequency = 10000; //should be 10000
    public final static int hostile3spawningFrequency = 5000;
    //
    public final static int hostile1scoreIfDestroyed = 10;
    public final static int hostile2scoreIfDestroyed = 20;
    public final static int hostile3scoreIfDestroyed = 50;
    //
    public final static int hostile1Width = 34;
    public final static int hostile2Width = 49; //TODO. negyzet alaku hitbox nem az igazi :/
    public final static int hostile3Width = 54;
    //
    public final static int hostile1Height = 22;
    public final static int hostile2Height = 32;
    public final static int hostile3Height = 35;
    //
    public final static int hostile1verticalMoveQuantity = 1;
    public final static int hostile2verticalMoveQuantity = 1;
    public final static int hostile3verticalMoveQuantity = 1;
    //
    public final static int hostile1horizontalMoveQuantity = 0;
    public final static int hostile2horizontalMoveQuantity = 1;
    public final static int hostile3horizontalMoveQuantity = 0;
    
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
	public final static int modifierSpawnFreqFrequentIfEasy = 5000;
	public final static int modifierSpawnFreqFrequentIfNormal = 5000;
	public final static int modifierSpawnFreqFrequentIfHard = 5000;
	public final static int modifierSpawnFreqMediumIfEasy = 5000;
	public final static int modifierSpawnFreqMediumIfNormal = 5000;
	public final static int modifierSpawnFreqMediumIfHard = 5000;
	public final static int modifierSpawnFreqRareIfEasy = 5000;
	public final static int modifierSpawnFreqRareIfNormal = 5000;
	public final static int modifierSpawnFreqRareIfHard = 5000;
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
