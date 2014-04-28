package server;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import enums.*;
import interfaces.AllServerInterfaces;
import interfaces.ClientForServer;
import server.game_elements.Fastener;
import server.game_elements.HostileType1;
import server.game_elements.HostileType2;
import server.game_elements.Modifier;
import server.game_elements.NPC;
import server.game_elements.Player;
import server.game_elements.Projectile;
import server.game_elements.ProjectileGoingDown;
import server.game_elements.ProjectileGoingUp;
import sound.SoundType;

public class Server implements AllServerInterfaces
{
	// Game Options
	private GameType type;
	private GameSkill difficulty;
	//
	private int score;
	private boolean isRunning;	
	// Booleans for player controlling
	private boolean player1MovingLeft;
	private boolean player1MovingRight;
	private boolean player1Shooting;
	private boolean player2MovingLeft;
	private boolean player2MovingRight;
	private boolean player2Shooting;
	// Declaring lists of GameElements
	private List<Player> listOfPlayers;
	private List<Projectile> listOfProjectiles;
	private List<Modifier> listOfModifiers;
	private List<NPC> listOfNPCs;
	// JSON arrays of GameElements
	private String playersJSON;
	private String projectilesJSON;
	private String modifiersJSON;
	private String npcsJSON;
	// Game Experience modifying flags
	private boolean isFastened;
	private boolean laserBeam;
	private boolean player1HasShield;
	private boolean player2HasShield;
	private boolean changedControls;
	private boolean leftRightIsChanged;
	private boolean aggressiveHostiles;
	private boolean noAmmo;
	private boolean halfScores;
	// Client interfaces
	private ClientForServer client1;
	private ClientForServer client2;
	//
	private boolean initState = true; // at the start of the game, has to call client1.updateObjects(), before changing GameState to RUNNING; or else the GUI cannot draw
	private Timer timer;

	// Constructor
	public Server(GameType type, GameSkill difficulty, ClientForServer cl1){
		// TODO: kliens1-et tárolni, kliens2 a setClient2-ben, ekkor ugyanis még nem ismert!
		this.type = type;
		this.difficulty = difficulty;
		client1 = cl1;
		score = 0;
		isRunning = false;
		player1MovingLeft = false;
		player1MovingRight = false;
		player1Shooting = false;
		player2MovingLeft = false;
		player2MovingRight = false;
		player2Shooting = false;
		// Initializing lists of GameElements
		listOfPlayers = new ArrayList<Player>();
		listOfProjectiles = new ArrayList<Projectile>();
		listOfModifiers = new ArrayList<Modifier>();
		listOfNPCs = new ArrayList<NPC>();
		
		// Setting game parameters according to difficulty level
		//TODO object speeds, spawning rates, etc
		
		// Spawning player/players
		if( type == GameType.SINGLE ){
			listOfPlayers.add(new Player(Constants.gameFieldWidth/2, Player.getPlayerheight()/2 + 2, 1));	// the only player to the middle..with playerID==1
		}
		else if( type==(GameType.MULTI_LOCAL) || type==(GameType.MULTI_NETWORK) ){
			listOfPlayers.add(new Player(Constants.gameFieldWidth/4, Player.getPlayerheight()/2 + 2, 1));	// with playerID==1
			listOfPlayers.add(new Player(Constants.gameFieldWidth/4*3, Player.getPlayerheight()/2 + 2, 2)); // with playerID==2
		}
		
		//TODO: registrating clients

	};
	
	
	// Timer-driven methods
	private void trackChanges(){
		moveNPCs();
		controlPlayers();	// move/shoot with players according to the pushed buttons
		detectCollision();	// between players and hostiles
		detectHits();
		detectModifierPickUps();
		
		removeNonExistentObjects();
				
		client1.updateObjects(allToJSON(listOfPlayers, listOfNPCs));
		
		isGameOver();

	}
		
	private void moveNPCs(){
		// Moving Projectiles
		for(int i=0; i < listOfProjectiles.size(); i++){
    		listOfProjectiles.get(i).autoMove();
    	}
    	// Moving hostile spaceships
    	for(int i=0; i < listOfNPCs.size(); i++){
    		listOfNPCs.get(i).autoMove();
    	}
    	// Moving Modifiers
    	for(int i=0; i < listOfModifiers.size(); i++){
    		listOfNPCs.get(i).autoMove();
    	}
	}
	
