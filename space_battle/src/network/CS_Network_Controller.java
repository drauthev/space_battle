package network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.security.KeyStore.Entry;
import java.util.SortedMap;
import java.util.Timer;
import java.util.TimerTask;

import client.Client;
import enums.*;
import interfaces.ClientForServer;
import interfaces.ServerForClient;
import interfaces.ServerForPlayerController;

public class CS_Network_Controller 
	implements ServerForClient, ServerForPlayerController, Runnable {
	
	class Listener implements Runnable
	{
		@SuppressWarnings("unchecked")
		@Override
		public void run() {
			try {
				ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
				while (true)
				{
					java.util.Map.Entry<String, Object> temp = (java.util.Map.Entry<String, Object>) ois.readObject();
					
					switch (temp.getKey())
					{
						case "updateObjects":
							client.updateObjects((String)temp.getValue());
						case "playSound":
							client.playSound((String)temp.getValue());
						case "changeGameState":
							client.changeGameState((GameState)temp.getValue());
						case "terminate":
							client.terminate();
					}
				}
			} catch (IOException | ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private Socket s;
	private ClientForServer client;
	private Thread listThread;

	@Override
	public void run() {
		s = new Socket();
		listThread = new Thread(new Listener());
		listThread.start();
	}
	
	public CS_Network_Controller(ClientForServer cl)
	{
		client = cl;
	}

	@Override
	public void moveLeft(int playerID) {
		// TODO Auto-generated method stub

	}

	@Override
	public void releaseLeft(int playerID) {
		// TODO Auto-generated method stub

	}

	@Override
	public void moveRight(int playerID) {
		// TODO Auto-generated method stub

	}

	@Override
	public void releaseRight(int playerID) {
		// TODO Auto-generated method stub

	}

	@Override
	public void fire(int playerID) {
		// TODO Auto-generated method stub

	}

	@Override
	public void releaseFire(int playerID) {
		// TODO Auto-generated method stub

	}

	@Override
	public void disconnect() {
		// TODO Auto-generated method stub

	}

	@Override
	public void pauseRequest() {
		// TODO Auto-generated method stub

	}

	@Override
	public void startRequest() {
		// TODO Auto-generated method stub

	}

	@Override
	public SortedMap<Integer, String> getHighScores() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void sendName(String name) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getHighestScore() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	void setPort(int port) {}
	void setIP(String ipv4) {}
	void setLocalIP(String ipv4) {}
	void setLocalPort(int port) {}
	boolean connect() {return false;}

	@Override
	public void terminate() {
		listThread.interrupt();
	}
}
