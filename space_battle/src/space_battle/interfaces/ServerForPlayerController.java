package space_battle.interfaces;

public interface ServerForPlayerController {
	// �rtelemszer�en; push-pull m�k�d�s

	public void moveLeft(int playerID);
	public void releaseLeft(int playerID);
	public void moveRight(int playerID);
	public void releaseRight(int playerID);
	public void fire(int playerID);
	public void releaseFire(int playerID);
}
