package space_battle.server.game_elements;

import space_battle.server.Constants;

/**
 * A power up: if picked up, all hostiles get destroyed on the screen.
 * @author daniel.szeifert
 * @version 1.0
 * @since 2014-05-17
 */
public class Boom extends Modifier{
	
	private static double verticalMoveQuantity = Constants.modifierSpeedFast;
		
	public Boom(double x, double y){
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
