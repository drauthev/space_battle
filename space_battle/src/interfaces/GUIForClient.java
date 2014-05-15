package interfaces;

import enums.GameSkill;
import enums.GameState;

public interface GUIForClient extends Runnable {
	// public void run() 						// entry point
	public void setGameState(GameState gs);		// kliens jelez, hogy változott a játék állapota
	public void error(String text);				// popup hibaüzenet
	public void terminate();					// zárul a program
	public void setSound(Boolean val);			// hang ki/be
	public void setDifficulty(GameSkill gs);	// játék nehézségének változtatása
	public void setRecentIPs(String[] iparr);	// Az utolsó 4 IP
}

