package network;

import interfaces.ClientForServer;
import interfaces.ServerForClient;
import interfaces.ServerForPlayerController;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import sound.SoundType;
import enums.GameState;

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
				client.changeGameState(GameState.CONNECTING);
				
				try {
					s = new Socket(ipv4, port);
					s.setTcpNoDelay(true);
					// s.setTcpNoDelay(true);
					oos = new ObjectOutputStream(s.getOutputStream());
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				startRequest(client);
				
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
								callQueue.clear();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				}, 0, 1000/cmdRate);
				
				try {
					ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
					while (!isShuttingDown)
					{
						java.util.Map.Entry<String, Object> temp = (java.util.Map.Entry<String, Object>) ois.readObject();
						
						switch (temp.getKey())
						{
							case "updateObjects":
								client.updateObjects((String)temp.getValue());
								break;
							case "playSound":
								client.playSound((SoundType)temp.getValue());
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
		playerStates |= Constants.left;
	}

	@Override
	public void releaseLeft(int playerID) {
		playerStates &= ~Constants.left;
	}

	@Override
	public void moveRight(int playerID) {
		playerStates |= Constants.right;
	}

	@Override
	public void releaseRight(int playerID) {
		playerStates &= ~Constants.right;
	}

	@Override
	public void fire(int playerID) {
		playerStates |= Constants.fire;
	}

	@Override
	public void releaseFire(int playerID) {
		playerStates &= ~Constants.fire;
	}

	@Override
	public void disconnect(ClientForServer c) {
		synchronized (callQueue) { callQueue.add(new AbstractMap.SimpleEntry<String, Object>("disconnect", null)); }
	}

	@Override
	public void pauseRequest(ClientForServer c) {
		synchronized (callQueue) { callQueue.add(new AbstractMap.SimpleEntry<String, Object>("pauseRequest", null)); }
	}

	@Override
	public void startRequest(ClientForServer c) {
		synchronized (callQueue) { callQueue.add(new AbstractMap.SimpleEntry<String, Object>("startRequest", null)); }
	}

	@Override
	public void sendName(String name) {
		synchronized (callQueue) { callQueue.add(new AbstractMap.SimpleEntry<String, Object>("sendName", name)); }
	}

	@Override
	public void terminate() {
		disconnect(client);
		
		try {
			Thread.sleep(1000/cmdRate);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		isShuttingDown = true;
		timer.cancel();
		
		if (s != null)
			try {
				s.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}

	@Override
	public void setClient2(ClientForServer c2) {}
}
