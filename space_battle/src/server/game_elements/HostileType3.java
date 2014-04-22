package server.game_elements;

public class HostileType3 extends NPC {
	
	// constants
	private final static int hostileType3Width = 10;
	private final static int hostileType3Heigth = 10;
	// static variables which depend on the game difficulty and modifiers
	private static int scoreIfDestroyed = 50;	// majd Controlb�l a be�ll�tott neh�zs�gi szintre be�ll�tani
	
	private static int verticalMoveQuantity = 1;	// egyenletesen lefele, viszont v�zszintesen random helyre teleport�l
	
	private static int shootingFrequency; //double ink�bb?
	private static int spawningFrequency; //double ink�bb?
	
	HostileType3(int x, int y){
		super(x,y);
		super.setHitBox(new HitBox(hostileType3Width, hostileType3Heigth, this));
	}
	
	public void autoMove(){
		//TODO
	}
	
	public Projectile shoot(){
		ProjectileGoingDown dummy = new ProjectileGoingDown(0,0);
		return dummy;
	}
	
	// Getters, setters

	
	
}