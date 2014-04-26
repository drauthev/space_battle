package gui;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

import javax.swing.*;
import javax.imageio.ImageIO;

import sound.SoundSystem;
import client.CModifier;
import client.CNPC;
import client.CPlayer;
import client.CProjectile;
import client.ObjectBuffer;
import enums.*;
import interfaces.*;

import java.util.*;
import java.util.Timer;
import java.io.File;
import java.io.IOException;
import java.net.URL;


public class GUI extends JFrame implements KeyListener, MouseListener, GUIForClient {

	private SoundSystem localSound;
	// GUI Window
	private static final long serialVersionUID = 1L;
	private static int frameWidth = 480;
	private static int frameHeight = 640;

	private int lastMenuHS;
	// Starting Timer 
	private Timer timer = new Timer(false);

	// Positioning background Image
	private double backgroundImgY;
	private static double backgroundSpeed = 0.2;

	private int isTextLine;
	// Parameters for positioning the images
	private int CoordinateX;
	private int CoordinateY;
	private static int optionsAX = frameWidth/4+30;
	private static int optionsBX = 3*frameWidth/4-30;
	private static int middleX   = frameWidth/2;
	private int dotCorX; 
	private static int firstLineY = 150;
	private static int lineHeight = 50;

	// Fonts
	private static Font scoreFont;
	private static Font titleFont;
	private static Font menuFont;
	private static Font stateFont;

	// The X-coordinate and Y-coordinate of the last click. 
	private int xpos; 
	private int ypos;

	// Font Sizes
	private static int scoreSize = 22;
	private static int titleSize = 42;
	private static int menuSize  = 26;
	private static int stateSize = 26;

	// Font Colors
	private static Color titleColor = Color.WHITE;
	private static Color menuColor  = Color.WHITE;
	private static Color stateColor = Color.WHITE;

	private int keyCode1Left;
	private int keyCode1Right;
	private int keyCode1Fire;
	private int keyCode2Left;
	private int keyCode2Right;
	private int keyCode2Fire;

	// Tick Counters
	private long localTick;
	private long tickDiff_div;

	private boolean isEffectOn;
	
	private ObjectBuffer localObjectBuffer;
	
	// Enums
	private GameState currentGameState;
	private MenuState currentMenuState;
	private GameSkill currentGameSkill;

	// Other variables
	private String textField;
	private int currentScore;
	private int numberOfLivesA;
	private int numberOfLivesB;
	private int dotLine;
	private int dotLineToDraw;
	private int mainDot;
	private int optionsDot;
	private int newGameDot;
	private int keyCode;
	private int keyBoardChangeSelected;

	// Image Buffers
	private static BufferedImage foregroundImg;
	private static BufferedImage backgroundImg;
	private static BufferedImage bulletImg1;
	private static BufferedImage bulletImg2;
	
    private static BufferedImage enemyBlowImg1;
	private static BufferedImage enemyBlowImg2;
	private static BufferedImage enemyBlowImg3;
	private static BufferedImage enemyBlowImg4;
	private static BufferedImage enemyImg1;
	private static BufferedImage enemyImg2;
	private static BufferedImage enemyImg3;
	
    private static BufferedImage enemyABlowImg1;
	private static BufferedImage enemyABlowImg2;
	private static BufferedImage enemyABlowImg3;
	private static BufferedImage enemyABlowImg4;
	private static BufferedImage enemyAImg1;
	private static BufferedImage enemyAImg2;
	private static BufferedImage enemyAImg3;
	private static BufferedImage enemyBBlowImg1;
	private static BufferedImage enemyBBlowImg2;
	private static BufferedImage enemyBBlowImg3;
	private static BufferedImage enemyBBlowImg4;
	private static BufferedImage enemyBImg1;
	private static BufferedImage enemyBImg2;
	private static BufferedImage enemyBImg3;
	private static BufferedImage enemyCBlowImg1;
	private static BufferedImage enemyCBlowImg2;
	private static BufferedImage enemyCBlowImg3;
	private static BufferedImage enemyCBlowImg4;
	private static BufferedImage enemyCImg1;
	private static BufferedImage enemyCImg2;
	private static BufferedImage enemyCImg3;
	private static BufferedImage lifeImg;
	private static BufferedImage spaceShipBombImg1;
	private static BufferedImage spaceShipBombImg2;
	private static BufferedImage spaceShipBombImg3;
	private static BufferedImage spaceShipBombImg4;
	private static BufferedImage spaceShipBombImg5;
	private static BufferedImage spaceShipImg1;
	private static BufferedImage spaceShipImg2;
	private static BufferedImage spaceShipImg3;
	private static BufferedImage spaceShipImg4;
	private static BufferedImage spaceShipImg5;
	private static BufferedImage spaceShipImg6;
	private static BufferedImage powerUpImg;
	private static BufferedImage powerDownImg;

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
	private int powerUpHeight;
	private int powerUpWidth;
	private int powerDownHeight;
	private int powerDownWidth;
	private int projectTileHeight;
	private int projectTileWidth;

	// Used for double buffering
	Graphics bufferGraphics;  
	Image offscreen;

	// Initialize Timer
	TimerTask reapaintTimer = new TimerTask() {
		@Override
		public void run() {
			repaint();
		}
	};

	private ClientForGUI client;

