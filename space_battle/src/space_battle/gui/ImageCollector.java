package space_battle.gui;

/**
 * ImageCollector class is used to collect all the ImageObjects, which are used to draw to the
 * screen.
 * It is used in {@link space_battle.gui.GUI}. class.
 * @author fimi 
 */
public class ImageCollector {
	
	private static ImageObject foregroundImgObj;
	private static ImageObject backgroundImgObj;
	private static ImageObject[] bulletImgObj = new ImageObject[3];
	private static ImageObject[] enemyABlowImgObj = new ImageObject[4];
	private static ImageObject[] enemyBBlowImgObj = new ImageObject[4];
	private static ImageObject[] enemyCBlowImgObj = new ImageObject[4];
	private static ImageObject[] enemyAImgObj = new ImageObject[3];
	private static ImageObject[] enemyBImgObj = new ImageObject[3];
	private static ImageObject[] enemyCImgObj = new ImageObject[3];
	private static ImageObject[] spaceShipBombImgObj = new ImageObject[5];
	private static ImageObject[] spaceShipImgObj = new ImageObject[6];
	private static ImageObject lifeImgObj;
	private static ImageObject[] powerUpImgObj= new ImageObject[5];
	private static ImageObject powerDownImgObj;
	private static ImageObject shieldImgObj;
	private static ImageObject projectileGoingDiagonallyLeftImgObj;
	private static ImageObject projectileGoingDiagonallyRightImgObj;
	
	private ImageObject[] enemyBlowImgObj = new ImageObject[4];
	private ImageObject[] enemyImgObj = new ImageObject[3];
	
