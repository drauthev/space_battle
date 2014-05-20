package space_battle.client;

/**
 * Basic game element class. Parent for all client side objects.
 * 
 * @author András
 *
 */
public class CGameElement {
	/**
	 * Position on the screen.
	 */
	public int x, y;
	
	/**
	 * Innermost name of the server side class (to identify which sprite belongs to this object).
	 */
	public String className;
}
