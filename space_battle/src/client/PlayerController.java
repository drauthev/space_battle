package client;

import interfaces.PlayerController_for_Client;
import interfaces.ServerForPlayerController;

import java.awt.event.KeyEvent;
import java.util.TreeMap;

import enums.PlayerAction;

public class PlayerController implements PlayerController_for_Client {
	interface Bridge {
		void exec(boolean pressed);
	}
	
	private ServerForPlayerController server;
	private TreeMap<Integer, Bridge> bindings = new TreeMap<>();
	
	private boolean p1fire = false, p1left = false, p1right = false;
	private boolean p2fire = false, p2left = false, p2right = false;
	
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

	public PlayerController(ServerForPlayerController sv)
	{
		server = sv;
	}
	
	@Override
	public void dispatchKeyEvent(KeyEvent e, boolean pressed) {
		Bridge b = bindings.get(e.getKeyCode());
		
		if (b != null)
		{
			b.exec(pressed);
		}
	}
}
