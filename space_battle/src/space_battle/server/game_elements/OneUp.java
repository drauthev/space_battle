package space_battle.server.game_elements;

import space_battle.server.Constants;

/**
 * A power up: if picked up, the player gets a plus life (for a maximum of five lives).
 * @author daniel.szeifert
 * @version 1.0
 * @since 2014-05-17
 */
public class OneUp extends Modifier {
	private static double verticalMoveQuantity = Constants.modifierSpeedFast;
		
	public OneUp(double x, double y){
		super(x,y);
	}
	/**
	 * {inheritDoc}
	 */
	@Override
	public void autoMove(){
		super.setCoordY(super.getCoordY() + verticalMoveQuantity);
	}

}
