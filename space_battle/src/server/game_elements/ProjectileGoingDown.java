package server.game_elements;


public class ProjectileGoingDown extends Projectile {

	ProjectileGoingDown(int x, int y) {
		super(x,y);
	}
	
	public void autoMove(){
		super.setCoordY(super.getCoordY() + verticalMoveQuantity); // (x,y)=(0,0) is the top-left corner of gamespace
	}

	@Override
	public boolean isHit(GameElement object) { //TODO: lehet, hogy y coordot itt nem is kellene nezni, hanem csak azokra hivni meg, amikre okes
		// projectile-related
		int bottom = getCoordY() + projectileHeight/2;
		int projX = getCoordX();
		// object-related
		int objWidth = object.getHitBox().getWidth();
		int objHeight = object.getHitBox().getHeigth();
		int objX = object.getCoordX();
		int objY = object.getCoordY();
		
		if( bottom > objY - objHeight/2 && bottom < objY + objHeight/2){ // match in Y dimension
			if ( (projX > objX - objWidth/2) && (projX < objX + objWidth/2) )
				return true;
			else return false; // match in Y dim, but no match in X dimension
		}
		else return false; 
		
	}

}
