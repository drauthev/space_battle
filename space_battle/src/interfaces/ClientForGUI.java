package interfaces;

import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.SortedMap;

import client.ObjectBuffer;
import enums.*;

public interface ClientForGUI {
	public SortedMap<Integer, String> getHighScores();			// SortedMap-ben score(K)-n�v(V) p�rok (#bridge)
	public HashMap<String, Integer> getKeyboardSettings();		// HashMap-ben action(K)-KeyCode(V) p�rok
	public ObjectBuffer getNewObjectBuffer();					// Request a friss object bufferre
	public void bindKey(String action, Integer key);			// billenty�zetbe�ll�t�s m�dos�t�sa
	public void setDifficulty(GameSkill gs);					// j�t�k neh�zs�g�nek v�ltoztat�sa
	public void setSound(Boolean val);							// hang ki/be
	public void dispatchKeyEvent(KeyEvent e);					// GUI tov�bb�tja a billenty�le�t�seket
	public void pauseRequest();									// Sz�netet k�rtek
	public void startRequest();									// Indulhat a j�t�k
	public void resetGameState();								// Befejezhet� a jelenlegi j�t�k
	public void newGame(GameType gt);							// �j j�t�k ind�t�sa (hosztol�s)
	public void joinGame(String ipv4);							// Csatlakoz�s a megadott IP-n fut� j�t�khoz
	public void terminate();									// exit a men�ben
	public void sendName(String name);							// j�t�kosn�v elk�ld�se
}
