package space_battle.interfaces;

import space_battle.enums.GameState;
import space_battle.sound.SoundType;

public interface ClientForServer {
	public void updateObjects(String JSONtext);	// JSONArray-ekben az objektumok GUI �s kliens sz�m�ra fontos
												// tulajdons�gai, plusz a current server tick, tripla bufferel�s!
	public void playSound(SoundType sound);		// adott t�pus� hang lej�tsz�sa (#bridge)
	public void changeGameState(GameState gs);	// V�ltozott a j�t�k�llapot.
}
