package space_battle.server.game_elements;

/**
 * Abstract class, ancestor of {@link space_battle.server.game_elements.Player}, {@link HostileType1}, {@link HostileType2} and {@link HostileType3}.
 * @author daniel.szeifert
 * @version 1.0
 * @since 2014-05-17
 */
public abstract class ShootingObject extends GameElement {
	/**
	 * Number of hits needed till explosion. Varied with the several child classes.
	 */
	private int lives;
	/**
	 * Sent to the GUI for the purpose of a flashing effect of the sprite.
	 */
	private long hitTime;
	/**
	 * Sent to the GUI for animating the explosion.
	 */
	private long explosionTime;
	/**
	 * Constructor of {@link ShootingObject}, sets {@link #hitTime} and {@link #explosionTime} 
	 * to 0, which means the object has never got hit or exploded.
	 * @param x Coordinate X of the place of spawning.
	 * @param y Coordinate Y of the place of spawning.
	 */
	ShootingObject(int x, int y){
		super(x,y);
		hitTime = 0;
		explosionTime = 0;
	}
	/**
	 * 
	 * @return One object of one of the child classes of {@link Projectile}.
	 */
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