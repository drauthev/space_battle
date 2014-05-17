package space_battle.server.game_elements;

/**
 * A power down: if picked up, players gets only half of the scores for shot hostiles for 15 sec.
 * @author daniel.szeifert
 * @version 1.0
 * @since 2014-05-17
 */
public class HalfScores extends PowerDown{
		
	public HalfScores(double x, double y){
		super(x, y);
		super.setTimeItLasts(15000);
	}

}