	public GUI(ClientForGUI client_param)
	{
		localSound = new SoundSystem();
		
		try {
			String projdir = System.getProperty("user.dir");
			backgroundImg	  = ImageIO.read(new File(projdir + "/res/sprites/backgroundImg.png"));
			foregroundImg	  = ImageIO.read(new File(projdir + "/res/sprites/foregroundImg.png"));
			bulletImg1 		  = ImageIO.read(new File(projdir + "/res/sprites/bulletImg1.png"));
			bulletImg2 		  = ImageIO.read(new File(projdir + "/res/sprites/bulletImg2.png"));
			enemyABlowImg1 	  = ImageIO.read(new File(projdir + "/res/sprites/1/enemyBlowImg1.png"));
			enemyABlowImg2 	  = ImageIO.read(new File(projdir + "/res/sprites/1/enemyBlowImg2.png"));
			enemyABlowImg3 	  = ImageIO.read(new File(projdir + "/res/sprites/1/enemyBlowImg3.png"));
			enemyABlowImg4 	  = ImageIO.read(new File(projdir + "/res/sprites/1/enemyBlowImg4.png"));
			enemyAImg1 		  = ImageIO.read(new File(projdir + "/res/sprites/1/enemyImg1.png"));
			enemyAImg2 		  = ImageIO.read(new File(projdir + "/res/sprites/1/enemyImg2.png"));
			enemyAImg3 		  = ImageIO.read(new File(projdir + "/res/sprites/1/enemyImg3.png"));
			enemyBBlowImg1 	  = ImageIO.read(new File(projdir + "/res/sprites/2/enemyBlowImg1.png"));
			enemyBBlowImg2 	  = ImageIO.read(new File(projdir + "/res/sprites/2/enemyBlowImg2.png"));
			enemyBBlowImg3 	  = ImageIO.read(new File(projdir + "/res/sprites/2/enemyBlowImg3.png"));
			enemyBBlowImg4 	  = ImageIO.read(new File(projdir + "/res/sprites/2/enemyBlowImg4.png"));
			enemyBImg1 		  = ImageIO.read(new File(projdir + "/res/sprites/2/enemyImg1.png"));
			enemyBImg2 		  = ImageIO.read(new File(projdir + "/res/sprites/2/enemyImg2.png"));
			enemyBImg3 		  = ImageIO.read(new File(projdir + "/res/sprites/2/enemyImg3.png"));
			enemyCBlowImg1 	  = ImageIO.read(new File(projdir + "/res/sprites/3/enemyBlowImg1.png"));
			enemyCBlowImg2 	  = ImageIO.read(new File(projdir + "/res/sprites/3/enemyBlowImg2.png"));
			enemyCBlowImg3 	  = ImageIO.read(new File(projdir + "/res/sprites/3/enemyBlowImg3.png"));
			enemyCBlowImg4 	  = ImageIO.read(new File(projdir + "/res/sprites/3/enemyBlowImg4.png"));
			enemyCImg1 		  = ImageIO.read(new File(projdir + "/res/sprites/3/enemyImg1.png"));
			enemyCImg2 		  = ImageIO.read(new File(projdir + "/res/sprites/3/enemyImg2.png"));
			enemyCImg3 		  = ImageIO.read(new File(projdir + "/res/sprites/3/enemyImg3.png"));
			lifeImg			  = ImageIO.read(new File(projdir + "/res/sprites/lifeImg.png"));
			spaceShipBombImg1 = ImageIO.read(new File(projdir + "/res/sprites/spaceShipBombImg1.png"));
			spaceShipBombImg2 = ImageIO.read(new File(projdir + "/res/sprites/spaceShipBombImg2.png"));
			spaceShipBombImg3 = ImageIO.read(new File(projdir + "/res/sprites/spaceShipBombImg3.png"));
			spaceShipBombImg4 = ImageIO.read(new File(projdir + "/res/sprites/spaceShipBombImg4.png"));
			spaceShipBombImg5 = ImageIO.read(new File(projdir + "/res/sprites/spaceShipBombImg5.png"));
			spaceShipImg1 	  = ImageIO.read(new File(projdir + "/res/sprites/spaceShipImg1.png"));
			spaceShipImg2 	  = ImageIO.read(new File(projdir + "/res/sprites/spaceShipImg2.png"));
			spaceShipImg3 	  = ImageIO.read(new File(projdir + "/res/sprites/spaceShipImg3.png"));
			spaceShipImg4 	  = ImageIO.read(new File(projdir + "/res/sprites/spaceShipImg4.png"));
			spaceShipImg5	  = ImageIO.read(new File(projdir + "/res/sprites/spaceShipImg5.png"));
			spaceShipImg6	  = ImageIO.read(new File(projdir + "/res/sprites/spaceShipImg6.png"));
			powerUpImg	      = ImageIO.read(new File(projdir + "/res/sprites/powerUpImg.png"));
			powerDownImg	  = ImageIO.read(new File(projdir + "/res/sprites/powerDownImg.png"));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		backgroundHeight 	= backgroundImg.getHeight();
		backgroundWidth  	= backgroundImg.getWidth();
		spaceShipHeight  	= spaceShipImg1.getHeight();
		spaceShipWidth   	= spaceShipImg1.getWidth();
		lifeImgWidth     	= lifeImg.getWidth();
		lifeImgHeight    	= lifeImg.getHeight();
		enemyAHeight      	= enemyAImg1.getHeight();
		enemyAWidth       	= enemyAImg1.getWidth();
		enemyBHeight      	= enemyBImg1.getHeight();
		enemyBWidth       	= enemyBImg1.getWidth();
		enemyCHeight      	= enemyCImg1.getHeight();
		enemyCWidth       	= enemyCImg1.getWidth();
		powerUpHeight     	= powerUpImg.getHeight();
		powerUpWidth      	= powerUpImg.getWidth();
		powerDownHeight     = powerDownImg.getHeight();
		powerDownWidth      = powerDownImg.getWidth();
		spaceShipBombHeight = spaceShipBombImg1.getHeight();
		spaceShipBombWidth 	= spaceShipBombImg1.getWidth();
		enemyABlowHeight	= enemyABlowImg1.getHeight();
		enemyABlowWidth 	= enemyABlowImg1.getWidth();
		enemyBBlowHeight 	= enemyBBlowImg1.getHeight();
		enemyBBlowWidth 	= enemyBBlowImg1.getWidth();
		enemyCBlowHeight 	= enemyCBlowImg1.getHeight();
		enemyCBlowWidth 	= enemyCBlowImg1.getWidth();
		projectTileHeight   = bulletImg1.getHeight();
		projectTileWidth    = bulletImg1.getWidth();

		// Initializing fonts for writing strings to the monitor
		scoreFont = new Font("monospscoreFont", Font.BOLD, scoreSize);
		titleFont = new Font("monospscoreFont", Font.BOLD, titleSize);
		menuFont  = new Font("monospscoreFont", Font.BOLD, menuSize);
		stateFont = new Font("monospscoreFont", Font.BOLD, stateSize);

		// Initialize Local variables
		backgroundImgY = 0;
		mainDot = 1;
		optionsDot = 1;
		newGameDot = 1;
		dotLine = 1;
		textField = "";
		keyBoardChangeSelected = 0;
		setMenuState(MenuState.MAIN_MENU);

		addKeyListener(this);
		addMouseListener(this); 

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
		offscreen = createImage(frameWidth,frameHeight); 
		bufferGraphics = offscreen.getGraphics();   

		client = client_param;

		// Ha close gombbal zárják a programot, akkor is kell terminate
		this.addWindowListener(new java.awt.event.WindowAdapter() {
		    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
		        client.terminate();
		    }
		});
	}


