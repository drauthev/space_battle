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
	private void trackChanges(){	//TODO
		moveNPCs();
		controlPlayers();	// move/shoot with players according to the pushed buttons
		detectCollision();	// between players and hostiles
		detectHits();
		detectModifierPickUps();
				
		client1.updateObjects(allToJSON(listOfPlayers, listOfNPCs));
		// meghivni az updateGameState-et TODO
		
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
		if(player1MovingLeft){//player1MovingLeft == 
			listOfPlayers.get(0).moveLeft(); // 0th index is player1
		}
		if(player1MovingRight)
			listOfPlayers.get(0).moveRight();
		if(player1Shooting){
			Projectile shot = listOfPlayers.get(0).shoot();
			listOfProjectiles.add(shot);
			//playSound(shot);
		}
		// Player 2 if multiplayer
		if( type==(GameType.MULTI_LOCAL) || type==GameType.MULTI_NETWORK){
			if(player2MovingLeft)
				listOfPlayers.get(1).moveLeft();	// 1st index is player1
			if(player2MovingRight)
				listOfPlayers.get(1).moveRight();
			if(player2Shooting){
				Projectile shot = listOfPlayers.get(1).shoot();
				listOfProjectiles.add(shot);
				//playSound(shot);
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
					
					// changing the list elements to the modified ones
					listOfPlayers.set(1, tempPlayer);
					listOfNPCs.set(i, tempNPC);
				}
			}
			//NOTE: if an NPC collides with both players, its explosionTime will be the time it's colliding with Player2 - no problem	
		}
	}
	
	private void detectHits(){
		
	}
	
	private void detectModifierPickUps(){
		
		for(int i=0; i<listOfModifiers.size(); i++){
			Modifier tempMod = listOfModifiers.get(i);
			if( tempMod.getHitBox().isCollision(listOfPlayers.get(0))){
				//TODO
			}
		}
		
	}
	
	private void isGameOver(){ //TODO
		
		if(type == GameType.SINGLE){
			if(listOfPlayers.get(0).getLives() == 0){
				//CALL CLIENT'S GAMEOVER() !
			}
		}
		else // multi
			if(listOfPlayers.get(0).getLives() == 0 || listOfPlayers.get(1).getLives() == 0){
				//CALL CLIENT'S GAMEOVER() !
			}
	}
	
	// Game experience modifier functions
	
	// JSON converters
	public String allToJSON(List<Player> listOfPlayers, List<NPC> listOfNPCs){
		JSONObject all = new JSONObject();
		
		playersToJSON(listOfPlayers);
		try {
			all.put("score", score);
			all.put("tick", java.lang.System.currentTimeMillis());
			// Putting all JSONArray to a big JSONObject
			all.put("Players", playersToJSON(listOfPlayers));
			all.put("NPCs", npcsToJSON(listOfNPCs));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return all.toString();
	}
	
	public JSONArray playersToJSON(List<Player> list){
		JSONObject currentPlayer = new JSONObject();
		ArrayList<JSONObject> playerList = new ArrayList<>();
		JSONArray playersJSON = new JSONArray();
		Player temp = list.get(0);
		try {
		currentPlayer.put("ID", 1);

		currentPlayer.put("lives", temp.getLives());
		currentPlayer.put("x", temp.getCoordX());
		currentPlayer.put("y", temp.getCoordY());
		currentPlayer.put("hitTime", temp.getHitTime());
		currentPlayer.put("explosionTime", temp.getExplosionTime());
		playerList.add(currentPlayer); // player1 added
		
		if(type == GameType.MULTI_LOCAL || type == GameType.MULTI_NETWORK){
			temp = list.get(1);
			currentPlayer.put("ID", 2);
			currentPlayer.put("lives", temp.getLives());
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
					currentNPC.put("type", 1);
				else if( temp instanceof HostileType2)
					currentNPC.put("type", 2);
				else
					currentNPC.put("type", 3);
				
				currentNPC.put("x", temp.getCoordX());
				currentNPC.put("y", temp.getCoordY());
				currentNPC.put("hitTime", temp.getHitTime());
				currentNPC.put("explosionTime", temp.getExplosionTime());
				// add each NPC JSONObject to the arraylist<JSONObject>
				npcList.add(currentNPC);
			}
			// each NPC JSONObject to the JSONArray
			npcsJSON = new JSONArray(npcList);
		}
		catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			}
		return npcsJSON;
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
				x += HostileType1.getHostiletype1width()/2;	// perem�?©rt�?©kek �?­gy x=0+hostileType1Width/2 �?‰S x=gameFieldWidth-hostileType1Width/2
				listOfNPCs.add( new HostileType1(x,Constants.gameFieldHeigth-HostileType1.getHostiletype1heigth()) );	// TODO: az Å±rhaj�?³k be�?ºsz�?¡sa miatt t�?ºlsk�?¡l�?¡zni
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
		Timer timer = new Timer(false);	// nem d�?©monk�?©nt indul, capable of keeping the application from termination
		
		// Update game state at a given rate
        timer.scheduleAtFixedRate(taskTrackChanges, 0, 1000/Constants.framePerSecond);	// 33.3333 millisecundumunk van megcsin�?¡lni, megjelen�?­teni mindent
		// Spawn enemies at given rate
        timer.scheduleAtFixedRate(taskSpawnHostileType1, 0, HostileType1.getSpawningFrequency());
        
		
	}
	
	@Override
	public void terminate(){
		
	}
	
	public void startRequest(){
		isRunning = true;
		// TODO: visszajelezni a klienseknek
	}
	
	public void pauseRequest(){
		isRunning = false;	
		// TODO: visszajelezni a klienseknek
	}
	
	public void disconnect(){
		//TODO
	}

	// Implementing ServerForPlayerController Interface
	// ------------------------------------------------------------------------------------------------------------------
	
	//TODO: AZ AL�?�BB KOMMENTEZETT SZAROKAT BERAKNI A TIMER TASKOKHOZ%!!!
	public void moveLeft(int playerID){	
		if(playerID == 1)
			player1MovingLeft = true;
		else if(playerID == 2)
			player2MovingLeft = true;
//		int x = listOfPlayers.get(playerID).getCoordX();
//		
//		if( x >= (0 + Player.getPlayerwidth()/2 + Player.getHorizontalMoveQuantity()) )	// moving left only till reaching the edge
//			listOfPlayers.get(playerID).moveLeft();
	}
	
	public void releaseLeft(int playerID){
		if(playerID == 1)
			player1MovingLeft = false;
		else if(playerID == 2)
			player2MovingLeft = false;
	}
	
	public void moveRight(int playerID){
		if(playerID == 1)
			player1MovingRight = true;
		else if(playerID == 2)
			player2MovingRight = true;
//		int x = this.getListOfPlayers().get(playerID).getCoordX();
//		
//		if (x <= gameFieldWidth - Player.getPlayerwidth()/2 - Player.getHorizontalMoveQuantity())
//			this.getListOfPlayers().get(playerID).moveRight();
	}
	
	public void releaseRight(int playerID){
		if(playerID == 1)
			player1MovingRight = false;
		else if(playerID == 2)
			player2MovingRight = false;
	}
	
	public void fire(int playerID){
		if(playerID == 1)
			player1Shooting = true;
		else if(playerID == 2)
			player2Shooting = true;
	}
	
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

	public List<Player> getListOfPlayers() {
		return listOfPlayers;
	}
	
	public List<Projectile> getListOfProjectiles() {
		return listOfProjectiles;
	}
	
	public List<NPC> getListOfNPCs() {
		return listOfNPCs;
	}
	
	public List<Modifier> getListOfModifiers() {
		return listOfModifiers;
	}


	public String getPlayersJSON() {
		return playersJSON;
	}

	public String getNpcsJSON() {
		return npcsJSON;
	}


	public String getProjectilesJSON() {
		return projectilesJSON;
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