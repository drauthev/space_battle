package space_battle.client;

/**
 * This class holds the client side equivalents of the server objects. It contains fixed number
 * of objects to take advantage of object reuse and thus reduce the overhead which would come from
 * the continuous allocation-deallocation. 
 * 
 * @author András
 *
 */
public class ObjectBuffer {
	public static int maxNPC = 20;
	public static int maxProjectile = 40;
	public static int maxModifier = 20;
	
	public CNPC[] npc = new CNPC[maxNPC];
	public CPlayer[] player = new CPlayer[2];
	public CProjectile[] proj = new CProjectile[maxProjectile];
	public CModifier[] mod = new CModifier[maxModifier];
	
	/**
	 * Server tick when this ObjectBuffer got assembled.
	 */
	public long currentTick;
	
	/**
	 * Actual score.
	 */
	public int score;
	
	/**
	 * Number of valid NPCs.
	 */
	public int npcCount;
	
	/**
	 * Number of valid players.
	 */
	public int playerCount;
	
	/**
	 * Number of valid projectiles.
	 */
	public int projCount;
	
	/**
	 * Number of valid modifiers.
	 */
	public int modCount;
}