	/********************* INTERFACE FOR CLIENT **************************/

	void setMenuState(MenuState ms)
	{
		if      (currentMenuState == MenuState.MAIN_MENU)     
			mainDot = dotLine;
		else if (currentMenuState == MenuState.PAUSED_MENU)  	 
		{
			mainDot = dotLine; 
			System.out.println("mainDot loaded");
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
		
		if      (ms == MenuState.MAIN_MENU)     
			dotLine = mainDot; 
		else if (ms == MenuState.PAUSED_MENU)
		{
			dotLine = mainDot; 
			System.out.println("dotLine loaded");
		}
		else if (ms == MenuState.OPTIONS_MENU) 
			dotLine = optionsDot;
		else if (ms == MenuState.NEW_GAME_MENU)
			dotLine = newGameDot;
		else 
			dotLine = 1;

		currentMenuState = ms;
		System.out.println("MenuState changed to " + currentMenuState);
	}

	public void setGameState(GameState gs)	
	{
		currentGameState = gs;
		System.out.println("GameState changed to " + currentGameState);

		textField = "";
		dotLine = 1;
		mainDot = 1;
		optionsDot = 1;
		newGameDot = 1;
	}

	public void run() {
		timer.scheduleAtFixedRate(reapaintTimer, 0, 1000/100);
	}


	public void error(String text) {
		infoBox(text);
	}


	public void terminate() {
		timer.cancel();
	}


	public static void infoBox(String infoMessage)
    {
        JOptionPane.showMessageDialog(null, infoMessage, "Error", JOptionPane.INFORMATION_MESSAGE);
    }



	/**************** GRAPHICAL PROCEDURES **************************/

	public void paint(Graphics g)
	{
		g.drawImage(offscreen,0,0,this);

		// Draw Background
		drawBackground();

		
		localObjectBuffer = client.getNewObjectBuffer();
		localTick = localObjectBuffer.currentTick;

			/*public CNPC[] npc;
			public CPlayer[] player;
			public CProjectile[] proj;
			public CModifier[] mod;*/
			
		if (currentGameState != GameState.NONE && currentGameState != GameState.PAUSED)
		{		
		
			int npcLength = localObjectBuffer.npc.length;
			for (int i = 0; i < npcLength; i++)
			{
				CNPC localNPC = localObjectBuffer.npc[i];

				if (localNPC.className == "HostileType1")
				{
				    enemyBlowImg1 	= enemyABlowImg1;
					enemyBlowImg2 	= enemyABlowImg2;
					enemyBlowImg3 	= enemyABlowImg3;
					enemyBlowImg4 	= enemyABlowImg4;
					enemyImg1 		= enemyAImg1;
					enemyImg2 		= enemyAImg2;
					enemyImg3 		= enemyAImg3;
					
					enemyHeight 	= enemyAHeight;
					enemyWidth 		= enemyAWidth;
					enemyBlowHeight = enemyABlowHeight;
					enemyBlowWidth 	= enemyABlowWidth;
				}
				
				else if (localNPC.className == "HostileType1")
				{
				    enemyBlowImg1 	= enemyBBlowImg1;
					enemyBlowImg2 	= enemyBBlowImg2;
					enemyBlowImg3 	= enemyBBlowImg3;
					enemyBlowImg4 	= enemyBBlowImg4;
					enemyImg1 		= enemyBImg1;
					enemyImg2 		= enemyBImg2;
					enemyImg3 		= enemyBImg3;
					
					enemyHeight 	= enemyBHeight;
					enemyWidth 		= enemyBWidth;
					enemyBlowHeight = enemyBBlowHeight;
					enemyBlowWidth 	= enemyBBlowWidth;
				}
				
				else if (localNPC.className == "HostileType1")
				{
				    enemyBlowImg1 	= enemyCBlowImg1;
					enemyBlowImg2 	= enemyCBlowImg2;
					enemyBlowImg3 	= enemyCBlowImg3;
					enemyBlowImg4 	= enemyCBlowImg4;
					enemyImg1 		= enemyCImg1;
					enemyImg2 		= enemyCImg2;
					enemyImg3 		= enemyCImg3;
					
					enemyHeight 	= enemyCHeight;
					enemyWidth 		= enemyCWidth;
					enemyBlowHeight = enemyCBlowHeight;
					enemyBlowWidth 	= enemyCBlowWidth;
				}

				if (localNPC.explosionTime != 0) 
				{
					tickDiff_div = (localNPC.explosionTime-localTick)/2000;
					if      (tickDiff_div == 0) bufferGraphics.drawImage(enemyBlowImg1		, localNPC.x	, localNPC.y	, enemyBlowWidth,enemyBlowHeight		, null);
					else if (tickDiff_div == 1) bufferGraphics.drawImage(enemyBlowImg2		, localNPC.x	, localNPC.y	, enemyBlowWidth,enemyBlowHeight		, null);
					else if (tickDiff_div == 2) bufferGraphics.drawImage(enemyBlowImg3		, localNPC.x	, localNPC.y	, enemyBlowWidth,enemyBlowHeight		, null);
					else if (tickDiff_div == 3) bufferGraphics.drawImage(enemyBlowImg4		, localNPC.x	, localNPC.y	, enemyBlowWidth,enemyBlowHeight		, null);
				}
				else if (localNPC.hitTime != 0) 
				{
					if ((localNPC.hitTime - localTick) < 2000)
					{
						tickDiff_div = (localNPC.hitTime - localTick)/5000;
						if      (tickDiff_div % 2 == 0) bufferGraphics.drawImage(enemyImg1		, localNPC.x	, localNPC.y	, enemyBlowWidth,enemyBlowHeight		, null);

					}
				}

				else 
				{
					tickDiff_div = (localNPC.creationTime-localTick)/1000;
					if      (tickDiff_div % 3 == 0) bufferGraphics.drawImage(enemyImg1		, localNPC.x	, localNPC.y	, enemyWidth,enemyHeight		, null);
					else if (tickDiff_div % 3 == 1) bufferGraphics.drawImage(enemyImg2		, localNPC.x	, localNPC.y	, enemyWidth,enemyHeight		, null);
					else 							bufferGraphics.drawImage(enemyImg3		, localNPC.x	, localNPC.y	, enemyWidth,enemyHeight		, null);
				}
			}


	
			int playerLength = localObjectBuffer.player.length;
			for (int i = 0; i < playerLength; i++)
			{
				CPlayer localPlayer = localObjectBuffer.player[i];
				// public long explosionTime;
				// public long hitTime;
				// public String className;
			
				if (i == 0) numberOfLivesA = localPlayer.numberOfLives;
				else if (i == 1) numberOfLivesB = localPlayer.numberOfLives;

			
				if (localPlayer.explosionTime != 0 )
				{
					tickDiff_div = (localPlayer.explosionTime-localTick)/3000;
					if      (tickDiff_div == 0) bufferGraphics.drawImage(spaceShipBombImg1		, localPlayer.x	, localPlayer.y	, spaceShipBombWidth,spaceShipBombHeight		, null);
					else if (tickDiff_div == 1) bufferGraphics.drawImage(spaceShipBombImg2		, localPlayer.x	, localPlayer.y	, spaceShipBombWidth,spaceShipBombHeight		, null);
					else if (tickDiff_div == 2) bufferGraphics.drawImage(spaceShipBombImg3		, localPlayer.x	, localPlayer.y	, spaceShipBombWidth,spaceShipBombHeight		, null);
					else if (tickDiff_div == 3) bufferGraphics.drawImage(spaceShipBombImg4		, localPlayer.x	, localPlayer.y	, spaceShipBombWidth,spaceShipBombHeight		, null);
					else if (tickDiff_div == 4) bufferGraphics.drawImage(spaceShipBombImg5		, localPlayer.x	, localPlayer.y	, spaceShipBombWidth,spaceShipBombHeight		, null);
				}
				else 
					bufferGraphics.drawImage(spaceShipImg1		, localPlayer.x	, localPlayer.y	, spaceShipWidth,spaceShipHeight		, null);

			}
			
			
			
			
			
			int projLength = localObjectBuffer.proj.length;
			for (int i = 0; i < projLength; i++)
			{
				CProjectile localProjectile = localObjectBuffer.proj[i];
				// String className;	
				if (localProjectile.className == "ProjectileGoingUp")
					bufferGraphics.drawImage(bulletImg1		, localProjectile.x	, localProjectile.y	, projectTileWidth,projectTileHeight		, null);
				else //ProjectileGoingDown
					bufferGraphics.drawImage(bulletImg2		, localProjectile.x	, localProjectile.y	, projectTileWidth,projectTileHeight		, null);
			}
			
			
			int modLength = localObjectBuffer.mod.length;
			for (int i = 0; i < modLength; i++)
			{
				CModifier localModifier = localObjectBuffer.mod[i];
				// public String className;
				// pickupTime;		
				
				if (localModifier.className == "PowerDown")
					bufferGraphics.drawImage(powerDownImg		, localModifier.x	, localModifier.y	, powerUpWidth,powerDownWidth		, null);
				else //PowerUp
					bufferGraphics.drawImage(powerUpImg		, localModifier.x	, localModifier.y	, powerUpWidth,powerDownWidth		, null);
			
			}
			
			
			
			testDrawing();

			drawLives();

			drawTopScores(localObjectBuffer.score,client.getHighestScore());

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
				drawMenuLine(2,"Join Game ");
				drawMenuLine(3,"Options");
				drawMenuLine(4,"High Scores");
				drawMenuLine(5,"Exit");
			}

			if (currentMenuState == MenuState.PAUSED_MENU)
			{
				drawTitle("PAUSED");
				drawMenuLine(1,"Resume");
				drawMenuLine(2,"Disconnect");
				drawMenuLine(3,"New Game");
				drawMenuLine(4,"Join Game ");
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
				//drawOptionsLine(1,"Józsika","53512");
				//drawOptionsLine(2,"Valami","43210");
				//drawOptionsLine(3,"Valami2222","43210");


				SortedMap<Integer,String> sm=new TreeMap<Integer, String>();

				Set s=sm.entrySet();

			    // Using iterator in SortedMap 
			    Iterator i=s.iterator();
			       int j= 1;

				while(i.hasNext())
		        {
		            Map.Entry m =(Map.Entry)i.next();

		            int key = (Integer)m.getKey();
		            String value=(String)m.getValue();

					drawOptionsLine(j,value,""+key);
					j++;
		        }

				drawMenuLine   (j,"Back");
				lastMenuHS = j;
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
				drawMenuLine   (3,"102.1.50.121");
				drawMenuLine   (4,"102.2.50.121");
				drawMenuLine   (5,"102.3.50.121");
				drawMenuLine   (6,"Back");
			}
			else if (currentMenuState == MenuState.KEYBOARD_SETTINGS_MENU)
			{
				int TimerOszott = (int)localTick/1000; 

				EnumMap<PlayerAction, Integer> asd = client.getKeyboardSettings().clone(); //TODO kell?		// HashMap-ben action(K)-KeyCode(V) párok
				
				keyCode1Left = asd.get(PlayerAction.P1LEFT);
				keyCode1Right = asd.get(PlayerAction.P1RIGHT);
				keyCode1Fire = asd.get(PlayerAction.P1FIRE);

				keyCode2Left = asd.get(PlayerAction.P2LEFT);
				keyCode2Right = asd.get(PlayerAction.P2RIGHT);
				keyCode2Fire = asd.get(PlayerAction.P2FIRE);
				String String1Left = KeyEvent.getKeyText(asd.get("p1left"));
				String String1Right = KeyEvent.getKeyText(asd.get("p1right"));
				String String1Fire = KeyEvent.getKeyText(asd.get("p1fire"));

				String String2Left = KeyEvent.getKeyText(asd.get("p2left"));
				String String2Right = KeyEvent.getKeyText(asd.get("p2right"));
				String String2Fire = KeyEvent.getKeyText(asd.get("p2fire"));

				if ((keyBoardChangeSelected == 1) && (TimerOszott % 2 == 1)) 
				{
					if (dotLine == 1) String1Left = "";
					else if (dotLine == 2) String1Right = "";
					else if (dotLine == 3) String1Fire = "";
					else if (dotLine == 4) String2Left = "";
					else if (dotLine == 5) String2Right = "";
					else if (dotLine == 6) String2Fire = "";
				}

				drawTitle("SET KEYBOARD");
				drawOptionsLine(1,"Player 1 Left",String1Left);
				drawOptionsLine(2,"Player 1 Right",String1Right);
				drawOptionsLine(3,"Player 1 Fire",String1Fire);
				drawOptionsLine(4,"Player 2 Left",String2Left);
				drawOptionsLine(5,"Player 2 Right",String2Right);
				drawOptionsLine(6,"Player 2 Fire",String2Fire);
				drawMenuLine   (7,"Back");



			}

			drawDot();
		}
	}

