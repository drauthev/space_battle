package space_battle.server;
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

import space_battle.client.Client;
import space_battle.client.PlayerController;
import space_battle.enums.GameSkill;
import space_battle.enums.GameState;
import space_battle.enums.GameType;
import space_battle.gui.GUI;
import space_battle.interfaces.AllServerInterfaces;
import space_battle.interfaces.ClientForServer;
import space_battle.server.game_elements.Boom;
import space_battle.server.game_elements.Fastener;
import space_battle.server.game_elements.GameElement;
import space_battle.server.game_elements.HalfScores;
import space_battle.server.game_elements.HostileFrenzy;
import space_battle.server.game_elements.HostileType1;
import space_battle.server.game_elements.HostileType2;
import space_battle.server.game_elements.HostileType3;
import space_battle.server.game_elements.Laser;
import space_battle.server.game_elements.LeftRightSwitcher;
import space_battle.server.game_elements.Modifier;
import space_battle.server.game_elements.NPC;
import space_battle.server.game_elements.NoAmmo;
import space_battle.server.game_elements.OneUp;
import space_battle.server.game_elements.Player;
import space_battle.server.game_elements.PowerDown;
import space_battle.server.game_elements.Projectile;
import space_battle.server.game_elements.ProjectileGoingDiagonallyLeft;
import space_battle.server.game_elements.ProjectileGoingDiagonallyRight;
import space_battle.server.game_elements.ProjectileGoingDown;
import space_battle.server.game_elements.ProjectileGoingUp;
import space_battle.server.game_elements.ProjectileLaser;
import space_battle.server.game_elements.Shield;
import space_battle.server.game_elements.SpaceShipSwitcher;
import space_battle.sound.SoundType;
/**
 * <h1>The class responsible for implementing the game logic.</h1>
 * This class communicates with the {@link Client} and {@link PlayerController} classes through {@link AllServerInterfaces}. 
 * In case of network multiplayer, an instance of this class is instantiated on the host side only - all game logic runs on the host machine.
 * 
 * @author daniel.szeifert
 * @version 1.0
 * @since 2014-05-17
 */
public class Server implements AllServerInterfaces
{
	// Game Options
	private GameType type;
	private GameSkill difficulty;
	//
	private int score;
	/**
	 * The timer is not stopped when someone request a pause, however each timed task runs only if this flag is true. (pause requests change it to false)
	 */
	private boolean isRunning;	
	// Booleans, variables for player controlling
	private boolean player1MovingLeft;
	private boolean player1MovingRight;
	private boolean player1Shooting;
	private boolean player2MovingLeft;
	private boolean player2MovingRight;
	private boolean player2Shooting;
	/**
	 * The {@link #isGameOver()} method checks this flag and sets {@link #gameIsOver} accordingly.
	 */
	private boolean player1Dead = false;
	/**
	 * The {@link #isGameOver()} method checks this flag and sets {@link #gameIsOver} accordingly.
	 */
	private boolean player2Dead = false;
	/**
	 * At gameOver, the gameStates are not changed immediately, because the GUI has to animate the final explosion of the player's spaceship.
	 * Although this flags gets true immediately, signalling to the game logic that no change it should make anymore.
	 */
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
	/**
	 * Used during network multi player for the Pause/Start request handling.
	 */
	private boolean client1Ready;
	/**
	 * Used during network multi player for the Pause/Start request handling.
	 */
	private boolean client2Ready;
	/**
	 * At the start of the game, {@link Server} has to call {@link space_battle.interfaces.ClientForServer#updateObjects(String)} on the clients before changing their game state to {@link GameState#RUNNING}.
	 * Otherwise the {@link GUI} cannot draw to the screen.
	 */
	private boolean initState = true;
	/**
	 * The one timer instance ued for all timed tasks.
	 */
	private Timer timer;
	
