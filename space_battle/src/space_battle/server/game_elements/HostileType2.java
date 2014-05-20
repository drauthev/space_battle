package space_battle.server.game_elements;

import space_battle.enums.GameSkill;
import space_battle.server.Constants;
/**
 * Hostile which goes in a zig-zag movement towards the bottom of the screen.
 * Shoots three projectiles at a time: a {@link ProjectileGoingDown}, a {@link ProjectileGoingDiagonallyLeft} and a {@link ProjectileGoingDiagonallyRight}.
 * @author daniel.szeifert
 * @version 1.0
 * @since 2014-05-17
 */
public class HostileType2 extends NPC {
	/**
	 * Axis of the zig-zag movement. The spawning X coordinate is stored in this field.
	 */
	private int axisX;
	/**
	 * Maximum diff from {@link #axisX} before turning around.
	 */
	private final static int diffFromAxis = 30;
	/**
	 * Boolean for indicating whether the hostile has switched from moving left to right.
	 * Used by {@link #autoMove()}
	 */
	private boolean turntRight = false;
	/**
	 * Boolean for indicating whether the hostile has switched from moving right to left.
	 * Used by {@link #autoMove()}
	 */
	private boolean turntLeft = false;
	
	/**
	 * 
	 * @param x Coordinate X of spawning.
	 * @param y Coordinate Y of spawning.
	 * @param difficulty Number of lives of the hostile according to difficulty stored in {@link space_battle.server.Server}.
	 */
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
	/**
	 * 
	 * {@inheritDoc}
	 * 
	 * <p> {@link HostileType2} moves down from the spawning coordinates with the following horizontal motion:
	 * it starts moving left to an amplitude of {@link #diffFromAxis}, then turns right, goes till it reaches {@link #axisX} + {@link #diffFromAxis}, turn left again, etc.
	 */
	@Override
	public void autoMove(){
		double x = super.getCoordX();
		double horizontalMoveQuantity = super.getHorizontalMoveQuantity();
		super.setCoordY(super.getCoordY() + super.getVerticalMoveQuantity());
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

	@Override
	public Projectile shoot(){
		ProjectileGoingDown dummy = new ProjectileGoingDown(this.getCoordX(), this.getCoordY() + Constants.hostile2Height/2 + Projectile.getProjectileheight()/2);
		return dummy;
	}
	/**
	 * Is called periodically by {@link space_battle.server.Server} (game logic)
	 * @return An instance of {@link ProjectileGoingDiagonallyLeft} with coordinates of the bottom of the hostile instance.
	 */
	public Projectile shootDiagonallyLeft(){
		ProjectileGoingDiagonallyLeft shot = new ProjectileGoingDiagonallyLeft(this.getCoordX(), this.getCoordY() + Constants.hostile2Height/2 + Projectile.getProjectileheight()/2);
		return shot;
	}
	/**
	 * Is called periodically by {@link space_battle.server.Server} (game logic)
	 * @return An instance of {@link ProjectileGoingDiagonallyRight} with coordinates of the bottom of the hostile instance.
	 */
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