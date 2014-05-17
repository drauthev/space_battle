package space_battle.server.game_elements;

/**
 * Abstract ancestor class of the three Hostile types: {@link HostileType1}, {@link HostileType2}, {@link HostileType3}.
 * @author daniel.szeifert
 * @version 1.0
 * @since 2014-05-17
 */
abstract public class NPC extends ShootingObject {

	private final int scoreIfDestroyed;
	private final double verticalMoveQuantity;
	private final double horizontalMoveQuantity;
	private final int shootingFrequency;	
	private long lastShotTime = 0;
	private long creationTime;
	
	/**
	 * 
	 * @param x coordinate X
	 * @param y coordinate Y
	 * @param scoreIfDestroyed set according to {@link space_battle.server.Constants}
	 * @param hostile1verticalmovequantity set according to {@link space_battle.server.Constants}
	 * @param hostile1horizontalmovequantity set according to {@link space_battle.server.Constants}
	 * @param shootingFrequency set according to {@link space_battle.server.Constants}
	 */
	NPC(int x, int y, int scoreIfDestroyed, double hostile1verticalmovequantity, double hostile1horizontalmovequantity, int shootingFrequency){
		super(x,y);
		creationTime = java.lang.System.currentTimeMillis();
		this.scoreIfDestroyed = scoreIfDestroyed;
		this.verticalMoveQuantity = hostile1verticalmovequantity;
		this.horizontalMoveQuantity = hostile1horizontalmovequantity;
		this.shootingFrequency = shootingFrequency;
	}

	// Getters, setters
	public long getCreationTime() {
		return creationTime;
	}
	
	public long getLastShotTime() {
		return lastShotTime;
	}

	public void setLastShotTime(long time) {
		this.lastShotTime = time;
	}

	public int getShootingFrequency() {
		return shootingFrequency;
	}


	public int getScoreIfDestroyed() {
		return scoreIfDestroyed;
	}


	public double getVerticalMoveQuantity() {
		return verticalMoveQuantity;
	}


	public double getHorizontalMoveQuantity() {
		return horizontalMoveQuantity;
	}


	public void setCreationTime(long creationTime) {
		this.creationTime = creationTime;
	}
}