	// Inner class definitions for TimerTasks, and instantiation of timerTasks
	// FASTENER
	/**
	 * TimerTask for terminating a modifier-effect. Scheduled by {@link Server#detectModifierPickUps()} for the time got by {@link Modifier#getTimeItLasts()}.
	 * Same pertains to the classes extending TimerTask in {@link #Server()}'s field.
	 * @author daniel.szeifert
	 *
	 */
	private class taskElapseFastenerPlayer1 extends TimerTask {
		@Override
		public void run() {
			for(int i=0; i<listOfPlayers.size(); i++){
				if(listOfPlayers.get(i).getID() == 0){
					listOfPlayers.get(i).setFastened(false);
					listOfPlayers.get(i).setTimeBetweenShots(Constants.timeBetweenShots);
				}
			}
		}
	}
	private class taskElapseFastenerPlayer2 extends TimerTask {
		@Override
		public void run() {
			for(int i=0; i<listOfPlayers.size(); i++){
				if(listOfPlayers.get(i).getID() == 1){
					listOfPlayers.get(i).setFastened(false);
					listOfPlayers.get(i).setTimeBetweenShots(Constants.timeBetweenShots);
				}
			}
		}
	}
	/**
	 * Instance of the modifier-effect terminating TimerTask class.
	 * Has to be defined here because {@link Server#detectModifierPickUps()} has to cancel and reschedule it for the proper time, if the same kind of modifier is picked up.
	 */
	taskElapseFastenerPlayer1 elapseFastenerPlayer1;
	taskElapseFastenerPlayer2 elapseFastenerPlayer2;
	// SHIELD
	private class taskElapseShieldPlayer1 extends TimerTask {
		@Override
		public void run() {
			for(int i=0; i<listOfPlayers.size(); i++){
				if(listOfPlayers.get(i).getID() == 0){
					listOfPlayers.get(i).setShielded(false);
				}
			}
		}
	}
	private class taskElapseShieldPlayer2 extends TimerTask {
		@Override
		public void run() {
			for(int i=0; i<listOfPlayers.size(); i++){
				if(listOfPlayers.get(i).getID() == 1){
					listOfPlayers.get(i).setShielded(false);
				}
			}
		}
	}
	taskElapseShieldPlayer1 elapseShieldPlayer1;
	taskElapseShieldPlayer2 elapseShieldPlayer2;
	// LASER
	private class taskElapseLaserPlayer1 extends TimerTask {
		@Override
		public void run() {
			for(int i=0; i<listOfPlayers.size(); i++){
				if(listOfPlayers.get(i).getID() == 0){
					listOfPlayers.get(i).setHasLaser(false);
				}
			}
		}
	}
	private class taskElapseLaserPlayer2 extends TimerTask {
		@Override
		public void run() {
			for(int i=0; i<listOfPlayers.size(); i++){
				if(listOfPlayers.get(i).getID() == 1){
					listOfPlayers.get(i).setHasLaser(false);
				}
			}
		}
	}
	taskElapseLaserPlayer1 elapseLaserPlayer1;
	taskElapseLaserPlayer2 elapseLaserPlayer2;
	// LEFT RIGHT SWITCHER
	private class taskElapseLeftRightSwitcherPlayer1 extends TimerTask {
		@Override
		public void run() {
			for(int i=0; i<listOfPlayers.size(); i++){
				if(listOfPlayers.get(i).getID() == 0){
					listOfPlayers.get(i).setLeftRighSwitched(false);
				}
			}
		}
	}
	private class taskElapseLeftRightSwitcherPlayer2 extends TimerTask {
		@Override
		public void run() {
			for(int i=0; i<listOfPlayers.size(); i++){
				if(listOfPlayers.get(i).getID() == 1){
					listOfPlayers.get(i).setLeftRighSwitched(false);
				}
			}
		}
	}
	taskElapseLeftRightSwitcherPlayer1 elapseLeftRightSwitcherPlayer1;
	taskElapseLeftRightSwitcherPlayer2 elapseLeftRightSwitcherPlayer2;
	// POWERDOWN ELAPSER TASK CLASSES
	private class taskElapseHostileFrenzy extends TimerTask {
		@Override
		public void run() {
				hostilesAreFrenzied = false;
			}
	}
	taskElapseHostileFrenzy elapseHostileFrenzy;
	// NOAMMO
	private class taskElapseNoAmmoPlayer1 extends TimerTask {
		@Override
		public void run() {
				for(int i=0; i<listOfPlayers.size(); i++){
					if(listOfPlayers.get(i).getID() == 0){
						listOfPlayers.get(i).setHasAmmo(true);
					}
				}
			}
	}
	private class taskElapseNoAmmoPlayer2 extends TimerTask {
		@Override
		public void run() {
				for(int i=0; i<listOfPlayers.size(); i++){
					if(listOfPlayers.get(i).getID() == 1){
						listOfPlayers.get(i).setHasAmmo(true);
					}
				}
			}
	}
	taskElapseNoAmmoPlayer1 elapseNoAmmoPlayer1;
	taskElapseNoAmmoPlayer2 elapseNoAmmoPlayer2;
	// HALF SCORES
	private class taskElapseHalfScores extends TimerTask {
		@Override
		public void run() {
				halfScores = false;
			}
	}
	taskElapseHalfScores elapseHalfScores;
	// SPACE SHIP SWITCHER
	private class taskElapseSpaceShipSwitcher extends TimerTask {
		@Override
		public void run() {
				spaceShipsSwitched = false;
			}
	}
	taskElapseSpaceShipSwitcher elapseSpaceShipSwitcher;
	

