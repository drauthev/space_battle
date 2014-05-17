package space_battle.server.game_elements;

/**
 * Class of projectiles shot by the {@link space_battle.server.game_elements.Player} class.
 * @author daniel.szeifert
 * @version 1.0
 * @since 2014-05-17
 */
public class ProjectileGoingUp extends Projectile {
	/**
	 * @param x Coordinate X of spawning.
	 * @param y Coordinate Y of spawning.
	 */
	ProjectileGoingUp(double x, double y) {
		super(x,y);
	}
	/**
	 * {inheritDoc}
	 */
	@Override
	public void autoMove(){
		super.setCoordY(super.getCoordY() - super.getVerticalMoveQuantity()); // minus, because (x,y)=(0,0) is the top-left corner of gamespace
	}
	/**
	 * {inheritDoc}
	 */
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
