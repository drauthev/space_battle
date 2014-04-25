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
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import enums.*;
import interfaces.ClientForServer;
import interfaces.ServerForClient;
import interfaces.ServerForPlayerController;
import network.Constants;

public class VirtualServer 
	implements ServerForClient, ServerForPlayerController, Runnable {
	
	private int cmdRate;
	private String ipv4;
	private int port;
	private Socket s;
	private ClientForServer client;
	private Timer timer;
	private ObjectOutputStream oos;
	private boolean isShuttingDown = false;
	
	private Integer playerStates = 0;
	private ArrayList<java.util.Map.Entry<String,Object>> callQueue = new ArrayList<>();

	@Override
	public void run() {
		Thread mainThread = new Thread(new Runnable()
		{
			@SuppressWarnings("unchecked")
			@Override
			public void run() {
				try {
					s = new Socket(ipv4, port);
					oos = new ObjectOutputStream(new GZIPOutputStream(s.getOutputStream()));
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				timer.scheduleAtFixedRate(new TimerTask() {
					@Override
					public void run() {
						synchronized (callQueue) {
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
					}
				}, 0, 1000/cmdRate);
				
				try {
					GZIPInputStream gis = new GZIPInputStream(s.getInputStream());
					ObjectInputStream ois = new ObjectInputStream(gis);
					while (!isShuttingDown)
					{
						java.util.Map.Entry<String, Object> temp = (java.util.Map.Entry<String, Object>) ois.readObject();
						
						switch (temp.getKey())
						{
							case "updateObjects":
								client.updateObjects((String)temp.getValue());
								break;
							case "playSound":
								client.playSound((String)temp.getValue());
								break;
							case "changeGameState":
								client.changeGameState((GameState)temp.getValue());
								break;
							case "terminate":
								client.terminate();
								break;
						}
					}
				} catch (IOException | ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		
		mainThread.start();
	}
	
	public VirtualServer(ClientForServer cl, String ipv4, int port, int cmdRate)
	{
		client = cl;
		this.cmdRate = cmdRate;
		this.ipv4 = ipv4;
		this.port = port;
		timer = new Timer();
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
		synchronized (callQueue) { callQueue.add(new AbstractMap.SimpleEntry<String, Object>("disconnect", null)); }
	}

	@Override
	public void pauseRequest() {
		synchronized (callQueue) { callQueue.add(new AbstractMap.SimpleEntry<String, Object>("pauseRequest", null)); }
	}

	@Override
	public void startRequest() {
		synchronized (callQueue) { callQueue.add(new AbstractMap.SimpleEntry<String, Object>("startRequest", null)); }
	}

	@Override
	public void sendName(String name) {
		synchronized (callQueue) { callQueue.add(new AbstractMap.SimpleEntry<String, Object>("sendName", name)); }
	}

	@Override
	public void terminate() {
		disconnect();
		isShuttingDown = true;
		timer.cancel();
	}

	@Override
	public void setClient2(ClientForServer c2) {}
}
