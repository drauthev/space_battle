package space_battle.server.game_elements;

/**
 * A power down, only available if there are two players.
 * If picked up, each player controls the other's spaceship for 10 sec.
 * @author daniel.szeifert
 * @version 1.0
 * @since 2014-05-17
 */
public class SpaceShipSwitcher extends PowerDown {
	
	public SpaceShipSwitcher(double x, double y){
		super(x, y);
		super.setTimeItLasts(10000);
	}

}
