package space_battle.server.game_elements;

import space_battle.server.Constants;

/**
 * Class representing the spaceships controlled by the players.
 * @author daniel.szeifert
 * @version 1.0
 * @since 2014-05-17
 */
public class Player extends ShootingObject {
	
	private final static int playerWidth = 34;
	private final static int playerHeight = 42;
	// static variables changing according to game difficulty/modifiers
	private static int horizontalMoveQuantity = 5;
	private static int horizontalMoveQuantityIfFastened = 10;
	private static int playerLivesAtStart = 3;
	//
	/**
	 * ID for player identification in case of Multi-player mode. 0 for Player1, 1 for Player2.
	 */
	private int ID;
	private int lives;
	//
	
	/**
	 * This is checked by the game logic and prevents the too frequent shooting. Decreased if player is fastened.
	 */
	private int timeBetweenShots = Constants.timeBetweenShots;
	private long lastShootTime = 0;
	/**
	 * Flags indicating a certain modifier's effect towards the Server (game logic).
	 */
	private boolean isFastened = false;
	private boolean isShielded;
	private boolean hasAmmo = true;
	private boolean leftRighSwitched = false;
	private boolean hasLaser;
	
	public Player(int x, int y, int ID){
		super(x,y);
		super.setHitBox(new HitBox(playerWidth, playerHeight, this));
		this.ID = ID;
		lives = playerLivesAtStart;
	}
	
	/**
	 * Moving the player left with the amount according to the current state (normal or Fastened).
	 * Or moving right if the LeftRightSwitcher modifier is active on the player.
	 */
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
	
	/**
	 * @see #moveLeft()
	 * This does the opposite.
	 */
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
	/**
	 * @return An instance of {@link space_battle.server.game_elements.ProjectileGoingUp} with the coordinates of the top of the player. 
	 */
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
	
	/**
	 * {@inheritDoc GameElement.autoMove()}
	 * This method is empty on purpose, as the {@link space_battle.server.game_elements.Player} class is controlled by the player.
	 */
	public void autoMove(){}
	
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