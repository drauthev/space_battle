package space_battle.server.game_elements;

public class ProjectileGoingDiagonallyLeft extends Projectile {
	
	private static int horizontalMoveQuantity = 1;
	
	ProjectileGoingDiagonallyLeft(double x, double y) {
		super(x,y);
	}
	
	@Override
	public void autoMove(){
		super.setCoordX(super.getCoordX() - horizontalMoveQuantity);
		super.setCoordY(super.getCoordY() + super.getVerticalMoveQuantity()); // (x,y)=(0,0) is the top-left corner of gamespace
	}

	@Override
	public boolean isHit(GameElement object) { //TODO!!
		// projectile-related
		int bottom = (int) (getCoordY() + super.getProjectileheight()/2);
		int projX = (int) getCoordX();
		// object-related
		int objWidth = object.getHitBox().getWidth();
		int objHeight = object.getHitBox().getHeigth();
		int objX = (int) object.getCoordX();
		int objY = (int) object.getCoordY();
		
		if( bottom > objY - objHeight/2 && bottom < objY + objHeight/2){ // match in Y dimension
			if ( (projX > objX - objWidth/2) && (projX < objX + objWidth/2) )
				return true;
			else return false; // match in Y dim, but no match in X dimension
		}
		else return false; 
		
	}
}
