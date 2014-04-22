package client;

import java.awt.event.KeyEvent;
import java.util.HashMap;

import interfaces.PlayerController_for_Client;
import server.*;;

public class PlayerController implements PlayerController_for_Client {
	boolean configure(HashMap<String, Character> bindings) { return false; }
	void registerServer(Server sv) {}
	
	
	@Override
	public void dispatchKeyEvent(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
}
