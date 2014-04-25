package client;

public class ObjectBuffer {
	public static int maxNPC = 20;
	public static int maxProjectile = 20;
	public static int maxModifier = 10;
	
	public CNPC[] npc = new CNPC[maxNPC];
	public CPlayer[] player = new CPlayer[2];
	public CProjectile[] proj = new CProjectile[maxProjectile];
	public CModifier[] mod = new CModifier[maxModifier];
	
	public long currentTick;
	public int score;
	
	/*
	ObjectBuffer(int npcs, int players, int projs, int mods)
	{
		npc = new CNPC[npcs];
		player = new CPlayer[players];
		proj = new CProjectile[projs];
		mod = new CModifier[mods];
	}
	*/
}
