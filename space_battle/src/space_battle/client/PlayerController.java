package space_battle.client;

import java.awt.event.KeyEvent;
import java.util.TreeMap;

import space_battle.enums.PlayerAction;
import space_battle.interfaces.PlayerController_for_Client;
import space_battle.interfaces.ServerForPlayerController;

/**
 * This class translates user key presses to server commands.
 * 
 * @author András
 *
 */
public class PlayerController implements PlayerController_for_Client {
	/**
	 * An interface which simplifies and unifies command execution.
	 * 
	 * @author András
	 *
	 */
	interface Bridge {
		void exec(boolean pressed);
	}
	
	/**
	 * Server object
	 */
	private ServerForPlayerController server;
	
	/**
	 * Action - server command bindings
	 */
	private TreeMap<Integer, Bridge> bindings = new TreeMap<>();
	
	/**
	 * Keypressed events are triggered continuously when a key is held down,
	 * so this must be filtered by registering whether a key is already pressed or not. 
	 */
	private boolean p1fire = false, p1left = false, p1right = false;
	private boolean p2fire = false, p2left = false, p2right = false;
	
	/**
	 * Binds a KeyCode to a specific {@link PlayerAction}. Called by the Client.
	 * 
	 * @param action {@link PlayerAction} to bind
	 * @param key KeyCode to bind to
	 * 
	 * @see KeyEvent
	 */
	public void bindKey(PlayerAction action, Integer key) {
		switch (action)
		{
			case P1FIRE: bindings.put(key, new Bridge(){
				public void exec(boolean pressed) {
					if (pressed && !p1fire){
						server.fire(0);
						p1fire = true;
					}
					else if (!pressed)
					{
						server.releaseFire(0);
						p1fire = false;
					}
				}
			} );
			break;
			case P2FIRE: bindings.put(key, new Bridge(){
				public void exec(boolean pressed) {
					if (pressed && !p2fire)
					{
						server.fire(1);
						p2fire = true;
					}
					else if (!pressed)
					{
						server.releaseFire(1);
						p2fire = false;
					}
				}
			} );
			break;
			case P1LEFT: bindings.put(key, new Bridge(){
				public void exec(boolean pressed) {
					if (pressed && !p1left) 
					{
						server.moveLeft(0);
						p1left = true;
					}
					else if (!pressed)
					{
						server.releaseLeft(0);
						p1left = false;
					}
				}
			} );
			break;
			case P2LEFT: bindings.put(key, new Bridge(){
				public void exec(boolean pressed) {
					if (pressed && !p2left) 
					{
						server.moveLeft(1);
						p2left = true;
					}
					else if (!pressed)
					{
						server.releaseLeft(1);
						p2left = false;
					}
				}
			} );
			break;
			case P1RIGHT: bindings.put(key, new Bridge(){
				public void exec(boolean pressed) {
					if (pressed && !p1right)
					{
						server.moveRight(0);
						p1right = true;
					}
					else if (!pressed)
					{
						server.releaseRight(0);
						p1right = false;
					}
				}
			} );
			break;
			case P2RIGHT: bindings.put(key, new Bridge(){
				public void exec(boolean pressed) {
					if (pressed && !p2right) 
					{
						server.moveRight(1);
						p2right = true;
					}
					else if (!pressed)
					{
						server.releaseRight(1);
						p2right = false;
					}
				}
			} );
			break;
		}
	}

	/**
	 * Constructor which registers server object which must be commanded.
	 * 
	 * @param sv {@link Server} object
	 */
	public PlayerController(ServerForPlayerController sv)
	{
		server = sv;
	}
	
	/**
	 * Executes the appropriate server command if there is a binding exists for the given key.
	 * 
	 * @param e KeyEvent which contains the KeyCode
	 * @param pressed whether the key is being pressed or being released
	 */
	@Override
	public void dispatchKeyEvent(KeyEvent e, boolean pressed) {
		Bridge b = bindings.get(e.getKeyCode());
		
		if (b != null)
		{
			b.exec(pressed);
		}
	}
}
