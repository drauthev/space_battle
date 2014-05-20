package space_battle.server.game_elements;

import space_battle.server.Constants;

/**
 * A power up: if picked up, players move and shoot faster for 10 sec.
 * @author daniel.szeifert
 * @version 1.0
 * @since 2014-05-17
 */
public class Fastener extends Modifier {
	
	private static double verticalMoveQuantity = Constants.modifierSpeedSlow;
		
	public Fastener(double x, double y){
		super(x,y);
		super.setTimeItLasts(10000);
	}

	@Override
	public void autoMove(){
		super.setCoordY(super.getCoordY() + verticalMoveQuantity);
	}
	
}
