package space_battle.client;

/**
 * Client side class for non-player characters (enemies).
 * 
 * @author András
 *
 */

public class CNPC extends CShootingObject {
	/**
	 * Server tick when this object first appeared on the screen.
	 */
	public long creationTime;
	
	/**
	 * Server tick when last teleportation occurred. (0 if not applicable)
	 */
	public long teleportTime;
}
