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

/**
 * This class functions as a bridge between the actual {@link Server} and the {@link GUI}.
 * The application's entry point is defined here and this is where GUI gets instantiated.
 * This class configures and sets up network controllers if necessary, as well as player
 * controller(s), and the appropriate server.   
 * 
 * @author András
 *
 */
public class Client implements ClientForGUI, ClientForServer {
	/**
	 * To store preferences (eg. keyboard settings)
	 */
	private final Preferences prefs = Preferences.userRoot().node("space_battle");
	
	/**
	 * Actual game state
	 */
	private GameState gameState = GameState.NONE;
	
	/**
	 * Actual game skill
	 */
	private GameSkill gameSkill = GameSkill.NORMAL;
	
	/**
	 * Whether sound effects are enabled or not
	 */
	private Boolean sounds = true;
	
	/**
	 * GUI instance
	 */
	private GUIForClient gui;
	
	/**
	 * SoundSystem instance
	 */
	private SoundSystemForClient soundSys;
	
	/**
	 * Server instance
	 */
	private ServerForClient server;
	
	/**
	 * Reference to the virtual client, if exists. It must be terminated
	 * after the server gets terminated. 
	 */
	private VirtualClient vc;
	
	/**
	 * PlayerController(s)
	 */
	private PlayerController[] playercontrollers;
	
	/**
	 * ObjectBuffers to hold client side equivalents of server objects
	 */
	private ObjectBuffer[] objectBuffer = new ObjectBuffer[3];
	
	/**
	 * States for all the object buffers
	 */
	private ObjectBufferState[] obStates = 
		{ObjectBufferState.INVALID, ObjectBufferState.INVALID, ObjectBufferState.INVALID};
	private Object objectBufferSyncObject = new Object();
	private String[] recentlyUsedIP = new String[4];
	private EnumMap<PlayerAction, Integer> keyboardSettings =
			new EnumMap<PlayerAction, Integer>(PlayerAction.class);

	/**
	 * Possible object buffer states
	 */
	private enum ObjectBufferState
	{
		USED, VALID, INVALID
	}
	
	/**
	 * Application's entry point.
	 * 
	 * @param args unused
	 */
	public static void main(String[] args) {
		new Client();
	}


	/**
	 * Constructor.
	 * 
	 * Loads previous run configuration, instantiates and configures GUI
	 * and starts it's thread. Initializes object buffers. 
	 */
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

	/**
	 * Initiate shutdown process from {@link GUI}.
	 */
	public void terminate() {
		if (gui != null)
			gui.terminate();

		if (server != null)
			server.terminate();
		
		saveConfig();
	}

	/**
	 * Loads previous run configuration from registry.
	 */
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

	/**
	 * Called periodically by the actual {@link Server} instance. It receives and parses a 
	 * <a href="http://en.wikipedia.org/wiki/JSON">JSON string</a>, which
	 * contains all the information from the server objects which is necessary for the {@link GUI} to draw
	 * and animate the objects.
	 * 
	 * @param JSONtext the <a href="http://en.wikipedia.org/wiki/JSON">JSON string</a> to be parsed
	 */
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


	/**
	 * Forwards the servers request to the {@link SoundSystem} to play the given type of sound.
	 * 
	 * @param soundType type of the sound, defined in {@link SoundType} enum
	 */
	@Override
	public void playSound(SoundType soundType) {
		if (sounds)
			soundSys.playSound(soundType);
	}


	/**
	 * Request the high score table from global high-score FTP server.
	 * 
	 * @return a List of Map Entries, which contain name-high score pairs.
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
	 * Requests the highest score from gloval high-score FTP server.
	 * 
	 * @return the highest score
	 */
	@Override
	public int getHighestScore()
	{
		return Server.getHighestScore();
	}


	/**
	 * Returns the current keyboard bindings. Called by the GUI.
	 * 
	 * @return {@link PlayerAction}-<a href="http://docs.oracle.com/javase/7/docs/api/java/awt/event/KeyEvent.html">KeyCode</a>
	 * pairs in an EnumMap
	 */
	@Override
	public EnumMap<PlayerAction, Integer> getKeyboardSettings() {
		if (keyboardSettings == null)
			loadConfig();
		
		return keyboardSettings;
	}

	/**
	 * Returns the newest available {@link ObjectBuffer}. Called by the GUI.
	 * 
	 * @return the newest ObjectBuffer
	 */
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


	/**
	 * Dispatches a {@link KeyEvent} to the {@link PlayerController}(s). Called by the GUI.
	 * 
	 * @param e the {@link KeyEvent} to be dispatched
	 * @param pressed whether the key is being pressed or being released
	 */
	@Override
	public void dispatchKeyEvent(KeyEvent e, boolean pressed) {
		if (gameState == GameState.RUNNING)
			for(int i = 0; i < playercontrollers.length; ++i)
				playercontrollers[i].dispatchKeyEvent(e, pressed);
	}


	/**
	 * Set current game state by the server.
	 * 
	 * @param gs new game state
	 * 
	 * @see GameState
	 */
	public void changeGameState(GameState gs)
	{	
		gameState = gs;
		gui.setGameState(gameState);
	}

	/**
	 * Save current configuration at exit to the registry.
	 */
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
	
	/**
	 * Dispatch a request from GUI to the Server to pause the game. 
	 */
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


	/**
	 * Dispatch a request from GUI to the Server to start the game. 
	 */
	@Override
	public void startRequest() {
		switch (gameState)
		{
			case PAUSED:
				server.startRequest(this);
			default:
		}
	}
	
	/**
	 * Immediately terminates the current game and cleans up. Called by the GUI.
	 */
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


	/**
	 * Starts a new server of the appropriate game type. This function initializes the {@link VirtualClient} and starts
	 * its thread for player 2 if necessary, and configures the {@link PlayerControllers}(s) as well.
	 * Called by the GUI.
	 * 
	 * @param gt game type
	 * 
	 * @see GameType
	 */
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


	/**
	 * Join to an existing network game. Called by the GUI. Sets up the {@link VirtualServer} and starts its
	 * thread. Configures {@link PlayerController}.
	 * 
	 * @param ipv4 IPv4 address to connect to
	 */
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


	/**
	 * Bind an action to a key. Called by the GUI.
	 * 
	 * @param action {@link PlayerAction} to bind
	 * @param key KeyCode to bind to
	 * 
	 * @see KeyEvent
	 */
	@Override
	public void bindKey(PlayerAction action, Integer key) {
		keyboardSettings.put(action, key);
	}


	/**
	 * Sets game skill. This won't affect the current game. Called by the GUI.
	 * 
	 * @param gs {@link GameSkill} to change to
	 */
	@Override
	public void setDifficulty(GameSkill gs) {
		gameSkill = gs;
	}


	/**
	 * Enable or disable sound effects. Called by the GUI.
	 * 
	 * @param val sound effects are enabled if true
	 */
	@Override
	public void setSound(Boolean val) {
		sounds = val;
	}


	/**
	 * Send a name to the global high-score FTP server, when a new high score is achieved. Called by the GUI.
	 * 
	 * @param name Player's name
	 */
	@Override
	public void sendName(String name) {
		server.sendName(name);
	}

}
