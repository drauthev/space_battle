package client;

public class ObjectBuffer {
	public CNPC[] npc;
	public CPlayer[] player;
	public CProjectile[] proj;
	public CModifier[] mod;
	
	public long currentTick;
	public int score;
	
	ObjectBuffer(int npcs, int players, int projs, int mods)
	{
		npc = new CNPC[npcs];
		player = new CPlayer[players];
		proj = new CProjectile[projs];
		mod = new CModifier[mods];
	}
}
