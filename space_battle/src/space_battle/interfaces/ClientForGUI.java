package space_battle.interfaces;

import java.awt.event.KeyEvent;
import java.util.EnumMap;
import java.util.List;

import space_battle.client.ObjectBuffer;
import space_battle.enums.GameSkill;
import space_battle.enums.GameType;
import space_battle.enums.PlayerAction;

public interface ClientForGUI {
	public List<java.util.Map.Entry<Integer, String>> getHighScores();	// SortedMap-ben score(K)-n�v(V) p�rok (#bridge)
	public int getHighestScore();								// legmagasabb pontsz�m lek�r�se (#bridge)
	public EnumMap<PlayerAction, Integer> getKeyboardSettings();// HashMap-ben action(K)-KeyCode(V) p�rok
	public ObjectBuffer getNewObjectBuffer();					// Request a friss object bufferre
	public void bindKey(PlayerAction action, Integer key);		// billenty�zetbe�ll�t�s m�dos�t�sa
	public void setDifficulty(GameSkill gs);					// j�t�k neh�zs�g�nek v�ltoztat�sa
	public void setSound(Boolean val);							// hang ki/be
	public void dispatchKeyEvent(KeyEvent e, boolean pressed);	// GUI tov�bb�tja a billenty�le�t�seket
	public void pauseRequest();									// Sz�netet k�rtek
	public void startRequest();									// Indulhat a j�t�k
	public void resetGameState();								// Befejezhet� a jelenlegi j�t�k
	public void newGame(GameType gt);							// �j j�t�k ind�t�sa (hosztol�s)
	public void joinGame(String ipv4);							// Csatlakoz�s a megadott IP-n fut� j�t�khoz
	public void terminate();									// exit a men�ben
	public void sendName(String name);							// j�t�kosn�v elk�ld�se
}