	private void controlPlayers(){
		// Player 1
		if(player1MovingLeft){		
			listOfPlayers.get(0).moveLeft(); // 0th index is player1
		}
		if(player1MovingRight)
			listOfPlayers.get(0).moveRight();
		if(player1Shooting){
			Projectile shot = listOfPlayers.get(0).shoot();
			listOfProjectiles.add(shot);
			// playing sounds
			client1.playSound(SoundType.shoot);
			if(type == GameType.MULTI_NETWORK){
				client2.playSound(SoundType.shoot);
			}
		}
		// Player 2 if multiplayer
		if( type==GameType.MULTI_LOCAL || type==GameType.MULTI_NETWORK){
			if(player2MovingLeft)
				listOfPlayers.get(1).moveLeft();	// 1st index is player1
			if(player2MovingRight)
				listOfPlayers.get(1).moveRight();
			if(player2Shooting){
				Projectile shot = listOfPlayers.get(1).shoot();
				listOfProjectiles.add(shot);
				// playing sounds
				client1.playSound(SoundType.shoot);
				if(type == GameType.MULTI_NETWORK){
					client2.playSound(SoundType.shoot);
				}
			}
		}
	}
	
	private void detectCollision(){
		for(int i=0; i<listOfNPCs.size(); i++){
			NPC tempNPC = listOfNPCs.get(i);
			Player tempPlayer = listOfPlayers.get(0);
			// check collisions for Player1
			if( tempNPC.getHitBox().isCollision(tempPlayer)==true ){ // there is collision
				// exploding the NPC
				tempNPC.setExplosionTime(java.lang.System.currentTimeMillis());
				
				tempPlayer.setLives(tempPlayer.getLives() - 1);
				// checking Player's lives
				// if it's 0, explode it; else indicating a hit
				if(tempPlayer.getLives() == 0)
					tempPlayer.setExplosionTime(java.lang.System.currentTimeMillis());
				else
					tempPlayer.setHitTime(java.lang.System.currentTimeMillis());
				
				// changing the list elements to the modified ones
				listOfPlayers.set(0, tempPlayer);
				listOfNPCs.set(i, tempNPC);
			}
				
			// check collisions for Player2 if gametype is multi
			if( type==(GameType.MULTI_LOCAL) || type==(GameType.MULTI_NETWORK) ){
				tempPlayer = listOfPlayers.get(1);
				
				if( tempNPC.getHitBox().isCollision(tempPlayer)==true ){ // there is collision
					// exploding the NPC
					tempNPC.setExplosionTime(java.lang.System.currentTimeMillis());
					
					tempPlayer.setLives(tempPlayer.getLives() - 1);
					// checking Player's lives
					// if it's 0, explode it; else indicating a hit
					if(tempPlayer.getLives() == 0)
						tempPlayer.setExplosionTime(java.lang.System.currentTimeMillis());
					else
						tempPlayer.setHitTime(java.lang.System.currentTimeMillis());
					
					// changing the list elements to the modified ones //TODO: is this needed or the get() method gives references?
					listOfPlayers.set(1, tempPlayer);
					listOfNPCs.set(i, tempNPC);
				}
			}
			//NOTE: if an NPC collides with both players, its explosionTime will be the time it's colliding with Player2 - no problem	
		}
	}
	
	private void detectHits(){
		for(int i=0; i<listOfProjectiles.size(); i++){
			
			Projectile proj = listOfProjectiles.get(i);
			// Projectile is shot by a player
			if( proj instanceof ProjectileGoingUp ){
				
				for(int j=0; j<listOfNPCs.size(); j++){
					NPC npc = listOfNPCs.get(j);
					int npcLives = npc.getLives();
					
					if ( proj.isHit(npc) ){ // Given NPC is hit
						npcLives--;
						if( npcLives == 0 ){
							npc.setExplosionTime(java.lang.System.currentTimeMillis());
						}
						else{
							npc.setHitTime(java.lang.System.currentTimeMillis());
							npc.setLives(npcLives);
							// decreasing lives is unnecessary as only explosionTime is propagated to the GUI
						}
						listOfNPCs.set(j, npc);
						listOfProjectiles.remove(i); // remove projectile which hit
					}
					// nothing to do if there is no hit
				}
			}
			
			// Projectile is shot by an NPC
			else{ 
				if( proj instanceof ProjectileGoingDown ){
					for(int j=0; j<listOfPlayers.size(); j++){
						Player player = listOfPlayers.get(j);
						int playerLives = player.getLives();
						
						if (proj.isHit(player) ){//TODO: lehet, hogy y coordot itt kellene nezni, ProjGoingDown isHit()-jeben nem, es csak azokra meghivni
							player.setLives(--playerLives);
							if( playerLives == 0 ){
								player.setExplosionTime(java.lang.System.currentTimeMillis());
							}
							else{
								player.setHitTime(java.lang.System.currentTimeMillis());
							}
							listOfPlayers.set(j, player);
							listOfProjectiles.remove(i); // remove projectile which hit
						}
						// nothing to do if there is no hit
					}
				}
				
			}
		}
	}
	
