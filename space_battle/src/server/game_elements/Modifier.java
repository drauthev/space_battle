package server.game_elements;

public abstract class Modifier extends NonShootingObject{
	// constants
	private final static int modifierWidth = 40;
	private final static int modifierHeigth = 40;
	//
	private long explosionTime = 0;
	private long pickUpTime = 0;	// to animate what's in it
	private long creationTime = 0;		// only changes if the modifier was created from another modifier by shooting it.. for animation reasons
	
	Modifier(int x, int y){
		super(x, y);
		super.setHitBox(new HitBox(modifierWidth, modifierHeigth, this));
		explosionTime = 0;
		pickUpTime = 0;
		creationTime = 0;
	}
	
	public abstract void autoMove();
	
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
	
	
}
