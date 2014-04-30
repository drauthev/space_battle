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
	private long player1ShootTime = 0;
	private long player2ShootTime = 0;
	private boolean player1Dead = false;
	private boolean player2Dead = true;
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
			listOfPlayers.add(new Player(Constants.gameFieldWidth/2, (Constants.gameFieldHeigth-Player.getPlayerheight()/2 - 60), 0));	// the only player to the middle..with playerID==1
		}
		else if( type==(GameType.MULTI_LOCAL) || type==(GameType.MULTI_NETWORK) ){
			listOfPlayers.add(new Player(Constants.gameFieldWidth/4, (Constants.gameFieldHeigth-Player.getPlayerheight()/2 - 60), 0));	// with playerID==1
			listOfPlayers.add(new Player(Constants.gameFieldWidth/4*3, (Constants.gameFieldHeigth-Player.getPlayerheight()/2 - 60), 1)); // with playerID==2
		}
	};
	
	
	// Timer-driven methods
	private void trackChanges(){
		moveNPCs();
		shootWithNPCs();
		controlPlayers();	// move/shoot with players according to the pushed buttons
		detectCollision();	// between players and hostiles
		detectHits();
		detectModifierPickUps();
		
		removeNonExistentObjects();
				
		client1.updateObjects(allToJSON());
		
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
	
	private void shootWithNPCs(){
		NPC temp;
		long lastShotTime;
		long currentTime;
		for(int i=0; i<listOfNPCs.size(); i++){
			temp = listOfNPCs.get(i);
			if(temp.getLives() > 0){ // "dead" hostiles are in the list for some time because of the explosion effect; this make sure they don't shoot
				lastShotTime = temp.getLastShotTime();
				currentTime = java.lang.System.currentTimeMillis();
				if(currentTime - lastShotTime > temp.getShootingFrequency()){ // shoot only if enough time has lasted
					temp.setLastShotTime(java.lang.System.currentTimeMillis());
					listOfProjectiles.add(temp.shoot());
					listOfNPCs.set(i, temp);
					// playing sounds
					client1.playSound(SoundType.shoot);
					if(type == GameType.MULTI_NETWORK){
						client2.playSound(SoundType.shoot);
					}
				}
			}
		}
	}
	
	private void controlPlayers(){		
		// Player 1 - // if SINGLE OR (if both players are alive, or the only alive is player2)
		if(listOfPlayers.size() == 2 || listOfPlayers.get(0).getID()==0 ){
			if(player1MovingLeft){	
				listOfPlayers.get(0).moveLeft(); // 0th index is player1
			}
			if(player1MovingRight)
				listOfPlayers.get(0).moveRight();
			if(player1Shooting){
				long currentTime = java.lang.System.currentTimeMillis();
				if(currentTime - player1ShootTime > Constants.timeBetweenShots){
					player1ShootTime = currentTime;
					Projectile shot = listOfPlayers.get(0).shoot();
					listOfProjectiles.add(shot);
					// playing sounds
					client1.playSound(SoundType.shoot);
					if(type == GameType.MULTI_NETWORK){
						client2.playSound(SoundType.shoot);
					}
				}	
			}
		}		
		// Player 2 if multiplayer
		if( type==GameType.MULTI_LOCAL || type==GameType.MULTI_NETWORK){
			if(listOfPlayers.size() == 2 || listOfPlayers.get(0).getID()==1){ // if both players are alive, or the only alive is player2
				if(player2MovingLeft)
					listOfPlayers.get(1).moveLeft();	// 1st index is player1
				if(player2MovingRight)
					listOfPlayers.get(1).moveRight();
				if(player2Shooting){
					long currentTime = java.lang.System.currentTimeMillis();
					if(currentTime - player2ShootTime > Constants.timeBetweenShots){
						player2ShootTime = currentTime;
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
		}
	}
	
	private void detectCollision(){
		NPC tempNPC;
		Player tempPlayer;
		
		for(int i=0; i<listOfPlayers.size(); i++){
			tempPlayer = listOfPlayers.get(i);
			for(int j=0; j<listOfNPCs.size(); j++){
				tempNPC = listOfNPCs.get(j);
				if( tempNPC.getLives() <= 0) continue; // dead hostiles which are in the list for animation purposes, can't hurt the players
				if( tempNPC.getHitBox().isCollision(tempPlayer)==true ){ // there is collision
					// exploding the NPC
					tempNPC.setLives(0);
					tempNPC.setExplosionTime(java.lang.System.currentTimeMillis());
					// playing sounds
					client1.playSound(SoundType.enemyExplosion);
					if(type == GameType.MULTI_NETWORK)	client2.playSound(SoundType.enemyExplosion);
					
					tempPlayer.setLives(tempPlayer.getLives() - 1);
					// checking Player's lives
					// if it's 0, explode it; else indicating a hit
					if(tempPlayer.getLives() == 0){
						if(tempPlayer.getID()==0)
							player1Dead = true;
						else
							player2Dead = true;
						tempPlayer.setExplosionTime(java.lang.System.currentTimeMillis());
					}
					else{
						tempPlayer.setHitTime(java.lang.System.currentTimeMillis());
					}
					// changing the list elements to the modified ones
					listOfPlayers.set(0, tempPlayer);
					listOfNPCs.set(i, tempNPC);
				}
			}
		}
	}
	
	private void detectHits(){
		Projectile proj;
		NPC npc;
		int npcLives;
		for(int i=0; i<listOfProjectiles.size(); i++){
			proj = listOfProjectiles.get(i);
			// Projectile is shot by a player
			if( proj instanceof ProjectileGoingUp ){
				for(int j=0; j<listOfNPCs.size(); j++){
					npc = listOfNPCs.get(j);
					npcLives = npc.getLives();
					if( npcLives <= 0 ) continue; // dead hostiles which are in the list for animation purposes, cannot absorb projectiles
					if( proj.isHit(npc) ){ // Given NPC is hit
						npcLives--;
						npc.setLives(npcLives);
						if( npcLives == 0 ){
							score += npc.getScoreIfDestroyed();
							npc.setExplosionTime(java.lang.System.currentTimeMillis());
							// playing sound
							client1.playSound(SoundType.enemyExplosion);
							if(type == GameType.MULTI_NETWORK) client2.playSound(SoundType.enemyExplosion);
						}
						else{
							npc.setHitTime(java.lang.System.currentTimeMillis());
							// playing sound
							client1.playSound(SoundType.beepA);
							if(type == GameType.MULTI_NETWORK) client2.playSound(SoundType.beepA);
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
							System.out.println("projectile hit");
							player.setLives(--playerLives);
							if( playerLives == 0 ){
								player.setExplosionTime(java.lang.System.currentTimeMillis());
								if(player.getID()==0)
									player1Dead = true;
								else
									player2Dead = true;
								// playing sound
								client1.playSound(SoundType.spaceShipExplosion);
								if(type == GameType.MULTI_NETWORK) client2.playSound(SoundType.spaceShipExplosion);
							}
							else{
								player.setHitTime(java.lang.System.currentTimeMillis());
								// playing sound
								client1.playSound(SoundType.beepA);
								if(type == GameType.MULTI_NETWORK) client2.playSound(SoundType.beepA);
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
		long explosionTime;
		
		// Remove GameElements which have left the track
		// NPCs
		for(int i=0; i<listOfNPCs.size(); i++){
			NPC temp = listOfNPCs.get(i);
			if( temp.getCoordY()-35 > Constants.gameFieldHeigth ){ //TODO: kicsit hack.. h lehetne szepen NPC "abstract static variable"
				listOfNPCs.remove(i);
			}
		}
		// Projectiles
		for(int i=0; i<listOfProjectiles.size(); i++){
			Projectile temp = listOfProjectiles.get(i);
			int y = temp.getCoordY();
			int x = temp.getCoordX();
			int height = Projectile.getProjectileheight();
			if( y-height/2 > Constants.gameFieldHeigth || y+height/2<0 || x<0 || x>Constants.gameFieldWidth){ //TODO: x koordinatak atgondol atlosra
				listOfProjectiles.remove(i);
			}
		}
		// Modifiers
		for(int i=0; i<listOfModifiers.size(); i++){
			Modifier temp = listOfModifiers.get(i);
			if( temp.getCoordY()-Modifier.getModifierheigth()/2 > Constants.gameFieldHeigth ){
				listOfModifiers.remove(i);
			}
		}

		
		// Remove exploded NPCs
		for(int i=0; i<listOfNPCs.size(); i++){
			// removing from list, if !!3sec!!? is lated since explosion
			explosionTime = listOfNPCs.get(i).getExplosionTime();
			if( (currentTime - explosionTime > 2000) && explosionTime!=0 ){//TODO: hany masodperc utan?
				listOfNPCs.remove(i);
			}
		}
		
		// Remove exploded Players
		for(int i=0; i<listOfPlayers.size(); i++){
			// removing from list, if !!3sec!!? is lated since explosion
			explosionTime = listOfPlayers.get(i).getExplosionTime();
			if( (currentTime -  explosionTime > 2000) && explosionTime!=0 ){//TODO: hany masodperc utan?
				listOfPlayers.remove(i);
			}
		}
		
		// Remove picked-up modifiers
		//TODO
		
		
	}
	
	private void isGameOver(){
		
		if(type == GameType.SINGLE){
			if(player1Dead){
				timer.cancel();
				//TODO: HIGHSCORE eseten GAMEOVER_HIGHSCORE-t hivni!
				client1.changeGameState(GameState.GAMEOVER);
				if(type == GameType.MULTI_NETWORK){
					client2.changeGameState(GameState.GAMEOVER); //TODO: mind2ot meg kell hivni?
				}
			}
		}
		else // multi
			if(player1Dead && player2Dead){
				timer.cancel();
				//TODO: HIGHSCORE eseten GAMEOVER_HIGHSCORE-t hivni!
				client1.changeGameState(GameState.GAMEOVER);
				if(type == GameType.MULTI_NETWORK){
					client2.changeGameState(GameState.GAMEOVER); //TODO: mind2ot meg kell hivni?
				}
			}
	}
	
	// Game experience modifier functions
	
	// JSON converters
	private String allToJSON(){
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
		//System.out.println(all.toString()); // PRINTING
		return all.toString();
	}
	
	private JSONArray playersToJSON(List<Player> list){
		JSONObject currentPlayer;
		ArrayList<JSONObject> playerList = new ArrayList<>();
		JSONArray playersJSON = new JSONArray();
		Player temp;
		try {
			for(int i=0; i<list.size(); i++){
				temp = list.get(i);
				currentPlayer = new JSONObject();
				currentPlayer.put("className", "Player"); // ososztalyban van Andrasnal a className, azert kell csak
				currentPlayer.put("id", temp.getID());
				currentPlayer.put("numberOfLives", temp.getLives());
				currentPlayer.put("x", temp.getCoordX());
				currentPlayer.put("y", temp.getCoordY());
				currentPlayer.put("hitTime", temp.getHitTime());
				currentPlayer.put("explosionTime", temp.getExplosionTime());
				playerList.add(currentPlayer); // player1 added
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
		JSONObject currentNPC;
		ArrayList<JSONObject> npcList = new ArrayList<>();
		JSONArray npcsJSON = new JSONArray();
		NPC temp;
		try {
			int listSize = list.size();
			for( int i=0; i<listSize; i++){
				temp = list.get(i);
				currentNPC = new JSONObject();
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
		JSONObject currentProjectile;
		ArrayList<JSONObject> projectileList = new ArrayList<>();
		JSONArray projectilesJSON = new JSONArray();
		Projectile temp;
		//return projectilesJSON;
		//TODO: csak dummy
		try {
			int listSize = list.size();
			for( int i=0; i<listSize; i++){
				temp = list.get(i);
				currentProjectile = new JSONObject();
				// type for GUI to paint the proper skin
				if( temp instanceof ProjectileGoingUp)
					currentProjectile.put("className", "ProjectileGoingUp");
				else if( temp instanceof ProjectileGoingDown)
					currentProjectile.put("className", "ProjectileGoingDown");
				else//TODO: goingdiagonallyLeft..
					currentProjectile.put("className", "HostileType3");
				
				currentProjectile.put("x", temp.getCoordX());
				currentProjectile.put("y", temp.getCoordY());
				// add each Projectile JSONObject to the arraylist<JSONObject>
				projectileList.add(currentProjectile);
			}
			// each NPC JSONObject to the JSONArray
			projectilesJSON = new JSONArray(projectileList);
		}
		catch (JSONException e) {
			e.printStackTrace();
			}
		return projectilesJSON;
		
	}
	
	private JSONArray modifiersToJSON(List<Modifier> list){
		JSONObject currentModifier;
		ArrayList<JSONObject> modifierList = new ArrayList<>();
		JSONArray modifiersJSON = new JSONArray();
		Modifier temp;
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
				
			// Hostile-spawner tasks
		TimerTask taskSpawnHostileType1 = new TimerTask() {
			@Override
			public void run() {
				if(isRunning){
				int x;
				x = (int)(Math.random()*(Constants.gameFieldWidth - Constants.hostile1Width));
				x += Constants.hostile1Width/2;	// peremertekek �?­gy x=0+hostileType1Width/2 �?‰S x=gameFieldWidth-hostileType1Width/2
				listOfNPCs.add( new HostileType1(x, Constants.hostile1Height+100, difficulty) );	// TODO: az Å±rhaj�?³k be�?ºsz�?¡sa miatt t�?ºlsk�?¡l�?¡zni
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
		timer = new Timer(false);
		
		// Update game state at a given rate
        timer.scheduleAtFixedRate(taskTrackChanges, 0, 1000/Constants.framePerSecond);	// 33.3333 millisecundumunk van megcsin�?¡lni, megjelen�?­teni mindent
		// Spawn enemies at given rate
        timer.scheduleAtFixedRate(taskSpawnHostileType1, 0, Constants.hostile1spawningFrequency);
        
		
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
			client1.updateObjects(allToJSON());
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
		if(playerID == 0)
			player1MovingLeft = true;
		else if(playerID == 1)
			player2MovingLeft = true;

	}
	
	@Override
	public void releaseLeft(int playerID){
		if(playerID == 0)
			player1MovingLeft = false;
		else if(playerID == 1)
			player2MovingLeft = false;
	}
	
	@Override
	public void moveRight(int playerID){
		if(playerID == 0)
			player1MovingRight = true;
		else if(playerID == 1)
			player2MovingRight = true;
	}
	
	@Override
	public void releaseRight(int playerID){
		if(playerID == 0)
			player1MovingRight = false;
		else if(playerID == 1)
			player2MovingRight = false;
	}
	
	@Override
	public void fire(int playerID){
		if(playerID == 0){
			if(listOfPlayers.size() == 2 || listOfPlayers.get(0).getID()==0 ){
				player1Shooting = true;
				// Shooting one projectile here; for the reason if the player presses the button for a very short amount of time
				// otherwise it can happen, if in the TimerTask, playerShooting flag will already be false and no shooting happen
				long currentTime = java.lang.System.currentTimeMillis();
				if(currentTime - player1ShootTime > Constants.timeBetweenShots){
					player1ShootTime = java.lang.System.currentTimeMillis();
					Projectile shot = listOfPlayers.get(0).shoot();
					listOfProjectiles.add(shot);
					// playing sounds
					client1.playSound(SoundType.shoot);
					if(type == GameType.MULTI_NETWORK){
						client2.playSound(SoundType.shoot);
					}
				}
			}
		}
		else if(playerID == 1 && (type == GameType.MULTI_LOCAL || type == GameType.MULTI_NETWORK)){
			if(listOfPlayers.size() == 2 || listOfPlayers.get(0).getID()==1){
				player2Shooting = true;
				// Shooting one projectile here; for the reason if the player presses the button for a very short amount of time
				// otherwise it can happen, if in the TimerTask, playerShooting flag will already be false and no shooting happen
				long currentTime = java.lang.System.currentTimeMillis();
				if(currentTime - player2ShootTime > Constants.timeBetweenShots){
					player2ShootTime = java.lang.System.currentTimeMillis();
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
	}
	
	@Override
	public void releaseFire(int playerID){
		if(playerID == 0)
			player1Shooting = false;
		else if(playerID == 1)
			player2Shooting = false;
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