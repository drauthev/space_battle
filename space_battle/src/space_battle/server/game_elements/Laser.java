package space_battle.server.game_elements;

import space_battle.server.Constants;

/**
 * A power up: if picked up, players shoot {@link ProjectileLaser} projectiles for 10 sec.
 * These can destroy all kind of hostiles in one hit, and doesn't get destroyed after a hit either.
 * @author daniel.szeifert
 * @version 1.0
 * @since 2014-05-17
 */
public class Laser extends Modifier{
	private static double verticalMoveQuantity = Constants.modifierSpeedMedium;
		
	public Laser(double x, double y){
		super(x,y);
		super.setTimeItLasts(6000);
	}

	@Override
	public void autoMove(){
		super.setCoordY(super.getCoordY() + verticalMoveQuantity);
	}

}
