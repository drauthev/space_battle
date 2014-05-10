package server.game_elements;

abstract public class NPC extends ShootingObject {
	
	final int scoreIfDestroyed;
	final int verticalMoveQuantity;
	final int horizontalMoveQuantity;
	final int shootingFrequency;
	
	private long lastShotTime = 0;
	private long creationTime;
	
	NPC(int x, int y, int scoreIfDestroyed, int verticalMoveQuantity, int horizontalMoveQuantity, int shootingFrequency){
		super(x,y);
		creationTime = java.lang.System.currentTimeMillis();
		this.scoreIfDestroyed = scoreIfDestroyed;
		this.verticalMoveQuantity = verticalMoveQuantity;
		this.horizontalMoveQuantity = horizontalMoveQuantity;
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


	public int getVerticalMoveQuantity() {
		return verticalMoveQuantity;
	}


	public int getHorizontalMoveQuantity() {
		return horizontalMoveQuantity;
	}


	public void setCreationTime(long creationTime) {
		this.creationTime = creationTime;
	}
}