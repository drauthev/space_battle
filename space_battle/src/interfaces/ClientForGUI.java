package interfaces;

import java.awt.event.KeyEvent;
import java.util.EnumMap;
import java.util.List;

import client.ObjectBuffer;
import enums.GameSkill;
import enums.GameType;
import enums.PlayerAction;

public interface ClientForGUI {
	public List<java.util.Map.Entry<Integer, String>> getHighScores();	// SortedMap-ben score(K)-név(V) párok (#bridge)
	public int getHighestScore();								// legmagasabb pontszám lekérése (#bridge)
	public EnumMap<PlayerAction, Integer> getKeyboardSettings();// HashMap-ben action(K)-KeyCode(V) párok
	public ObjectBuffer getNewObjectBuffer();					// Request a friss object bufferre
	public void bindKey(PlayerAction action, Integer key);		// billentyûzetbeállítás módosítása
	public void setDifficulty(GameSkill gs);					// játék nehézségének változtatása
	public void setSound(Boolean val);							// hang ki/be
	public void dispatchKeyEvent(KeyEvent e, boolean pressed);	// GUI továbbítja a billentyûleütéseket
	public void pauseRequest();									// Szünetet kértek
	public void startRequest();									// Indulhat a játék
	public void resetGameState();								// Befejezhetõ a jelenlegi játék
	public void newGame(GameType gt);							// Új játék indítása (hosztolás)
	public void joinGame(String ipv4);							// Csatlakozás a megadott IP-n futó játékhoz
	public void terminate();									// exit a menüben
	public void sendName(String name);							// játékosnév elküldése
}
