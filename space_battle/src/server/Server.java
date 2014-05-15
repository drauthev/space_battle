package server;
import interfaces.AllServerInterfaces;
import interfaces.ClientForServer;

import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import server.game_elements.Boom;
import server.game_elements.Fastener;
import server.game_elements.HalfScores;
import server.game_elements.HostileFrenzy;
import server.game_elements.HostileType1;
import server.game_elements.HostileType2;
import server.game_elements.HostileType3;
import server.game_elements.Laser;
import server.game_elements.LeftRightSwitcher;
import server.game_elements.Modifier;
import server.game_elements.NPC;
import server.game_elements.NoAmmo;
import server.game_elements.OneUp;
import server.game_elements.Player;
import server.game_elements.PowerDown;
import server.game_elements.Projectile;
import server.game_elements.ProjectileGoingDiagonallyLeft;
import server.game_elements.ProjectileGoingDiagonallyRight;
import server.game_elements.ProjectileGoingDown;
import server.game_elements.ProjectileGoingUp;
import server.game_elements.ProjectileLaser;
import server.game_elements.Shield;
import server.game_elements.SpaceShipSwitcher;
import sound.SoundType;
import enums.GameSkill;
import enums.GameState;
import enums.GameType;

public class Server implements AllServerInterfaces
{
	// Game Options
	private GameType type;
	private GameSkill difficulty;
	//
	private int score;
	private boolean isRunning;	
	// Booleans, variables for player controlling
	private boolean player1MovingLeft;
	private boolean player1MovingRight;
	private boolean player1Shooting;
	private boolean player2MovingLeft;
	private boolean player2MovingRight;
	private boolean player2Shooting;
	private long player1ShootTime = 0;
	private long player2ShootTime = 0;
	private boolean player1Dead = false;
	private boolean player2Dead = false;
	private boolean gameIsOver = false;
	// Declaring lists of GameElements
	private List<Player> listOfPlayers;
	private List<Projectile> listOfProjectiles;
	private List<Modifier> listOfModifiers;
	private List<NPC> listOfNPCs;
	// Game Experience modifying flags
	private boolean spaceShipsSwitched = false;
	private boolean hostilesAreFrenzied;
	private boolean halfScores;
	// Client interfaces
	private ClientForServer client1;
	private ClientForServer client2;
	private boolean client1Ready;
	private boolean client2Ready;
	//
	private boolean initState = true; // at the start of the game, has to call client1.updateObjects(), before changing GameState to RUNNING; or else the GUI cannot draw
	private Timer timer;
	
	// TESZT: TODO: TimerTasks to inner classes
	private class myTimerTaskTest extends TimerTask {

		@Override
		public void run() {
			System.out.println("elapse lefutott" + java.lang.System.currentTimeMillis());
			for(int i=0; i<listOfPlayers.size(); i++){
				if(listOfPlayers.get(i).getID() == 0){
					listOfPlayers.get(i).setFastened(false);
					listOfPlayers.get(i).setTimeBetweenShots(Constants.timeBetweenShots);
				}
			}
		}

	}

	// Constructor
	public Server(GameType type, GameSkill difficulty, ClientForServer cl1){
		this.type = type;
		this.difficulty = difficulty;
		System.out.println(difficulty);
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
		if(!gameIsOver){
			moveNPCs();
			hostile3ShootingEnablerDisabler(); // auxiliary func; determines if Hostile3s can burst shoot or not
			shootWithNPCs();
			controlPlayers();	// move/shoot with players according to the pushed buttons
			detectCollision();	// between players and hostiles
			detectHits();
			detectModifierHits(); // separate function -> no interference with hostile hit detect -> no list indexing issues
			detectModifierPickUps();
			removeNonExistentObjects();
			isGameOver();
		}
		client1.updateObjects(allToJSON());
		if(type == GameType.MULTI_NETWORK)	client2.updateObjects(allToJSON());		
	}
		
	private void moveNPCs(){
		// Moving Projectiles
		for(int i=0; i < listOfProjectiles.size(); i++){
    		listOfProjectiles.get(i).autoMove();
    	}
    	// Moving hostile spaceships
    	for(int i=0; i < listOfNPCs.size(); i++){
    		listOfNPCs.get(i).autoMove();
    		if(hostilesAreFrenzied) listOfNPCs.get(i).autoMove(); // moving 'em to double distance if Frenzied
    		// Teleporting with HostileType3s
    		long currentTime = java.lang.System.currentTimeMillis();
			if(listOfNPCs.get(i) instanceof HostileType3){
				if( currentTime - ((HostileType3) listOfNPCs.get(i)).getTeleportTime() > Constants.hostile3TeleportFrequency){
					if( listOfNPCs.get(i).getCoordY() < Constants.gameFieldHeigth-Player.getPlayerheight()-100){ // don't teleport under a perimeter -- avoiding teleportation onto Players
						((HostileType3) listOfNPCs.get(i)).teleport();
					}		
				}
			}
    	}
				
    	// Moving Modifiers
    	for(int i=0; i < listOfModifiers.size(); i++){
    		if( listOfModifiers.get(i).getExplosionTime() == 0){ // exploded modifiers don't move
    			listOfModifiers.get(i).autoMove();
    		}
    	}
	}
	
	private void hostile3ShootingEnablerDisabler(){
		NPC tempNPC;
		HostileType3 tempHostile;
		long currentTime;
		long timeSinceLastEnable;
		for(int i=0; i<listOfNPCs.size(); i++){
			tempNPC = listOfNPCs.get(i);
			if(tempNPC instanceof HostileType3){
				tempHostile = (HostileType3) tempNPC;
				currentTime = java.lang.System.currentTimeMillis();
				timeSinceLastEnable = currentTime - tempHostile.getShootingWasEnabled();
				if( timeSinceLastEnable  > 3*tempNPC.getShootingFrequency() && !hostilesAreFrenzied || timeSinceLastEnable  > 1.5*tempNPC.getShootingFrequency() && hostilesAreFrenzied){
					tempHostile.setShootingIsEnabled(false); // disable shooting
					if( timeSinceLastEnable > Constants.hostile3timeBetweenBurstShots && !hostilesAreFrenzied || timeSinceLastEnable > Constants.hostile3timeBetweenBurstShots/2 && hostilesAreFrenzied){ // enabling shooting if enough time lasted
						tempHostile.setShootingIsEnabled(true);
						tempHostile.setShootingWasEnabled(currentTime);
					}
				}
			}
		}
	}
	
