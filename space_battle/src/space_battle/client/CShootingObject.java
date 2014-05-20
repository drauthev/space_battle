package space_battle.client;


/**
 * Client side parent class for shooting objects, namely: <br>
 * {@link CPlayer}<br>
 * {@link CNPC}<br>
 * 
 * @author András
 *
 */
public class CShootingObject extends CGameElement {
	/**
	 * Server side tick when the object exploded (0 if it didn't yet)
	 */
	public long explosionTime;
	
	/**
	 * Server side tick when the last hit occurred (0 if never been hit)
	 */
	public long hitTime;
}
