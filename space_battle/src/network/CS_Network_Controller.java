package network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import enums.*;
import interfaces.ClientForServer;
import interfaces.ServerForClient;
import interfaces.ServerForPlayerController;
import network.Constants;

public class CS_Network_Controller 
	implements ServerForClient, ServerForPlayerController, Runnable {
	
	private Socket s;
	private ClientForServer client;
	private Thread listThread;
	private Timer timer;
	private ObjectOutputStream oos;
	
	private int playerStates = 0;
	private ArrayList<java.util.Map.Entry<String,Object>> callQueue = new ArrayList<>();

	@Override
	public void run() {
		listThread = new Thread(new Runnable()
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
		});
		
		listThread.start();
	}
	
	public CS_Network_Controller(ClientForServer cl, String ipv4, int port, int cmdRate)
	{
		client = cl;
		try {
			s = new Socket(ipv4, port);
			oos = new ObjectOutputStream(s.getOutputStream());
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				callQueue.add(new AbstractMap.SimpleEntry<String, Object>("playerStates", playerStates));
				try {
					for (java.util.Map.Entry<String, Object> m : callQueue)
					{
						oos.writeObject(m);
					}
					oos.flush();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}, 0, 1000/cmdRate);
	}

	@Override
	public void moveLeft(int playerID) {
		switch (playerID)
		{
			case 0: playerStates |= Constants.p1left; break;
			case 1: playerStates |= Constants.p2left; break;
			default:
		}
	}

	@Override
	public void releaseLeft(int playerID) {
		switch (playerID)
		{
			case 0: playerStates &= ~Constants.p1left; break;
			case 1: playerStates &= ~Constants.p2left; break;
			default:
		}
	}

	@Override
	public void moveRight(int playerID) {
		switch (playerID)
		{
			case 0: playerStates |= Constants.p1right; break;
			case 1: playerStates |= Constants.p2right; break;
			default:
		}
	}

	@Override
	public void releaseRight(int playerID) {
		switch (playerID)
		{
			case 0: playerStates &= ~Constants.p1right; break;
			case 1: playerStates &= ~Constants.p2right; break;
			default:
		}
	}

	@Override
	public void fire(int playerID) {
		switch (playerID)
		{
			case 0: playerStates |= Constants.p1fire; break;
			case 1: playerStates |= Constants.p2fire; break;
			default:
		}
	}

	@Override
	public void releaseFire(int playerID) {
		switch (playerID)
		{
			case 0: playerStates &= ~Constants.p1fire; break;
			case 1: playerStates &= ~Constants.p2fire; break;
			default:
		}
	}

	@Override
	public void disconnect() {
		callQueue.add(new AbstractMap.SimpleEntry<String, Object>("disconnect", null));
	}

	@Override
	public void pauseRequest() {
		callQueue.add(new AbstractMap.SimpleEntry<String, Object>("pauseRequest", null));
	}

	@Override
	public void startRequest() {
		callQueue.add(new AbstractMap.SimpleEntry<String, Object>("startRequest", null));
	}

	@Override
	public void sendName(String name) {
		callQueue.add(new AbstractMap.SimpleEntry<String, Object>("sendName", name));
	}

	@Override
	public void terminate() {
		listThread.interrupt();
	}
}
