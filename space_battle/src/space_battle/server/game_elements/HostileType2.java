package space_battle.server.game_elements;

import space_battle.enums.GameSkill;
import space_battle.server.Constants;

public class HostileType2 extends NPC {
	
	// this hostileType is going down in zig-zag around this axis
	private final static int diffFromAxis = 30; // max diff from axis before turning around
	private int axisX;
	// booleans for indicating whether the Hostile has switched moving direction yet or not
	private boolean turntRight = false;
	private boolean turntLeft = false;
		
	public HostileType2(int x, int y, GameSkill difficulty){
		super(x,y, Constants.hostile2scoreIfDestroyed, Constants.hostile2verticalMoveQuantity, Constants.hostile2horizontalMoveQuantity,Constants.hostile2shootingFrequency);
		super.setHitBox(new HitBox(Constants.hostile2Width, Constants.hostile2Height, this));
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
		axisX = x; // axis will be the X coordinate at the spawning event
	}
	
	public void autoMove(){
		double x = super.getCoordX();
		super.setCoordY(super.getCoordY() + verticalMoveQuantity);
		if( x <= axisX ){ // on the left side of axis
			turntLeft = false;
			// moving left IF: the axis let it, the gameField let it, and Hostile hasn't turnt right yet
			if( (x-horizontalMoveQuantity) > (axisX-diffFromAxis) && turntRight==false && (x-horizontalMoveQuantity)>(0+Constants.hostile2Width/2)){
				super.setCoordX(x - horizontalMoveQuantity);
			}
			else{ // turn right
				turntRight = true;
				super.setCoordX(x + horizontalMoveQuantity);
			}
		}
		else{ // on the right side of axis
			turntRight = false;
			// moving right IF: the axis let it, the gameField let it, and Hostile hasn't turnt left yet
			if( (x+horizontalMoveQuantity) < (axisX+diffFromAxis) && turntLeft==false && (x+horizontalMoveQuantity)<(Constants.gameFieldWidth-Constants.hostile2Width/2) ){
				super.setCoordX(x + horizontalMoveQuantity);
			}
			else{ // turn left
				turntLeft = true;
				super.setCoordX(x - horizontalMoveQuantity);
			}
		}
	}
	
	public Projectile shoot(){
		ProjectileGoingDown dummy = new ProjectileGoingDown(this.getCoordX(), this.getCoordY() + Constants.hostile2Height/2 + Projectile.getProjectileheight()/2);
		return dummy;
	}
	
	public Projectile shootDiagonallyLeft(){
		ProjectileGoingDiagonallyLeft shot = new ProjectileGoingDiagonallyLeft(this.getCoordX(), this.getCoordY() + Constants.hostile2Height/2 + Projectile.getProjectileheight()/2);
		return shot;
	}
	
	public Projectile shootDiagonallyRight(){
		ProjectileGoingDiagonallyRight shot = new ProjectileGoingDiagonallyRight(this.getCoordX(), this.getCoordY() + Constants.hostile2Height/2 + Projectile.getProjectileheight()/2);
		return shot;
	}

	// Getters, setters
	// ---------------------------
	public boolean isTurntRight() {
		return turntRight;
	}

	public void setTurntRight(boolean turntRight) {
		this.turntRight = turntRight;
	}

	public boolean isTurntLeft() {
		return turntLeft;
	}

	public void setTurntLeft(boolean turntLeft) {
		this.turntLeft = turntLeft;
	}
	
	

		
	
	
}