package network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import sound.SoundType;
import enums.*;
import interfaces.AllServerInterfaces;
import interfaces.ClientForServer;

public class VirtualClient implements ClientForServer, Runnable {
	private ServerSocket ss;
	private Socket s;
	
	private AllServerInterfaces server;
	private Timer timer;
	private ObjectOutputStream oos;
	
	private ArrayList<java.util.Map.Entry<String,Object>> callQueue = new ArrayList<>();
	private boolean isShuttingDown = false;
	private int cmdRate;

	@Override
	public void run() {
		Thread mainThread = new Thread(new Runnable()
		{
			@SuppressWarnings("unchecked")
			@Override
			public void run() {
				try {
					s = ss.accept();
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
							case "playerStates":
								{
									Integer playerStates = (Integer) temp.getValue();
									
									if ((playerStates & Constants.p1left) == 0)
										server.releaseLeft(0);
									else server.moveLeft(0);
									
									if ((playerStates & Constants.p2left) == 0)
										server.releaseLeft(1);
									else server.moveLeft(1);
									
									if ((playerStates & Constants.p1right) == 0)
										server.releaseRight(0);
									else server.moveRight(0);
									
									if ((playerStates & Constants.p2right) == 0)
										server.releaseRight(1);
									else server.moveRight(1);
									
									if ((playerStates & Constants.p1fire) == 0)
										server.releaseFire(0);
									else server.fire(0);
									
									if ((playerStates & Constants.p2fire) == 0)
										server.releaseFire(1);
									else server.fire(1);
								}
								break;
							case "disconnect":
								server.disconnect(); break;
							case "pauseRequest":
								server.pauseRequest(); break;
							case "startRequest":
								server.startRequest(); break;
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
		isShuttingDown = true;
		timer.cancel();
	}

}
