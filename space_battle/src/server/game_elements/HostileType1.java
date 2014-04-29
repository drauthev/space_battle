package server.game_elements;

import server.Constants;
import enums.GameSkill;
import enums.GameType;

public class HostileType1 extends NPC {
	
	private final static int hostileType1Width = 34;
	private final static int hostileType1Heigth = 22;
	// static variables which depend on the game difficulty and modifiers
	private static int scoreIfDestroyed = 10;	// teszt; majd Controlból a beállított nehézségi szintre beállítani
	private static int verticalMoveQuantity = 1;
	private static int shootingFrequency = 2000;
	private static int spawningFrequency = 5000; // 5 secenként
	
	
	public HostileType1(int x, int y, GameSkill difficulty){
		super(x,y);
		super.setHitBox(new HitBox(hostileType1Width, hostileType1Heigth, this));
		// setting NPC's lives
		if(difficulty == GameSkill.EASY){
			super.setLives(Constants.hostileType1livesIfEasy);
		}
		else if(difficulty == GameSkill.NORMAL){
			super.setLives(Constants.hostileType1livesIfNormal);
		}
		else{
			super.setLives(Constants.hostileType1livesIfHard);
		}
	}
	
	public void autoMove(){
		super.setCoordY(super.getCoordY() + verticalMoveQuantity);
	}
	
	public Projectile shoot(){
		ProjectileGoingDown shot = new ProjectileGoingDown(0,0);
		return shot;
	}
	
	// Getters, setters
	// ------------------------------------------------------------------------------------------------------------------
	public static int getScoreIfDestroyed() {
		return scoreIfDestroyed;
	}
	
	public static void setScoreIfDestroyed(int scoreIfDestroyed) {
		HostileType1.scoreIfDestroyed = scoreIfDestroyed;
	}

	public static int getVerticalMoveQuantity() {
		return verticalMoveQuantity;
	}

	public static void setVerticalMoveQuantity(int verticalMoveQuantity) {
		HostileType1.verticalMoveQuantity = verticalMoveQuantity;
	}

	public static int getShootingFrequency() {
		return shootingFrequency;
	}

	public static void setShootingFrequency(int shootingFrequency) {
		HostileType1.shootingFrequency = shootingFrequency;
	}

	public static int getSpawningFrequency() {
		return spawningFrequency;
	}

	public static void setSpawningFrequency(int spawningFrequency) {
		HostileType1.spawningFrequency = spawningFrequency;
	}

	public static int getHostiletype1width() {
		return hostileType1Width;
	}

	public static int getHostiletype1heigth() {
		return hostileType1Heigth;
	}
		
}