	public void drawForeground(int width, int height)
	{
		bufferGraphics.drawImage(foregroundImg	, (frameWidth)/2-150	, 165	, 300	,200, null);
	}

	public void drawBackground()
	{
		backgroundImgY += backgroundSpeed;
		if (backgroundImgY >= 640) backgroundImgY -= 640;

		if (backgroundImgY != 0)  
			bufferGraphics.drawImage(backgroundImg	, 0	, -640 + (int)backgroundImgY	, backgroundWidth	,backgroundHeight, null);
		bufferGraphics.drawImage(backgroundImg	, 0	, (int)backgroundImgY		, backgroundWidth	,backgroundHeight, null);
	}

	public void drawTitle(String title)
	{
		bufferGraphics.setFont(titleFont);
		bufferGraphics.setColor(titleColor);
		CoordinateX = middleX - bufferGraphics.getFontMetrics().stringWidth(title)/2;
		CoordinateY = 120;
		bufferGraphics.drawString(title,CoordinateX, CoordinateY);
	}

	public void drawMenuLine(int lineNumber, String content)
	{
		bufferGraphics.setFont(menuFont);
		bufferGraphics.setColor(menuColor);
		CoordinateX = middleX - bufferGraphics.getFontMetrics().stringWidth(content)/2;
		CoordinateY = firstLineY + lineNumber * lineHeight;
		bufferGraphics.drawString(content,CoordinateX, CoordinateY);
		if (dotLineToDraw == lineNumber) dotCorX = CoordinateX;
	}

