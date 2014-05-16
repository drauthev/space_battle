package space_battle.interfaces;

import space_battle.enums.GameSkill;
import space_battle.enums.GameState;

public interface GUIForClient extends Runnable {
	// public void run() 						// entry point
	public void setGameState(GameState gs);		// kliens jelez, hogy v�ltozott a j�t�k �llapota
	public void terminate();					// z�rul a program
	public void setSound(Boolean val);			// hang ki/be
	public void setDifficulty(GameSkill gs);	// j�t�k neh�zs�g�nek v�ltoztat�sa
	public void setRecentIPs(String[] iparr);	// Az utols� 4 IP
}

