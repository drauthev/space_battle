package interfaces;

import sound.SoundType;
import enums.*;

public interface ClientForServer {
	public void updateObjects(String JSONtext);	// JSONArray-ekben az objektumok GUI �s kliens sz�m�ra fontos
												// tulajdons�gai, plusz a current server tick, tripla bufferel�s!
	public void playSound(SoundType sound);		// adott t�pus� hang lej�tsz�sa (#bridge)
	public void changeGameState(GameState gs);	// V�ltozott a j�t�k�llapot.
	public void terminate();					// Z�rul a program.
}
