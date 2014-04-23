package interfaces;

import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.SortedMap;

import client.ObjectBuffer;
import enums.*;

public interface ClientForGUI {
	public SortedMap<Integer, String> getHighScores();			// SortedMap-ben score(K)-név(V) párok (#bridge)
	public int getHighestScore();								// legmagasabb pontszám lekérése (#bridge)
	public HashMap<String, Integer> getKeyboardSettings();		// HashMap-ben action(K)-KeyCode(V) párok
	public ObjectBuffer getNewObjectBuffer();					// Request a friss object bufferre
	public void bindKey(String action, Integer key);			// billentyûzetbeállítás módosítása
	public void setDifficulty(GameSkill gs);					// játék nehézségének változtatása
	public void setSound(Boolean val);							// hang ki/be
	public void dispatchKeyEvent(KeyEvent e);					// GUI továbbítja a billentyûleütéseket
	public void pauseRequest();									// Szünetet kértek
	public void startRequest();									// Indulhat a játék
	public void resetGameState();								// Befejezhetõ a jelenlegi játék
	public void newGame(GameType gt);							// Új játék indítása (hosztolás)
	public void joinGame(String ipv4);							// Csatlakozás a megadott IP-n futó játékhoz
	public void terminate();									// exit a menüben
	public void sendName(String name);							// játékosnév elküldése
}