	public void drawWritingLine(int lineNumber, String content)
	{
		bufferGraphics.setFont(menuFont);
		bufferGraphics.setColor(menuColor);
		CoordinateX = frameWidth/2 - bufferGraphics.getFontMetrics().stringWidth(content)/2;
		CoordinateY = firstLineY + lineNumber * lineHeight;
		//localTick
		if (localTick/1000 % 2 == 0) bufferGraphics.drawString(content,      CoordinateX, CoordinateY);
		else 							 bufferGraphics.drawString(content + "_",CoordinateX, CoordinateY);
		if (dotLineToDraw == lineNumber) dotCorX = CoordinateX;
	}

	public void drawOptionsLine(int lineNumber, String content1, String content2)
	{
		bufferGraphics.setFont(menuFont);
		bufferGraphics.setColor(menuColor);
		CoordinateY = firstLineY + lineNumber * lineHeight;

		CoordinateX = optionsAX - bufferGraphics.getFontMetrics().stringWidth(content1)/2;
		bufferGraphics.drawString(content1,CoordinateX, CoordinateY);
		if (dotLineToDraw == lineNumber) dotCorX = CoordinateX;

		CoordinateX = optionsBX - bufferGraphics.getFontMetrics().stringWidth(content2)/2;
		bufferGraphics.drawString(content2,CoordinateX, CoordinateY);

	}	

