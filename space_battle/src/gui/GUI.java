package gui;

// Java packages
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

//Game packages
import client.CModifier;
import client.CNPC;
import client.CPlayer;
import client.CProjectile;
import client.ObjectBuffer;
import enums.GameSkill;
import enums.GameState;
import enums.GameType;
import enums.PlayerAction;
import interfaces.ClientForGUI;
import interfaces.GUIForClient;


public class GUI extends JFrame implements KeyListener, MouseListener, GUIForClient {

	// GUI Window
	private static final long serialVersionUID = 1L;
	private static final int frameWidth = 480;
	private static final int frameHeight = 640;

	// Positioning background Image
	private double backgroundImgY = 0;
	private static final double backgroundSpeed = 0.2;

	// Parameters for positioning the images and text
	private static final int optionsAX = frameWidth/4+30;
	private static final int optionsBX = 3*frameWidth/4-30;
	private static final int middleX   = frameWidth/2;
	private static final int firstLineY = 150;
	private static final int lineHeight = 50;
	private static final int lineHeight_HS = 44;

	private int dotX; 

	// Fonts
	private static FontType score1FontType;
	private static FontType score2FontType;
	private static FontType titleFontType;
	private static FontType menuFontType;
	private static FontType stateFontType;
	private static FontType powerUpFontType;
	private static FontType powerDownFontType;

	// Enums
	private GameState currentGameState;
	private MenuState currentMenuState;
	private GameSkill currentGameSkill;

	// GraphicObjects
	private ImageCollector imgCollector = new ImageCollector();

	// Used for double buffering
	Graphics2D bufferGraphics;  
	BufferedImage offscreen;

	// Tick Counters
	private long localTick = 0;

	// Timer
	private Timer timer = new Timer(false);

	// Initialize Timer
	TimerTask reapaintTimer = new TimerTask() {
		@Override
		public void run() {
			repaint();
			localTick += 16;
		}
	};

	//Strings containing Keyboard settings
	private String[] actionKeys = new String[6];

	//Client
	private ClientForGUI client;

	// Object buffer
	private ObjectBuffer localObjectBuffer;

	// IP handling
	private String[] ipAddresses = null;
	private static final int ipAddresseslength = 4;

	// HS handling
	private String highScoreValues[] = new String[10];
	private String highScoreString[] = new String[10];

	// Dot handling
	private int dotLine;
	private int dotLineToDraw;
	private int mainDot;
	private int optionsDot;
	private int newGameDot;

	//Handling Keys
	private static final String[] actionKeysNames = {"P1 Left","P1 Right","P1 Fire","P2 Left","P2 Right","P2 Fire"};
	private int keyBoardChangeSelected = 0;

	// Other variables
	private String textField;
	private boolean isEffectOn;
	private int lastMenuHS = 1;
	private int currentHighScore;
	private int shipID[] = new int[2];

