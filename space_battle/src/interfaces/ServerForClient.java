package interfaces;

import java.util.SortedMap;

public interface ServerForClient extends Runnable {
	// public void run() 							// entry point
	public void disconnect();						// lel�p a kliens (vagy a network controller elveszti a kapcsolatot)
	public void pauseRequest();						// kliens k�rv�nyezi a sz�netel�st
	public void startRequest();						// kliens k�rv�nyezi a j�t�k ind�t�s�t
	public void sendName(String name);				// j�t�kosn�v elk�ld�se; szerver fel�rhatja a high scores-ba
	public void terminate();						// z�rul a program (csak client2-re h�vja vissza!)
}

// konstruktor: Server(GameType gt, GameSkill gs, Client_for_Server c1, Client_for_Server c2);