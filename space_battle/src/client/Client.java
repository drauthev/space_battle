package client;

import java.awt.event.KeyEvent;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.SortedMap;

import enums.*;
import gui.GUI;
import interfaces.ClientForGUI;
import interfaces.ClientForServer;
import interfaces.GUIForClient;
import interfaces.ServerForClient;
import interfaces.SoundSystemForClient;
import network.SS_Network_Controller;

public class Client implements ClientForGUI, ClientForServer {
	private static final String configFile = "options.ini";
	private static final boolean debugMode = true;
	
	private GameState gameState = GameState.NONE;
	private GameSkill gameSkill = GameSkill.NORMAL;
	private Boolean sounds = true;
	
	private GUIForClient gui;
	private SoundSystemForClient soundSys;
	private ServerForClient server;
	private SS_Network_Controller ssnc;	// Call terminate(), when server gets terminated after a network game.
	private PlayerController[] playercontrollers;
	private ObjectBuffer[] objectBuffer;
	private ObjectBufferState[] obStates = 
		{ObjectBufferState.INVALID, ObjectBufferState.INVALID, ObjectBufferState.INVALID};
	private Object objectBufferSyncObject = new Object();
	private String[] recentlyUsedIP = new String[4];
	private HashMap<String, Integer> keyboardSettings;

	private enum ObjectBufferState
	{
		USED, VALID, INVALID
	}
	
	public static void main(String[] args) {
		Client cl = new Client();
	}


	public Client() {
		gui = new GUI(this);
		loadConfig();		

		gui.setDifficulty(gameSkill);
		gui.setSound(sounds);
		gui.setRecentIPs(recentlyUsedIP);
		// TODO Instantiate GUI, start new thread. Instantiate SoundSystem.
		
		Thread t = new Thread(gui);
		t.run();
	}

	public void terminate() {
		if (gui != null)
			gui.terminate();

		if (server != null)
			server.terminate();
		
		saveConfig();
	}

	@SuppressWarnings("unchecked")
	private void loadConfig() {
		try {
			FileInputStream fis = new FileInputStream(configFile);
			ObjectInputStream ois = new ObjectInputStream(fis);
			
			keyboardSettings = (HashMap<String, Integer>) ois.readObject();
			recentlyUsedIP = (String[]) ois.readObject();
			gameSkill = (GameSkill) ois.readObject();
			sounds = (Boolean) ois.readObject();
			
			ois.close();
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
			gui.error("Error while loading configuration file!");
		} finally {
			if (keyboardSettings != null)
				return;
			
			keyboardSettings = new HashMap<String, Integer>(10);
			
			// Load default keyboard configuration.
			keyboardSettings.put("p1left", KeyEvent.VK_A);
			keyboardSettings.put("p1right", KeyEvent.VK_D);
			keyboardSettings.put("p1fire", KeyEvent.VK_SPACE);
			keyboardSettings.put("p2left", KeyEvent.VK_LEFT);
			keyboardSettings.put("p2right", KeyEvent.VK_RIGHT);
			keyboardSettings.put("p2fire", KeyEvent.VK_NUMPAD0);
			keyboardSettings.put("pause", KeyEvent.VK_P);
		}
	}


	@Override
	public void updateObjects(String JSONtext) {
		int idx = -1;	// ObjectBuffer to work with.
		
		for(int i = 0; i < 3; ++i)
		{
			if (obStates[i] == ObjectBufferState.INVALID)
			{
				idx = i;
				break;
			}
		}
		
		// TODO Parse objects.
		
		synchronized (objectBufferSyncObject)
		{
			for(int i = 0; i < 3; ++i)
			{
				if (obStates[i] == ObjectBufferState.VALID)
					obStates[i] = ObjectBufferState.INVALID;
			}
			
			obStates[idx] = ObjectBufferState.VALID;
		}
	}


	@Override
	public void playSound(String sound) {
		if (sounds)
			soundSys.playSound(sound);
	}


	@Override
	public SortedMap<Integer, String> getHighScores() {
		return server.getHighScores();
	}


	@Override
	public HashMap<String, Integer> getKeyboardSettings() {
		if (keyboardSettings == null)
			loadConfig();
		
		return keyboardSettings;
	}

	@Override
	public ObjectBuffer getNewObjectBuffer() {
		synchronized (objectBufferSyncObject)
		{
			int used = -1, valid = -1;
			
			for(int i = 0; i < 3; ++i)
			{
				switch (obStates[i])
				{
					case VALID: valid = i; break;
					case USED: used = i; break;
					default:
				}
			}
			
			// A new, valid ObjectBuffer is available, use it.
			if (valid != -1)
			{
				if (used != -1)
					obStates[used] = ObjectBufferState.INVALID;
				
				obStates[valid] = ObjectBufferState.USED;
				return objectBuffer[valid];
			}
			
			// There is no new ObjectBuffer available at the moment, use the old one.
			if (used != -1)
				return objectBuffer[used];
			
			// No valid ObjectBuffer exists.
			else return null;
		}
	}


	@Override
	public int getHighestScore() {
		return server.getHighestScore();
	}


	@Override
	public void dispatchKeyEvent(KeyEvent e) {
		if (gameState == GameState.RUNNING)
			for(int i = 0; i < playercontrollers.length; ++i)
				playercontrollers[i].dispatchKeyEvent(e);
	}


	public void changeGameState(GameState gs)
	{	
		gameState = gs;
		gui.setGameState(gameState);
	}

	private void saveConfig() {
		try {
			FileOutputStream fos = new FileOutputStream(configFile);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			
			oos.writeObject(keyboardSettings);
			oos.writeObject(recentlyUsedIP);
			oos.writeObject(gameSkill);
			oos.writeObject(sounds);
			
			oos.close();
		} catch (IOException e) {
			e.printStackTrace();
			gui.error("Error while saving configuration file!");
		}
	}
	
	@Override
	public void pauseRequest() {
		switch (gameState)
		{	
			case RUNNING:
			case WAITING:
				server.pauseRequest();
			default:
		}
	}


	@Override
	public void startRequest() {
		switch (gameState)
		{
			case PAUSED:
				server.startRequest();
			default:
		}
	}
	
	@Override
	public void resetGameState() {
		changeGameState(GameState.NONE);
		
		if (server != null)
		{
			server.terminate();
			server = null;
		}
		
		if (ssnc != null)
		{
			ssnc.terminate();
			ssnc = null;
		}
	}


	@Override
	public void newGame(GameType gt) {
		resetGameState();
		// TODO: instantiate/start the appropriate server
		server.startRequest();
	}


	@Override
	public void joinGame(String ipv4) {
		resetGameState();
		System.arraycopy(recentlyUsedIP, 0, recentlyUsedIP, 1, 3);
		recentlyUsedIP[0] = ipv4;
		gui.setRecentIPs(recentlyUsedIP);
		// TODO: instantiate/configure/start CS_Network_Controller. 
	}


	@Override
	public void bindKey(String action, Integer key) {
		keyboardSettings.put(action, key);
	}


	@Override
	public void setDifficulty(GameSkill gs) {
		gameSkill = gs;
	}


	@Override
	public void setSound(Boolean val) {
		sounds = val;
	}


	@Override
	public void sendName(String name) {
		server.sendName(name);
	}
}
