package space_battle.server.game_elements;

import space_battle.server.Constants;

/**
 * A power up: if picked up, player gets a shield around it, defending it from projectiles.
 * Shield does not defend players from collision with hostiles. In that case, shield is removed and the player's lives are decreased by one.
 * @author daniel.szeifert
 * @version 1.0
 * @since 2014-05-17
 */
public class Shield extends Modifier{
	private static double verticalMoveQuantity = Constants.modifierSpeedFast;
		
	public Shield(double x, double y){
		super(x,y);
		super.setTimeItLasts(20000);
	}
	/**
	 * {inheritDoc}
	 */
	@Override
	public void autoMove(){
		super.setCoordY(super.getCoordY() + verticalMoveQuantity);
	}
	
}
