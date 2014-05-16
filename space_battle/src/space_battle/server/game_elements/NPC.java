package space_battle.server.game_elements;

abstract public class NPC extends ShootingObject {
	
	final int scoreIfDestroyed;
	final double verticalMoveQuantity;
	final double horizontalMoveQuantity;
	final int shootingFrequency;
	
	private long lastShotTime = 0;
	private long creationTime;
	
	NPC(int x, int y, int scoreIfDestroyed, double hostile1verticalmovequantity, double hostile1horizontalmovequantity, int shootingFrequency){
		super(x,y);
		creationTime = java.lang.System.currentTimeMillis();
		this.scoreIfDestroyed = scoreIfDestroyed;
		this.verticalMoveQuantity = hostile1verticalmovequantity;
		this.horizontalMoveQuantity = hostile1horizontalmovequantity;
		this.shootingFrequency = shootingFrequency;
	}
	
	
	public long getCreationTime() {
		return creationTime;
	}
	
	// Getters, setters
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