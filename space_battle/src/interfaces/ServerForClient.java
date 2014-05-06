package interfaces;

public interface ServerForClient extends Runnable {
	// public void run() 							// entry point
	public void disconnect(ClientForServer c);		// lelép a kliens (vagy a network controller elveszti a kapcsolatot)
	public void pauseRequest(ClientForServer c);	// kliens kérvényezi a szünetelést
	public void startRequest(ClientForServer c);	// kliens kérvényezi a játék indítását
	public void sendName(String name);				// játékosnév elküldése; szerver felírhatja a high scores-ba
	public void terminate();						// zárul a program (csak client2-re hívja vissza!)
	public void setClient2(ClientForServer c2);		// kereszthivatkozás feloldása miatt kell
}

// konstruktor: Server(GameType gt, GameSkill gs, Client_for_Server c1, Client_for_Server c2);