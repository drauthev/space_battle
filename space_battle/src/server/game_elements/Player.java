package server.game_elements;

import server.Constants;

public class Player extends ShootingObject {
	
	// constants
	private final static int playerWidth = 34;
	private final static int playerHeight = 42;
	// static variables changing according to game difficulty/modifiers
	private static int horizontalMoveQuantity = 5;
	private static int horizontalMoveQuantityIfFastened = 10;
	private static int playerLivesAtStart = 3;
	//
	private int ID;	// for player identification in case of Multi-player mode
	private int lives;
	//
	private boolean isFastened = false;
	private int timeBetweenShots = Constants.timeBetweenShots; // will be less if player is fastened
	private long lastShootTime = 0;
	private boolean isShielded;
	private boolean hasAmmo = true;
	private boolean leftRighSwitched = false;
	private boolean hasLaser; //TODO: ki lehet cselezni, ha sokszor losz; SOLUTION lehet: timeBetweenShots-ot magasabbra állítani ezido alatt, mint a LAser lastingja
	
	public Player(int x, int y, int ID){
		super(x,y);
		super.setHitBox(new HitBox(playerWidth, playerHeight, this));
		this.ID = ID;
		lives = playerLivesAtStart;
	}
	
	public void moveLeft(){
		if(leftRighSwitched){ // moving right..
			if(isFastened){
				if(this.getCoordX() + horizontalMoveQuantityIfFastened + playerWidth/2 <= Constants.gameFieldWidth-1) // only if there is enough space at the right edge
					this.setCoordX(this.getCoordX() + horizontalMoveQuantityIfFastened);
			}
			else{
				if(this.getCoordX() + horizontalMoveQuantity + playerWidth/2 <= Constants.gameFieldWidth-1) // only if there is enough space at the right edge
					this.setCoordX(this.getCoordX() + horizontalMoveQuantity);
			}	
		}
		else{ // normal operation
			if(isFastened){
				if (this.getCoordX() - horizontalMoveQuantityIfFastened >= 0 + playerWidth/2)	// only if there is enough space at the left edge
					this.setCoordX(this.getCoordX() - horizontalMoveQuantityIfFastened);
			}
			else{
				if (this.getCoordX() - horizontalMoveQuantity >= 0 + playerWidth/2)	// only if there is enough space at the left edge
					this.setCoordX(this.getCoordX() - horizontalMoveQuantity);
			}			
		}
	}
	
	public void moveRight(){
		if(leftRighSwitched){ // moving left..
			if(isFastened){
				if (this.getCoordX() - horizontalMoveQuantityIfFastened >= 0 + playerWidth/2)	// only if there is enough space at the left edge
					this.setCoordX(this.getCoordX() - horizontalMoveQuantityIfFastened);
			}
			else{
				if (this.getCoordX() - horizontalMoveQuantity >= 0 + playerWidth/2)	// only if there is enough space at the left edge
					this.setCoordX(this.getCoordX() - horizontalMoveQuantity);
			}		
		}
		else{ // normal operation..
			if(isFastened){
				if(this.getCoordX() + horizontalMoveQuantityIfFastened + playerWidth/2 <= Constants.gameFieldWidth-1) // only if there is enough space at the right edge
					this.setCoordX(this.getCoordX() + horizontalMoveQuantityIfFastened);
			}
			else{
				if(this.getCoordX() + horizontalMoveQuantity + playerWidth/2 <= Constants.gameFieldWidth-1) // only if there is enough space at the right edge
					this.setCoordX(this.getCoordX() + horizontalMoveQuantity);
			}	
		}
	}
	
	public Projectile shoot(){
		if(hasLaser){
			ProjectileLaser shot = new ProjectileLaser(this.getCoordX(), this.getCoordY() - Player.playerHeight/2 - Projectile.getProjectileheight()/2, this);
			return shot;
		}
		else{
			ProjectileGoingUp shot = new ProjectileGoingUp(this.getCoordX(), this.getCoordY() - Player.playerHeight/2 - Projectile.getProjectileheight()/2);
			return shot;
		}
	}
	
	//
	public void autoMove(){}	// empty on purpose
	
	// Getters, setters
	// ------------------------------------------------------------------------------------------------------------------
	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public int getLives() {
		return lives;
	}
	
	public void setLives(int lives) {
		this.lives = lives;
	}

	public static int getHorizontalMoveQuantity() {
		return horizontalMoveQuantity;
	}

	public static void setHorizontalMoveQuantity(int horizontalMoveQuantity) {
		Player.horizontalMoveQuantity = horizontalMoveQuantity;
	}

	public static int getPlayerLivesAtStart() {
		return playerLivesAtStart;
	}

	public static void setPlayerLivesAtStart(int playerLivesAtStart) {
		Player.playerLivesAtStart = playerLivesAtStart;
	}

	public static int getPlayerwidth() {
		return playerWidth;
	}

	public static int getPlayerheight() {
		return playerHeight;
	}

	public void setFastened(boolean isFastened) {
		this.isFastened = isFastened;
	}

	public boolean isFastened() {
		return isFastened;
	}

	public int getTimeBetweenShots() {
		return timeBetweenShots;
	}

	public void setTimeBetweenShots(int timeBetweenShots) {
		this.timeBetweenShots = timeBetweenShots;
	}

	public boolean isShielded() {
		return isShielded;
	}

	public void setShielded(boolean isShielded) {
		this.isShielded = isShielded;
	}

	public boolean isHasLaser() {
		return hasLaser;
	}

	public void setHasLaser(boolean hasLaser) {
		this.hasLaser = hasLaser;
	}

	public boolean isHasAmmo() {
		return hasAmmo;
	}

	public void setHasAmmo(boolean hasAmmo) {
		this.hasAmmo = hasAmmo;
	}

	public boolean isLeftRighSwitched() {
		return leftRighSwitched;
	}

	public void setLeftRighSwitched(boolean leftRighSwitched) {
		this.leftRighSwitched = leftRighSwitched;
	}

	public long getLastShootTime() {
		return lastShootTime;
	}

	public void setLastShootTime(long lastShootTime) {
		this.lastShootTime = lastShootTime;
	}

	
}