	private void detectModifierPickUps(){
		for(int i=0; i<listOfModifiers.size(); i++){
			Modifier tempMod = listOfModifiers.get(i);
			if( tempMod.getHitBox().isCollision(listOfPlayers.get(0))){
				//TODO
			}
		}
		
	}
	
	private void removeNonExistentObjects(){ // removes objects after a certain time, for animation purposes
		long currentTime = java.lang.System.currentTimeMillis();
		
		// Remove exploded NPCs
		for(int i=0; i<listOfNPCs.size(); i++){
			// removing from list, if !!3sec!!? is lated since explosion
			if(currentTime - listOfNPCs.get(i).getExplosionTime() > 3000){
				listOfNPCs.remove(i);
			}
		}
		
		// Remove exploded Players
		for(int i=0; i<listOfPlayers.size(); i++){
			// removing from list, if !!3sec!!? is lated since explosion
			if(currentTime - listOfPlayers.get(i).getExplosionTime() > 3000){
				listOfPlayers.remove(i);
			}
		}
		
		// Remove picked-up modifiers
		//TODO
		
		
	}
	
	private void isGameOver(){
		
		if(type == GameType.SINGLE){
			if(listOfPlayers.get(0).getLives() == 0){
				//TODO: HIGHSCORE eseten GAMEOVER_HIGHSCORE-t hivni!
				client1.changeGameState(GameState.GAMEOVER);
				if(type == GameType.MULTI_NETWORK){
					client2.changeGameState(GameState.GAMEOVER); //TODO: mind2ot meg kell hivni?
				}
			}
		}
		else // multi
			if(listOfPlayers.get(0).getLives() == 0 || listOfPlayers.get(1).getLives() == 0){
				//TODO: HIGHSCORE eseten GAMEOVER_HIGHSCORE-t hivni!
				client1.changeGameState(GameState.GAMEOVER);
				if(type == GameType.MULTI_NETWORK){
					client2.changeGameState(GameState.GAMEOVER); //TODO: mind2ot meg kell hivni?
				}
			}
	}
	
	// Game experience modifier functions
	
