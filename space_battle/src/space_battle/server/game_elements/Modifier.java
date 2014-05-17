package space_battle.server.game_elements;

import space_battle.server.Server;

/**
 * Game experience modifiers which can be picked up by players.
 * Or they can be hit, in that case two other {@link Modifier} appears.
 * @author daniel.szeifert
 * @version 1.0
 * @since 2014-05-17
 */
public abstract class Modifier extends GameElement{
	// constants
	private final static int modifierWidth = 40;
	private final static int modifierHeigth = 40;
	/**
	 * A TimerTask in {@link Server} is scheduled after the pickup, which cancels the power down's effect.
	 * It is scheduled for timeItLasts microseconds after pickup.
	 */
	private static int timeItLasts;
	/**
	 * Sent to GUI for animating the explosion.
	 */
	private long explosionTime = 0;
	/**
	 * Sent to GUI to animate what's in it.
	 */
	private long pickUpTime = 0;
	/**
	 * Only changes if the modifier was created from another modifier by shooting it.
	 * Sent to the GUI for animation purposes.
	 */
	private long creationTime = 0; 
	/**
	 * Constructor of {@link Modifier}. Sets the {@link HitBox} of the game element.
	 * @param x
	 * @param y
	 */
	Modifier(double x, double y){
		super(x, y);
		super.setHitBox(new HitBox(modifierWidth, modifierHeigth, this));
	}
	
	// Getters, setters
	public long getExplosionTime() {
		return explosionTime;
	}

	public void setExplosionTime(long explosionTime) {
		this.explosionTime = explosionTime;
	}

	public long getPickUpTime() {
		return pickUpTime;
	}

	public void setPickUpTime(long pickUpTime) {
		this.pickUpTime = pickUpTime;
	}

	public long getSpawnTime() {
		return creationTime;
	}

	public void setSpawnTime(long spawnTime) {
		this.creationTime = spawnTime;
	}

	public static int getModifierwidth() {
		return modifierWidth;
	}

	public static int getModifierheigth() {
		return modifierHeigth;
	}
	
	public long getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(long creationTime) {
		this.creationTime = creationTime;
	}

	public static int getTimeItLasts() {
		return timeItLasts;
	}

	public static void setTimeItLasts(int timeItLasts) {
		Modifier.timeItLasts = timeItLasts;
	}
	
	
}
