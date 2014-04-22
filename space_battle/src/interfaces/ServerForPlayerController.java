package interfaces;

public interface ServerForPlayerController {
	// Értelemszerûen; push-pull mûködés

	public void moveLeft(int playerID);
	public void releaseLeft(int playerID);
	public void moveRight(int playerID);
	public void releaseRight(int playerID);
	public void fire(int playerID);
	public void releaseFire(int playerID);
}
