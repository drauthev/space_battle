package server.game_elements;

public class ProjectileLaser extends Projectile {
		
	private Player whoShotThis;  // ProjectileLaser always "follows" the player who shot it
	private long shootTime;
	
	ProjectileLaser(int x, int y, Player whoShotThis) {
		super(x,y);
		this.whoShotThis = whoShotThis;
		shootTime = java.lang.System.currentTimeMillis();
	}
	
	public void autoMove(){
		super.setCoordX(whoShotThis.getCoordX());
		//super.setCoordX(whoShotThis.getCoordY()); //TODO: playertol a palya tetejeig tartson
	}
	
	@Override
	public boolean isHit(GameElement object){
		//TODO:!!! vastagabb sav lesz, akkor nem egy pixelt kene nezni
		int projX = getCoordX();
		// object-related
		int objWidth = object.getHitBox().getWidth();
		int objHeight = object.getHitBox().getHeigth();
		int objX = object.getCoordX();
		int objY = object.getCoordY();
		
		if( (projX > objX - objWidth/2) && (projX < objX + objWidth/2) ){ // projectile's coordX in range
			return true;
		}
		else 
			return false;
	}

	public long getShootTime() {
		return shootTime;
	}

	public void setShootTime(long shootTime) {
		this.shootTime = shootTime;
	}
}