	private void shootWithNPCs(){
		NPC temp;
		Projectile shot;
		long lastShotTime;
		long currentTime;
		long timeSinceLastShot;
		for(int i=0; i<listOfNPCs.size(); i++){
			temp = listOfNPCs.get(i);
			if(temp.getLives() > 0){ // "dead" hostiles are in the list for some time because of the explosion effect; this make sure they don't shoot
				lastShotTime = temp.getLastShotTime();
				currentTime = java.lang.System.currentTimeMillis();
				timeSinceLastShot = currentTime - lastShotTime;
				if( (timeSinceLastShot > temp.getShootingFrequency() && !hostilesAreFrenzied) || (timeSinceLastShot > temp.getShootingFrequency()/2 && hostilesAreFrenzied) ){ // shoot only if enough time has lasted
					temp.setLastShotTime(java.lang.System.currentTimeMillis());
					shot = temp.shoot();
					if( shot != null){ // HostileType3's shoot() returns null if its shooting is currently disabled
						listOfProjectiles.add(shot);
						// shooting diagonally-moving projectile if temp is a HostileType2		
						if( temp instanceof HostileType2 ){
							shot = ((HostileType2) temp).shootDiagonallyLeft();
							listOfProjectiles.add(shot);
							shot = ((HostileType2) temp).shootDiagonallyRight();
							listOfProjectiles.add(shot);
						}
						// update NPC
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
	}
	
	private void controlPlayers(){
		Player temp;
		long currentTime;
		for(int i=0; i<listOfPlayers.size(); i++){
			temp = listOfPlayers.get(i);
			if(temp.getID() == 0){
				if( (player1MovingLeft && !spaceShipsSwitched) || (player2MovingLeft && spaceShipsSwitched) ){
					temp.moveLeft();
				}
				if( (player1MovingRight && !spaceShipsSwitched) || (player2MovingRight && spaceShipsSwitched) ){
					temp.moveRight();
				}			
				if( (player1Shooting && !spaceShipsSwitched) || (player2Shooting && spaceShipsSwitched) ){
					currentTime = java.lang.System.currentTimeMillis();
					if( temp.isHasAmmo() && (currentTime - temp.getLastShootTime() > Constants.timeBetweenShots) ){
						temp.setLastShootTime(currentTime);
						Projectile shot = temp.shoot();
						listOfProjectiles.add(shot);
						// playing sounds
						client1.playSound(SoundType.shoot);
						if(type == GameType.MULTI_NETWORK){
							client2.playSound(SoundType.shoot);
						}					
					}
				}
				listOfPlayers.set(i, temp); // Update the player list
			}
			else if(temp.getID() == 1){
				if( (player2MovingLeft && !spaceShipsSwitched) || (player1MovingLeft && spaceShipsSwitched) ){
					temp.moveLeft();
				}
				if( (player2MovingRight && !spaceShipsSwitched) || (player1MovingRight && spaceShipsSwitched) ){
					temp.moveRight();
				}
				if( (player2Shooting && !spaceShipsSwitched) || (player1Shooting && spaceShipsSwitched) ){
					currentTime = java.lang.System.currentTimeMillis();
					if( temp.isHasAmmo() && (currentTime - temp.getLastShootTime() > Constants.timeBetweenShots) ){
							temp.setLastShootTime(currentTime);
							Projectile shot = temp.shoot();
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
					if(halfScores)
						score += tempNPC.getScoreIfDestroyed()/2;
					else
						score += tempNPC.getScoreIfDestroyed();
					// playing sounds
					client1.playSound(SoundType.enemyExplosion);
					if(type == GameType.MULTI_NETWORK)	client2.playSound(SoundType.enemyExplosion);
					
					tempPlayer.setLives(tempPlayer.getLives() - 1);
					// checking Player's lives
					// if it's 0, explode it; else indicating a hit
					if(tempPlayer.getLives() == 0){
						spaceShipsSwitched = false; // giving back the control to the original player if there is only one spaceship left
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
				// Detect NPC-hits
				for(int j=0; j<listOfNPCs.size(); j++){
					npc = listOfNPCs.get(j);
					npcLives = npc.getLives();
					if( npcLives <= 0 ) continue; // dead hostiles which are in the list for animation purposes, cannot absorb projectiles
					if( proj.isHit(npc) ){ // Given NPC is hit
						if( proj instanceof ProjectileLaser ){ 
							npc.setLives(0);
							npc.setExplosionTime(java.lang.System.currentTimeMillis());
							if(halfScores)
								score += npc.getScoreIfDestroyed()/2;
							else
								score += npc.getScoreIfDestroyed();
							// playing sound
							client1.playSound(SoundType.enemyExplosion);
							if(type == GameType.MULTI_NETWORK) client2.playSound(SoundType.enemyExplosion);
						}
						else{
							npcLives--;
							npc.setLives(npcLives);
							if( npcLives == 0 ){
								if(halfScores)
									score += npc.getScoreIfDestroyed()/2;
								else
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
							listOfProjectiles.remove(i); // remove projectile which hit
							break; // should not check for the other NPCs, if Projectile is not laser.. this could cause exceptions in some cases, if the projectile would hit more than one NPC (e.g. teleporting hostiletype3s...)
						}
						listOfNPCs.set(j, npc);
					}
					// If NPC is not hit, there is nothing to do
				}
			}		
			// Projectile is shot by an NPC
			else{ 
				if( proj instanceof ProjectileGoingDown || proj instanceof ProjectileGoingDiagonallyLeft || proj instanceof ProjectileGoingDiagonallyRight ){
					for(int j=0; j<listOfPlayers.size(); j++){
						Player player = listOfPlayers.get(j);
						int playerLives = player.getLives();
						
						if (proj.isHit(player)){//TODO: lehet, hogy y coordot itt kellene nezni, ProjGoingDown isHit()-jeben nem, es csak azokra meghivni
							listOfProjectiles.remove(i); // remove projectile which hit
							// explode player or decrement player's lives
							if(!player.isShielded()){
								player.setLives(--playerLives);
								if( playerLives == 0 ){
									spaceShipsSwitched = false; // giving back the control to the original player if there is only one spaceship left
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
							}
							else{ // if player is shielded, only thing to do is setting hit Time for GUI to flash the player skin
								player.setHitTime(java.lang.System.currentTimeMillis());
							}
						}
						// nothing to do if there is no hit
					}
				}
				
			}
		}
	}
	
	private void detectModifierHits(){ // Separate function for mod hit detect; this way there are no problems with list indexing
		Projectile proj;
		Modifier mod;
		for(int i=0; i<listOfProjectiles.size(); i++){
			proj = listOfProjectiles.get(i);
			// Detect Modifier-hits
			for(int j=0; j<listOfModifiers.size(); j++){
				mod = listOfModifiers.get(j);
				if( mod.getExplosionTime() == 0 && (java.lang.System.currentTimeMillis() - mod.getCreationTime() > 1000) ){ // doing nothing if the modifier is already exploded, or was just created
					if( proj instanceof ProjectileGoingUp || proj instanceof ProjectileLaser){ // only investigating for player-shooted powerups
						if( proj.isHit(mod) ){
							mod.setExplosionTime(java.lang.System.currentTimeMillis());
							// playing sound
							client1.playSound(SoundType.enemyExplosion);
							if(type == GameType.MULTI_NETWORK) client2.playSound(SoundType.enemyExplosion);
							// Spawning 2 new modifiers from the exploded one
							createModifiersFromAnExplodedOne(mod);
							if( !(proj instanceof ProjectileLaser) ){ 
								listOfProjectiles.remove(i); // remove projectile which hit, except it is a ProjectileLaser
							}
						}
					}			
				}	
			}
		}
	}
	
	private void createModifiersFromAnExplodedOne(Modifier explodedMod){
		// Adding the 2 new modifiers to the list
		for(int i=0; i<2; i++){
			// For choosing what to spawn
			double whichFrequency = Math.random();
			double whatToSpawn = Math.random();
			// Determining coordinates
			double x;
			double y = explodedMod.getCoordY() + Modifier.getModifierheigth()/2;
			if(i==0){
				x = explodedMod.getCoordX() - Modifier.getModifierwidth();
			}
			else{
				x = explodedMod.getCoordX() + Modifier.getModifierwidth();
			}
			
			if(explodedMod instanceof PowerDown){
				if(whichFrequency <= 0.4){ // spawning a frequent mod
					listOfModifiers.add( new HostileFrenzy(x, y) );
				}
				else{ // spawning a medium frequent one (no rare PowerDowns exist)
					if( listOfPlayers.size() == 2 ){ // SpaceShipSwitcher only available in MULTIPLAYER (and spawn it only if both players are alive)
						if(whatToSpawn <= 0.25){
							listOfModifiers.add( new LeftRightSwitcher(x, y) );
						}
						else if(whatToSpawn > 0.25 && whatToSpawn <= 0.5){
							listOfModifiers.add( new NoAmmo(x, y) );
						}
						else if(whatToSpawn > 0.5 && whatToSpawn <= 0.75){
							listOfModifiers.add( new HalfScores(x, y) );	
						}
						else{
							listOfModifiers.add( new SpaceShipSwitcher(x, y) );	
						}
					}
					else{
						if(whatToSpawn <= 0.3){
							listOfModifiers.add( new LeftRightSwitcher(x, y) );
						}
						else if(whatToSpawn > 0.3 && whatToSpawn <= 0.6){
							listOfModifiers.add( new NoAmmo(x, y) );
						}
						else{
							listOfModifiers.add( new HalfScores(x, y) );	
						}
					}
				}
			}
			else{ //exploded mod is a Powerup
				if(whichFrequency <= 0.4){
					listOfModifiers.add( new Fastener(x, y) );
				}
				else if(whichFrequency > 0.4 && whichFrequency < 0.8){
					if(whatToSpawn < 0.5)
						listOfModifiers.add( new Shield(x, y) );
					else
						listOfModifiers.add( new Laser(x, y) );
				}
				else{
					if(whatToSpawn < 0.5)
						listOfModifiers.add( new OneUp(x, y) );
					else
						listOfModifiers.add( new Boom(x, y) );
				}
			}
			// Setting the creationTime of the new modifier - it is the last element of the list
			listOfModifiers.get(listOfModifiers.size()-1).setCreationTime(java.lang.System.currentTimeMillis());
		}
	}
	
	private void spawnModifier(double x, double y){
		listOfModifiers.add( new Fastener(200, y) );
		double spawnOrNot = Math.random(); // some randomness.. spawn smthng or not at all
		if(spawnOrNot >= 0.6){
			// Choosing what to spawn
			double whichFrequency = Math.random();
			double whatToSpawn = Math.random();
			// spawn a frequent modifier [fastener, hostileFrenzy]
			if(whichFrequency <= 0.4){
				if(whatToSpawn >= 0.5)
					listOfModifiers.add( new Fastener(x, y) );
				else
					listOfModifiers.add( new HostileFrenzy(x, y) );
			}
			//spawn a mod with medium frequency [Shield, Laser, controlChangerMULTIONLY, LeftRightSwitcher, noAmmo, HalfScores, SpaceShipSwitcher]
			else if(whichFrequency > 0.4 && whichFrequency < 0.8){
				if( listOfPlayers.size() == 2){ // SpaceShipSwitcher only available in MULTIPLAYER (and spawn it only if both players are alive)
					if(whatToSpawn < 0.1667){
						listOfModifiers.add( new Shield(x, y) );
					}
					else if(whatToSpawn >= 0.1667 && whatToSpawn < 0.333){
						listOfModifiers.add( new Laser(x, y) );
					}
					else if(whatToSpawn >= 0.333 && whatToSpawn < 0.5){
						listOfModifiers.add( new LeftRightSwitcher(x, y) );
					}
					else if(whatToSpawn >= 0.5 && whatToSpawn < 0.667){
						listOfModifiers.add( new NoAmmo(x, y) );
					}
					else if(whatToSpawn >= 0.667 && whatToSpawn < 0.8333){
						listOfModifiers.add( new HalfScores(x, y) );
					}
					else{
						listOfModifiers.add( new SpaceShipSwitcher(x, y) );
					}
				}
				else{
					if(whatToSpawn < 0.2){
						listOfModifiers.add( new Shield(x, y) );
					}
					else if(whatToSpawn >= 0.2 && whatToSpawn < 0.4){
						listOfModifiers.add( new Laser(x, y) );
					}
					else if(whatToSpawn >= 0.4 && whatToSpawn < 0.6){
						listOfModifiers.add( new LeftRightSwitcher(x, y) );
					}
					else if(whatToSpawn >= 0.6 && whatToSpawn < 0.8){
						listOfModifiers.add( new NoAmmo(x, y) );
					}
					else{
						listOfModifiers.add( new HalfScores(x, y) );
					}
				}
			}
			// spawn a rare modifier [OneUp, Boom]
			else{
				if(whatToSpawn >= 0.5)
					listOfModifiers.add( new OneUp(x, y) );
				else
					listOfModifiers.add( new Boom(x, y) );
			}
		}
	}
	
	private void detectModifierPickUps(){
		//TODO: teszt
		myTimerTaskTest test = new myTimerTaskTest();
		
		
		// TimerTasks for elapsing the modfier-effects
		TimerTask taskElapseFastenerPlayer1 = new TimerTask() {
			@Override
			public void run() {
				for(int i=0; i<listOfPlayers.size(); i++){
					if(listOfPlayers.get(i).getID() == 0){
						listOfPlayers.get(i).setFastened(false);
						listOfPlayers.get(i).setTimeBetweenShots(Constants.timeBetweenShots);
					}
				}
			}
		};
		TimerTask taskElapseFastenerPlayer2 = new TimerTask() {
			@Override
			public void run() {
				for(int i=0; i<listOfPlayers.size(); i++){
					if(listOfPlayers.get(i).getID() == 1){
						listOfPlayers.get(i).setFastened(false);
						listOfPlayers.get(i).setTimeBetweenShots(Constants.timeBetweenShots);
					}
				}
			}
		};
		//
		TimerTask taskElapseShieldPlayer1 = new TimerTask() {
			@Override
			public void run() {
				for(int i=0; i<listOfPlayers.size(); i++){
					if(listOfPlayers.get(i).getID() == 0){
						listOfPlayers.get(i).setShielded(false);
					}
				}
			}
		};
		TimerTask taskElapseShieldPlayer2 = new TimerTask() {
			@Override
			public void run() {
				for(int i=0; i<listOfPlayers.size(); i++){
					if(listOfPlayers.get(i).getID() == 1){
						listOfPlayers.get(i).setShielded(false);
					}
				}
			}
		};
		//
		TimerTask taskElapseLaserPlayer1 = new TimerTask() {
			@Override
			public void run() {
				for(int i=0; i<listOfPlayers.size(); i++){
					if(listOfPlayers.get(i).getID() == 0){
						listOfPlayers.get(i).setHasLaser(false);
					}
				}
			}
		};
		TimerTask taskElapseLaserPlayer2 = new TimerTask() {
			@Override
			public void run() {
				for(int i=0; i<listOfPlayers.size(); i++){
					if(listOfPlayers.get(i).getID() == 1){
						listOfPlayers.get(i).setHasLaser(false);
					}
				}
			}
		};
		//
		TimerTask taskElapseLeftRightSwitcherPlayer0 = new TimerTask() {
			@Override
			public void run() {
				for(int i=0; i<listOfPlayers.size(); i++){
					if(listOfPlayers.get(i).getID() == 0){
						listOfPlayers.get(i).setLeftRighSwitched(false);
					}
				}
			}
		};
		TimerTask taskElapseLeftRightSwitcherPlayer1 = new TimerTask() {
			@Override
			public void run() {
				for(int i=0; i<listOfPlayers.size(); i++){
					if(listOfPlayers.get(i).getID() == 1){
						listOfPlayers.get(i).setLeftRighSwitched(false);
					}
				}
			}
		};
		//
		TimerTask taskElapseHostileFrenzy = new TimerTask() {
			@Override
			public void run() {
					hostilesAreFrenzied = false;
				}
		};
		//
		TimerTask taskElapseNoAmmoPlayer0 = new TimerTask() {
			@Override
			public void run() {
					for(int i=0; i<listOfPlayers.size(); i++){
						if(listOfPlayers.get(i).getID() == 0){
							listOfPlayers.get(i).setHasAmmo(true);
						}
					}
				}
		};
		TimerTask taskElapseNoAmmoPlayer1 = new TimerTask() {
			@Override
			public void run() {
				for(int i=0; i<listOfPlayers.size(); i++){
					if(listOfPlayers.get(i).getID() == 1){
						listOfPlayers.get(i).setHasAmmo(true);
					}
				}
				}
		};
		//
		TimerTask taskElapseHalfScores = new TimerTask() {
			@Override
			public void run() {
					halfScores = false;
				}
		};
		//
		TimerTask taskElapseSpaceShipSwitcher = new TimerTask() {
			@Override
			public void run() {
					spaceShipsSwitched = false;
				}
		};

		
		for(int i=0; i<listOfPlayers.size(); i++){
			Player tempPlayer = listOfPlayers.get(i);
			for(int j=0; j<listOfModifiers.size(); j++){
				Modifier tempMod = listOfModifiers.get(j);
				if( tempMod.getExplosionTime() == 0 ){ // no investigation if the modifier is stays in the list only for animation purposes
					if( tempMod.getHitBox().isCollision(tempPlayer)){
						// Playing sound
						if( tempMod instanceof PowerDown){
							client1.playSound(SoundType.powerDown);
							if( type == GameType.MULTI_NETWORK) client2.playSound(SoundType.powerDown);
						}
						else{
							client1.playSound(SoundType.powerUp);
							if( type == GameType.MULTI_NETWORK) client2.playSound(SoundType.powerUp);
						}
						// Taking effect..
						if(tempMod.getPickUpTime() == 0){ // modifier staying in the list for animation purposes, so have to make sure that it takes effect only once
							tempMod.setPickUpTime(java.lang.System.currentTimeMillis());
							if(tempMod instanceof Fastener){
								//tempMod.setPickUpTime(java.lang.System.currentTimeMillis()); // removeNonExistentObjects() will delete it from list
								//tempPlayer.setFastened(true);
								//tempPlayer.setTimeBetweenShots(Constants.timeBetweenShotsIfFastened);
								listOfPlayers.set(i, tempPlayer);
								if(tempPlayer.getID() == 0){
									//timer.schedule(taskElapseFastenerPlayer1, Fastener.getTimeItLasts());
									System.out.println(java.lang.System.currentTimeMillis());
									if( tempPlayer.isFastened()){
										boolean asd = test.cancel();
										if(asd){
											System.out.println("cancel true");
											
										}
										else{
											System.out.println("cancel false");
										}
										test = new myTimerTaskTest();
										timer.purge();
										timer.schedule(test, Fastener.getTimeItLasts());
									}
									else{
										tempPlayer.setFastened(true);
										tempPlayer.setTimeBetweenShots(Constants.timeBetweenShotsIfFastened);
										timer.schedule(test, Fastener.getTimeItLasts());
									}
									
								}
								else{
									timer.schedule(taskElapseFastenerPlayer2, Fastener.getTimeItLasts());
									//TODO:
								}
							}
							if(tempMod instanceof OneUp){
								if(tempPlayer.getLives() < 5)
									listOfPlayers.get(i).setLives(tempPlayer.getLives()+1);
							}
							if(tempMod instanceof Shield){
								listOfPlayers.get(i).setShielded(true);
								if(tempPlayer.getID() == 0)
									timer.schedule(taskElapseShieldPlayer1, Shield.getTimeItLasts());
								else
									timer.schedule(taskElapseShieldPlayer2, Shield.getTimeItLasts());
							}
							if(tempMod instanceof Boom){
								for(int i1=0; i1<listOfNPCs.size(); i1++){
									score += listOfNPCs.get(i1).getScoreIfDestroyed();
									listOfNPCs.get(i1).setLives(0);
									listOfNPCs.get(i1).setExplosionTime(java.lang.System.currentTimeMillis());
									// playing sound
									client1.playSound(SoundType.enemyExplosion);
									if(type == GameType.MULTI_NETWORK) client2.playSound(SoundType.enemyExplosion);
								}
							}
							if(tempMod instanceof Laser){
								tempPlayer.setHasLaser(true);
								if(tempPlayer.getID() == 0){
									timer.schedule(taskElapseLaserPlayer1, Laser.getTimeItLasts());
								}
								else{
									timer.schedule(taskElapseLaserPlayer2, Laser.getTimeItLasts());
								}							
							}
							if(tempMod instanceof LeftRightSwitcher){
								tempPlayer.setLeftRighSwitched(true);
								if(tempPlayer.getID() == 0){
									timer.schedule(taskElapseLeftRightSwitcherPlayer0, LeftRightSwitcher.getTimeItLasts());
								}	
								else{
									timer.schedule(taskElapseLeftRightSwitcherPlayer1, LeftRightSwitcher.getTimeItLasts());
								}
							}
							if(tempMod instanceof HostileFrenzy){
								hostilesAreFrenzied = true;
								timer.schedule(taskElapseHostileFrenzy, HostileFrenzy.getTimeItLasts());		
							}
							if(tempMod instanceof NoAmmo){
								tempPlayer.setHasAmmo(false);
								if(tempPlayer.getID() == 0){
									timer.schedule(taskElapseNoAmmoPlayer0, NoAmmo.getTimeItLasts());
								}	
								else{
									timer.schedule(taskElapseNoAmmoPlayer1, NoAmmo.getTimeItLasts());
								}	
							}
							if(tempMod instanceof HalfScores){
								halfScores = true;
								timer.schedule(taskElapseHalfScores, HalfScores.getTimeItLasts());
							}
							if(tempMod instanceof SpaceShipSwitcher){
								if( listOfPlayers.size() == 2){ // there could be a remainder switcher, which MUST NOT take effect, as the player would lose control over the only spaceship
									spaceShipsSwitched = true; // picking up a switcher during the effect of a previously picked up won't revert its effect, only lengthen it;
									timer.schedule(taskElapseSpaceShipSwitcher, SpaceShipSwitcher.getTimeItLasts());
								}
							}
						}
					}
				}
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
			double y = temp.getCoordY();
			double x = temp.getCoordX();
			int height = Projectile.getProjectileheight();
			if( y-height/2 > Constants.gameFieldHeigth || y+height/2<0 || x<0 || x>Constants.gameFieldWidth){
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
			if( (currentTime - explosionTime > 1000) && explosionTime!=0 ){//TODO: hany masodperc utan?
				listOfNPCs.remove(i);
			}
		}
		
		// Remove exploded Players
		for(int i=0; i<listOfPlayers.size(); i++){
			// removing from list, if !!3sec!!? is lated since explosion
			explosionTime = listOfPlayers.get(i).getExplosionTime();
			if( (currentTime - explosionTime > 2000) && explosionTime!=0 ){//TODO: hany masodperc utan?
				listOfPlayers.remove(i);
			}
		}
		
		// Remove exploded Modifiers
		for(int i=0; i<listOfModifiers.size(); i++){
			// removing from list, if !!3sec!!? is lated since explosion
			explosionTime = listOfModifiers.get(i).getExplosionTime();
			if( (currentTime - explosionTime > 1000) && explosionTime!=0 ){//TODO: hany masodperc utan?
				listOfModifiers.remove(i);
			}
		}
		
		long pickUpTime;
		// Remove picked-up modifiers
		for(int i=0; i<listOfModifiers.size(); i++){
			pickUpTime = listOfModifiers.get(i).getPickUpTime();
			if( (currentTime - pickUpTime > 1500) && pickUpTime!=0 ){//TODO: mennyi ido utan?
				listOfModifiers.remove(i);
			}
		}	
	}
	
	private void isGameOver(){
		// GameOver delaying task
		// this Task provides the delay in changing gamestate to GameOver
		// this delay is needed for the Player exploison animation
		TimerTask taskGameOver = new TimerTask() {
			@Override
			public void run() {
				if(type == GameType.SINGLE || type == GameType.MULTI_LOCAL){
					if(isThereNewHighScore()){
						client1.changeGameState(GameState.GAMEOVER_NEW_HIGHSCORE);
					}
					else{
						client1.changeGameState(GameState.GAMEOVER);
					}				
				}
				else{ // network game
					if(isThereNewHighScore()){
						client1.changeGameState(GameState.GAMEOVER_NEW_HIGHSCORE);
					}
					else{
						client1.changeGameState(GameState.GAMEOVER);
						
					}
					client2.changeGameState(GameState.GAMEOVER); // ONLY client1 types the new highscore;
					
				}
			}
		};
		if(type == GameType.SINGLE){
			if(player1Dead){
				gameIsOver = true;
				timer.schedule(taskGameOver, 2000);
			}
		}
		else{ // multi
			if(player1Dead && player2Dead){
				gameIsOver = true;
				timer.schedule(taskGameOver, 2000);
			}
		}
	}
	
	private boolean isThereNewHighScore(){
		List<Map.Entry<Integer, String>> highScores = new ArrayList<>();
		highScores = getHighScores();
		if( highScores.size() == 0){ // no valid highscore table is available - FTP problems
			return false;
		}
		if( score > highScores.get(highScores.size()-1).getKey() ){ // new highscore! the last element in the ArrayList is the one with the least score..
			return true;
		}
		else{
			return false;
		}

	}
	
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
				currentPlayer.put("x", (int)(temp.getCoordX()) );
				currentPlayer.put("y", (int)(temp.getCoordY()) );
				currentPlayer.put("hitTime", temp.getHitTime());
				currentPlayer.put("explosionTime", temp.getExplosionTime());
				currentPlayer.put("isShielded", temp.isShielded());
				playerList.add(currentPlayer); // player1 added
			}
		playersJSON = new JSONArray( playerList );
		}
		catch (JSONException e) {
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
				else{
					currentNPC.put("className", "HostileType3");
					currentNPC.put("teleportTime", ((HostileType3) temp).getTeleportTime());
				}
				currentNPC.put("x", (int)(temp.getCoordX()) );
				currentNPC.put("y", (int)(temp.getCoordY()) );
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
		try {
			int listSize = list.size();
			for( int i=0; i<listSize; i++){
				temp = list.get(i);
				currentProjectile = new JSONObject();
				// type for GUI to paint the proper skin
				if( temp instanceof ProjectileGoingDown)
					currentProjectile.put("className", "ProjectileGoingDown");
				else if( temp instanceof ProjectileLaser)
					currentProjectile.put("className", "ProjectileLaser"); // HAVE to precede ProjGoingUp, projLaser extends it
				else if( temp instanceof ProjectileGoingUp)
					currentProjectile.put("className", "ProjectileGoingUp");
				else if( temp instanceof ProjectileGoingDiagonallyLeft )
					currentProjectile.put("className", "ProjectileGoingDiagonallyLeft");
				else
					currentProjectile.put("className", "ProjectileGoingDiagonallyRight");
				
				currentProjectile.put("x", (int)(temp.getCoordX()) );
				currentProjectile.put("y", (int)(temp.getCoordY()) );
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
		try {
			int listSize = list.size();
			for( int i=0; i<listSize; i++){
				temp = list.get(i);
				currentModifier = new JSONObject();
				// type for GUI to paint the proper skin
				if( temp instanceof Fastener)
					currentModifier.put("className", "Fastener");
				else if(temp instanceof OneUp)
					currentModifier.put("className", "OneUp");
				else if(temp instanceof Shield)
					currentModifier.put("className", "Shield");
				else if(temp instanceof Boom)
					currentModifier.put("className", "Boom");
				else if(temp instanceof Laser){
					currentModifier.put("className", "Laser");
				}
				else if(temp instanceof LeftRightSwitcher){
					currentModifier.put("className", "LeftRightSwitcher");
				}
				else if(temp instanceof HostileFrenzy){
					currentModifier.put("className", "HostileFrenzy");
				}
				else if(temp instanceof NoAmmo){
					currentModifier.put("className", "NoAmmo");
				}
				else if(temp instanceof HalfScores){
					currentModifier.put("className", "HalfScores");
				}
				else{
					currentModifier.put("className", "SpaceShipSwitcher");
				}
				
				currentModifier.put("x", (int)(temp.getCoordX()) );
				currentModifier.put("y", (int)(temp.getCoordY()) );
				currentModifier.put("pickupTime", temp.getPickUpTime());
				currentModifier.put("creationTime", temp.getSpawnTime());
				currentModifier.put("explosionTime", temp.getExplosionTime());
				// add each Projectile JSONObject to the arraylist<JSONObject>
				modifierList.add(currentModifier);
			}
			// each NPC JSONObject to the JSONArray
			modifiersJSON = new JSONArray(modifierList);
		}
		catch (JSONException e) {
			e.printStackTrace();
			}
		return modifiersJSON;
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
				x += Constants.hostile1Width/2;
				listOfNPCs.add( new HostileType1(x, Constants.hostile1Height+100, difficulty) );
				}
			}
		};		
		TimerTask taskSpawnHostileType2 = new TimerTask() {
			@Override
			public void run() {
				if(isRunning){
					// take some "randomness" into the spawning
					double spawnOrNot = Math.random();
					if(spawnOrNot > 0.5){
						int x;
						x = (int)(Math.random()*(Constants.gameFieldWidth - Constants.hostile2Width));
						x += Constants.hostile2Width/2;
						listOfNPCs.add( new HostileType2(x, Constants.hostile2Height+100, difficulty) );
					}
				}
			}
		};
		TimerTask taskSpawnHostileType3 = new TimerTask() {
			@Override
			public void run() {
				if(isRunning){
					// take some "randomness" into the spawning
					double spawnOrNot = Math.random();
					if(spawnOrNot > 0.5){
						int x;
						x = (int)(Math.random()*(Constants.gameFieldWidth - Constants.hostile3Width));
						x += Constants.hostile3Width/2;
						listOfNPCs.add( new HostileType3(x, Constants.hostile3Height+100, difficulty) );
					}
				}
			}
		};
			
			// Modifier-spawner task
		TimerTask taskSpawnModifiers = new TimerTask() {
			@Override
			public void run() {
				if(isRunning){
					// Determine the place of spawning
					double x = (int)(Math.random()*(Constants.gameFieldWidth - Modifier.getModifierwidth()) );
					x += Modifier.getModifierwidth()/2;
					
					spawnModifier(x, Modifier.getModifierheigth()+100);
					}
				}
		};
		
		// Starting Timer 
		timer = new Timer(false);
		
		// Update game state at a given rate
        timer.scheduleAtFixedRate(taskTrackChanges, 0, 1000/Constants.framePerSecond);	// 33.3333 millisecundumunk van megcsin�?¡lni, megjelen�?­teni mindent
		// Spawn enemies at given rates
        timer.scheduleAtFixedRate(taskSpawnHostileType1, 0, Constants.hostile1spawningFrequency);
        timer.scheduleAtFixedRate(taskSpawnHostileType2, 1535, Constants.hostile2spawningFrequency);
        timer.scheduleAtFixedRate(taskSpawnHostileType3, 2768, Constants.hostile3spawningFrequency);
        // Spawn modifiers at given rates
        timer.scheduleAtFixedRate(taskSpawnModifiers, 300, 3000);		
	}
	
	@Override
	public void terminate(){
		// deleting timer thread
		timer.cancel();
		// terminating second client if exists
		if(type == GameType.MULTI_NETWORK){
			client1.changeGameState(GameState.DISCONNECTED);
			client2.changeGameState(GameState.DISCONNECTED);
		}
	}
	
	@Override
	public void startRequest(ClientForServer c){
		if(type == GameType.SINGLE || type == GameType.MULTI_LOCAL){
			// have to send object list to client before changing its gamestate
			if(initState){ 
				initState = false;
				client1.updateObjects(allToJSON());
			}
			//
			isRunning = true;
			client1.changeGameState(GameState.RUNNING);
		}
		// MULTI_NETWORK -> 2 clients
		else{
			if(initState){
				initState = false;
				client1.updateObjects(allToJSON());
				client2.updateObjects(allToJSON());
				client1.changeGameState(GameState.WAITING);
				client2.changeGameState(GameState.PAUSED);
			}	
			if( c == client1 ){ // start requested by client1
//				System.out.println("player1 startReq");
//				System.out.println("player1ready: " + client1Ready);
//				System.out.println("player2ready: " + client2Ready);
				client1Ready = true;
				if(client2Ready){ // starting the game
					isRunning = true;
					client1.changeGameState(GameState.RUNNING);
					client2.changeGameState(GameState.RUNNING);				
				}
				else{
					client1.changeGameState(GameState.WAITING);
				}
			}
			else{ // start requested by client2
//				System.out.println("player2 startReq");
//				System.out.println("player1ready: " + client1Ready);
//				System.out.println("player2ready: " + client2Ready);
				client2Ready = true;
				if(client1Ready){ // starting the game
					isRunning = true;
					client1.changeGameState(GameState.RUNNING);
					client2.changeGameState(GameState.RUNNING);
				}
				else{
					client2.changeGameState(GameState.WAITING);
				}
			}
		}
	}
	
	@Override
	public void pauseRequest(ClientForServer c){
		isRunning = false;
		if(type == GameType.SINGLE || type == GameType.MULTI_LOCAL){
			client1.changeGameState(GameState.PAUSED);
		}
		// MULTI_NETWORK -> 2 clients
		else{
			client1Ready = false;
			client2Ready = false;
			client1.changeGameState(GameState.PAUSED);
			client2.changeGameState(GameState.PAUSED);
		}	
	}
	
	@Override
	public void disconnect(ClientForServer c){
		isRunning = false;
		if( c == client1 ){
			client2.changeGameState(GameState.GAMEOVER);
		}
		else{
			client1.changeGameState(GameState.GAMEOVER);
		}
	}

	// Implementing ServerForPlayerController Interface
	// ------------------------------------------------------------------------------------------------------------------
	@Override
	public void moveLeft(int playerID){
		if(playerID == 0){
			player1MovingLeft = true;
		}
		else if(playerID == 1){
			player2MovingLeft = true;
		}
	}
	
	@Override
	public void releaseLeft(int playerID){
		if(playerID == 0){
			player1MovingLeft = false;
		}
		else if(playerID == 1){
			player2MovingLeft = false;
		}
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
				// otherwise it can happen, that in the TimerTask trackChanges, playerShooting flag will already be false and no shooting happen
				Player temp;
				Projectile shot;
				long currentTime = java.lang.System.currentTimeMillis();
				for(int i=0; i<listOfPlayers.size(); i++){
					temp = listOfPlayers.get(i);
					if(!spaceShipsSwitched){
						if(temp.getID() == 0 && temp.isHasAmmo() && (currentTime - temp.getLastShootTime() > Constants.timeBetweenShots) ){
							temp.setLastShootTime(currentTime);
							shot = temp.shoot();
							listOfProjectiles.add(shot);
							// playing sounds
							client1.playSound(SoundType.shoot);
							if(type == GameType.MULTI_NETWORK){
								client2.playSound(SoundType.shoot);
							}
						}
					}
					else{ // space ships are switched, shooting with the other player's ship | NO GOING here if only one player is alive (cause in that case, spaceShipsSwitched==false, so no prob
						if(temp.getID() == 1 && temp.isHasAmmo() && (currentTime - temp.getLastShootTime() > Constants.timeBetweenShots) ){
							temp.setLastShootTime(currentTime);
							shot = temp.shoot();
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
		else{ // playerID == 1 -> player2 wants to shoot
			if(listOfPlayers.size() == 2 || listOfPlayers.get(0).getID()==1 ){ // 2 players alive, or only player2 is alive
				player2Shooting = true;
				// Shooting one projectile here; for the reason if the player presses the button for a very short amount of time
				// otherwise it can happen, that in the TimerTask trackChanges, playerShooting flag will already be false and no shooting happen
				Player temp;
				Projectile shot;
				long currentTime = java.lang.System.currentTimeMillis();
				for(int i=0; i<listOfPlayers.size(); i++){
					temp = listOfPlayers.get(i);
					if(!spaceShipsSwitched){
						if(temp.getID() == 1 && temp.isHasAmmo() && (currentTime - temp.getLastShootTime() > Constants.timeBetweenShots) ){
							temp.setLastShootTime(currentTime);
							shot = temp.shoot();
							listOfProjectiles.add(shot);
							// playing sounds
							client1.playSound(SoundType.shoot);
							if(type == GameType.MULTI_NETWORK){
								client2.playSound(SoundType.shoot);
							}
						}
					}
					else{ // space ships are switched, shooting with the other player's ship | | NO GOING here if only one player is alive (cause in that case, spaceShipsSwitched==false, so no prob
						if(temp.getID() == 0 && temp.isHasAmmo() && (currentTime - temp.getLastShootTime() > Constants.timeBetweenShots) ){
							temp.setLastShootTime(currentTime);
							shot = temp.shoot();
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
		FTPConnector ftp;
		List<Map.Entry<Integer, String>> highScores = getHighScores();
		highScores.remove(highScores.size()-1); // removing the last element - the one with the least score
		highScores.add(new AbstractMap.SimpleEntry<Integer, String>(score, name)); // adding the new high score
		
		int highscoreSize = highScores.size(); // should be 10	
		String highscoreContent = ""; // String to write to highscores.txt
		int key;
		String value;
		// taking out the elements from the ArrayList to the String
		while( highscoreSize > 0 ){
			key = highScores.get(highscoreSize-1).getKey(); // iterating from the highest score to the last (the direction doesnt matter too much..)
			value = highScores.get(highscoreSize-1).getValue();
			highscoreContent = highscoreContent + key + "," + value + ",";
			highScores.remove(--highscoreSize);
		}
		// Converting String to a byte[], than uploading the new high score table to the FTP server (no sorting, getHighScores() does that)
		byte[] byteArrayToUpload = highscoreContent.getBytes(Charset.forName("US-ASCII"));
		try {
			// Uploading it to the FTP server
			ftp = new FTPConnector("drauthev.sch.bme.hu", "space_battle", "");
			ftp.setFileTypeToBinary();
			OutputStream ostream = ftp.getFtp().storeFileStream("newhighscores.txt");
			ostream.write(byteArrayToUpload);
			ostream.close();
			boolean completed = ftp.getFtp().completePendingCommand();
            if (completed) {
                System.out.println("New highscore table uploaded successfully.");
                // deleting the old highscore table, then renaming the new one to highscores.txt -- renaming is an atomic operation, but there could be errors during uploading -- this grants that no compromise will happen to the existing highscore table     
                ftp.getFtp().deleteFile("highscores.txt");
                ftp.getFtp().rename("newhighscores.txt", "highscores.txt");
            }
            else{
            	System.out.println("Error during uploading new highscores to server. High score table is not updated.");
            }
	        
	        ftp.disconnect();
	        client1.changeGameState(GameState.NONE);
	        if( type == GameType.MULTI_NETWORK) client2.changeGameState(GameState.NONE);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
		
	// returns an ArrayList with the ordered highscore table; first element of the list is with the highest score
	public static List<Map.Entry<Integer, String>> getHighScores()
	{
	  // creating a new ListArray for the high score records
	  List<Map.Entry<Integer, String>> highScores = new ArrayList<>();


	 // SortedMap<Integer, String> highScores = new TreeMap<Integer, String>();
	  FTPConnector ftp;
	  String highscoresFileContent;
	  String[] highscoreEntries;
	  
	  try {
		ftp = new FTPConnector("drauthev.sch.bme.hu", "space_battle", "");
		if( ftp.getFtp().isConnected() ){
			highscoresFileContent = ftp.downloadFileAndCopyToString("highscores.txt");
			
			if(highscoresFileContent != null){
				highscoreEntries = highscoresFileContent.split(","); // each odd member of the String[] will be a score, the next even member will be the playerName attached to it
				for(int i=0; i<highscoreEntries.length; i+=2){
					int key = Integer.parseInt(highscoreEntries[i]);
					String value = highscoreEntries[i+1];
					highScores.add(new AbstractMap.SimpleEntry<Integer, String>(key, value));			
				}
			  // Sorting the ArrayList with a custom Comparator - decreasing order
			  Collections.sort(highScores, new Comparator<Map.Entry<Integer, String>>() {
				  @Override
				  public int compare(Map.Entry<Integer, String> record1, Map.Entry<Integer, String> record2) {
				    if(record1.getKey() == record2.getKey())
				    	return 0;
				    else if(record1.getKey() > record2.getKey()) // sorting to decreasing order
				    	return -1;
				    else
				    	return 1;
				  }
				});
			}
			else{ // if there is no highscores.txt on the FTP server, creating a dummy, and returning an empty SortedMap
				OutputStream ostream = ftp.getFtp().storeFileStream("highscores.txt");
				String dummyHighScoreTable = "10,LameGameMakers,10,LameGameMakers,10,LameGameMakers,10,LameGameMakers,10,LameGameMakers,10,LameGameMakers,10,LameGameMakers,10,LameGameMakers,10,LameGameMakers,10,LameGameMakers";
				ostream.write(dummyHighScoreTable.getBytes());
				ostream.close();
				boolean completed = ftp.getFtp().completePendingCommand();
	            if (completed) {
	                System.out.println("getHighScore(): Dummy highscore table uploaded successfully.");
	            }
	            else{
	            	 System.out.println("getHighScore(): Error during the upload of the dummy highscore table.");
	            }
			}		
			ftp.disconnect();
		}
		// else returning an empty sortedmap	
	  } catch (Exception e) {
		e.printStackTrace();
	  }
	  return highScores;
	}
	
	public static int getHighestScore()
	{
		List<Map.Entry<Integer, String>> highScores = new ArrayList<>();
		highScores = getHighScores();
		if( highScores.size() == 0){ // no valid highScore table -- broken FTP connection
			return 0;
		}
		else{
			int highestScore = highScores.get(0).getKey(); // the first element in the arrayList is the one with the highest score
			return highestScore;
		}
	}


	@Override
	public void setClient2(ClientForServer c2) {
		client2 = c2;	
	}

}
