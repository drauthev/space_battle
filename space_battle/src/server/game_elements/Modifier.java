package server.game_elements;

public abstract class Modifier extends NonShootingObject{
	// constants
	private final static int modifierWidth = 10;
	private final static int modifierHeigth = 10;
	//
	private long explosionTime;
	private long pickUpTime;	// to animate what's in it
	private long spawnTime;		// only changes if the modifier was created from another modifier by shooting it.. for animation reasons
	
	Modifier(int x, int y){
		super(x, y);
		super.setHitBox(new HitBox(modifierWidth, modifierHeigth, this));
		explosionTime = 0;
		pickUpTime = 0;
		spawnTime = 0;
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
		return spawnTime;
	}

	public void setSpawnTime(long spawnTime) {
		this.spawnTime = spawnTime;
	}

	public static int getModifierwidth() {
		return modifierWidth;
	}

	public static int getModifierheigth() {
		return modifierHeigth;
	}
	
	
}