	public void drawDot()
	{
		CoordinateY = firstLineY - 20 + dotLineToDraw * lineHeight;
		CoordinateX = dotCorX - 50;
		bufferGraphics.drawImage(lifeImg	, CoordinateX	, CoordinateY	, lifeImgWidth	,lifeImgHeight	, null);

	}	


	public void drawState(String content)
	{
		bufferGraphics.setFont(stateFont);
		bufferGraphics.setColor(stateColor);
		CoordinateX = middleX - bufferGraphics.getFontMetrics().stringWidth(content)/2;
		bufferGraphics.drawString(content,CoordinateX, 230);
	}

	public void drawLives()
	{
		for (int i = 0; i < numberOfLivesA; i++)
			bufferGraphics.drawImage(lifeImg	, 20+ 40*i	, 600	, lifeImgWidth	,lifeImgHeight	, null);

		for (int i = 0; i < numberOfLivesB; i++)
			bufferGraphics.drawImage(lifeImg	, frameWidth - 40 -  40*i	, 600	, lifeImgWidth	,lifeImgHeight	, null);
	}		

	public void testDrawing()
	{
		bufferGraphics.drawImage(spaceShipImg1	, 200	, 500	, spaceShipWidth	,spaceShipHeight	, null);
		bufferGraphics.drawImage(spaceShipImg2	, 300	, 500	, spaceShipWidth	,spaceShipHeight	, null);

		bufferGraphics.drawImage(enemyAImg1		, 80	, 300	, enemyAWidth		,enemyAHeight		, null);
		bufferGraphics.drawImage(enemyBImg1		, 150	, 300	, enemyBWidth		,enemyBHeight		, null);
		bufferGraphics.drawImage(enemyCImg1		, 220	, 300	, enemyCWidth		,enemyCHeight		, null);
		bufferGraphics.drawImage(enemyCImg1		, 130	, 370	, enemyCWidth		,enemyCHeight		, null);
		bufferGraphics.drawImage(enemyCImg1		, 190	, 350	, enemyCWidth		,enemyCHeight		, null);

		bufferGraphics.drawImage(enemyCImg2		, 30	, 100	, enemyCWidth		,enemyCHeight		, null);
		bufferGraphics.drawImage(enemyCImg2		, 100	, 100	, enemyCWidth		,enemyCHeight		, null);
		bufferGraphics.drawImage(enemyCImg2		, 180	, 100	, enemyCWidth		,enemyCHeight		, null);

		bufferGraphics.drawImage(enemyCImg3		, 320	, 300	, enemyCWidth		,enemyCHeight		, null);
		bufferGraphics.drawImage(enemyCImg3		, 390	, 300	, enemyCWidth		,enemyCHeight		, null);

		bufferGraphics.drawImage(powerUpImg		, 300	, 200	, powerUpWidth		,powerUpHeight		, null);
		bufferGraphics.drawImage(powerDownImg		, 190	, 400	, powerDownWidth		,powerDownHeight		, null);
	}

	public void drawTopScores(int curr, int high)
	{
		bufferGraphics.setColor(Color.YELLOW);
		bufferGraphics.drawString("SCORE" ,    GUI.frameWidth/8,    44);
		bufferGraphics.drawString("HIGHSCORE", GUI.frameWidth/3+12, 44);

		bufferGraphics.setFont(scoreFont);
		bufferGraphics.setColor(Color.GREEN); 
		bufferGraphics.drawString(Integer.toString(curr),    GUI.frameWidth/8,    62);
		bufferGraphics.drawString(Integer.toString(high),       GUI.frameWidth/3+60, 62);

	}


	int getLastLine()
	{
		if      (currentMenuState == MenuState.MAIN_MENU) return 7;
		else if (currentMenuState == MenuState.PAUSED_MENU) return 9;
		else if (currentMenuState == MenuState.NEW_GAME_MENU) return 3;
		else if (currentMenuState == MenuState.MULTI_MENU) return 3;
		else if (currentMenuState == MenuState.HIGH_SCORES_MENU) return lastMenuHS;
		else if (currentMenuState == MenuState.OPTIONS_MENU) return 4;
		else if (currentMenuState == MenuState.KEYBOARD_SETTINGS_MENU) return 7;
		else if (currentMenuState == MenuState.JOIN_GAME_MENU) return 6;
		else return 1;
	}

