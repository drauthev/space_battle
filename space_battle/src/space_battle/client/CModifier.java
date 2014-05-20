package space_battle.client;

/**
 * Client side information of modifiers.
 * 
 * @author András
 *
 */
public class CModifier extends CGameElement {
	/**
	 * Server tick when the modifier got picked up.
	 */
	public long pickupTime;
	
	/**
	 * Server tick when the modifier first appeared on the screen.
	 */
	public long creationTime;
	
	/**
	 * Server tick when the modifier exploded, if it didn't then 0.
	 */
	public long explosionTime;
}
