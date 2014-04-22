package server.game_elements;

public abstract class ShootingObject extends GameElement {
	
	private int lives;
	private long hitTime;
	private long explosionTime;
	
	ShootingObject(int x, int y){
		super(x,y);
		hitTime = 0;
		explosionTime = 0;
	}
	
	public abstract Projectile shoot();
	
	// Getters, setters
	public int getLives() {
		return lives;
	}
	public void setLives(int lives) {
		this.lives = lives;
	}

	public long getHitTime() {
		return hitTime;
	}

	public void setHitTime(long hitTime) {
		this.hitTime = hitTime;
	}

	public long getExplosionTime() {
		return explosionTime;
	}

	public void setExplosionTime(long explosionTime) {
		this.explosionTime = explosionTime;
	}
	
}