package space_battle.client;

import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.prefs.Preferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import space_battle.enums.GameSkill;
import space_battle.enums.GameState;
import space_battle.enums.GameType;
import space_battle.enums.PlayerAction;
import space_battle.gui.GUI;
import space_battle.interfaces.AllServerInterfaces;
import space_battle.interfaces.ClientForGUI;
import space_battle.interfaces.ClientForServer;
import space_battle.interfaces.GUIForClient;
import space_battle.interfaces.ServerForClient;
import space_battle.interfaces.SoundSystemForClient;
import space_battle.network.VirtualClient;
import space_battle.network.VirtualServer;
import space_battle.server.Server;
import space_battle.sound.SoundSystem;
import space_battle.sound.SoundType;

public class Client implements ClientForGUI, ClientForServer {
	private final Preferences prefs = Preferences.userRoot().node("space_battle");
	
	private GameState gameState = GameState.NONE;
	private GameSkill gameSkill = GameSkill.NORMAL;
	private Boolean sounds = true;
	
	private GUIForClient gui;
	private SoundSystemForClient soundSys;
	private ServerForClient server;
	private VirtualClient vc;	// Call terminate(), when server gets terminated after a network game.
	private PlayerController[] playercontrollers;
	private ObjectBuffer[] objectBuffer = new ObjectBuffer[3];
	private ObjectBufferState[] obStates = 
		{ObjectBufferState.INVALID, ObjectBufferState.INVALID, ObjectBufferState.INVALID};
	private Object objectBufferSyncObject = new Object();
	private String[] recentlyUsedIP = new String[4];
	private EnumMap<PlayerAction, Integer> keyboardSettings =
			new EnumMap<PlayerAction, Integer>(PlayerAction.class);

	private enum ObjectBufferState
	{
		USED, VALID, INVALID
	}
	
	public static void main(String[] args) {
		new Client();
	}


