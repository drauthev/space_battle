package gui;

import java.util.*;
import java.util.Map.Entry;
import java.util.Timer;
import java.io.File;
import java.io.IOException;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
import javax.imageio.ImageIO;

import client.*;
import enums.*;
import interfaces.*;


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
	
	private int dotX; 

	// Fonts
	private static Font scoreFont;
	private static Font titleFont;
	private static Font menuFont;
	private static Font stateFont;
	private static Font powerFont;

	// Font Sizes
	private static final int scoreSize = 22;
	private static final int titleSize = 42;
	private static final int menuSize  = 26;
	private static final int stateSize = 26;
	private static final int powerSize = 26;

	// Font Colors
	private static final Color titleColor = Color.WHITE;
	private static final Color menuColor  = Color.WHITE;
	private static final Color stateColor = Color.WHITE;
	private static final Color powerUpColor = Color.GREEN;
	private static final Color powerDownColor = Color.RED;

	// Tick Counters
	private long localTick = 0;

	// Enums
	private GameState currentGameState;
	private MenuState currentMenuState;
	private GameSkill currentGameSkill;

	// Other variables
	private String textField;
	private int dotLine;
	private int dotLineToDraw;
	private int mainDot;
	private int optionsDot;
	private int newGameDot;
	private int keyBoardChangeSelected = 0;

	// Image Buffers
	private static BufferedImage foregroundImg;
	private static BufferedImage backgroundImg;
	private static BufferedImage[] bulletImg = new BufferedImage[3];
	private static BufferedImage[] enemyBlowImg = new BufferedImage[4];
	private static BufferedImage[] enemyABlowImg = new BufferedImage[4];
	private static BufferedImage[] enemyBBlowImg = new BufferedImage[4];
	private static BufferedImage[] enemyCBlowImg = new BufferedImage[4];
	private static BufferedImage[] enemyImg = new BufferedImage[3];
	private static BufferedImage[] enemyAImg = new BufferedImage[3];
	private static BufferedImage[] enemyBImg = new BufferedImage[3];
	private static BufferedImage[] enemyCImg = new BufferedImage[3];
	private static BufferedImage[] spaceShipBombImg = new BufferedImage[5];
	private static BufferedImage[] spaceShipImg = new BufferedImage[6];
	private static BufferedImage lifeImg;
	private static BufferedImage powerUpImg;
	private static BufferedImage powerDownImg;
	private static BufferedImage[] powerUpBlowImg = new BufferedImage[4];
	private static BufferedImage shieldImg;
	private static BufferedImage projectileGoingDiagonallyLeftImg;
	private static BufferedImage projectileGoingDiagonallyRightImg;
	
	// Image widths and heights
	private int backgroundWidth;
	private int backgroundHeight;
	private int spaceShipHeight;
	private int spaceShipWidth;
	private int spaceShipBombHeight;
	private int spaceShipBombWidth;
	private int enemyHeight;
	private int enemyWidth;
	private int enemyBlowHeight;
	private int enemyBlowWidth;
	private int enemyAHeight;
	private int enemyAWidth;
	private int enemyBHeight;
	private int enemyBWidth;
	private int enemyCHeight;
	private int enemyCWidth;
	private int enemyABlowHeight;
	private int enemyABlowWidth;
	private int enemyBBlowHeight;
	private int enemyBBlowWidth;
	private int enemyCBlowHeight;
	private int enemyCBlowWidth;
	private int lifeImgHeight;
	private int lifeImgWidth;
	private int powerHeight;
	private int powerWidth;
	private int projectTileHeight;
	private int projectTileWidth;
	private int shieldHeight;
	private int shieldWidth;
	private int projectileGoingDiagonallyHeight;
	private int projectileGoingDiagonallyWidth;

	// Used for double buffering
	Graphics2D bufferGraphics;  
	BufferedImage offscreen;

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

	// IP handling
	private String[] ipAddresses = null;
	private static final int ipAddresseslength = 4;

	// Timer
	private Timer timer = new Timer(false);

	//Objectbuffer
	private ObjectBuffer localObjectBuffer;

	//Others
	private boolean isEffectOn;
	private int lastMenuHS = 1;
	private String highScoreValues[] = new String[10];
	private String highScoreString[] = new String[10];
	private int currentHighScore;

	private static final String[] actionKeysNames = {"P1 Left","P1 Right","P1 Fire","P2 Left","P2 Right","P2 Fire"};


	public GUI(ClientForGUI client_param)
	{

		try {
			String projdir = System.getProperty("user.dir");
			backgroundImg	  	  = ImageIO.read(new File(projdir + "/res/sprites/backgroundImg.png"));
			foregroundImg	 	  = ImageIO.read(new File(projdir + "/res/sprites/foregroundImg.png"));
			bulletImg[0] 		  = ImageIO.read(new File(projdir + "/res/sprites/bulletImg1.png"));
			bulletImg[1] 		  = ImageIO.read(new File(projdir + "/res/sprites/bulletImg2.png"));
			bulletImg[2] 		  = ImageIO.read(new File(projdir + "/res/sprites/bulletImg3.png"));
			enemyABlowImg[0]  	  = ImageIO.read(new File(projdir + "/res/sprites/1/enemyBlowImg1.png"));
			enemyABlowImg[1] 	  = ImageIO.read(new File(projdir + "/res/sprites/1/enemyBlowImg2.png"));
			enemyABlowImg[2] 	  = ImageIO.read(new File(projdir + "/res/sprites/1/enemyBlowImg3.png"));
			enemyABlowImg[3] 	  = ImageIO.read(new File(projdir + "/res/sprites/1/enemyBlowImg4.png"));
			enemyAImg[0] 		  = ImageIO.read(new File(projdir + "/res/sprites/1/enemyImg1.png"));
			enemyAImg[1] 		  = ImageIO.read(new File(projdir + "/res/sprites/1/enemyImg2.png"));
			enemyAImg[2] 		  = ImageIO.read(new File(projdir + "/res/sprites/1/enemyImg3.png"));
			enemyBBlowImg[0] 	  = ImageIO.read(new File(projdir + "/res/sprites/2/enemyBlowImg1.png"));
			enemyBBlowImg[1] 	  = ImageIO.read(new File(projdir + "/res/sprites/2/enemyBlowImg2.png"));
			enemyBBlowImg[2] 	  = ImageIO.read(new File(projdir + "/res/sprites/2/enemyBlowImg3.png"));
			enemyBBlowImg[3] 	  = ImageIO.read(new File(projdir + "/res/sprites/2/enemyBlowImg4.png"));
			enemyBImg[0] 		  = ImageIO.read(new File(projdir + "/res/sprites/2/enemyImg1.png"));
			enemyBImg[1] 		  = ImageIO.read(new File(projdir + "/res/sprites/2/enemyImg2.png"));
			enemyBImg[2] 		  = ImageIO.read(new File(projdir + "/res/sprites/2/enemyImg3.png"));
			enemyCBlowImg[0] 	  = ImageIO.read(new File(projdir + "/res/sprites/3/enemyBlowImg1.png"));
			enemyCBlowImg[1] 	  = ImageIO.read(new File(projdir + "/res/sprites/3/enemyBlowImg2.png"));
			enemyCBlowImg[2] 	  = ImageIO.read(new File(projdir + "/res/sprites/3/enemyBlowImg3.png"));
			enemyCBlowImg[3] 	  = ImageIO.read(new File(projdir + "/res/sprites/3/enemyBlowImg4.png"));
			enemyCImg[0] 		  = ImageIO.read(new File(projdir + "/res/sprites/3/enemyImg1.png"));
			enemyCImg[1] 		  = ImageIO.read(new File(projdir + "/res/sprites/3/enemyImg2.png"));
			enemyCImg[2] 		  = ImageIO.read(new File(projdir + "/res/sprites/3/enemyImg3.png"));
			lifeImg			  	  = ImageIO.read(new File(projdir + "/res/sprites/lifeImg.png"));
			spaceShipBombImg[0]   = ImageIO.read(new File(projdir + "/res/sprites/spaceShipBombImg1.png"));
			spaceShipBombImg[1]   = ImageIO.read(new File(projdir + "/res/sprites/spaceShipBombImg2.png"));
			spaceShipBombImg[2]   = ImageIO.read(new File(projdir + "/res/sprites/spaceShipBombImg3.png"));
			spaceShipBombImg[3]   = ImageIO.read(new File(projdir + "/res/sprites/spaceShipBombImg4.png"));
			spaceShipBombImg[4]   = ImageIO.read(new File(projdir + "/res/sprites/spaceShipBombImg5.png"));
			spaceShipImg[0] 	  = ImageIO.read(new File(projdir + "/res/sprites/spaceShipImg1.png"));
			spaceShipImg[1] 	  = ImageIO.read(new File(projdir + "/res/sprites/spaceShipImg2.png"));
			spaceShipImg[2] 	  = ImageIO.read(new File(projdir + "/res/sprites/spaceShipImg3.png"));
			spaceShipImg[3] 	  = ImageIO.read(new File(projdir + "/res/sprites/spaceShipImg4.png"));
			spaceShipImg[4]	      = ImageIO.read(new File(projdir + "/res/sprites/spaceShipImg5.png"));
			spaceShipImg[5]	   	  = ImageIO.read(new File(projdir + "/res/sprites/spaceShipImg6.png"));
			powerUpImg	      	  = ImageIO.read(new File(projdir + "/res/sprites/powerUpImg.png"));
			powerDownImg	  	  = ImageIO.read(new File(projdir + "/res/sprites/powerDownImg.png"));
			shieldImg	  	      = ImageIO.read(new File(projdir + "/res/sprites/shieldImg.png"));
			powerUpBlowImg[0]  	  = ImageIO.read(new File(projdir + "/res/sprites/powerUpBlowImg1.png"));
			powerUpBlowImg[1] 	  = ImageIO.read(new File(projdir + "/res/sprites/powerUpBlowImg2.png"));
			powerUpBlowImg[2] 	  = ImageIO.read(new File(projdir + "/res/sprites/powerUpBlowImg3.png"));
			powerUpBlowImg[3] 	  = ImageIO.read(new File(projdir + "/res/sprites/powerUpBlowImg4.png"));
			projectileGoingDiagonallyLeftImg	= ImageIO.read(new File(projdir + "/res/sprites/projectileGoingDiagonallyLeftImg.png"));
			projectileGoingDiagonallyRightImg	= ImageIO.read(new File(projdir + "/res/sprites/projectileGoingDiagonallyRightImg.png"));

		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		backgroundHeight 	= backgroundImg.getHeight();
		backgroundWidth  	= backgroundImg.getWidth();
		spaceShipHeight  	= spaceShipImg[0].getHeight();
		spaceShipWidth   	= spaceShipImg[0].getWidth();
		lifeImgWidth     	= lifeImg.getWidth();
		lifeImgHeight    	= lifeImg.getHeight();
		enemyAHeight      	= enemyAImg[0].getHeight();
		enemyAWidth       	= enemyAImg[0].getWidth();
		enemyBHeight      	= enemyBImg[0].getHeight();
		enemyBWidth       	= enemyBImg[0].getWidth();
		enemyCHeight      	= enemyCImg[0].getHeight();
		enemyCWidth       	= enemyCImg[0].getWidth();
		powerHeight     	= powerUpImg.getHeight();
		powerWidth      	= powerUpImg.getWidth();
		spaceShipBombHeight = spaceShipBombImg[0].getHeight();
		spaceShipBombWidth 	= spaceShipBombImg[0].getWidth();
		enemyABlowHeight	= enemyABlowImg[0].getHeight();
		enemyABlowWidth 	= enemyABlowImg[0].getWidth();
		enemyBBlowHeight 	= enemyBBlowImg[0].getHeight();
		enemyBBlowWidth 	= enemyBBlowImg[0].getWidth();
		enemyCBlowHeight 	= enemyCBlowImg[0].getHeight();
		enemyCBlowWidth 	= enemyCBlowImg[0].getWidth();
		projectTileHeight   = bulletImg[0].getHeight();
		projectTileWidth    = bulletImg[0].getWidth();
		shieldHeight        = shieldImg.getHeight();
		shieldWidth         = shieldImg.getWidth();
		projectileGoingDiagonallyHeight        = projectileGoingDiagonallyRightImg.getHeight();
		projectileGoingDiagonallyWidth         = projectileGoingDiagonallyRightImg.getWidth();

		// Initializing fonts for writing strings to the monitor
		scoreFont = new Font("monospscoreFont", Font.BOLD, scoreSize);
		titleFont = new Font("monospscoreFont", Font.BOLD, titleSize);
		menuFont  = new Font("monospscoreFont", Font.BOLD, menuSize);
		stateFont = new Font("monospscoreFont", Font.BOLD, stateSize);
		powerFont = new Font("monospscoreFont", Font.BOLD, powerSize);

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
		EnumMap<PlayerAction, Integer> asd = client.getKeyboardSettings().clone(); //TODO kell?		// HashMap-ben action(K)-KeyCode(V) pĂˇrok

		actionKeys[0] = KeyEvent.getKeyText(asd.get(PlayerAction.P1LEFT));
		actionKeys[1] = KeyEvent.getKeyText(asd.get(PlayerAction.P1RIGHT));
		actionKeys[2] = KeyEvent.getKeyText(asd.get(PlayerAction.P1FIRE));
		actionKeys[3] = KeyEvent.getKeyText(asd.get(PlayerAction.P2LEFT));
		actionKeys[4] = KeyEvent.getKeyText(asd.get(PlayerAction.P2RIGHT));
		actionKeys[5] = KeyEvent.getKeyText(asd.get(PlayerAction.P2FIRE));

	}


	/********************* INTERFACE FOR CLIENT **************************/

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

		SortedMap<Integer,String> localSortedMap = client.getHighScores();	

		if (localSortedMap != null)
		{
			Set<Entry<Integer, String>> s=localSortedMap.entrySet();


			// Using iterator in SortedMap 
			Iterator<Entry<Integer, String>> i=s.iterator();
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

	public void setGameState(GameState gs)	
	{
		textField = "";
		dotLine = 1;
		mainDot = 1;
		optionsDot = 1;
		newGameDot = 1;

		currentGameState = gs;
		System.out.println("GameState changed to " + currentGameState);
	}

	public void run() {
		timer.scheduleAtFixedRate(reapaintTimer, 0, 1000/100);
	}

	public void error(String text) {
		infoBox(text);
	}

	public void terminate() {
		timer.cancel();
		setVisible(false);
		dispose();	
	}

	public static void infoBox(String infoMessage)
	{
		JOptionPane.showMessageDialog(null, infoMessage, "Error", JOptionPane.INFORMATION_MESSAGE);
	}


	/**************** GRAPHICAL PROCEDURES **************************/

	public void paint(Graphics g)
	{
		g.drawImage(offscreen,0,0,this);

		drawBackground();

		if (currentGameState == GameState.RUNNING)
			localObjectBuffer = client.getNewObjectBuffer();

		if (currentGameState != GameState.NONE && currentGameState != GameState.PAUSED)
		{		
			if (currentGameState != GameState.CONNECTING && currentGameState != GameState.WAITING)
			{
				long serverTick = localObjectBuffer.currentTick;

				int numberOfLivesA = 0;
				int numberOfLivesB = 0;

				for (int i = 0; i < localObjectBuffer.npcCount; i++)
				{
					CNPC localNPC = localObjectBuffer.npc[i];

					if (localNPC.className.equals("HostileType1"))
					{
						for (int j = 0; j < 4; j++)
							enemyBlowImg[j] = enemyABlowImg[j];

						for (int j = 0; j < 3; j++)
							enemyImg[j] 		= enemyAImg[j];

						enemyHeight 	= enemyAHeight;
						enemyWidth 		= enemyAWidth;
						enemyBlowHeight = enemyABlowHeight;
						enemyBlowWidth 	= enemyABlowWidth;
					}

					else if (localNPC.className.equals("HostileType2"))
					{
						for (int j = 0; j < 4; j++)
							enemyBlowImg[j] = enemyBBlowImg[j];

						for (int j = 0; j < 3; j++)
							enemyImg[j] 		= enemyBImg[j];

						enemyHeight 	= enemyBHeight;
						enemyWidth 		= enemyBWidth;
						enemyBlowHeight = enemyBBlowHeight;
						enemyBlowWidth 	= enemyBBlowWidth;
					}

					else if (localNPC.className.equals("HostileType3"))
					{
						for (int j = 0; j < 4; j++)
							enemyBlowImg[j] = enemyCBlowImg[j];

						for (int j = 0; j < 3; j++)
							enemyImg[j] 		= enemyCImg[j];

						enemyHeight 	= enemyCHeight;
						enemyWidth 		= enemyCWidth;
						enemyBlowHeight = enemyCBlowHeight;
						enemyBlowWidth 	= enemyCBlowWidth;
					}

					if (localNPC.explosionTime != 0) 
					{
						int tickDiff_div = (int) (serverTick-localNPC.explosionTime)/130;
						if (tickDiff_div < 4) drawObject(enemyBlowImg[tickDiff_div], localNPC.x, localNPC.y, enemyBlowWidth,enemyBlowHeight);
					}
					else if (localNPC.hitTime != 0 && ((serverTick - localNPC.hitTime) < 1500)) 
					{							
						int tickDiff_div = (int) (serverTick-localNPC.creationTime)/200;
						if (tickDiff_div % 2 == 0) drawObject(enemyImg[0], localNPC.x, localNPC.y, enemyWidth,enemyHeight);
					}
					else 
					{
						int tickDiff_div = (int) (serverTick-localNPC.creationTime)/1000;
						drawObject(enemyImg[tickDiff_div % 3], localNPC.x, localNPC.y, enemyWidth,enemyHeight);
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
						if      (tickDiff_div < 5) drawObject(spaceShipBombImg[tickDiff_div], localPlayer.x, localPlayer.y, spaceShipBombWidth, spaceShipBombHeight);
					}
					else 
					{
						if (localObjectBuffer.player[i].isShielded)
							drawObject(shieldImg, localPlayer.x, localPlayer.y, shieldWidth, shieldHeight);
						drawObject(spaceShipImg[localObjectBuffer.player[i].id], localPlayer.x, localPlayer.y, spaceShipWidth, spaceShipHeight);
					}
				}

				for (int i = 0; i < localObjectBuffer.projCount; i++)
				{
					CProjectile localProjectile = localObjectBuffer.proj[i];

					if (localProjectile.className.equals("ProjectileGoingUp"))
						drawObject(bulletImg[0]		, localProjectile.x	, localProjectile.y	, projectTileWidth,projectTileHeight);
					else if (localProjectile.className.equals("ProjectileGoingDown"))
						drawObject(bulletImg[1]		, localProjectile.x	, localProjectile.y	, projectTileWidth,projectTileHeight);
					else if (localProjectile.className.equals("ProjectileLaser"))
						drawObject(bulletImg[2]		, localProjectile.x	, localProjectile.y	, projectTileWidth,projectTileHeight);
					
					else if (localProjectile.className.equals("ProjectileGoingDiagonallyRight"))
						drawObject(projectileGoingDiagonallyRightImg, localProjectile.x	, localProjectile.y	, projectileGoingDiagonallyWidth,projectileGoingDiagonallyHeight);
					else if (localProjectile.className.equals("ProjectileGoingDiagonallyLeft"))
						drawObject(projectileGoingDiagonallyLeftImg, localProjectile.x	, localProjectile.y	, projectileGoingDiagonallyWidth,projectileGoingDiagonallyHeight);

					else
					{
						drawObject(bulletImg[1]		, localProjectile.x	, localProjectile.y	, projectTileWidth,projectTileHeight);
						System.out.println("Projectile with unknown name: " + localProjectile.className);
					}
				}

				int modificationNumber = 0;
				for (int i = 0; i < localObjectBuffer.modCount; i++)
				{
					boolean isUp;

					CModifier localModifier = localObjectBuffer.mod[i];	
					//if (localModifier.pickupTime == 0 || (serverTick - localModifier.pickupTime > 1000) || ((serverTick - localModifier.pickupTime)/200) % 2 == 0) ;

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


					System.out.println(localModifier.explosionTime + "    " + localModifier.pickupTime );
					if (localModifier.explosionTime != 0)
					{
						int tickDiff_div = (int) (serverTick-localModifier.explosionTime)/100;
						if (tickDiff_div < 4) drawObject(powerUpBlowImg[tickDiff_div], localModifier.x, localModifier.y, enemyBlowWidth,enemyBlowHeight);
					}
					
					
					else if (localModifier.pickupTime == 0)
					{
						if (isUp)
							drawObject(powerUpImg	, localModifier.x	, localModifier.y	, powerWidth,powerHeight);
						else
							drawObject(powerDownImg		, localModifier.x	, localModifier.y	, powerWidth,powerHeight);
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
				String levelString;
				if (currentGameSkill == GameSkill.EASY) levelString = "Easy";
				else if (currentGameSkill == GameSkill.NORMAL) levelString = "Normal";
				else levelString = "Hard";

				String effectString = "Off";
				if (isEffectOn == true) effectString = "On";

				drawTitle("OPTIONS");
				drawOptionsLine(1,"Effects",effectString);
				drawOptionsLine(2,"Level",levelString);
				drawMenuLine   (3,"Set Keyboard");
				drawMenuLine   (4,"Back");
			}

			else if (currentMenuState == MenuState.HIGH_SCORES_MENU)
			{
				drawTitle("HIGH SCORE");

				for(int j = 0; j < lastMenuHS-1; j++) 
					drawOptionsLine(j,highScoreString[j],highScoreValues[j]);

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
				int TimerOszott = (int)localTick/1000; 

				String[] localActionKeys = new String[6];
				localActionKeys = actionKeys.clone(); 
				if ((keyBoardChangeSelected == 1) && (TimerOszott % 2 == 0)) localActionKeys[dotLine-1] = "";

				drawTitle("SET KEYBOARD");	
				for (int i = 0; i < 6; i++)
					drawOptionsLine(i+1,actionKeysNames[i],localActionKeys[i]);
				drawMenuLine   (7,"Back");
			}

			drawDot();
		}
	}

	private void drawObject(BufferedImage img, int x, int y,
			int width, int height) {
		bufferGraphics.drawImage(img ,x-width/2 , y-height/2, width, height, null);
	}


	public void drawForeground(int width, int height)
	{
		bufferGraphics.drawImage(foregroundImg, (frameWidth)/2-150, 165, 300, 200, null);
	}

	public void drawBackground()
	{
		backgroundImgY += backgroundSpeed;
		if (backgroundImgY >= 640) backgroundImgY -= 640;

		if (backgroundImgY != 0)  
			bufferGraphics.drawImage(backgroundImg	, 0	, -640 + (int)backgroundImgY	, backgroundWidth	,backgroundHeight, null);
		bufferGraphics.drawImage(backgroundImg	, 0	, (int)backgroundImgY		, backgroundWidth	,backgroundHeight,null);
	}

	public void drawTitle(String title)
	{
		bufferGraphics.setFont(titleFont);
		bufferGraphics.setColor(titleColor);
		int CoordinateX = middleX - bufferGraphics.getFontMetrics().stringWidth(title)/2;
		int CoordinateY = 120;
		bufferGraphics.drawString(title,CoordinateX, CoordinateY);
	}

	public void drawMenuLine(int lineNumber, String content)
	{
		bufferGraphics.setFont(menuFont);
		bufferGraphics.setColor(menuColor);
		int CoordinateX = middleX - bufferGraphics.getFontMetrics().stringWidth(content)/2;
		int CoordinateY = firstLineY + lineNumber * lineHeight;
		bufferGraphics.drawString(content,CoordinateX, CoordinateY);
		if (dotLineToDraw == lineNumber) dotX = CoordinateX;
	}

	public void drawPowerTitle(int lineNumber, String content, boolean isPowerUp)
	{	
		bufferGraphics.setFont(powerFont);
		if(isPowerUp)
			bufferGraphics.setColor(powerUpColor);
		else
			bufferGraphics.setColor(powerDownColor);

		int CoordinateX = middleX - bufferGraphics.getFontMetrics().stringWidth(content)/2;
		int CoordinateY = firstLineY + lineNumber * lineHeight;
		bufferGraphics.drawString(content,CoordinateX, CoordinateY);
	}

	public void drawWritingLine(int lineNumber, String content)
	{
		bufferGraphics.setFont(menuFont);
		bufferGraphics.setColor(menuColor);

		int CoordinateX = frameWidth/2 - bufferGraphics.getFontMetrics().stringWidth(content)/2;
		int CoordinateY = firstLineY + lineNumber * lineHeight;

		if (localTick/1000 % 2 == 0) bufferGraphics.drawString(content,      CoordinateX, CoordinateY);
		else 							 bufferGraphics.drawString(content + "_",CoordinateX, CoordinateY);
		if (dotLineToDraw == lineNumber) dotX = CoordinateX;
	}

	public void drawOptionsLine(int lineNumber, String content1, String content2)
	{
		bufferGraphics.setFont(menuFont);
		bufferGraphics.setColor(menuColor);
		int CoordinateY = firstLineY + lineNumber * lineHeight;

		int CoordinateX = optionsAX - bufferGraphics.getFontMetrics().stringWidth(content1)/2;
		bufferGraphics.drawString(content1,CoordinateX, CoordinateY);
		if (dotLineToDraw == lineNumber) dotX = CoordinateX;

		CoordinateX = optionsBX - bufferGraphics.getFontMetrics().stringWidth(content2)/2;
		bufferGraphics.drawString(content2,CoordinateX, CoordinateY);

	}	

	public void drawDot()
	{
		int CoordinateY = firstLineY - 20 + dotLineToDraw * lineHeight;
		int CoordinateX = dotX - 50;
		bufferGraphics.drawImage(lifeImg	, CoordinateX	, CoordinateY	, lifeImgWidth	,lifeImgHeight,null);

	}	


	public void drawState(String content)
	{
		bufferGraphics.setFont(stateFont);
		bufferGraphics.setColor(stateColor);
		int CoordinateX = middleX - bufferGraphics.getFontMetrics().stringWidth(content)/2;
		bufferGraphics.drawString(content,CoordinateX, 230);
	}

	public void drawLives(int numberOfLives1, int numberOfLives2)
	{
		for (int i = 0; i < numberOfLives1; i++)
			drawObject(lifeImg	, 40 + 40*i	, 600	, lifeImgWidth	,lifeImgHeight);

		for (int i = 0; i < numberOfLives2; i++)
			drawObject(lifeImg	, frameWidth - 45 -  40*i	, 600	, lifeImgWidth	,lifeImgHeight);
	}		

	public void drawTopScores(int curr, int high)
	{
		bufferGraphics.setFont(scoreFont);
		bufferGraphics.setColor(Color.YELLOW);
		bufferGraphics.drawString("SCORE",    GUI.frameWidth/8, 46);
		bufferGraphics.drawString("HIGHSCORE", GUI.frameWidth/3+12, 46);

		bufferGraphics.setColor(Color.GREEN); 
		bufferGraphics.drawString(Integer.toString(curr), GUI.frameWidth/8, 64);
		bufferGraphics.drawString(Integer.toString(high), GUI.frameWidth/3+60, 64);

	}


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
				else if (currentLine == 2) setMenuState(MenuState.MAIN_MENU); 
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
						client.newGame(GameType.SINGLE);
				}
				else if (currentLine == 2) setMenuState(MenuState.MULTI_MENU);
				else if (currentLine == 3) setMenuState(MenuState.MAIN_MENU);
			}
			else if (currentMenuState == MenuState.MULTI_MENU)
			{

				if      (currentLine == 1)
				{
					currentHighScore = client.getHighestScore();
					client.newGame(GameType.MULTI_LOCAL);
				}
				else if (currentLine == 2)
				{
					currentHighScore = client.getHighestScore();
					client.newGame(GameType.MULTI_NETWORK);
				}
				else if (currentLine == 3) setMenuState(MenuState.NEW_GAME_MENU);
			}	

			else if (currentMenuState == MenuState.HIGH_SCORES_MENU)
			{	
				if (currentLine == getLastLine()) setMenuState(MenuState.MAIN_MENU);
			}


			else if (currentMenuState == MenuState.OPTIONS_MENU)
			{
				if (currentLine == 3) setMenuState(MenuState.KEYBOARD_SETTINGS_MENU);
				else if (currentLine == 4) setMenuState(MenuState.MAIN_MENU); 
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
				if (currentLine == getLastLine()) setMenuState(MenuState.MAIN_MENU);
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


	// Handle the key typed event from the text field.
	public void keyTyped(KeyEvent e) {}

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
					else if (currentGameState != GameState.GAMEOVER_NEW_HIGHSCORE)
					{
						if (currentGameState != GameState.PAUSED) client.pauseRequest();
						setMenuState(MenuState.PAUSED_MENU); // Game Paused or Something else..!
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
					else 
					{
						if (e.getKeyChar() == ',' && keyCode != KeyEvent.VK_COMMA) textField = textField + ".";
						else						   textField = textField + e.getKeyChar();
						textField = textField.toUpperCase();
						
					}
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

					if (dotLine < 6) actionKeys[dotLine - 1] = KeyEvent.getKeyText(keyCode);

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
							textField = textField.substring(0, textField.length() - 1);
						else 
						{
							if (e.getKeyChar() == ',' && keyCode != KeyEvent.VK_COMMA) textField = textField + ".";
							else						   textField = textField + e.getKeyChar(); //getKeyChar
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


	// Handle the key-released event from the text field.
	public void keyReleased(KeyEvent e) {
		if ((currentGameState != GameState.NONE && currentGameState != GameState.PAUSED && currentGameState != GameState.GAMEOVER_NEW_HIGHSCORE ) && e.getKeyCode() != KeyEvent.VK_ESCAPE && e.getKeyCode() != KeyEvent.VK_BACK_SPACE)
		{
			client.dispatchKeyEvent(e,false);
		}
	}


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

	public void setSound(Boolean val) {
		isEffectOn = val;
	}

	public void setDifficulty(GameSkill gs) {
		currentGameSkill = gs;
	}

	public void setRecentIPs(String[] iparr) {
		ipAddresses = iparr.clone(); //TODO ez }gy jĂł?
	}
}