	void someThingIsEntered(int currentLine, int isKey)
	{
		if (currentGameState == GameState.NONE || currentGameState == GameState.PAUSED)
		{
			if (currentMenuState == MenuState.MAIN_MENU)
			{			
				if      (currentLine == 1) setMenuState(MenuState.NEW_GAME_MENU);
				else if (currentLine == 2) setMenuState(MenuState.JOIN_GAME_MENU); // Join Game
				else if (currentLine == 3) setMenuState(MenuState.OPTIONS_MENU);
				else if (currentLine == 4) setMenuState(MenuState.HIGH_SCORES_MENU);
				else if (currentLine == 5) client.terminate(); //terminate()
			}

			else if (currentMenuState == MenuState.PAUSED_MENU)
			{			
				System.out.println("asd");
				if      (currentLine == 1) client.startRequest();
				else if (currentLine == 2) setMenuState(MenuState.MAIN_MENU); 
				else if (currentLine == 3) setMenuState(MenuState.NEW_GAME_MENU); 
				else if (currentLine == 4) setMenuState(MenuState.JOIN_GAME_MENU); // Join Game
				else if (currentLine == 5) setMenuState(MenuState.OPTIONS_MENU);
				else if (currentLine == 6) setMenuState(MenuState.HIGH_SCORES_MENU);
				else if (currentLine == 7) client.terminate(); //terminate()
			}

			else if (currentMenuState == MenuState.NEW_GAME_MENU)
			{
				// drawMenuLine   (1,"Single Game");
				// drawMenuLine   (2,"Multi Game");
				// drawMenuLine   (3,"Back");

				if      (currentLine == 1) client.newGame(GameType.SINGLE); // Join Game
				else if (currentLine == 2) setMenuState(MenuState.MULTI_MENU);
				else if (currentLine == 3) setMenuState(MenuState.MAIN_MENU);
			}
			else if (currentMenuState == MenuState.MULTI_MENU)
			{

				if      (currentLine == 1) client.newGame(GameType.MULTI_LOCAL); // Join Game
				else if (currentLine == 2) client.newGame(GameType.MULTI_NETWORK);
				else if (currentLine == 3) setMenuState(MenuState.NEW_GAME_MENU);
			}	

			else if (currentMenuState == MenuState.HIGH_SCORES_MENU)
			{	
				if (currentLine == 4) setMenuState(MenuState.MAIN_MENU);
			}


			else if (currentMenuState == MenuState.OPTIONS_MENU)
			{		    	// drawOptionsLine(1,"Music","On");
				// drawOptionsLine(2,"Sound Effects","Off");
				// drawMenuLine   (3,"Set Keyboard");
				// drawMenuLine   (4,"Back");

				// if (currentLine == 1) setMenuState(NEW_GAME_MENU);
				// else if (currentLine == 2) setMenuState(NEW_GAME_MENU);
				// else if (currentLine == 3) setMenuState(NEW_GAME_MENU);
				// else if (currentLine == 4) setMenuState(MAIN_MENU);
				if (currentLine == 3) setMenuState(MenuState.KEYBOARD_SETTINGS_MENU);
				else if (currentLine == 4) setMenuState(MenuState.MAIN_MENU); 
			}		 
			else if (currentMenuState == MenuState.KEYBOARD_SETTINGS_MENU)
			{
				if (currentLine == getLastLine()) 
				{
					setMenuState(MenuState.OPTIONS_MENU);
					// leküld...
				}
				else keyBoardChangeSelected = 1;

				// drawOptionsLine(1,"Up","Up");
				// drawOptionsLine(2,"Down","Down");
				// drawOptionsLine(3,"Anyad","K");
				// drawOptionsLine(4,"Right",">");
				// drawOptionsLine(5,"Left","<");
				// drawMenuLine   (6,"Save");
				// drawMenuLine   (7,"Back");

			}
			else if (currentMenuState == MenuState.JOIN_GAME_MENU)
			{
				// drawWritingLine(textField);
				// drawMenuLine   (3,"102.1.50.121");
				// drawMenuLine   (4,"102.2.50.121");
				// drawMenuLine   (5,"102.3.50.121");
				// drawMenuLine   (6,"Back");

				if (currentLine == getLastLine()) setMenuState(MenuState.MULTI_MENU);
				else 
				{
					client.joinGame(textField);
					//setGameState(GameState.WAITING);
				}


				//else if (currentLine == 1) jatek indul felsot elkülld + elment
				//else
				//	for (int i = 0; i < 3; i++)
				//			if (currentLine == i+1) jatek indul, IP cim elküld
			}
		}
		else if (currentGameState == GameState.GAMEOVER_NEW_HIGHSCORE) 
			client.sendName(textField);

	}

	// Handle the key typed event from the text field.
	public void keyTyped(KeyEvent e) {}