	public Client() {
		loadConfig();
		
		gui = new GUI(this);
		gui.setDifficulty(gameSkill);
		gui.setSound(sounds);
		gui.setRecentIPs(recentlyUsedIP);
		
		soundSys = new SoundSystem();
		
		for(int i = 0; i < 3; ++i)
		{
			objectBuffer[i] = new ObjectBuffer();
			
			for(int j = 0; j < objectBuffer[i].npc.length; ++j)
				objectBuffer[i].npc[j] = new CNPC();
			
			for(int j = 0; j < objectBuffer[i].player.length; ++j)
				objectBuffer[i].player[j] = new CPlayer();
			
			for(int j = 0; j < objectBuffer[i].mod.length; ++j)
				objectBuffer[i].mod[j] = new CModifier();
			
			for(int j = 0; j < objectBuffer[i].proj.length; ++j)
				objectBuffer[i].proj[j] = new CProjectile();
		}
		
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

	private void loadConfig() {
		keyboardSettings.put(PlayerAction.P1LEFT, prefs.getInt("p1left", KeyEvent.VK_J));
		keyboardSettings.put(PlayerAction.P1RIGHT, prefs.getInt("p1right", KeyEvent.VK_L));
		keyboardSettings.put(PlayerAction.P1FIRE, prefs.getInt("p1fire", KeyEvent.VK_SPACE));
		keyboardSettings.put(PlayerAction.P2LEFT, prefs.getInt("p2left", KeyEvent.VK_LEFT));
		keyboardSettings.put(PlayerAction.P2RIGHT, prefs.getInt("p2right", KeyEvent.VK_RIGHT));
		keyboardSettings.put(PlayerAction.P2FIRE, prefs.getInt("p2fire", KeyEvent.VK_NUMPAD0));
		
		recentlyUsedIP[0] = prefs.get("recIP0", "127.0.0.1");
		recentlyUsedIP[1] = prefs.get("recIP1", "127.0.0.1");
		recentlyUsedIP[2] = prefs.get("recIP2", "127.0.0.1");
		recentlyUsedIP[3] = prefs.get("recIP3", "127.0.0.1");
		
		switch (prefs.get("gameSkill", "NORMAL"))
		{
			case "EASY": gameSkill = GameSkill.EASY; break;
			case "NORMAL": gameSkill = GameSkill.NORMAL; break;
			case "HARD": gameSkill = GameSkill.HARD; break;
		}
		
		sounds = prefs.getBoolean("sounds", true);
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
		
		try {
			JSONObject wrapper = new JSONObject(JSONtext);
			
			objectBuffer[idx].currentTick = wrapper.getLong("currentTick");
			objectBuffer[idx].score = wrapper.getInt("score");
			
			JSONArray npc = wrapper.getJSONArray("npcs");
			objectBuffer[idx].npcCount = npc.length();
			for(int i = 0; i < npc.length(); ++i)
			{
				JSONObject curr = npc.getJSONObject(i);
				
				objectBuffer[idx].npc[i].className = curr.getString("className");
				objectBuffer[idx].npc[i].x = curr.getInt("x");
				objectBuffer[idx].npc[i].y = curr.getInt("y");
				objectBuffer[idx].npc[i].creationTime = curr.getLong("creationTime");
				objectBuffer[idx].npc[i].explosionTime = curr.getLong("explosionTime");
				objectBuffer[idx].npc[i].hitTime = curr.getLong("hitTime");
				
				try {
					objectBuffer[idx].npc[i].teleportTime = curr.getLong("teleportTime");
				}
				catch (JSONException e) {}
			}
			
			JSONArray player = wrapper.getJSONArray("players");
			objectBuffer[idx].playerCount = player.length();
			for(int i = 0; i < player.length(); ++i)
			{
				JSONObject curr = player.getJSONObject(i);
				
				objectBuffer[idx].player[i].className = curr.getString("className");
				objectBuffer[idx].player[i].x = curr.getInt("x");
				objectBuffer[idx].player[i].y = curr.getInt("y");
				objectBuffer[idx].player[i].numberOfLives = curr.getInt("numberOfLives");
				objectBuffer[idx].player[i].explosionTime = curr.getLong("explosionTime");
				objectBuffer[idx].player[i].hitTime = curr.getLong("hitTime");
				objectBuffer[idx].player[i].id = curr.getInt("id");
				objectBuffer[idx].player[i].isShielded = curr.getBoolean("isShielded");
			}
			
			JSONArray proj = wrapper.getJSONArray("projectiles");
			objectBuffer[idx].projCount = proj.length();
			for(int i = 0; i < proj.length(); ++i)
			{
				JSONObject curr = proj.getJSONObject(i);
				
				objectBuffer[idx].proj[i].className = curr.getString("className");
				objectBuffer[idx].proj[i].x = curr.getInt("x");
				objectBuffer[idx].proj[i].y = curr.getInt("y");
			}
			
			JSONArray mod = wrapper.getJSONArray("modifiers");
			objectBuffer[idx].modCount = mod.length();
			for(int i = 0; i < mod.length(); ++i)
			{
				JSONObject curr = mod.getJSONObject(i);
				
				objectBuffer[idx].mod[i].className = curr.getString("className");
				objectBuffer[idx].mod[i].x = curr.getInt("x");
				objectBuffer[idx].mod[i].y = curr.getInt("y");
				objectBuffer[idx].mod[i].pickupTime = curr.getLong("pickupTime");
				objectBuffer[idx].mod[i].creationTime = curr.getLong("creationTime");
				objectBuffer[idx].mod[i].explosionTime = curr.getLong("explosionTime");
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		
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
	public void playSound(SoundType soundType) {
		if (sounds)
			soundSys.playSound(soundType);
	}


	/**
	 * Blocking! 
	 */
	@Override
	public List<java.util.Map.Entry<Integer, String>> getHighScores() {
		// Wait for the remote server until it updates high scores.
		while (gameState == GameState.GAMEOVER_NEW_HIGHSCORE)
		{
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return Server.getHighScores();
	}
	
	/**
	 * Blocking!  
	 */
	@Override
	public int getHighestScore()
	{
		return Server.getHighestScore();
	}


	@Override
	public EnumMap<PlayerAction, Integer> getKeyboardSettings() {
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
	public void dispatchKeyEvent(KeyEvent e, boolean pressed) {
		if (gameState == GameState.RUNNING)
			for(int i = 0; i < playercontrollers.length; ++i)
				playercontrollers[i].dispatchKeyEvent(e, pressed);
	}


	public void changeGameState(GameState gs)
	{	
		gameState = gs;
		gui.setGameState(gameState);
	}

	private void saveConfig() {
		prefs.putInt("p1left", keyboardSettings.get(PlayerAction.P1LEFT));
		prefs.putInt("p1right", keyboardSettings.get(PlayerAction.P1RIGHT));
		prefs.putInt("p1fire", keyboardSettings.get(PlayerAction.P1FIRE));
		prefs.putInt("p2left", keyboardSettings.get(PlayerAction.P2LEFT));
		prefs.putInt("p2right", keyboardSettings.get(PlayerAction.P2RIGHT));
		prefs.putInt("p2fire", keyboardSettings.get(PlayerAction.P2FIRE));
		
		prefs.put("recIP0", recentlyUsedIP[0]);
		prefs.put("recIP1", recentlyUsedIP[1]);
		prefs.put("recIP2", recentlyUsedIP[2]);
		prefs.put("recIP3", recentlyUsedIP[3]);
		
		String skill = null;
		switch (gameSkill)
		{
			case EASY: skill = "EASY"; break;
			case NORMAL: skill = "NORMAL"; break;
			case HARD: skill = "HARD"; break;
		}
		prefs.put("gameSkill", skill);
		
		prefs.putBoolean("sounds", sounds);
	}
	
	@Override
	public void pauseRequest() {
		switch (gameState)
		{	
			case RUNNING:
			case WAITING:
				server.pauseRequest(this);
			default:
		}
	}


	@Override
	public void startRequest() {
		switch (gameState)
		{
			case PAUSED:
				server.startRequest(this);
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
		
		if (vc != null)
		{
			vc.terminate();
			vc = null;
		}
	}


	@Override
	public void newGame(GameType gt) {
		resetGameState();
		
		AllServerInterfaces temp = new Server(gt, gameSkill, this);
		
		server = temp;
		
		if (gt == GameType.MULTI_NETWORK)
		{
			vc = new VirtualClient(temp, 47987, 100);
			
			temp.setClient2(vc);

			Thread t = new Thread(vc);
			t.start();
		}
		
		if (gt == GameType.MULTI_LOCAL)
		{
			playercontrollers = new PlayerController[2];
			
			playercontrollers[1] = new PlayerController(temp);
			playercontrollers[1].bindKey(PlayerAction.P2FIRE, keyboardSettings.get(PlayerAction.P2FIRE));
			playercontrollers[1].bindKey(PlayerAction.P2LEFT, keyboardSettings.get(PlayerAction.P2LEFT));
			playercontrollers[1].bindKey(PlayerAction.P2RIGHT, keyboardSettings.get(PlayerAction.P2RIGHT));
		}
		else playercontrollers = new PlayerController[1];
		
		playercontrollers[0] = new PlayerController(temp);
		playercontrollers[0].bindKey(PlayerAction.P1FIRE, keyboardSettings.get(PlayerAction.P1FIRE));
		playercontrollers[0].bindKey(PlayerAction.P1LEFT, keyboardSettings.get(PlayerAction.P1LEFT));
		playercontrollers[0].bindKey(PlayerAction.P1RIGHT, keyboardSettings.get(PlayerAction.P1RIGHT));
		
		Thread t = new Thread(server);
		t.start();
		
		server.startRequest(this);
	}


	@Override
	public void joinGame(String ipv4) {
		resetGameState();
		
		if (!Arrays.asList(recentlyUsedIP).contains(ipv4))
		{
			System.arraycopy(recentlyUsedIP, 0, recentlyUsedIP, 1, 3);
		}
		else
		{
			for(int i = 3, j = 3; i > 0; --i, --j)
			{
				if (recentlyUsedIP[i].equals(ipv4)  && i == j)
					--j;
				
				recentlyUsedIP[i] = recentlyUsedIP[j]; 
			}
		}
		
		recentlyUsedIP[0] = ipv4;
		gui.setRecentIPs(recentlyUsedIP);
		
		VirtualServer vs = new VirtualServer(this, ipv4, 47987, 100);
		server = vs;
		
		playercontrollers = new PlayerController[1];
		
		playercontrollers[0] = new PlayerController(vs);
		playercontrollers[0].bindKey(PlayerAction.P1FIRE, keyboardSettings.get(PlayerAction.P1FIRE));
		playercontrollers[0].bindKey(PlayerAction.P1LEFT, keyboardSettings.get(PlayerAction.P1LEFT));
		playercontrollers[0].bindKey(PlayerAction.P1RIGHT, keyboardSettings.get(PlayerAction.P1RIGHT));
		
		Thread t = new Thread(vs);
		t.start();
	}


	@Override
	public void bindKey(PlayerAction action, Integer key) {
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
