package server.game_elements;


public class ProjectileGoingDown extends Projectile {

	ProjectileGoingDown(int x, int y) {
		super(x,y);
	}
	
	@Override
	public void autoMove(){
		super.setCoordY(super.getCoordY() + super.getVerticalMoveQuantity()); // (x,y)=(0,0) is the top-left corner of gamespace
	}

	@Override
	public boolean isHit(GameElement object) { // TODO: oldal, teteje? ha blemaszik a palyer?
		// projectile-related
		int bottom = getCoordY() + super.getProjectileheight()/2;
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