	/**
	 * Constructor for ImageCollector class.
	 * It loads the ImageTypes from image files.
	 * @author fimi 
	 */
	public ImageCollector() {
		backgroundImgObj	  	  = new ImageObject("/space_battle/res/sprites/backgroundImg.png");
		foregroundImgObj	 	  = new ImageObject("/space_battle/res/sprites/foregroundImg.png");
		bulletImgObj[0] 		  = new ImageObject("/space_battle/res/sprites/bulletImg1.png");
		bulletImgObj[1] 		  = new ImageObject("/space_battle/res/sprites/bulletImg2.png");
		bulletImgObj[2] 		  = new ImageObject("/space_battle/res/sprites/bulletImg3.png");
		enemyABlowImgObj[0]  	  = new ImageObject("/space_battle/res/sprites/1/enemyBlowImg1.png");
		enemyABlowImgObj[1] 	  = new ImageObject("/space_battle/res/sprites/1/enemyBlowImg2.png");
		enemyABlowImgObj[2] 	  = new ImageObject("/space_battle/res/sprites/1/enemyBlowImg3.png");
		enemyABlowImgObj[3] 	  = new ImageObject("/space_battle/res/sprites/1/enemyBlowImg4.png");
		enemyAImgObj[0] 		  = new ImageObject("/space_battle/res/sprites/1/enemyImg1.png");
		enemyAImgObj[1] 		  = new ImageObject("/space_battle/res/sprites/1/enemyImg2.png");
		enemyAImgObj[2] 		  = new ImageObject("/space_battle/res/sprites/1/enemyImg3.png");
		enemyBBlowImgObj[0] 	  = new ImageObject("/space_battle/res/sprites/2/enemyBlowImg1.png");
		enemyBBlowImgObj[1] 	  = new ImageObject("/space_battle/res/sprites/2/enemyBlowImg2.png");
		enemyBBlowImgObj[2] 	  = new ImageObject("/space_battle/res/sprites/2/enemyBlowImg3.png");
		enemyBBlowImgObj[3] 	  = new ImageObject("/space_battle/res/sprites/2/enemyBlowImg4.png");
		enemyBImgObj[0] 		  = new ImageObject("/space_battle/res/sprites/2/enemyImg1.png");
		enemyBImgObj[1] 		  = new ImageObject("/space_battle/res/sprites/2/enemyImg2.png");
		enemyBImgObj[2] 		  = new ImageObject("/space_battle/res/sprites/2/enemyImg3.png");
		enemyCBlowImgObj[0] 	  = new ImageObject("/space_battle/res/sprites/3/enemyBlowImg1.png");
		enemyCBlowImgObj[1] 	  = new ImageObject("/space_battle/res/sprites/3/enemyBlowImg2.png");
		enemyCBlowImgObj[2] 	  = new ImageObject("/space_battle/res/sprites/3/enemyBlowImg3.png");
		enemyCBlowImgObj[3] 	  = new ImageObject("/space_battle/res/sprites/3/enemyBlowImg4.png");
		enemyCImgObj[0] 		  = new ImageObject("/space_battle/res/sprites/3/enemyImg1.png");
		enemyCImgObj[1] 		  = new ImageObject("/space_battle/res/sprites/3/enemyImg2.png");
		enemyCImgObj[2] 		  = new ImageObject("/space_battle/res/sprites/3/enemyImg3.png");
		lifeImgObj			  	  = new ImageObject("/space_battle/res/sprites/lifeImg.png");
		spaceShipBombImgObj[0]    = new ImageObject("/space_battle/res/sprites/spaceShipBombImg1.png");
		spaceShipBombImgObj[1]    = new ImageObject("/space_battle/res/sprites/spaceShipBombImg2.png");
		spaceShipBombImgObj[2]    = new ImageObject("/space_battle/res/sprites/spaceShipBombImg3.png");
		spaceShipBombImgObj[3]    = new ImageObject("/space_battle/res/sprites/spaceShipBombImg4.png");
		spaceShipBombImgObj[4]    = new ImageObject("/space_battle/res/sprites/spaceShipBombImg5.png");
		spaceShipImgObj[0] 	  	  = new ImageObject("/space_battle/res/sprites/spaceShipImg1.png");
		spaceShipImgObj[1] 	  	  = new ImageObject("/space_battle/res/sprites/spaceShipImg2.png");
		spaceShipImgObj[2] 	  	  = new ImageObject("/space_battle/res/sprites/spaceShipImg3.png");
		spaceShipImgObj[3] 	  	  = new ImageObject("/space_battle/res/sprites/spaceShipImg4.png");
		spaceShipImgObj[4]	      = new ImageObject("/space_battle/res/sprites/spaceShipImg5.png");
		spaceShipImgObj[5]	   	  = new ImageObject("/space_battle/res/sprites/spaceShipImg6.png");
		powerUpImgObj[0]	      = new ImageObject("/space_battle/res/sprites/powerUpImg1.png");
		powerUpImgObj[1]	      = new ImageObject("/space_battle/res/sprites/powerUpImg2.png");
		powerUpImgObj[2]	      = new ImageObject("/space_battle/res/sprites/powerUpImg3.png");
		powerUpImgObj[3]	      = new ImageObject("/space_battle/res/sprites/powerUpImg4.png");
		powerUpImgObj[4]	      = new ImageObject("/space_battle/res/sprites/powerUpImg5.png");
		powerDownImgObj	  	      = new ImageObject("/space_battle/res/sprites/powerDownImg.png");
		shieldImgObj	  	      = new ImageObject("/space_battle/res/sprites/shieldImg.png");
		projectileGoingDiagonallyLeftImgObj	  = new ImageObject("/space_battle/res/sprites/projectileGoingDiagonallyLeftImg.png");
		projectileGoingDiagonallyRightImgObj  = new ImageObject("/space_battle/res/sprites/projectileGoingDiagonallyRightImg.png");
		
		enemyImgObj = enemyAImgObj;
		enemyBlowImgObj = enemyABlowImgObj;
	}

	/**
	 * Returns the ForeGround ImageObject
	 * @return the ForeGround ImageObject
	 * @author fimi
	 */
	public ImageObject getForegroundImgObj() {
		return foregroundImgObj;
	}

	/**
	 * Returns the BackGround ImageObject
	 * @return the BackGround ImageObject
	 * @author fimi
	 */
	public ImageObject getBackgroundImgObj() {
		return backgroundImgObj;
	}

	/**
	 * Returns the specified Bullet ImageObject from the three Bullet ImageObjects. 
	 * @param i the ordinal number of the returned Bullet ImageObject. 
	 * @return the specified Bullet ImageObject
	 * @author fimi
	 */
	public ImageObject getBulletImgObj(int i) {
		return bulletImgObj[i % 3];
	}

	/**
	 * Returns the specified SpaceShipBomb ImageObject from the five SpaceShipBomb ImageObjects 
	 * @param i the ordinal number of the returned SpaceShipBomb ImageObject
	 * @return the specified SpaceShipBomb ImageObject
	 * @author fimi
	 */
	public ImageObject getSpaceShipBombImgObj(int i) {
		return spaceShipBombImgObj[i % 5];
	}

