package client;

import java.awt.event.KeyEvent;
import java.util.TreeMap;

import enums.PlayerAction;
import interfaces.PlayerController_for_Client;
import interfaces.ServerForPlayerController;;

public class PlayerController implements PlayerController_for_Client {
	interface Bridge {
		void exec(boolean pressed);
	}
	
	private ServerForPlayerController server;
	private TreeMap<Integer, Bridge> bindings = new TreeMap<>();
	
	public void bindKey(PlayerAction action, Integer key) {
		switch (action)
		{
			case P1FIRE: bindings.put(key, new Bridge(){
				public void exec(boolean pressed) {
					if (pressed) server.fire(0);
					else server.releaseFire(0);
				}
			} );
			break;
			case P2FIRE: bindings.put(key, new Bridge(){
				public void exec(boolean pressed) {
					if (pressed) server.fire(1);
					else server.releaseFire(1);
				}
			} );
			break;
			case P1LEFT: bindings.put(key, new Bridge(){
				public void exec(boolean pressed) {
					if (pressed) server.moveLeft(0);
					else server.releaseLeft(0);
				}
			} );
			break;
			case P2LEFT: bindings.put(key, new Bridge(){
				public void exec(boolean pressed) {
					if (pressed) server.moveLeft(1);
					else server.releaseLeft(1);
				}
			} );
			break;
			case P1RIGHT: bindings.put(key, new Bridge(){
				public void exec(boolean pressed) {
					if (pressed) server.moveRight(0);
					else server.releaseRight(0);
				}
			} );
			break;
			case P2RIGHT: bindings.put(key, new Bridge(){
				public void exec(boolean pressed) {
					if (pressed) server.moveRight(1);
					else server.releaseRight(1);
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
