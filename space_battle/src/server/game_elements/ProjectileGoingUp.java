package server.game_elements;

public class ProjectileGoingUp extends Projectile {
	
	ProjectileGoingUp(double x, double y) {
		super(x,y);
	}
	
	@Override
	public void autoMove(){
		super.setCoordY(super.getCoordY() - super.getVerticalMoveQuantity()); // minus, because (x,y)=(0,0) is the top-left corner of gamespace
	}
	
	@Override
	public boolean isHit(GameElement object){
		// projectile-related
		int top = (int) (getCoordY() - super.getProjectileheight()/2);
		int projX = (int) getCoordX();
		// object-related
		int objWidth = object.getHitBox().getWidth();
		int objHeight = object.getHitBox().getHeigth();
		int objX = (int) object.getCoordX();
		int objY = (int) object.getCoordY();
		
		
		if( (projX > objX - objWidth/2) && (projX < objX + objWidth/2) ){ // projectile's coordX in range
			if( (top > objY - objHeight/2) && (top < objY + objHeight/2) ){
				return true;
			}
			else return false; // x range matches but no match in dimension Y
		}
		else return false;
	}

}
