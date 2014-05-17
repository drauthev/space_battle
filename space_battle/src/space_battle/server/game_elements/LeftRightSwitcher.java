package space_battle.server.game_elements;

/**
 * A power down: if picked up, players' left and right controller keys change their effect for 20 sec.
 * @author daniel.szeifert
 * @version 1.0
 * @since 2014-05-17
 */
public class LeftRightSwitcher extends PowerDown{
	
	public LeftRightSwitcher(double x, double y){
		super(x, y);
		super.setTimeItLasts(20000);
	}

}
