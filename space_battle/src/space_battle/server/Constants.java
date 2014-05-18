package space_battle.server;
/**
 * Class containing global constants.
 * Most of these constants are parameters changing the game experience.
 * @author daniel.szeifert
 * @version 1.0
 * @since 2014-05-17
 */
public class Constants {
	public final static int framePerSecond = 30;
    public final static int gameFieldWidth = 480;
    public final static int gameFieldHeigth = 640;
    
    public final static int timeBetweenShots = 400;
    public final static int timeBetweenShotsIfFastened = 300;
    
    // Hostile-related constants
    public final static int hostile3TeleportFrequency = 3000;
    public final static int hostile3timeBetweenBurstShots = 8000;
    public final static int hostile3burstShootingFrequency = 500;
    //
    public final static int hostile1shootingFrequency = 2000;
    public final static int hostile2shootingFrequency = 4000;
    //
    public final static int hostile1spawningFrequency = 7000;
    public final static int hostile2spawningFrequency = 10000;
    public final static int hostile3spawningFrequency = 5000;
    //
    public final static int hostile1scoreIfDestroyed = 10;
    public final static int hostile2scoreIfDestroyed = 20;
    public final static int hostile3scoreIfDestroyed = 50;
    //
    public final static int hostile1Width = 34;
    public final static int hostile2Width = 49;
    public final static int hostile3Width = 54;
    //
    public final static int hostile1Height = 22;
    public final static int hostile2Height = 32;
    public final static int hostile3Height = 35;
    //
    public final static double hostile1verticalMoveQuantity = 2;
    public final static double hostile2verticalMoveQuantity = 1.5;
    public final static double hostile3verticalMoveQuantity = 1;
    //
    public final static double hostile1horizontalMoveQuantity = 0;
    public final static double hostile2horizontalMoveQuantity = 1;
    public final static double hostile3horizontalMoveQuantity = 0;
    //
	public final static int hostileType1livesIfEasy = 1;
	public final static int hostileType1livesIfNormal = 2;
	public final static int hostileType1livesIfHard = 2;
	//
	public final static int hostileType2livesIfEasy = 1;
	public final static int hostileType2livesIfNormal = 2;
	public final static int hostileType2livesIfHard = 3;
	//
	public final static int hostileType3livesIfEasy = 2;
	public final static int hostileType3livesIfNormal = 2;
	public final static int hostileType3livesIfHard = 3;
	
	// modifier-related consts
	public final static double modifierSpeedSlow = 2;
	public final static double modifierSpeedMedium = 4;
	public final static double modifierSpeedFast = 6;

}