	// JSON converters
	private String allToJSON(List<Player> listOfPlayers, List<NPC> listOfNPCs){
		JSONObject all = new JSONObject();
		
		playersToJSON(listOfPlayers);
		try {
			all.put("score", score);
			all.put("currentTick", java.lang.System.currentTimeMillis()); // FIXME: CAUSES exception at Client.updateObjects, it its; game doesn't start
			// Putting all JSONArray to a big JSONObject
			all.put("players", playersToJSON(listOfPlayers));
			all.put("npcs", npcsToJSON(listOfNPCs));
			all.put("projectiles", projectilesToJSON(listOfProjectiles));
			all.put("modifiers", modifiersToJSON(listOfModifiers));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(all.toString());
		return all.toString();
	}
	
	private JSONArray playersToJSON(List<Player> list){
		JSONObject currentPlayer = new JSONObject();
		ArrayList<JSONObject> playerList = new ArrayList<>();
		JSONArray playersJSON = new JSONArray();
		Player temp = list.get(0);
		try {
		currentPlayer.put("className", "Player"); // ososztalyban van Andrasnal a className, azer kell csak
		currentPlayer.put("id", 0);
		currentPlayer.put("numberOfLives", temp.getLives());
		currentPlayer.put("x", temp.getCoordX());
		currentPlayer.put("y", temp.getCoordY());
		currentPlayer.put("hitTime", temp.getHitTime());
		currentPlayer.put("explosionTime", temp.getExplosionTime());
		playerList.add(currentPlayer); // player1 added
		
		if(type == GameType.MULTI_LOCAL || type == GameType.MULTI_NETWORK){
			temp = list.get(1);
			currentPlayer.put("className", "Player");
			currentPlayer.put("id", 1);
			currentPlayer.put("numberOfLives", temp.getLives());
			currentPlayer.put("x", temp.getCoordX());
			currentPlayer.put("y", temp.getCoordY());
			currentPlayer.put("hitTime", temp.getHitTime());
			currentPlayer.put("explosionTime", temp.getExplosionTime());
			playerList.add(currentPlayer); // player2 added
		}
		
		playersJSON = new JSONArray( playerList );
		}
		catch (JSONException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		}
		return playersJSON;
	}
	
	private JSONArray npcsToJSON(List<NPC> list){
		JSONObject currentNPC = new JSONObject();
		ArrayList<JSONObject> npcList = new ArrayList<>();
		JSONArray npcsJSON = new JSONArray();
		NPC temp;
		try {
			int listSize = list.size();
			for( int i=0; i<listSize-1; i++){
				temp = list.get(i);
				// type for GUI to paint the proper skin
				if( temp instanceof HostileType1)
					currentNPC.put("className", "HostileType1");
				else if( temp instanceof HostileType2)
					currentNPC.put("className", "HostileType2");
				else
					currentNPC.put("className", "HostileType3");
				
				currentNPC.put("x", temp.getCoordX());
				currentNPC.put("y", temp.getCoordY());
				currentNPC.put("creationTime", temp.getCreationTime());
				currentNPC.put("hitTime", temp.getHitTime());
				currentNPC.put("explosionTime", temp.getExplosionTime());
				// add each NPC JSONObject to the arraylist<JSONObject>
				npcList.add(currentNPC);
			}
			// each NPC JSONObject to the JSONArray
			npcsJSON = new JSONArray(npcList);
		}
		catch (JSONException e) {
			e.printStackTrace();
			}
		return npcsJSON;
	}
	
	private JSONArray projectilesToJSON(List<Projectile> list){
		JSONArray projectilesJSON = new JSONArray();
		return projectilesJSON;
		//TODO: csak dummy
	}
	
	private JSONArray modifiersToJSON(List<Modifier> list){
		JSONArray modifiersJSON = new JSONArray();
		return modifiersJSON;
		//TODO: csak dummy
	}
	// Implementing ServerForClient Interface
	// ------------------------------------------------------------------------------------------------------------------
	@Override
	public void run(){
		
		// Defining TimerTasks
		TimerTask taskTrackChanges = new TimerTask() {
		    @Override
		    public void run() {
		    	if(isRunning){
		    	trackChanges();
		    	}
		    }
		};
		
		//TODO: shoot with NPCS...
		
			// Hostile-spawner tasks
		TimerTask taskSpawnHostileType1 = new TimerTask() {
			@Override
			public void run() {
				if(isRunning){
				int x;
				x = (int)(Math.random()*(Constants.gameFieldWidth - HostileType1.getHostiletype1width()));
				x += HostileType1.getHostiletype1width()/2;	// peremertekek �?­gy x=0+hostileType1Width/2 �?‰S x=gameFieldWidth-hostileType1Width/2
				listOfNPCs.add( new HostileType1(x,Constants.gameFieldHeigth-HostileType1.getHostiletype1heigth(), difficulty) );	// TODO: az Å±rhaj�?³k be�?ºsz�?¡sa miatt t�?ºlsk�?¡l�?¡zni
				}
			}
		};
				
		TimerTask taskSpawnHostileType2 = new TimerTask() {
			@Override
			public void run() {
				if(isRunning){
				// TODO Auto-generated method stub
				}
			}
		};
			
			// Modifier-spawner tasks
		TimerTask taskSpawnFastener = new TimerTask() {
			@Override
			public void run() {
				if(isRunning){
				int x;
				x = (int)(Math.random()*(Constants.gameFieldWidth - Modifier.getModifierwidth()) );
				listOfModifiers.add( new Fastener(x,Constants.gameFieldHeigth-Modifier.getModifierheigth()) );
				}
			}
		};
		
		// Starting Timer 
		timer = new Timer(false);	// nem d�?©monk�?©nt indul, capable of keeping the application from termination
		
		// Update game state at a given rate
        timer.scheduleAtFixedRate(taskTrackChanges, 0, 1000/Constants.framePerSecond);	// 33.3333 millisecundumunk van megcsin�?¡lni, megjelen�?­teni mindent
		// Spawn enemies at given rate
        timer.scheduleAtFixedRate(taskSpawnHostileType1, 0, HostileType1.getSpawningFrequency());
        
		
	}
	
	@Override
	public void terminate(){
		// deleting timer thread
		timer.cancel();
		// terminating second client if exists
		if(type == GameType.MULTI_NETWORK){
			client2.terminate();
		}
	}
	
	@Override
	public void startRequest(ClientForServer c){
		if(initState == true){
			initState = false;
			client1.updateObjects(allToJSON(listOfPlayers, listOfNPCs));
		}
		if(type == GameType.SINGLE || type == GameType.MULTI_LOCAL){
			isRunning = true;
			client1.changeGameState(GameState.RUNNING);
		}
		else{ // network, 2 clients
			//TODO vizsgalni, h mely kliens hivott, ready, wait stb
		}
	}
	
	@Override
	public void pauseRequest(ClientForServer c){
		if(type == GameType.SINGLE || type == GameType.MULTI_LOCAL){
			isRunning = false;
			client1.changeGameState(GameState.PAUSED);
		}
		else{
		// TODO:
		}	
	}
	
	@Override
	public void disconnect(){
		//TODO
	}

	// Implementing ServerForPlayerController Interface
	// ------------------------------------------------------------------------------------------------------------------
	@Override
	public void moveLeft(int playerID){	
		if(playerID == 1)
			player1MovingLeft = true;
		else if(playerID == 2)
			player2MovingLeft = true;

	}
	
	@Override
	public void releaseLeft(int playerID){
		if(playerID == 1)
			player1MovingLeft = false;
		else if(playerID == 2)
			player2MovingLeft = false;
	}
	
	@Override
	public void moveRight(int playerID){
		if(playerID == 1)
			player1MovingRight = true;
		else if(playerID == 2)
			player2MovingRight = true;
	}
	
	@Override
	public void releaseRight(int playerID){
		if(playerID == 1)
			player1MovingRight = false;
		else if(playerID == 2)
			player2MovingRight = false;
	}
	
	@Override
	public void fire(int playerID){
		if(playerID == 1)
			player1Shooting = true;
		else if(playerID == 2)
			player2Shooting = true;
	}
	
	@Override
	public void releaseFire(int playerID){
		if(playerID == 1)
			player1Shooting = false;
		else if(playerID == 2)
			player2Shooting = false;
	}
	
	// Getters, Setters
	// ------------------------------------------------------------------------------------------------------------------
	public int getScore() {
		return score;
	}

	@Override
	public void sendName(String name) {
		// TODO Auto-generated method stub
		// TODO A végén setGameState(GameState.NONE) !!!
	}
	
	
	
	/* FTP szerveren lesznek tárolva a high score-ok, mondjuk egy highscores.txt
	 * nevű állományban. Innen kell minden egyes függvényhívásnál leszedni, ha pedig 
	 * van új high score, akkor azt beleírni egy új fájlba az FTP szerveren (más nevű fájl!),
	 * majd átnevezni arra, ami a tényleges high score fájl. Azért kell így,
	 * mert az átnevezés atomi művelet, viszont ha megszakadna a kapcsolat
	 * a fájl feltöltése közben, akkor baszhatnánk a korábbi eredményeket.
	 * 
	 * A szerver adatai:
	 * URL: ftp://drauthev.sch.bme.hu
	 * user: space_battle
	 * pw nincs!
	 */
	
	public static SortedMap<Integer, String> getHighScores()
	{
		// TODO Itt kell majd valami értelmes highscore táblát visszaadni...

		// TODO return előtt setGameState(GameStates.NONE) !!!
		return null;
	}
	
	public static int getHighestScore()
	{
		// TODO Ezt is meg kell csinálni faszául.
		return 0;
	}


	@Override
	public void setClient2(ClientForServer c2) {
		client2 = c2;	
	}

}