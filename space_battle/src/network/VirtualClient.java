package network;

import interfaces.AllServerInterfaces;
import interfaces.ClientForServer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import sound.SoundType;
import enums.GameState;

public class VirtualClient implements ClientForServer, Runnable {
	private ServerSocket ss;
	private Socket s;
	
	private AllServerInterfaces server;
	private Timer timer;
	private ObjectOutputStream oos;
	
	private ArrayList<java.util.Map.Entry<String,Object>> callQueue = new ArrayList<>();
	private boolean isShuttingDown = false;
	private int cmdRate;
	
	private ClientForServer thisClient = this;

	@Override
	public void run() {
		Thread mainThread = new Thread(new Runnable()
		{
			@SuppressWarnings("unchecked")
			@Override
			public void run() {
				try {
					s = ss.accept();
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
				
				timer.scheduleAtFixedRate(new TimerTask() {
					@Override
					public void run() {
						synchronized (callQueue) {
							try {
								for (java.util.Map.Entry<String, Object> m : callQueue)
								{
									oos.writeObject(m);
								}
								oos.flush();
								callQueue.clear();
							} catch (SocketException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								terminate();
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
							case "playerStates":
								{
									Integer playerStates = (Integer) temp.getValue();
									
									if ((playerStates & Constants.left) == 0)
										server.releaseLeft(1);
									else server.moveLeft(1);
									
									if ((playerStates & Constants.right) == 0)
										server.releaseRight(1);
									else server.moveRight(1);

									if ((playerStates & Constants.fire) == 0)
										server.releaseFire(1);
									else server.fire(1);
								}
								break;
							case "disconnect":
								server.disconnect(thisClient); break;
							case "pauseRequest":
								server.pauseRequest(thisClient); break;
							case "startRequest":
								server.startRequest(thisClient); break;
							case "sendName":
								server.sendName((String)temp.getValue());
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
	
	public VirtualClient(AllServerInterfaces sv, int port, int cmdRate)
	{
		server = sv;
		this.cmdRate = cmdRate;
		
		try {
			ss = new ServerSocket(port);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		timer = new Timer();
	}

	@Override
	public void updateObjects(String JSONtext) {
		synchronized (callQueue) { callQueue.add(new AbstractMap.SimpleEntry<String, Object>("updateObjects", JSONtext)); }
	}

	@Override
	public void playSound(SoundType sound) {
		synchronized (callQueue) { callQueue.add(new AbstractMap.SimpleEntry<String, Object>("playSound", sound)); }
	}

	@Override
	public void changeGameState(GameState gs) {
		synchronized (callQueue) { callQueue.add(new AbstractMap.SimpleEntry<String, Object>("changeGameState", gs)); }
	}

	@Override
	public void terminate() {
		try {
			Thread.sleep(1000/cmdRate);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		isShuttingDown = true;
		timer.cancel();
		try {
			if (ss != null) ss.close();
			if (s != null) s.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
