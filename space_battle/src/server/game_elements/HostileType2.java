package server.game_elements;

import server.Constants;
import enums.GameSkill;

public class HostileType2 extends NPC {
	
	// constants
	private final static int hostileType2Width = 49;
	private final static int hostileType2Heigth = 32;
	// static variables which depend on the game difficulty and modifiers
	private static int scoreIfDestroyed = 20;	// majd Controlb�l a be�ll�tott neh�zs�gi szintre be�ll�tani
	
	private static int verticalMoveQuantity = 1;	// biztos kellenek ezek? "sinus ment�n megy"..
	private static int horizontalMoveQuantity = 1;
	
	private static int shootingFrequency = 3000; //double ink�bb?
	private static int spawningFrequency = 5000; //double ink�bb?
	
	HostileType2(int x, int y, GameSkill difficulty){
		super(x,y);
		super.setHitBox(new HitBox(hostileType2Width, hostileType2Heigth, this));
		// setting NPC's lives
		if(difficulty == GameSkill.EASY){
			super.setLives(Constants.hostileType2livesIfEasy);
		}
		else if(difficulty == GameSkill.NORMAL){
			super.setLives(Constants.hostileType2livesIfNormal);
		}
		else{
			super.setLives(Constants.hostileType2livesIfHard);
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