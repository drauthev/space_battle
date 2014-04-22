package interfaces;

import java.util.SortedMap;

public interface ServerForClient extends Runnable {
	// public void run() 							// entry point
	public void disconnect();						// lelép a kliens (vagy a network controller elveszti a kapcsolatot)
	public void pauseRequest();						// kliens kérvényezi a szünetelést
	public void startRequest();						// kliens kérvényezi a játék indítását
	public SortedMap<Integer, String> getHighScores();	// SortedMap-ben score(K)-név(V) 
	public void sendName(String name);				// játékosnév elküldése; szerver felírhatja a high scores-ba
	public int getHighestScore();					// legmagasabb pontszám lekérése
	public void terminate();						// zárul a program (csak client2-re hívja vissza!)
}

// konstruktor: Server(GameType gt, GameSkill gs, Client_for_Server c1, Client_for_Server c2);