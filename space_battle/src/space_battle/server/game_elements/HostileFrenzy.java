package space_battle.server.game_elements;

/**
 * A power down: if picked up, hostiles are moving and shooting faster for 10 sec.
 * @author daniel.szeifert
 * @version 1.0
 * @since 2014-05-17
 */
public class HostileFrenzy extends PowerDown{
	
	public HostileFrenzy(double x, double y){
		super(x, y);
		super.setTimeItLasts(10000);
	}
	
}
