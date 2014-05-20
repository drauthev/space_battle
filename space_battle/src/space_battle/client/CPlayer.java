package space_battle.client;

/**
 * Client side representation of players.
 * 
 * @author Andr�s
 *
 */
public class CPlayer extends CShootingObject {
	/**
	 * Number of player's lives.
	 */
	public int numberOfLives;
	
	/**
	 * Identifier of the player.
	 */
	public int id;
	
	/**
	 * Whether shield present around the player or not.
	 */
	public boolean isShielded;
}
