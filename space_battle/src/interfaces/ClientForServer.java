package interfaces;

import enums.*;

public interface ClientForServer {
	public void updateObjects(String JSONtext);	// JSONArray-ekben az objektumok GUI és kliens számára fontos
												// tulajdonságai, plusz a current server tick, tripla bufferelés!
	public void playSound(String sound);		// adott nevû hang lejátszása (#bridge)
	public void changeGameState(GameState gs);	// Változott a játékállapot.
	public void terminate();					// Zárul a program.
}
