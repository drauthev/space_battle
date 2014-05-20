package space_battle.interfaces;

import space_battle.enums.GameState;
import space_battle.sound.SoundType;

public interface ClientForServer {
	public void updateObjects(String JSONtext);	// JSONArray-ekben az objektumok GUI és kliens számára fontos
												// tulajdonságai, plusz a current server tick, tripla bufferelés!
	public void playSound(SoundType sound);		// adott típusú hang lejátszása (#bridge)
	public void changeGameState(GameState gs);	// Változott a játékállapot.
}