	public GUI(ClientForGUI client_param)
	{

		// Initializing fonts for writing strings to the monitor
		score1FontType = new FontType("monospscoreFont", 22,Color.YELLOW);
		score2FontType = new FontType("monospscoreFont", 22,Color.GREEN);
		titleFontType = new FontType("monospscoreFont", 42,Color.WHITE);
		menuFontType  = new FontType("monospscoreFont", 26,Color.WHITE);
		stateFontType = new FontType("monospscoreFont", 26,Color.WHITE);
		powerUpFontType = new FontType("monospscoreFont", 26,Color.GREEN);
		powerDownFontType = new FontType("monospscoreFont", 26,Color.RED);

		// Initialize Local variables
		setGameState(GameState.NONE);
		setMenuState(MenuState.MAIN_MENU);

		// Generate Window
		setTitle("Space Battle GUI");
		setSize(frameWidth, frameHeight);
		final Toolkit toolkit = Toolkit.getDefaultToolkit();
		final Dimension screenSize = toolkit.getScreenSize();
		final int x = (screenSize.width - frameWidth) / 2;
		final int y = (screenSize.height - frameHeight) / 2;
		setLocation(x, y);
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		// Manage Double Buffering
		offscreen = new BufferedImage(frameWidth,frameHeight,BufferedImage.TYPE_INT_RGB); 
		bufferGraphics = offscreen.createGraphics();  

		// Manage Rendering
		bufferGraphics.setRenderingHint(
				RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);

		// Client
		client = client_param;

		// Add listeners
		addKeyListener(this);
		addMouseListener(this); 
		addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent windowEvent) {
				client.terminate();
			}
		});

		// Get Keyboard settings
		EnumMap<PlayerAction, Integer> asd = client.getKeyboardSettings();

		actionKeys[0] = KeyEvent.getKeyText(asd.get(PlayerAction.P1LEFT));
		actionKeys[1] = KeyEvent.getKeyText(asd.get(PlayerAction.P1RIGHT));
		actionKeys[2] = KeyEvent.getKeyText(asd.get(PlayerAction.P1FIRE));
		actionKeys[3] = KeyEvent.getKeyText(asd.get(PlayerAction.P2LEFT));
		actionKeys[4] = KeyEvent.getKeyText(asd.get(PlayerAction.P2RIGHT));
		actionKeys[5] = KeyEvent.getKeyText(asd.get(PlayerAction.P2FIRE));

	}

	public void run() {
		timer.scheduleAtFixedRate(reapaintTimer, 0, 1000/100);
	}


	/******************************* INTERFACE FOR CLIENT ****************************************/

	public void setGameState(GameState gs)	
	{
		textField = "";
		dotLine = 1;
		mainDot = 1;
		optionsDot = 1;
		newGameDot = 1;

		if (gs == GameState.PAUSED) setMenuState(MenuState.PAUSED_MENU);
		else if  (gs == GameState.NONE) setMenuState(MenuState.MAIN_MENU);

		currentGameState = gs;
		System.out.println("GameState changed to " + currentGameState);
	}

	public void error(String text) {
		JOptionPane.showMessageDialog(null, text, "Error", JOptionPane.INFORMATION_MESSAGE);
	}

	public void terminate() {
		timer.cancel();
		setVisible(false);
		dispose();	
	}

	public void setSound(Boolean val) {
		isEffectOn = val;
	}

	public void setDifficulty(GameSkill gs) {
		currentGameSkill = gs;
	}

	public void setRecentIPs(String[] iparr) {
		ipAddresses = iparr.clone();
	}


	/******************************** PAINT PROCEDURE ************************************/

	public void paint(Graphics g)
	{
		// Draw previous screen
		g.drawImage(offscreen,0,0,this);

		// Draw Background
		drawBackground();

		// Refresh object buffer if it's necessary
		if (currentGameState == GameState.RUNNING)
			localObjectBuffer = client.getNewObjectBuffer();

		// Draw GAME related content
		if (currentGameState != GameState.NONE && currentGameState != GameState.PAUSED)
		{		
			if (currentGameState != GameState.CONNECTING && currentGameState != GameState.WAITING && currentGameState != GameState.DISCONNECTED)
			{
				long serverTick = localObjectBuffer.currentTick;

				int numberOfLivesA = 0;
				int numberOfLivesB = 0;

				for (int i = 0; i < localObjectBuffer.npcCount; i++)
				{
					CNPC localNPC = localObjectBuffer.npc[i];

					if (localNPC.className.equals("HostileType1"))
						imgCollector.setEnemy(1);
					else if (localNPC.className.equals("HostileType2"))
						imgCollector.setEnemy(2);
					else if (localNPC.className.equals("HostileType3"))
						imgCollector.setEnemy(3);

					if (localNPC.explosionTime != 0) 
					{
						int tickDiff_div = (int) (serverTick-localNPC.explosionTime)/130;
						if (tickDiff_div < 4) drawObject(imgCollector.getEnemyBlowImgObj(tickDiff_div), localNPC.x, localNPC.y);
					}
					else if (localNPC.hitTime != 0 && ((serverTick - localNPC.hitTime) < 1500)) 
					{							
						int tickDiff_div = (int) (serverTick-localNPC.hitTime)/200;
						if (tickDiff_div % 2 == 1) drawObject(imgCollector.getEnemyImgObj(0), localNPC.x, localNPC.y);
					}
					else 
					{
						if (localNPC.className.equals("HostileType3"))
							if (localNPC.teleportTime != 0)
								if (serverTick - localNPC.teleportTime < 600)
									;//drawObject(teleportImgObj, localNPC.x, localNPC.y, teleportWidth,teleportHeight);

						int tickDiff_div = (int) (serverTick-localNPC.creationTime)/1000;
						drawObject(imgCollector.getEnemyImgObj(tickDiff_div), localNPC.x, localNPC.y);
					}
				}

				for (int i = 0; i < localObjectBuffer.playerCount; i++)
				{
					CPlayer localPlayer = localObjectBuffer.player[i];

					if (localObjectBuffer.player[i].id == 0) numberOfLivesA = localPlayer.numberOfLives;
					else numberOfLivesB = localPlayer.numberOfLives;

					if (localPlayer.explosionTime != 0 )
					{
						int tickDiff_div = (int) (serverTick - localPlayer.explosionTime)/200;
						if      (tickDiff_div < 5) drawObject(imgCollector.getSpaceShipBombImgObj(tickDiff_div), localPlayer.x, localPlayer.y);
					}

					else if (localPlayer.hitTime != 0 && ((serverTick - localPlayer.hitTime) < 1500)) 
					{							
						int tickDiff_div = (int) (serverTick-localPlayer.hitTime)/200;
						if (tickDiff_div % 2 == 1) 
						{
							if (localObjectBuffer.player[i].isShielded)
								drawObject(imgCollector.getShieldImgObj(), localPlayer.x, localPlayer.y);
							drawObject(imgCollector.getSpaceShipImgObj(shipID[localObjectBuffer.player[i].id]), localPlayer.x, localPlayer.y);
						}
					}
					else
					{
						if (localObjectBuffer.player[i].isShielded)
							drawObject(imgCollector.getShieldImgObj(), localPlayer.x, localPlayer.y);
						drawObject(imgCollector.getSpaceShipImgObj(shipID[localObjectBuffer.player[i].id]), localPlayer.x, localPlayer.y);
					}
				}

				for (int i = 0; i < localObjectBuffer.projCount; i++)
				{
					CProjectile localProjectile = localObjectBuffer.proj[i];

					if (localProjectile.className.equals("ProjectileGoingUp"))
						drawObject(imgCollector.getBulletImgObj(0)		, localProjectile.x	, localProjectile.y);
					else if (localProjectile.className.equals("ProjectileGoingDown"))
						drawObject(imgCollector.getBulletImgObj(1)		, localProjectile.x	, localProjectile.y);
					else if (localProjectile.className.equals("ProjectileLaser"))
						drawObject(imgCollector.getBulletImgObj(2)		, localProjectile.x	, localProjectile.y);

					else if (localProjectile.className.equals("ProjectileGoingDiagonallyRight"))
						drawObject(imgCollector.getProjectileGoingDiagonallyRightImgObj(), localProjectile.x	, localProjectile.y);
					else if (localProjectile.className.equals("ProjectileGoingDiagonallyLeft"))
						drawObject(imgCollector.getProjectileGoingDiagonallyLeftImgObj(), localProjectile.x	, localProjectile.y);

					else
					{
						drawObject(imgCollector.getBulletImgObj(1)		, localProjectile.x	, localProjectile.y	);
						System.out.println("Projectile with unknown name: " + localProjectile.className);
					}
				}

				int modificationNumber = 0;
				for (int i = 0; i < localObjectBuffer.modCount; i++)
				{
					boolean isUp;

					CModifier localModifier = localObjectBuffer.mod[i];	

					if (localModifier.className.equals("Shield") ||
							localModifier.className.equals("Boom") || localModifier.className.equals("Fastener") ||
							localModifier.className.equals("Laser") || localModifier.className.equals("OneUp"))
						isUp = true;

					else if (localModifier.className.equals("HalfScores") || localModifier.className.equals("HostileFrenzy") ||
							localModifier.className.equals("LeftRightSwitcher") || localModifier.className.equals("NoAmmo") ||
							localModifier.className.equals("SpaceShipSwitcher"))
						isUp = false;

					else
					{
						System.out.println("Not known powerUp/Down: " + localModifier.className);
						isUp = false;
					}


					if (localModifier.explosionTime != 0)
					{
						int tickDiff_div = (int) (serverTick-localModifier.explosionTime)/150;
						if (tickDiff_div < 4) drawObject(imgCollector.getPowerUpBlowImgObj(tickDiff_div), localModifier.x, localModifier.y);
					}

					else if (localModifier.pickupTime == 0)
					{
						if     (localModifier.className.equals("Shield"))	drawObject(imgCollector.getPowerUpImgObj(2)	, localModifier.x	, localModifier.y	);
						else if(localModifier.className.equals("Boom"))		drawObject(imgCollector.getPowerUpImgObj(3)	, localModifier.x	, localModifier.y	);
						else if(localModifier.className.equals("Fastener")) drawObject(imgCollector.getPowerUpImgObj(0)	, localModifier.x	, localModifier.y	);
						else if(localModifier.className.equals("Laser"))	drawObject(imgCollector.getPowerUpImgObj(4)	, localModifier.x	, localModifier.y	);
						else if(localModifier.className.equals("OneUp"))	drawObject(imgCollector.getPowerUpImgObj(1)	, localModifier.x	, localModifier.y	);
						else
							drawObject(imgCollector.getPowerDownImgObj()		, localModifier.x	, localModifier.y);
					}

					else
					{
						drawPowerTitle(7-modificationNumber,localModifier.className.toUpperCase(),isUp);
						modificationNumber++;
					}

				}

				drawLives(numberOfLivesA,numberOfLivesB);

				drawTopScores(localObjectBuffer.score,currentHighScore);

			}

			if (currentGameState == GameState.WAITING)
			{
				drawForeground(300,300);
				drawState("WAITING");
			}
			else if (currentGameState == GameState.CONNECTING)
			{
				drawForeground(300,300);
				drawState("CONNECT");
			}
			else if (currentGameState == GameState.GAMEOVER)
			{
				drawForeground(300,300);
				drawState("GAME OVER");
			}
			else if (currentGameState == GameState.GAMEOVER_NEW_HIGHSCORE)
			{
				drawForeground(500,300);
				drawState("NEW HIGH SCORE!");
				drawWritingLine(3,textField);
			}
			else if (currentGameState == GameState.DISCONNECTED)
			{
				drawForeground(300,300);
				drawState("DISCONNECTED!");
			}
		}

		// Draw MENU related content
		else
		{
			if (currentMenuState == MenuState.JOIN_GAME_MENU) dotLineToDraw = dotLine + 1;
			else dotLineToDraw = dotLine;

			if (currentMenuState == MenuState.MAIN_MENU)
			{
				drawTitle("SPACE BATTLE");
				drawMenuLine(1,"New Game");
				drawMenuLine(2,"Join Game");
				drawMenuLine(3,"Options");
				drawMenuLine(4,"High Scores");
				drawMenuLine(5,"Exit");
			}

			else if (currentMenuState == MenuState.PAUSED_MENU)
			{
				drawTitle("PAUSED");
				drawMenuLine(1,"Resume");
				drawMenuLine(2,"Disconnect");
				drawMenuLine(3,"New Game");
				drawMenuLine(4,"Join Game");
				drawMenuLine(5,"Options");
				drawMenuLine(6,"High Scores");
				drawMenuLine(7,"Exit");
			}

			else if (currentMenuState == MenuState.OPTIONS_MENU)
			{
				// Create string content for level and effects
				String levelString;
				if (currentGameSkill == GameSkill.EASY) levelString = "Easy";
				else if (currentGameSkill == GameSkill.NORMAL) levelString = "Normal";
				else levelString = "Hard";

				String effectString = "Off";
				if (isEffectOn == true) effectString = "On";

				// Draw menu
				drawTitle("OPTIONS");
				drawOptionsLine(1,"Effects",effectString);
				drawOptionsLine(2,"Level",levelString);
				drawMenuLine   (3,"Set Keyboard");
				drawMenuLine   (4,"Back");
			}

			else if (currentMenuState == MenuState.HIGH_SCORES_MENU)
			{
				drawTitle("HIGH SCORES");

				for(int j = 0; j < lastMenuHS-1; j++) 
					drawOptionsLine(j+1,highScoreString[j],highScoreValues[j]);

				drawMenuLine   (lastMenuHS,"Back");
			}

			else if (currentMenuState == MenuState.NEW_GAME_MENU)
			{
				drawTitle("NEW GAME");
				drawMenuLine   (1,"Single Game");
				drawMenuLine   (2,"Multi Game");
				drawMenuLine   (3,"Back");
			}

			else if (currentMenuState == MenuState.MULTI_MENU)
			{
				drawTitle("MULTIPLAYER GAME");
				drawMenuLine   (1,"Local Game");
				drawMenuLine   (2,"Network Game");
				drawMenuLine   (3,"Back");
			}
			else if (currentMenuState == MenuState.JOIN_GAME_MENU)
			{
				drawTitle("NETWORK GAME");
				drawMenuLine   (1,"Please Give Us the IP Address!");
				drawWritingLine(2,textField);

				for (int i = 0; i < ipAddresseslength; i++)
					drawMenuLine   (i+3,ipAddresses[i]);

				drawMenuLine   (getLastLine(),"Back");
			}
			else if (currentMenuState == MenuState.KEYBOARD_SETTINGS_MENU)
			{
				drawTitle("SET KEYBOARD");

				for (int i = 0; i < 6; i++)
					if (((keyBoardChangeSelected == 1) && (localTick/1000 % 2 == 0)) && i == dotLine - 1)
						drawOptionsLine(i+1,actionKeysNames[i],"");
					else
						drawOptionsLine(i+1,actionKeysNames[i],actionKeys[i]);

				drawMenuLine   (7,"Back");
			}

			// Draw Dot
			drawDot();
		}
	}

	/******************************** PAINT PART FUNCTIONS ************************************/

	// Function, use to draw Background image to screen
	public void drawBackground()
	{
		backgroundImgY += backgroundSpeed;
		if (backgroundImgY >= 640) backgroundImgY -= 640;

		if (backgroundImgY != 0)  
			bufferGraphics.drawImage(imgCollector.getBackgroundImgObj().getBufferedImg()	, 0	, -640 + (int)backgroundImgY	, imgCollector.getBackgroundImgObj().getWidth()	,imgCollector.getBackgroundImgObj().getHeight(),null);
		bufferGraphics.drawImage(imgCollector.getBackgroundImgObj().getBufferedImg()	, 0	, (int)backgroundImgY		, imgCollector.getBackgroundImgObj().getWidth()	,imgCollector.getBackgroundImgObj().getHeight(),null);
	}

	// Function, use to draw Foreground image to screen
	public void drawForeground(int width, int height)
	{
		bufferGraphics.drawImage(imgCollector.getForegroundImgObj().getBufferedImg(), (frameWidth)/2-150, 165, 300, 200, null);
	}

	// Function, use to draw ImageObjects to screen
	private void drawObject(ImageObject img, int x, int y) {
		bufferGraphics.drawImage(img.getBufferedImg() ,x-img.getWidth()/2 , y-img.getHeight()/2, img.getWidth(), img.getHeight(), null);
	}

	// Function, use to draw title to screen
	public void drawTitle(String title)
	{
		bufferGraphics.setFont(titleFontType.getFont());
		bufferGraphics.setColor(titleFontType.getColor());
		int CoordinateX = middleX - bufferGraphics.getFontMetrics().stringWidth(title)/2;
		int CoordinateY = 120;
		bufferGraphics.drawString(title,CoordinateX, CoordinateY);
	}

	// Function, use to draw a menu line to screen
	public void drawMenuLine(int lineNumber, String content)
	{
		bufferGraphics.setFont(menuFontType.getFont());
		bufferGraphics.setColor(menuFontType.getColor());
		int CoordinateX = middleX - bufferGraphics.getFontMetrics().stringWidth(content)/2;
		int CoordinateY = firstLineY + lineNumber * lineHeight;
		bufferGraphics.drawString(content,CoordinateX, CoordinateY);
		if (dotLineToDraw == lineNumber) dotX = CoordinateX;
	}

	// Function, use to draw a powerUp/Down name to screen
	public void drawPowerTitle(int lineNumber, String content, boolean isPowerUp)
	{	
		if(isPowerUp)
		{
			bufferGraphics.setFont(powerUpFontType.getFont());
			bufferGraphics.setColor(powerUpFontType.getColor());
		}
		else
		{
			bufferGraphics.setFont(powerDownFontType.getFont());
			bufferGraphics.setColor(powerDownFontType.getColor());
		}

		int CoordinateX = middleX - bufferGraphics.getFontMetrics().stringWidth(content)/2;
		int CoordinateY = firstLineY + lineNumber * lineHeight;
		bufferGraphics.drawString(content,CoordinateX, CoordinateY);
	}

	// Function, use to draw a writing line to screen (same as menu line, but there is an underline at the end of the string, flashing
	public void drawWritingLine(int lineNumber, String content)
	{
		bufferGraphics.setFont(menuFontType.getFont());
		bufferGraphics.setColor(menuFontType.getColor());

		int CoordinateX = frameWidth/2 - bufferGraphics.getFontMetrics().stringWidth(content)/2;
		int CoordinateY = firstLineY + lineNumber * lineHeight;

		if (localTick/1000 % 2 == 0) bufferGraphics.drawString(content,      CoordinateX, CoordinateY);
		else 							 bufferGraphics.drawString(content + "_",CoordinateX, CoordinateY);
		if (dotLineToDraw == lineNumber) dotX = CoordinateX;
	}

	// Function, use to draw a line with two rows
	public void drawOptionsLine(int lineNumber, String content1, String content2)
	{
		bufferGraphics.setFont(menuFontType.getFont());
		bufferGraphics.setColor(menuFontType.getColor());

		int CoordinateY;
		if (currentMenuState == MenuState.HIGH_SCORES_MENU)  CoordinateY = firstLineY + lineNumber * lineHeight_HS;
		else CoordinateY = firstLineY + lineNumber * lineHeight;

		int CoordinateX = optionsAX - bufferGraphics.getFontMetrics().stringWidth(content1)/2;
		if (currentMenuState == MenuState.HIGH_SCORES_MENU) CoordinateX = CoordinateX + 50;
		bufferGraphics.drawString(content1,CoordinateX, CoordinateY);
		if (dotLineToDraw == lineNumber) dotX = CoordinateX;

		CoordinateX = optionsBX - bufferGraphics.getFontMetrics().stringWidth(content2)/2;
		if (currentMenuState == MenuState.HIGH_SCORES_MENU) CoordinateX = CoordinateX + 70;
		bufferGraphics.drawString(content2,CoordinateX, CoordinateY);

	}	

	// Function, use to draw the dot before a menu line
	public void drawDot()
	{
		int CoordinateY = firstLineY - 20 + dotLineToDraw * lineHeight;
		int CoordinateX = dotX - 50;
		bufferGraphics.drawImage(imgCollector.getLifeImgObj().getBufferedImg()	, CoordinateX	, CoordinateY	, imgCollector.getLifeImgObj().getWidth()	,imgCollector.getLifeImgObj().getHeight(),null);

	}	


	// Function, use to draw the state in different Game States
	public void drawState(String content)
	{
		bufferGraphics.setFont(stateFontType.getFont());
		bufferGraphics.setColor(stateFontType.getColor());
		int CoordinateX = middleX - bufferGraphics.getFontMetrics().stringWidth(content)/2;
		bufferGraphics.drawString(content,CoordinateX, 230);
	}

	// Draw LIFE images into the bottom if the screen
	public void drawLives(int numberOfLives1, int numberOfLives2)
	{
		for (int i = 0; i < numberOfLives1; i++)
			drawObject(imgCollector.getLifeImgObj()	, 40 + 40*i	, 600);

		for (int i = 0; i < numberOfLives2; i++)
			drawObject(imgCollector.getLifeImgObj()	, frameWidth - 45 -  40*i	, 600);
	}		

	// Draw Scores and Titles to the top of the page
	public void drawTopScores(int curr, int high)
	{
		bufferGraphics.setFont(score1FontType.getFont());
		bufferGraphics.setColor(score1FontType.getColor());
		bufferGraphics.drawString("SCORE",    GUI.frameWidth/8, 46);
		bufferGraphics.drawString("HIGHSCORE", GUI.frameWidth/3+12, 46);

		bufferGraphics.setFont(score2FontType.getFont());
		bufferGraphics.setColor(score2FontType.getColor());
		bufferGraphics.drawString(Integer.toString(curr), GUI.frameWidth/8, 64);
		bufferGraphics.drawString(Integer.toString(high), GUI.frameWidth/3+60, 64);

	}

	/******************************** FUNCTIONS RELATED TO GRAPHIC PARTS  ************************************/

	// Get the number of the last line
	int getLastLine()
	{
		if      (currentMenuState == MenuState.MAIN_MENU) return 5;
		else if (currentMenuState == MenuState.PAUSED_MENU) return 7;
		else if (currentMenuState == MenuState.NEW_GAME_MENU) return 3;
		else if (currentMenuState == MenuState.MULTI_MENU) return 3;
		else if (currentMenuState == MenuState.HIGH_SCORES_MENU) return lastMenuHS;
		else if (currentMenuState == MenuState.OPTIONS_MENU) return 4;
		else if (currentMenuState == MenuState.KEYBOARD_SETTINGS_MENU) return 7;
		else if (currentMenuState == MenuState.JOIN_GAME_MENU) return ipAddresseslength +3;
		else return 1;
	}

	// Check if the IP is valid
	private boolean isValidIP(String ip) {
		try {
			if (ip == null || ip.isEmpty()) {
				return false;
			}

			String[] parts = ip.split( "\\." );
			if ( parts.length != 4 ) {
				return false;
			}

			for ( String s : parts ) {
				int i = Integer.parseInt( s );
				if ( (i < 0) || (i > 255) ) {
					return false;
				}
			}
			if(ip.endsWith(".")) {
				return false;
			}

			return true;
		} catch (NumberFormatException nfe) {
			return false;
		}
	}

	// Set Menu State
	void setMenuState(MenuState ms)
	{
		if      (currentMenuState == MenuState.MAIN_MENU || currentMenuState == MenuState.PAUSED_MENU)     
		{
			mainDot = dotLine;
		}
		else if (currentMenuState == MenuState.OPTIONS_MENU) 
		{
			if   (ms == MenuState.KEYBOARD_SETTINGS_MENU) optionsDot = dotLine;
			else optionsDot = 1;
		}
		else if (currentMenuState == MenuState.NEW_GAME_MENU) 
		{
			if (ms == MenuState.MULTI_MENU) newGameDot = dotLine;
			else newGameDot = 1;
		}

		if      (ms == MenuState.MAIN_MENU || ms == MenuState.PAUSED_MENU)   
			dotLine = mainDot; 
		else if (ms == MenuState.OPTIONS_MENU) 
			dotLine = optionsDot;
		else if (ms == MenuState.NEW_GAME_MENU)
			dotLine = newGameDot;
		else 
			dotLine = 1;

		if (ms == MenuState.HIGH_SCORES_MENU)
			refreshHighScore();

		currentMenuState = ms;
		System.out.println("MenuState changed to " + currentMenuState);

	}

	private void refreshHighScore() {

		List<Entry<Integer,String>> localList = client.getHighScores();	

		if (localList != null)
		{
			Iterator<Entry<Integer, String>> i=localList.iterator();
			int j= 0;

			while(i.hasNext())
			{
				Entry<Integer, String> m =i.next();

				highScoreValues[j]    = Integer.toString((Integer)m.getKey());
				highScoreString[j]    = (String)m.getValue();

				j++;
			}

			lastMenuHS = j+1;}
		else lastMenuHS = 1;

	}

	private void generateRandomShipID() {
		shipID[0] = (int )(6 * Math.random());
		shipID[1] = (int )(6 * Math.random());
	}

	/************************************** KEY EVENTS ****************************************/

	// Handle the key typed event from the text field.
	public void keyTyped(KeyEvent e) 
	{
		if (isTextLine())
		{
			if (e.getKeyChar() != '')
			{
				if (e.getKeyChar() == ',' && e.getKeyCode() != KeyEvent.VK_COMMA) textField = textField + ".";
				else						   textField = textField + e.getKeyChar();
			}
		}
	}

	// Handle the key-pressed event from the text field.
	public void keyPressed(KeyEvent e) {

		if ((currentGameState != GameState.NONE && currentGameState != GameState.PAUSED && currentGameState != GameState.GAMEOVER_NEW_HIGHSCORE ) && e.getKeyCode() != KeyEvent.VK_ESCAPE && e.getKeyCode() != KeyEvent.VK_BACK_SPACE)
		{
			client.dispatchKeyEvent(e,true);
		}
		else
		{
			int keyCode = e.getKeyCode();
			if ((keyCode == KeyEvent.VK_BACK_SPACE && !(isTextLine() && textField.length() != 0)) || keyCode == KeyEvent.VK_ESCAPE) //ESCAPE
			{
				if (currentGameState == GameState.NONE || currentGameState == GameState.PAUSED)
				{
					if      (currentMenuState == MenuState.MAIN_MENU || currentMenuState == MenuState.PAUSED_MENU)
					{
						if (currentMenuState == MenuState.MAIN_MENU && keyCode == KeyEvent.VK_ESCAPE)				
							dotLine = getLastLine();
					}
					else if (currentMenuState == MenuState.MULTI_MENU) 
						setMenuState(MenuState.NEW_GAME_MENU);
					else if (currentMenuState == MenuState.JOIN_GAME_MENU) 
						setMenuState(MenuState.MAIN_MENU);
					else if (currentMenuState == MenuState.KEYBOARD_SETTINGS_MENU) 
						setMenuState(MenuState.OPTIONS_MENU);
					else
						if (currentGameState == GameState.NONE) setMenuState(MenuState.MAIN_MENU);
						else if (currentGameState == GameState.PAUSED) setMenuState(MenuState.PAUSED_MENU);
				}

				else
				{
					if (currentGameState == GameState.DISCONNECTED || currentGameState == GameState.GAMEOVER)
					{
						client.resetGameState();
						setMenuState(MenuState.MAIN_MENU);
					}
					else if (currentGameState == GameState.CONNECTING  || currentGameState == GameState.WAITING)
					{
						client.resetGameState();
						setMenuState(MenuState.MAIN_MENU);
					}	
					else
						{
							setMenuState(MenuState.PAUSED_MENU); // Game Paused or Something else..!
							client.pauseRequest();
						}

				}

			}

			else if (keyCode == KeyEvent.VK_ENTER) 
			{
				someThingIsEntered(dotLineToDraw,1);
			}

			else if (currentGameState == GameState.GAMEOVER_NEW_HIGHSCORE)
			{
				if (keyCode == KeyEvent.VK_BACK_SPACE)
					textField = textField.substring(0, textField.length() - 1);
			}

			else if (currentGameState == GameState.NONE || currentGameState == GameState.PAUSED)
			{
				if (keyBoardChangeSelected == 1)
				{
					if      (dotLine == 1) client.bindKey(PlayerAction.P1LEFT, keyCode);
					else if (dotLine == 2) client.bindKey(PlayerAction.P1RIGHT, keyCode);
					else if (dotLine == 3) client.bindKey(PlayerAction.P1FIRE, keyCode);
					else if (dotLine == 4) client.bindKey(PlayerAction.P2LEFT, keyCode);
					else if (dotLine == 5) client.bindKey(PlayerAction.P2RIGHT, keyCode);
					else if (dotLine == 6) client.bindKey(PlayerAction.P2FIRE, keyCode);

					if (dotLine < 7) actionKeys[dotLine - 1] = KeyEvent.getKeyText(keyCode);

					keyBoardChangeSelected = 0;
				}
				else
				{
					if (keyCode == KeyEvent.VK_UP)
					{
						if  (dotLine != 1) dotLine--;
					}

					else if (keyCode == KeyEvent.VK_DOWN)
					{
						if      (dotLine != getLastLine()) dotLine++;
					}

					else if (isTextLine())
					{
						if (keyCode == KeyEvent.VK_BACK_SPACE)
						{
							textField = textField.substring(0, textField.length()-1);
						}
					}
					else if (currentMenuState == MenuState.OPTIONS_MENU)
					{
						if (dotLine == 1)
						{
							if (keyCode == KeyEvent.VK_LEFT || keyCode == KeyEvent.VK_RIGHT) 
							{
								if (isEffectOn == true)
								{
									isEffectOn = false;
									client.setSound(false);
								}
								else
								{
									isEffectOn = true;
									client.setSound(true);
								}


							}
						}
						else if (dotLine == 2) 
						{
							if (keyCode == KeyEvent.VK_LEFT) 
							{
								if (currentGameSkill == GameSkill.NORMAL) 
								{
									currentGameSkill = GameSkill.EASY;
									client.setDifficulty(GameSkill.EASY);
								}
								else if (currentGameSkill == GameSkill.HARD) 
								{
									currentGameSkill = GameSkill.NORMAL;
									client.setDifficulty(GameSkill.NORMAL);
								}
							}
							else if (keyCode == KeyEvent.VK_RIGHT)
							{
								if (currentGameSkill == GameSkill.NORMAL)
								{
									currentGameSkill = GameSkill.HARD;
									client.setDifficulty(GameSkill.HARD);
								}
								else if (currentGameSkill == GameSkill.EASY) 
								{
									currentGameSkill = GameSkill.NORMAL;
									client.setDifficulty(GameSkill.NORMAL);
								}
							}
						}
					}
				}
			}
		}
	}

	// Handle the key-released event from the text field.
	public void keyReleased(KeyEvent e) {
		if ((currentGameState != GameState.NONE && currentGameState != GameState.PAUSED && currentGameState != GameState.GAMEOVER_NEW_HIGHSCORE ) && e.getKeyCode() != KeyEvent.VK_ESCAPE && e.getKeyCode() != KeyEvent.VK_BACK_SPACE)
		{
			client.dispatchKeyEvent(e,false);
		}
	}

	/************************************** KEY EVENT RELATED FUNCTIONS  ****************************************/

	private boolean isTextLine() {
		if (currentGameState == GameState.NONE || currentGameState == GameState.PAUSED)
		{
			if (currentMenuState == MenuState.JOIN_GAME_MENU && dotLine == 1)
				return true;
		}
		else if (currentGameState == GameState.GAMEOVER_NEW_HIGHSCORE)
			return true;
		return false;
	}


	void someThingIsEntered(int currentLine, int isKey)
	{
		if (currentGameState == GameState.NONE || currentGameState == GameState.PAUSED)
		{
			if (currentMenuState == MenuState.MAIN_MENU)
			{			
				if      (currentLine == 1) setMenuState(MenuState.NEW_GAME_MENU);
				else if (currentLine == 2) setMenuState(MenuState.JOIN_GAME_MENU);
				else if (currentLine == 3) setMenuState(MenuState.OPTIONS_MENU);
				else if (currentLine == 4) setMenuState(MenuState.HIGH_SCORES_MENU);
				else if (currentLine == 5) client.terminate();
			}

			else if (currentMenuState == MenuState.PAUSED_MENU)
			{			
				if      (currentLine == 1) client.startRequest();
				else if (currentLine == 2) client.resetGameState();
				else if (currentLine == 3) setMenuState(MenuState.NEW_GAME_MENU); 
				else if (currentLine == 4) setMenuState(MenuState.JOIN_GAME_MENU);
				else if (currentLine == 5) setMenuState(MenuState.OPTIONS_MENU);
				else if (currentLine == 6) setMenuState(MenuState.HIGH_SCORES_MENU);
				else if (currentLine == 7) client.terminate();
			}

			else if (currentMenuState == MenuState.NEW_GAME_MENU)
			{
				if      (currentLine == 1) 
				{
					currentHighScore = client.getHighestScore();
					generateRandomShipID();
					client.newGame(GameType.SINGLE);
				}
				else if (currentLine == 2) setMenuState(MenuState.MULTI_MENU);
				else if (currentLine == 3) 
				{
					if (currentGameState == GameState.NONE) setMenuState(MenuState.MAIN_MENU);
					else setMenuState(MenuState.PAUSED_MENU);
				}
			}
			else if (currentMenuState == MenuState.MULTI_MENU)
			{

				if      (currentLine == 1)
				{
					currentHighScore = client.getHighestScore();
					generateRandomShipID();
					client.newGame(GameType.MULTI_LOCAL);
				}
				else if (currentLine == 2)
				{
					currentHighScore = client.getHighestScore();
					generateRandomShipID();
					client.newGame(GameType.MULTI_NETWORK);
				}
				else if (currentLine == 3) setMenuState(MenuState.NEW_GAME_MENU);
			}	

			else if (currentMenuState == MenuState.HIGH_SCORES_MENU)
			{	
				if (currentGameState == GameState.NONE) setMenuState(MenuState.MAIN_MENU);
				else setMenuState(MenuState.PAUSED_MENU);
			}


			else if (currentMenuState == MenuState.OPTIONS_MENU)
			{
				if (currentLine == 3) setMenuState(MenuState.KEYBOARD_SETTINGS_MENU);
				else if (currentLine == 4)
				{
					if (currentGameState == GameState.NONE) setMenuState(MenuState.MAIN_MENU);
					else setMenuState(MenuState.PAUSED_MENU);
				}
			}		 

			else if (currentMenuState == MenuState.KEYBOARD_SETTINGS_MENU)
			{
				if (currentLine == getLastLine()) 
				{
					setMenuState(MenuState.OPTIONS_MENU);
				}
				else 
				{
					keyBoardChangeSelected = 1;
					localTick = 0;
				}

			}
			else if (currentMenuState == MenuState.JOIN_GAME_MENU)
			{
				if (currentLine == getLastLine())
				{
					if (currentGameState == GameState.NONE) setMenuState(MenuState.MAIN_MENU);
					else setMenuState(MenuState.PAUSED_MENU);
				}
				else if (currentLine == 2)
				{
					if (isValidIP(textField))
					{
						client.joinGame(textField);
					}
					else error ("Invalid IP Address");
				}
				else
					client.joinGame(ipAddresses[currentLine-3]);
			}
		}
		else if (currentGameState == GameState.GAMEOVER_NEW_HIGHSCORE) 
		{
			System.out.println("client.sendName(textField);");
			client.sendName(textField);
		}

	}

	/************************************** MOUSE EVENTS ****************************************/

	// This method will be called when the mouse has been clicked. 
	public void mouseClicked (MouseEvent me) {

		// Save the coordinates of the click
		int xpos = me.getX(); 
		int ypos = me.getY();

		if (xpos < 500 && xpos > 140)
			if (currentGameState == GameState.PAUSED || currentGameState == GameState.NONE)
				for (int i = 1; i < getLastLine()+1; i++) 
					if (ypos >= 150 + 50 *(i-1) && ypos < 150 + 50 *i)
						someThingIsEntered(i, 0);
	}

	// This is called when the mouse has been pressed 
	public void mousePressed (MouseEvent me) {}

	// When it has been released 
	public void mouseReleased (MouseEvent me) {} 

	// This is executed when the mouse enters the window
	public void mouseEntered (MouseEvent me) {}

	// When the Mouse leaves the window
	public void mouseExited (MouseEvent me) {} 

}

