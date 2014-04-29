package server.game_elements;

public class ProjectileGoingUp extends Projectile {
	
	ProjectileGoingUp(int x, int y) {
		super(x,y);
	}
	
	public void autoMove(){
		super.setCoordY(super.getCoordY() - verticalMoveQuantity); // minus, because (x,y)=(0,0) is the top-left corner of gamespace
	}
	
	@Override
	public boolean isHit(GameElement object){
		// projectile-related
		int top = getCoordY() + projectileHeight;
		int projX = getCoordX();
		// object-related
		int objWidth = object.getHitBox().getWidth();
		int objHeight = object.getHitBox().getHeigth();
		int objX = object.getCoordX();
		int objY = object.getCoordY();
		
		
		if( (projX > objX - objWidth/2) && (projX < objX + objWidth/2) ){ // projectile's coordX in range
			if( (top > objY - objHeight/2) && (top < objY - objHeight/2) ){
				return true;
			}
			else return false; // x range matches but no match in dimension Y
		}
		else return false;
	}

}
