package space_battle.server.game_elements;

import space_battle.server.Constants;
/**
 * Abstract ancestor of the five power downs: {@link HostileFrenzy}, {@link HalfScores}, {@link LeftRightSwitcher}, {@link NoAmmo}, {@link SpaceShipSwitcher}.
 * (PowerDowns have the same {@link #verticalMoveQuantity}, power ups ( {@link Boom}, {@link Fastener}, {@link Laser}, {@link Shield}, {@link OneUp} ) doesn't, that is why they don't have an ancestor class.)
 * @author daniel.szeifert
 * @version 1.0
 * @since 2014-05-17
 */
public abstract class PowerDown extends Modifier {
	
	private static double verticalMoveQuantity = Constants.modifierSpeedFast;	// all powerdowns move fast
	
	/**
	 * @param x Coordinate X of spawning.
	 * @param y Coordinate Y of spawning.
	 */
	PowerDown(double x, double y){
		super(x, y);
	}

	public void autoMove(){
		super.setCoordY(super.getCoordY() + verticalMoveQuantity);
	}
	
}