	// Handle the key-pressed event from the text field.
	public void keyPressed(KeyEvent e) {

		keyCode = e.getKeyCode();

		if 		(keyCode == KeyEvent.VK_A) localSound.playSound(SoundType.beepA);
		else if (keyCode == KeyEvent.VK_S) localSound.playSound(SoundType.beepB);
		else if (keyCode == KeyEvent.VK_D) localSound.playSound(SoundType.enemyExplosion);
		else if (keyCode == KeyEvent.VK_F) localSound.playSound(SoundType.spaceShiftExplosion);
		else if (keyCode == KeyEvent.VK_G) localSound.playSound(SoundType.powerUp);
		else if (keyCode == KeyEvent.VK_H) localSound.playSound(SoundType.powerDown);
		else if (keyCode == KeyEvent.VK_J) localSound.playSound(SoundType.shoot);
/*
		else if (keyCode == KeyEvent.VK_Q) setGameState(GameState.RUNNING);
		else if (keyCode == KeyEvent.VK_W) setGameState(GameState.PAUSED);
		else if (keyCode == KeyEvent.VK_E) setGameState(GameState.WANITING);
		else if (keyCode == KeyEvent.VK_R) setGameState(GameState.MENU);
		else if (keyCode == KeyEvent.VK_T) setGameState(GameState.CONNECT);
		else if (keyCode == KeyEvent.VK_Z) setGameState(GameState.GAMEOVER);
		else if (keyCode == KeyEvent.VK_U) setGameState(GameState.DISCONNECTED);
		else if (keyCode == KeyEvent.VK_I) setGameState(GameState.NEW_HIGH_SCORE);


		else if (keyCode == KeyEvent.VK_1) setGameState(GameState.DISCONNECTED);
		else if (keyCode == KeyEvent.VK_2) setGameState(GameState.GAMEOVER);
		else if (keyCode == KeyEvent.VK_3) setGameState(GameState.NEW_HIGH_SCORE);
		else if (keyCode == KeyEvent.VK_4) setGameState(GameState.RUNNING);

*/
		else if ((keyCode == KeyEvent.VK_BACK_SPACE && !(isTextLine == 1 && textField.length() != 0)) || keyCode == KeyEvent.VK_ESCAPE) //ESCAPE
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
				if (currentGameState == GameState.DISCONNECTED || currentGameState == GameState.GAMEOVER_NEW_HIGHSCORE || currentGameState == GameState.GAMEOVER)
				{
					client.resetGameState();
					setMenuState(MenuState.MAIN_MENU);
				}
				else
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
				keyBoardChangeSelected = 0;
				//majd struktúra szerint...
			}
			else
			{
				if (keyCode == KeyEvent.VK_UP)

				{
					if  (dotLine != 1) dotLine--;
				}

				else if (keyCode == KeyEvent.VK_DOWN)
					if      (currentMenuState == MenuState.HIGH_SCORES_MENU && dotLine == 4);
					else if (currentMenuState == MenuState.KEYBOARD_SETTINGS_MENU && dotLine == 6);
					else if (currentMenuState == MenuState.MAIN_MENU && dotLine == 5);
					else if (currentMenuState == MenuState.MULTI_MENU && dotLine == 3);
					else if (currentMenuState == MenuState.JOIN_GAME_MENU && dotLine == 5);
					else if (currentMenuState == MenuState.NEW_GAME_MENU && dotLine == 3);
					else if (currentMenuState == MenuState.OPTIONS_MENU && dotLine == 4);
					else if (currentMenuState == MenuState.PAUSED_MENU && dotLine == 7);
					else dotLine++;


				else if (isTextLine == 1)
				{
					if (keyCode == KeyEvent.VK_BACK_SPACE)
						textField = textField.substring(0, textField.length() - 1);
					else 
					{
						if (keyCode == KeyEvent.VK_NUMPAD0 ||
								keyCode == KeyEvent.VK_NUMPAD1 ||
								keyCode == KeyEvent.VK_NUMPAD2 ||
								keyCode == KeyEvent.VK_NUMPAD3 ||
								keyCode == KeyEvent.VK_NUMPAD4 ||
								keyCode == KeyEvent.VK_NUMPAD5 ||
								keyCode == KeyEvent.VK_NUMPAD6 ||
								keyCode == KeyEvent.VK_NUMPAD7 ||
								keyCode == KeyEvent.VK_NUMPAD8 ||
								keyCode == KeyEvent.VK_NUMPAD9)
							textField = textField + KeyEvent.getKeyText(keyCode).substring(7,8);
						else if (keyCode == KeyEvent.VK_PERIOD) textField = textField + ".";
						else if (keyCode == KeyEvent.VK_COMMA) textField = textField + ",";
						else if (keyCode == KeyEvent.VK_MINUS) textField = textField + "-";
						else 
							textField = textField + KeyEvent.getKeyText(keyCode); //getKeyChar
					}
				}
				else if (currentMenuState == MenuState.OPTIONS_MENU)
				{
					if (dotLine == 1)
					{
						if (keyCode == KeyEvent.VK_LEFT || keyCode == KeyEvent.VK_RIGHT) 
							{
							if (isEffectOn == true) isEffectOn = false;
							else isEffectOn = true;
							}
					}
					else if (dotLine == 2) 
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
		else if (currentGameState == GameState.RUNNING) 
		{
			if      (keyCode == keyCode1Left);
			else if (keyCode == keyCode1Right);
			else if (keyCode == keyCode1Fire);
			if      (keyCode == keyCode2Left);
			else if (keyCode == keyCode2Right);
			else if (keyCode == keyCode2Fire);

		}
	}


	// Handle the key-released event from the text field.
	public void keyReleased(KeyEvent e) {

	}


	// This method will be called when the mouse has been clicked. 
	public void mouseClicked (MouseEvent me) {

		// Save the coordinates of the click
		xpos = me.getX(); 
		ypos = me.getY();

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

	//public static void main(String[] args) {
	//	GUI f = new GUI();
	//	SwingUtilities.invokeLater(f);
	//}

	@Override
	public void setSound(Boolean val) {
		isEffectOn = val;
	}


	public void setDifficulty(GameSkill gs) {
		currentGameSkill = gs;
	}


	@Override
	public void setRecentIPs(String[] iparr) {
		// TODO Itt majd vedd át, tárold õket!
	}
}

//egybõl ne dobja fe a menut