	/**
	 * Returns the specified SpaceShip ImageObject from the six SpaceShip ImageObjects 
	 * @param i the ordinal number of the returned SpaceShip ImageObject
	 * @return the specified SpaceShip ImageObject
	 * @author fimi
	 */
	public ImageObject getSpaceShipImgObj(int i) {
		return spaceShipImgObj[i % 6];
	}

	/**
	 * Returns the Life ImageObject 
	 * @return the Life ImageObject
	 * @author fimi
	 */
	public ImageObject getLifeImgObj() {
		return lifeImgObj;
	}

	/**
	 * Returns the specified PowerUp ImageObject from the five PowerUp ImageObjects 
	 * @param i the ordinal number of the returned PowerUp ImageObject
	 * @return the specified PowerUp ImageObject
	 * @author fimi
	 */
	public ImageObject getPowerUpImgObj(int i) {
		return powerUpImgObj[i % 5];
	}


	/**
	 * Returns the specified PowerDown ImageObject from the five PowerDown ImageObjects 
	 * @param i the ordinal number of the returned PowerDown ImageObject
	 * @return the specified PowerDown ImageObject
	 * @author fimi
	 */
	public ImageObject getPowerDownImgObj() {
		return powerDownImgObj;
	}


	/**
	 * Returns the specified PowerUpBlow ImageObject from the five PowerUpBlow ImageObjects 
	 * @param i the ordinal number of the returned PowerUpBlow ImageObject
	 * @return the specified PowerUpBlow ImageObject
	 * @author fimi
	 */
	public ImageObject getPowerUpBlowImgObj(int i) {
		return enemyABlowImgObj[i % 4];
	}

	/**
	 * Returns the Shield ImageObject 
	 * @return the Shield ImageObject
	 * @author fimi
	 */
	public ImageObject getShieldImgObj() {
		return shieldImgObj;
	}

	/**
	 * Returns the ProjectileGoingDiagonallyLeft ImageObject 
	 * @return the ProjectileGoingDiagonallyLeft ImageObject
	 * @author fimi
	 */
	public ImageObject getProjectileGoingDiagonallyLeftImgObj() {
		return projectileGoingDiagonallyLeftImgObj;
	}

	/**
	 * Returns the ProjectileGoingDiagonallyRight ImageObject 
	 * @return the ProjectileGoingDiagonallyRight ImageObject
	 * @author fimi
	 */
	public ImageObject getProjectileGoingDiagonallyRightImgObj() {
		return projectileGoingDiagonallyRightImgObj;
	}

	/**
	 * Used to set which NPC images (HostileType1, HostileType2, HostileType3) is need to returned by
	 * {@link #getEnemyImgObj(int)} and {@link #getEnemyBlowImgObj(int)}. <br>
	 * It sets the field variables enemyImgObj and enemyImgBlowObj to
	 * enemyXImgObj and enemyImgXBlowImgObj, where X stands for A, B or C.
	 * @param i 1 - HostileType1 is need to be set <br> 2 - HostileType2 is need to be set <br> 3 - HostileType3 is need to be set
	 * @author fimi
	 */
	public void setEnemy(int i) {
		if (i == 1)
		{
			enemyImgObj = enemyAImgObj;
			enemyBlowImgObj = enemyABlowImgObj;
		}
		else if (i == 2)
		{
			enemyImgObj = enemyBImgObj;
			enemyBlowImgObj = enemyBBlowImgObj;
		}
		else if (i == 3)
		{
			enemyImgObj = enemyCImgObj;
			enemyBlowImgObj = enemyCBlowImgObj;
		}
	}

	/**
	 * Returns the specified EnemyBlow ImageObject from the four EnemyBlow ImageObjects. <br>
	 * Each enemy (HostileType1, HostileType2, HostileType3) has four EnemyBlow Images.
	 * The chosen enemy is need to be set with {@link #setEnemy(int)}.
	 * @param i the ordinal number of the returned EnemyBlow ImageObject
	 * @return the specified EnemyBlow ImageObject
	 * @author fimi
	 */
	public ImageObject getEnemyBlowImgObj(int i) {
		return enemyBlowImgObj[i % 4];
	}

	/**
	 * Returns the specified Enemy ImageObject from the four Enemy ImageObjects. <br>
	 * Each enemy (HostileType1, HostileType2, HostileType3) has four Enemy Images.
	 * The chosen enemy is need to be set with {@link #setEnemy(int)}.
	 * @param i the ordinal number of the returned Enemy ImageObject
	 * @return the specified Enemy ImageObject
	 * @author fimi
	 */
	public ImageObject getEnemyImgObj(int i) {
		return enemyImgObj[i % 3];
	}
	
}