	/**
	 * Constructor. Sets {@link #difficulty} and {@link #type}, creates the empty lists for each GameElement, initializes flags and creates 1/2 {@link Player} object according to {@link #difficulty}
	 * @param type {@link GameType#SINGLE} or {@link GameType#MULTI_LOCAL} or {@link GameType#MULTI_NETWORK}.
	 * @param difficulty {@link GameSkill#EASY} or {@link GameSkill#NORMAL} or {@link GameSkill#HARD}.
	 * @param cl1 Reference to the {@link ClientForServer} interface of the host side.
	 */
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
	/**
	 * Calls all methods which are responsible for the game dynamic.
	 * Scheduled by {@link #run()}.
	 */
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
	/**
	 * Update the coordinates of {@link NPC}s, {@link Projectile}s and {@link Modifier}s.
	 * <p>Called by {@link #trackChanges()}.
	 */
	private void moveNPCs(){
		// Moving Projectiles
		for(int i=0; i < listOfProjectiles.size(); i++){
    		listOfProjectiles.get(i).autoMove();
    	}
    	// Moving hostile spaceships
    	for(int i=0; i < listOfNPCs.size(); i++){
    		if( listOfNPCs.get(i).getExplosionTime() == 0){ // moving "alive" NPCs only
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
    	}
				
    	// Moving Modifiers
    	for(int i=0; i < listOfModifiers.size(); i++){
    		if( listOfModifiers.get(i).getExplosionTime() == 0){ // exploded modifiers don't move
    			listOfModifiers.get(i).autoMove();
    		}
    	}
	}
	/**
	 * Auxilary method for the {@link HostileType3}s "shooting periods". Calls {@link HostileType3#setShootingIsEnabled(boolean)} and {@link HostileType3#setShootingWasEnabled(long)}.
	 * <p>Called by {@link #trackChanges()}.
	 */
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
	/**
	 * Calls {@link NPC#shoot()} on hostiles in {@link #listOfNPCs} and add the returned {@link Projectile} to {@link #listOfProjectiles}.
	 * <p>Called by {@link #trackChanges()}.
	 */
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
	/**
	 * Update {@link Player}s' coordinates and calls their {@link Player#shoot()} method according to the flags set by {@link #moveLeft(int)}, {@link #releaseLeft(int)}, {@link #moveRight(int)}, {@link #releaseRight(int)} and {@link #fire(int)}, {@link #releaseFire(int)}. 
	 * <p>Called by {@link #trackChanges()}.
	 */
	private void controlPlayers(){
		Player temp;
		long currentTime;
		for(int i=0; i<listOfPlayers.size(); i++){
			temp = listOfPlayers.get(i);
			if(temp.getID() == 0 && temp.getExplosionTime() == 0){
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
			else if(temp.getID() == 1 && temp.getExplosionTime() == 0){
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
	/**
	 * Checks if {@link Player}s overlap with {@link NPC}s in {@link #listOfNPCs}.
	 * Make the {@link NPC} explode and decrease {@link Player}'s lives by one if there is an overlap.
	 * <p>Called by {@link #trackChanges()}.
	 */
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
										
					// checking Player's lives
					tempPlayer.setLives(tempPlayer.getLives() - 1);
					tempPlayer.setShielded(false); // collision removes Shield
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
				}
			}
		}
	}
	/**
	 * Checks if elements of {@link #listOfProjectiles} overlap with {@link Player}s/ {@link NPC}s.
	 * Decrease lives/explode objects if needed.
	 * <p> Called by {@link #trackChanges()}. 
	 */
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
	/** Checks if an element in {@link #listOfProjectiles} hits an element in {@link #listOfModifiers}.
	 * If a {@link Modifier} is hit, it explodes and two new {@link Modifier} spawn under the explosion.
	 * <p> Called by {@link #trackChanges()}.
	 * <p> Calls {@link #createModifiersFromAnExplodedOne(Modifier)}
	 */
	private void detectModifierHits(){ // Separate function for mod hit detect; this way there are no problems with list indexing
		Projectile proj;
		Modifier mod;
		for(int i=0; i<listOfProjectiles.size(); i++){
			proj = listOfProjectiles.get(i);
			// Detect Modifier-hits
			for(int j=0; j<listOfModifiers.size(); j++){
				mod = listOfModifiers.get(j);
				if( mod.getExplosionTime() == 0 ){ // doing nothing if the modifier is already exploded (but in the list for animation)
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
	/**
	 * Function responsible for the spawning of the two new {@link Modifier}s from an exploded one.
	 * If the exploded one was a {@link PowerDown}, the two new ones will be one, too and vice versa.
	 * <p> Called by {@link #detectModifierHits()}
	 * @param explodedMod Reference to the exploded {@link Modifier} to get the coordinates.
	 */
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
	/**
	 * A function called by a periodic TimerTask scheduled in {@link #run()}.
	 * <p> There is a 40% chance this method spawns a random {@link PowerDown} with the given coordinates.
	 * @param x Coordinate X of the spawning.
	 * @param y Coordinate Y of the spawning.
	 */
	private void spawnPowerDown(double x, double y){	
		double spawnOrNot = Math.random(); // some randomness.. spawn smthng or not at all
		if(spawnOrNot >= 0.6){
			double whatToSpawn = Math.random();
			if( listOfPlayers.size() == 2){
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
			else{ // single player or only 1 player alive -> no SpaceShipSwitcher
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
	/**
	 * A function called by a periodic TimerTask scheduled in {@link #run()}.
	 * <p> There is a 40% chance this method spawns a power up with the given coordinates.
	 * <p> There is a spawning frequency related to the power ups - frequent/normal/rare - which this method considers.
	 * @param x Coordinate X of the spawning.
	 * @param y Coordinate Y of the spawning.
	 */
	private void spawnPowerUp(double x, double y){
		double spawnOrNot = Math.random(); // some randomness.. spawn smthng or not at all
		if(spawnOrNot >= 0.6){
			// Choosing what to spawn
			double whichFrequency = Math.random();
			double whatToSpawn = Math.random();
			if(whichFrequency <= 0.4){ // frequent modifier(s)
				listOfModifiers.add( new Fastener(x, y) );
			}
			else if(whichFrequency > 0.4 && whichFrequency < 0.8){ // mods /w normal freq
				if(whatToSpawn < 0.5)
					listOfModifiers.add( new Shield(x, y) );
				else
					listOfModifiers.add( new Laser(x, y) );
			}
			else{ // rare mods
				if(whatToSpawn < 0.5)
					listOfModifiers.add( new OneUp(x, y) );
				else
					listOfModifiers.add( new Boom(x, y) );
			}
		}
	}
	/**
	 * Detects if there is an overlap between elements of {@link #listOfModifiers} and {@link #listOfPlayers}, that is a player picked up a modifier.
	 * <p> If there is a pickup, the method sets the proper flags in the field of {@link Server} or sets the flags in the field of the {@link Player} object.
	 * <p> Also schedules modifier-effect terminating TimerTasks if the modifier takes effect for a period of time (most of the cases).
	 * <p> Called by {@link #trackChanges()}. 
	 */
	private void detectModifierPickUps(){
	
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
								if( tempPlayer.isFastened() ){ // already fastened - cancel TimerTask which elapses the Fastener effect, then reschedule it (renew the time it lasts)
									if( tempPlayer.getID() == 0){ // player1
										elapseFastenerPlayer1.cancel();
										elapseFastenerPlayer1 = new taskElapseFastenerPlayer1();
										timer.purge();
										timer.schedule(elapseFastenerPlayer1, Fastener.getTimeItLasts());
									}
									else{ // player2
										elapseFastenerPlayer2.cancel();
										elapseFastenerPlayer2 = new taskElapseFastenerPlayer2();
										timer.purge();
										timer.schedule(elapseFastenerPlayer2, Fastener.getTimeItLasts());
									}					
								}
								else{ // not fastened yet
									if( tempPlayer.getID() == 0){ //player1
										tempPlayer.setFastened(true);
										tempPlayer.setTimeBetweenShots(Constants.timeBetweenShotsIfFastened);
										elapseFastenerPlayer1 = new taskElapseFastenerPlayer1();
										timer.schedule(elapseFastenerPlayer1, Fastener.getTimeItLasts());
									}
									else{ //player2
										tempPlayer.setFastened(true);
										tempPlayer.setTimeBetweenShots(Constants.timeBetweenShotsIfFastened);
										elapseFastenerPlayer2 = new taskElapseFastenerPlayer2();
										timer.schedule(elapseFastenerPlayer2, Fastener.getTimeItLasts());
									}				
								}
							}
							if(tempMod instanceof OneUp){
								if(tempPlayer.getLives() < 5)
									listOfPlayers.get(i).setLives(tempPlayer.getLives()+1);
							}
							if(tempMod instanceof Shield){
								if( tempPlayer.isShielded() ){
									if( tempPlayer.getID() == 0){ // player1
										elapseShieldPlayer1.cancel();
										elapseShieldPlayer1 = new taskElapseShieldPlayer1();
										timer.purge();
										timer.schedule(elapseShieldPlayer1, Shield.getTimeItLasts());
									}
									else{ // player2
										elapseShieldPlayer2.cancel();
										elapseShieldPlayer2 = new taskElapseShieldPlayer2();
										timer.purge();
										timer.schedule(elapseShieldPlayer2, Shield.getTimeItLasts());
									}
								}
								else{
									tempPlayer.setShielded(true);
									if(tempPlayer.getID() == 0){
										elapseShieldPlayer1 = new taskElapseShieldPlayer1();
										timer.schedule(elapseShieldPlayer1, Shield.getTimeItLasts());
									}
										
									else{
										elapseShieldPlayer2 = new taskElapseShieldPlayer2();
										timer.schedule(elapseShieldPlayer2, Shield.getTimeItLasts());
									}									
								}
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
								if( tempPlayer.isHasLaser() ){ // already has laser
									if( tempPlayer.getID() == 0){ // player1
										elapseLaserPlayer1.cancel();
										elapseLaserPlayer1 = new taskElapseLaserPlayer1();
										timer.purge();
										timer.schedule(elapseLaserPlayer1, Laser.getTimeItLasts());
									}
									else{ // player2
										elapseLaserPlayer2.cancel();
										elapseLaserPlayer2 = new taskElapseLaserPlayer2();
										timer.purge();
										timer.schedule(elapseLaserPlayer2, Laser.getTimeItLasts());
									}
								}
								else{ // has not got laser yet
									tempPlayer.setHasLaser(true);
									if(tempPlayer.getID() == 0){
										elapseLaserPlayer1 = new taskElapseLaserPlayer1();
										timer.schedule(elapseLaserPlayer1, Laser.getTimeItLasts());
									}
									else{
										elapseLaserPlayer2 = new taskElapseLaserPlayer2();
										timer.schedule(elapseLaserPlayer2, Laser.getTimeItLasts());
									}									
								}						
							}
							if(tempMod instanceof LeftRightSwitcher){
								if( tempPlayer.isLeftRighSwitched() ){ // left-right already switched
									if( tempPlayer.getID() == 0){ // player1
										elapseLeftRightSwitcherPlayer1.cancel();
										elapseLeftRightSwitcherPlayer1 = new taskElapseLeftRightSwitcherPlayer1();
										timer.purge();
										timer.schedule(elapseLeftRightSwitcherPlayer1, LeftRightSwitcher.getTimeItLasts());
									}
									else{ // player2
										elapseLeftRightSwitcherPlayer2.cancel();
										elapseLeftRightSwitcherPlayer2 = new taskElapseLeftRightSwitcherPlayer2();
										timer.purge();
										timer.schedule(elapseLeftRightSwitcherPlayer2, LeftRightSwitcher.getTimeItLasts());
									}
								}
								else{
									tempPlayer.setLeftRighSwitched(true);
									if(tempPlayer.getID() == 0){
										elapseLeftRightSwitcherPlayer1 = new taskElapseLeftRightSwitcherPlayer1();
										timer.schedule(elapseLeftRightSwitcherPlayer1, LeftRightSwitcher.getTimeItLasts());
									}	
									else{
										elapseLeftRightSwitcherPlayer2 = new taskElapseLeftRightSwitcherPlayer2();
										timer.schedule(elapseLeftRightSwitcherPlayer2, LeftRightSwitcher.getTimeItLasts());
									}
								}
							}
							if(tempMod instanceof HostileFrenzy){
								if( hostilesAreFrenzied ){ // hostiles are already frenzy
									elapseHostileFrenzy.cancel();
									elapseHostileFrenzy = new taskElapseHostileFrenzy();
									timer.purge();
									timer.schedule(elapseHostileFrenzy, HostileFrenzy.getTimeItLasts());
								}
								else{
									hostilesAreFrenzied = true;
									elapseHostileFrenzy = new taskElapseHostileFrenzy();
									timer.schedule(elapseHostileFrenzy, HostileFrenzy.getTimeItLasts());	
								}			
							}
							if(tempMod instanceof NoAmmo){
								if( !tempPlayer.isHasAmmo() ){ // player is already "out of ammo"
									if( tempPlayer.getID() == 0){ // player1
										elapseNoAmmoPlayer1.cancel();
										elapseNoAmmoPlayer1 = new taskElapseNoAmmoPlayer1();
										timer.purge();
										timer.schedule(elapseNoAmmoPlayer1, NoAmmo.getTimeItLasts());
									}
									else{ // player2
										elapseNoAmmoPlayer2.cancel();
										elapseNoAmmoPlayer2 = new taskElapseNoAmmoPlayer2();
										timer.purge();
										timer.schedule(elapseNoAmmoPlayer2, NoAmmo.getTimeItLasts());
									}
								}
								else{
									tempPlayer.setHasAmmo(false);
									if(tempPlayer.getID() == 0){
										elapseNoAmmoPlayer1 = new taskElapseNoAmmoPlayer1();
										timer.schedule(elapseNoAmmoPlayer1, NoAmmo.getTimeItLasts());
									}	
									else{
										elapseNoAmmoPlayer2 = new taskElapseNoAmmoPlayer2();
										timer.schedule(elapseNoAmmoPlayer2, NoAmmo.getTimeItLasts());
									}	
								}				
							}
							if(tempMod instanceof HalfScores){
								if(halfScores){
									elapseHalfScores.cancel();
									elapseHalfScores = new taskElapseHalfScores();
									timer.purge();
									timer.schedule(elapseHalfScores, HalfScores.getTimeItLasts());
								}
								else{
									halfScores = true;
									elapseHalfScores = new taskElapseHalfScores();
									timer.schedule(elapseHalfScores, HalfScores.getTimeItLasts());
								}						
							}
							if(tempMod instanceof SpaceShipSwitcher){
								if( listOfPlayers.size() == 2){ // there could be a remainder switcher, which MUST NOT take effect, as the player would lose control over the only spaceship
									if( spaceShipsSwitched ){ // already switched
										elapseSpaceShipSwitcher.cancel();
										elapseSpaceShipSwitcher = new taskElapseSpaceShipSwitcher();
										timer.purge();
										timer.schedule(elapseSpaceShipSwitcher, SpaceShipSwitcher.getTimeItLasts());
									}
									else{
										spaceShipsSwitched = true; // picking up a switcher during the effect of a previously picked up won't revert its effect, only lengthen it;
										elapseSpaceShipSwitcher = new taskElapseSpaceShipSwitcher();
										timer.schedule(elapseSpaceShipSwitcher, SpaceShipSwitcher.getTimeItLasts());
									}								
								}
							}
						}
					}
				}
			}
		}		
	}
	/**
	 * Removes {@link NPC}s, {@link Projectile}s and {@link Modifier}s which moved "under the game field".
	 * <p> Also removes exploded {@link NPC}s, {@link Player}s, {@link Modifier}s, and picked up {@link Modifier}s after the time needed by the {@link GUI}, too.
	 * These remained in the list for animation purposes only.
	 */
	private void removeNonExistentObjects(){
		long currentTime = java.lang.System.currentTimeMillis();
		long explosionTime;
		
		// Remove GameElements which have left the track
		// NPCs
		for(int i=0; i<listOfNPCs.size(); i++){
			NPC temp = listOfNPCs.get(i);
			if( temp.getCoordY()-35 > Constants.gameFieldHeigth ){
				if( difficulty == GameSkill.HARD){ // decrease score if the player(s) couldn't destroy the hostile (only on hard mode)
					score -= listOfNPCs.get(i).getScoreIfDestroyed()/2;
				}
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
	/**
	 * Method checking if the conditions of GameOver were true, i.e. the player(s) are dead.
	 * <p> If they are dead, sets {@link #gameIsOver} true, and schedules a TimerTask which will 
	 * change the client(s) game state(s) to {@link GameState#GAMEOVER} or 
	 * {@link GameState#GAMEOVER_NEW_HIGHSCORE} after 2 seconds, which is needed by the {@link GUI} for animating the player's explosion.
	 * <p> Calls {@link #isThereNewHighScore()} for deciding which game state to switch to.
	 * <p> Called by {@link #trackChanges()}.
	 */
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
	/**
	 * Calls {@link #getHighScores()} to determine if there is a new high score at the and of the game or not.
	 * @return true, if there is new high score, false otherwise.
	 */
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
	/**
	 * Calls the JSON converter functions - {@link #playersToJSON(List)}, {@link #npcsToJSON(List)}, {@link #projectilesToJSON(List)}, {@link #modifiersToJSON(List)} -  of the several {@link GameElement} types and concatenate their result to one JSON string.
	 * @return A String which is a JSON string, containing all the data with the current state of the game, needed by the {@link GUI} to draw.
	 */
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
	/**
	 * Converts information needed by the {@link GUI} of the {@link Player}s to a JSONArray.
	 * <p> Called by {@link #allToJSON()}.
	 * @param list ArrayList of the {@link Player} objects ({@link #listOfPlayers})
	 * @return A JSONArray containing the needed attributes of the {@link Player}s.
	 */
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
	/**
	 * Converts information needed by the {@link GUI} of all {@link NPC}s to a JSONArray.
	 * <p> Called by {@link #allToJSON()}.
	 * @param list ArrayList of the {@link NPC} objects ({@link #listOfNPCs})
	 * @return A JSONArray containing the needed attributes of the {@link NPC}s.
	 */
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
	/**
	 * Converts information needed by the {@link GUI} of all {@link Projectile}s to a JSONArray.
	 * <p> Called by {@link #allToJSON()}.
	 * @param list ArrayList of the {@link Projectile} objects ({@link #listOfProjectiles})
	 * @return A JSONArray containing the needed attributes of the {@link Projectile}s.
	 */
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
	/**
	 * Converts information needed by the {@link GUI} of all {@link Modifier}s to a JSONArray.
	 * <p> Called by {@link #allToJSON()}.
	 * @param list ArrayList of the {@link Modifier} objects ({@link #listOfModifiers})
	 * @return A JSONArray containing the needed attributes of the {@link Modifier}s.
	 */
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
	/**
	 * Run method of the {@link Server} class.
	 * <p> Starts the timer and schedules the periodic tasks according to {@link #difficulty}.
	 */
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
		TimerTask taskSpawnPowerUps = new TimerTask() {
			@Override
			public void run() {
				if(isRunning){
					// Determine the place of spawning
					double x = (int)(Math.random()*(Constants.gameFieldWidth - Modifier.getModifierwidth()) );
					x += Modifier.getModifierwidth()/2;
					
					spawnPowerUp(x, Modifier.getModifierheigth()+100);
					}
				}
		};
		TimerTask taskSpawnPowerDowns = new TimerTask() {
			@Override
			public void run() {
				if(isRunning){
					// Determine the place of spawning
					double x = (int)(Math.random()*(Constants.gameFieldWidth - Modifier.getModifierwidth()) );
					x += Modifier.getModifierwidth()/2;
					
					spawnPowerDown(x, Modifier.getModifierheigth()+100);
					}
				}
		};
		
		// Starting Timer 
		timer = new Timer(false);
		// Update game state at a given rate
        timer.scheduleAtFixedRate(taskTrackChanges, 0, 1000/Constants.framePerSecond);
        // Spawn hostiles and modifiers at rates according to game difficulty
        if( difficulty == GameSkill.EASY ){
        	timer.scheduleAtFixedRate(taskSpawnHostileType1, 0, Constants.hostile1spawningFrequency);
            timer.scheduleAtFixedRate(taskSpawnHostileType2, 1535, Constants.hostile2spawningFrequency/2);
            timer.scheduleAtFixedRate(taskSpawnHostileType3, 2768, Constants.hostile3spawningFrequency/2);
            //
        	timer.scheduleAtFixedRate(taskSpawnPowerUps, 300, 3000);	
            timer.scheduleAtFixedRate(taskSpawnPowerDowns, 350, 6000);
        }
        else if( difficulty == GameSkill.NORMAL ){
        	timer.scheduleAtFixedRate(taskSpawnHostileType1, 0, Constants.hostile1spawningFrequency);
            timer.scheduleAtFixedRate(taskSpawnHostileType2, 1535, Constants.hostile2spawningFrequency);
            timer.scheduleAtFixedRate(taskSpawnHostileType3, 2768, Constants.hostile3spawningFrequency);
            //
        	timer.scheduleAtFixedRate(taskSpawnPowerUps, 300, 3500);	
            timer.scheduleAtFixedRate(taskSpawnPowerDowns, 350, 3500);
        }
        else{
        	timer.scheduleAtFixedRate(taskSpawnHostileType1, 0, Constants.hostile1spawningFrequency);
            timer.scheduleAtFixedRate(taskSpawnHostileType2, 1535, Constants.hostile2spawningFrequency);
            timer.scheduleAtFixedRate(taskSpawnHostileType3, 2768, Constants.hostile3spawningFrequency);
            //
        	timer.scheduleAtFixedRate(taskSpawnPowerUps, 300, 7000);	
            timer.scheduleAtFixedRate(taskSpawnPowerDowns, 350, 2000);
        }     
	}
	/**
	 * Cancels the timer.
	 * <p> In case of network multiplayer, it changes the {@link Client}s' game states properly.
	 */
	@Override
	public void terminate(){
		// deleting timer thread
		timer.cancel();
		// terminating second client if exists
		if(type == GameType.MULTI_NETWORK){
			client1.changeGameState(GameState.NONE);
			client2.changeGameState(GameState.DISCONNECTED);
		}
	}
	/**
	 * When called, the game is started (or continued after a pause) in case of {@link GameType#SINGLE} or {@link GameType#MULTI_LOCAL}.
	 * <p> In case of {@link GameType#MULTI_NETWORK} it's a bit complicated, as it checks whether both players showed they are ready.
	 * @param c Reference to the {@link ClientForServer} requested the start.
	 */
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
	/**
	 * Pauses the game in case of {@link GameType#SINGLE} or {@link GameType#MULTI_LOCAL}.
	 * In case of {@link GameType#MULTI_NETWORK} it pauses the game and sets {@link #client1Ready}, {@link #client2Ready} to false.
	 * @param c Reference to the {@link ClientForServer} requested the pause.
	 */
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
	/**
	 * Gets called when some of the clients quit during game.  Make the DISCONNECTED window pop up at the other player's screen by changing game states to {@link GameState#DISCONNECTED}.
	 */
	@Override
	public void disconnect(ClientForServer c){
		isRunning = false;
		if( c == client1 ){
			client2.changeGameState(GameState.DISCONNECTED);
		}
		else{
			client1.changeGameState(GameState.DISCONNECTED);
		}
	}

	// Implementing ServerForPlayerController Interface
	// ------------------------------------------------------------------------------------------------------------------
	/**
	 * Sets {@link #player1MovingLeft} or {@link #player2MovingLeft} to true.
	 * <p> {@link #controlPlayers()} does the moving itself.
	 * @param  playerID 0/1, according to which player (1/2) is requested to move.
	 */
	@Override
	public void moveLeft(int playerID){
		if(playerID == 0){
			player1MovingLeft = true;
		}
		else if(playerID == 1){
			player2MovingLeft = true;
		}
	}
	/**
	 * Sets {@link #player1MovingLeft} or {@link #player2MovingLeft} to false.
	 * @param  playerID 0/1, according to which player (1/2) is requested the stop of the movement.
	 */
	@Override
	public void releaseLeft(int playerID){
		if(playerID == 0){
			player1MovingLeft = false;
		}
		else if(playerID == 1){
			player2MovingLeft = false;
		}
	}
	/**
	 * Sets {@link #player1MovingRight} or {@link #player2MovingRight} to true.
	 * <p> {@link #controlPlayers()} does the moving itself.
	 * @param  playerID 0/1, according to which player (1/2) is requested to move.
	 */
	@Override
	public void moveRight(int playerID){
		if(playerID == 0)
			player1MovingRight = true;
		else if(playerID == 1)
			player2MovingRight = true;
	}
	/**
	 * Sets {@link #player1MovingRight} or {@link #player2MovingRight} to false.
	 * @param  playerID 0/1, according to which player (1/2) is requested the stop of the movement.
	 */
	@Override
	public void releaseRight(int playerID){
		if(playerID == 0)
			player1MovingRight = false;
		else if(playerID == 1)
			player2MovingRight = false;
	}
	/**
	 * Sets {@link #player1Shooting} or {@link #player2Shooting} to true.
	 * <p> The continuous shooting is done by {@link #controlPlayers()} if the former flag is true.
	 * <p> This method also calls the {@link Player}'s {@link Player#shoot()} method once and adds the returned {@link Projectile} to {@link #listOfProjectiles}.
	 * This is done to guarantee one shoot in the case if the player presses the fire button for a very short amount of time and it gets released before the network controller would have sent the {@link #fire(int)} call.
	 * @param  playerID 0/1, according to which player (1/2) is requested to shoot.
	 */
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
	/**
	 * Sets player1Shooting or player2Shooting to false, according to the parameter.
	 * @param playerID 0/1 according to player1/2
	 */
	@Override
	public void releaseFire(int playerID){
		if(playerID == 0)
			player1Shooting = false;
		else if(playerID == 1)
			player2Shooting = false;
	}
	/**
	 * Being called after a player typed a name for the high score board.
	 * <p> Downloads the high score table from the FTP server, removes the last record, appends the new record and uploads the new table to the server.
	 * @param name Player/team name entered by the player to be on the high score board.
	 */
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
	/**
	 * Downloads the high score table from the FTP server and parse its content, than return an ordered list - first element is with the highest score.
	 * @return An ArrayList of Map.Entry<Integer, String> elements, which contain the ten record on the high score table.
	 */
	public static List<Map.Entry<Integer, String>> getHighScores()
	{
	  // creating a new ListArray for the high score records
	  List<Map.Entry<Integer, String>> highScores = new ArrayList<>();

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
	/**
	 * Downloads the high score table from the FTP server and return the highest score on it to display on the top of the screen during the game.
	 * @return An integer with the highest score reached yet.
	 */
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
	/**
	 * Sets the reference to the second client. During the constructor call, client2 is not ready yet.
	 */
	@Override
	public void setClient2(ClientForServer c2) {
		client2 = c2;	
	}

}
