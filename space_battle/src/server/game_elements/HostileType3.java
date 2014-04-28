package server.game_elements;

import server.Constants;
import enums.GameSkill;

public class HostileType3 extends NPC {
	
	// constants
	private final static int hostileType3Width = 10;
	private final static int hostileType3Heigth = 10;
	// static variables which depend on the game difficulty and modifiers
	private static int scoreIfDestroyed = 50;	// majd Controlból a beállított nehézségi szintre beállítani
	
	private static int verticalMoveQuantity = 1;	// egyenletesen lefele, viszont vízszintesen random helyre teleportál
	
	private static int shootingFrequency; //double inkább?
	private static int spawningFrequency; //double inkább?
	
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