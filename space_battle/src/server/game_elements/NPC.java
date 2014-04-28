package server.game_elements;

abstract public class NPC extends ShootingObject {
	
	private long creationTime;
	
	NPC(int x, int y){
		super(x,y);
		creationTime = java.lang.System.currentTimeMillis();
	}
	
	
	public long getCreationTime() {
		return creationTime;
	}
	
}