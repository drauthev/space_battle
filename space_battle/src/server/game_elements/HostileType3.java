package server.game_elements;

import server.Constants;
import enums.GameSkill;

public class HostileType3 extends NPC {
	
	// constants
	private final static int hostileType3Width = 54;
	private final static int hostileType3Heigth = 35;
	// static variables which depend on the game difficulty and modifiers
	private static int scoreIfDestroyed = 50;	// majd Controlb�l a be�ll�tott neh�zs�gi szintre be�ll�tani
	
	private static int verticalMoveQuantity = 1;	// egyenletesen lefele, viszont v�zszintesen random helyre teleport�l
	
	private static int shootingFrequency; //double ink�bb?
	private static int spawningFrequency; //double ink�bb?
	
	HostileType3(int x, int y, GameSkill difficulty){
		super(x,y);
		super.setHitBox(new HitBox(hostileType3Width, hostileType3Heigth, this));
		// setting NPC's lives
		if(difficulty == GameSkill.EASY){
			super.setLives(Constants.hostileType3livesIfEasy);
		}
		else if(difficulty == GameSkill.NORMAL){
			super.setLives(Constants.hostileType3livesIfNormal);
		}
		else{
			super.setLives(Constants.hostileType3livesIfHard);
		}
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