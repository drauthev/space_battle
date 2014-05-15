package gui;

import java.io.File;


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
	private static ImageObject[] powerUpBlowImgObj = new ImageObject[4];
	private static ImageObject shieldImgObj;
	private static ImageObject projectileGoingDiagonallyLeftImgObj;
	private static ImageObject projectileGoingDiagonallyRightImgObj;
	
	private ImageObject[] enemyBlowImgObj = new ImageObject[4];
	private ImageObject[] enemyImgObj = new ImageObject[3];
	
	public ImageCollector() {
		String projdir = System.getProperty("user.dir");
		backgroundImgObj	  	  = new ImageObject(new File(projdir + "/res/sprites/backgroundImg.png"));
		foregroundImgObj	 	  = new ImageObject(new File(projdir + "/res/sprites/foregroundImg.png"));
		bulletImgObj[0] 		  = new ImageObject(new File(projdir + "/res/sprites/bulletImg1.png"));
		bulletImgObj[1] 		  = new ImageObject(new File(projdir + "/res/sprites/bulletImg2.png"));
		bulletImgObj[2] 		  = new ImageObject(new File(projdir + "/res/sprites/bulletImg3.png"));
		enemyABlowImgObj[0]  	  = new ImageObject(new File(projdir + "/res/sprites/1/enemyBlowImg1.png"));
		enemyABlowImgObj[1] 	  = new ImageObject(new File(projdir + "/res/sprites/1/enemyBlowImg2.png"));
		enemyABlowImgObj[2] 	  = new ImageObject(new File(projdir + "/res/sprites/1/enemyBlowImg3.png"));
		enemyABlowImgObj[3] 	  = new ImageObject(new File(projdir + "/res/sprites/1/enemyBlowImg4.png"));
		enemyAImgObj[0] 		  = new ImageObject(new File(projdir + "/res/sprites/1/enemyImg1.png"));
		enemyAImgObj[1] 		  = new ImageObject(new File(projdir + "/res/sprites/1/enemyImg2.png"));
		enemyAImgObj[2] 		  = new ImageObject(new File(projdir + "/res/sprites/1/enemyImg3.png"));
		enemyBBlowImgObj[0] 	  = new ImageObject(new File(projdir + "/res/sprites/2/enemyBlowImg1.png"));
		enemyBBlowImgObj[1] 	  = new ImageObject(new File(projdir + "/res/sprites/2/enemyBlowImg2.png"));
		enemyBBlowImgObj[2] 	  = new ImageObject(new File(projdir + "/res/sprites/2/enemyBlowImg3.png"));
		enemyBBlowImgObj[3] 	  = new ImageObject(new File(projdir + "/res/sprites/2/enemyBlowImg4.png"));
		enemyBImgObj[0] 		  = new ImageObject(new File(projdir + "/res/sprites/2/enemyImg1.png"));
		enemyBImgObj[1] 		  = new ImageObject(new File(projdir + "/res/sprites/2/enemyImg2.png"));
		enemyBImgObj[2] 		  = new ImageObject(new File(projdir + "/res/sprites/2/enemyImg3.png"));
		enemyCBlowImgObj[0] 	  = new ImageObject(new File(projdir + "/res/sprites/3/enemyBlowImg1.png"));
		enemyCBlowImgObj[1] 	  = new ImageObject(new File(projdir + "/res/sprites/3/enemyBlowImg2.png"));
		enemyCBlowImgObj[2] 	  = new ImageObject(new File(projdir + "/res/sprites/3/enemyBlowImg3.png"));
		enemyCBlowImgObj[3] 	  = new ImageObject(new File(projdir + "/res/sprites/3/enemyBlowImg4.png"));
		enemyCImgObj[0] 		  = new ImageObject(new File(projdir + "/res/sprites/3/enemyImg1.png"));
		enemyCImgObj[1] 		  = new ImageObject(new File(projdir + "/res/sprites/3/enemyImg2.png"));
		enemyCImgObj[2] 		  = new ImageObject(new File(projdir + "/res/sprites/3/enemyImg3.png"));
		lifeImgObj			  	  = new ImageObject(new File(projdir + "/res/sprites/lifeImg.png"));
		spaceShipBombImgObj[0]   = new ImageObject(new File(projdir + "/res/sprites/spaceShipBombImg1.png"));
		spaceShipBombImgObj[1]   = new ImageObject(new File(projdir + "/res/sprites/spaceShipBombImg2.png"));
		spaceShipBombImgObj[2]   = new ImageObject(new File(projdir + "/res/sprites/spaceShipBombImg3.png"));
		spaceShipBombImgObj[3]   = new ImageObject(new File(projdir + "/res/sprites/spaceShipBombImg4.png"));
		spaceShipBombImgObj[4]   = new ImageObject(new File(projdir + "/res/sprites/spaceShipBombImg5.png"));
		spaceShipImgObj[0] 	  = new ImageObject(new File(projdir + "/res/sprites/spaceShipImg1.png"));
		spaceShipImgObj[1] 	  = new ImageObject(new File(projdir + "/res/sprites/spaceShipImg2.png"));
		spaceShipImgObj[2] 	  = new ImageObject(new File(projdir + "/res/sprites/spaceShipImg3.png"));
		spaceShipImgObj[3] 	  = new ImageObject(new File(projdir + "/res/sprites/spaceShipImg4.png"));
		spaceShipImgObj[4]	      = new ImageObject(new File(projdir + "/res/sprites/spaceShipImg5.png"));
		spaceShipImgObj[5]	   	  = new ImageObject(new File(projdir + "/res/sprites/spaceShipImg6.png"));
		powerUpImgObj[0]	      = new ImageObject(new File(projdir + "/res/sprites/powerUpImg1.png"));
		powerUpImgObj[1]	      = new ImageObject(new File(projdir + "/res/sprites/powerUpImg2.png"));
		powerUpImgObj[2]	      = new ImageObject(new File(projdir + "/res/sprites/powerUpImg3.png"));
		powerUpImgObj[3]	      = new ImageObject(new File(projdir + "/res/sprites/powerUpImg4.png"));
		powerUpImgObj[4]	      = new ImageObject(new File(projdir + "/res/sprites/powerUpImg5.png"));
		powerDownImgObj	  	  = new ImageObject(new File(projdir + "/res/sprites/powerDownImg.png"));
		shieldImgObj	  	      = new ImageObject(new File(projdir + "/res/sprites/shieldImg.png"));
		powerUpBlowImgObj[0]  	  = new ImageObject(new File(projdir + "/res/sprites/powerUpBlowImg1.png"));
		powerUpBlowImgObj[1] 	  = new ImageObject(new File(projdir + "/res/sprites/powerUpBlowImg2.png"));
		powerUpBlowImgObj[2] 	  = new ImageObject(new File(projdir + "/res/sprites/powerUpBlowImg3.png"));
		powerUpBlowImgObj[3] 	  = new ImageObject(new File(projdir + "/res/sprites/powerUpBlowImg4.png"));
		projectileGoingDiagonallyLeftImgObj	= new ImageObject(new File(projdir + "/res/sprites/projectileGoingDiagonallyLeftImg.png"));
		projectileGoingDiagonallyRightImgObj	= new ImageObject(new File(projdir + "/res/sprites/projectileGoingDiagonallyRightImg.png"));
		
		enemyImgObj = enemyAImgObj;
		enemyBlowImgObj = enemyABlowImgObj;
	}

	public ImageObject getForegroundImgObj() {
		return foregroundImgObj;
	}

	public ImageObject getBackgroundImgObj() {
		return backgroundImgObj;
	}

	public ImageObject getBulletImgObj(int i) {
		return bulletImgObj[i % 3];
	}

	public ImageObject getEnemyBlowImgObj(int i) {
		return enemyBlowImgObj[i % 4];
	}

	public ImageObject getEnemyImgObj(int i) {
		return enemyImgObj[i % 3];
	}

	public ImageObject getSpaceShipBombImgObj(int i) {
		return spaceShipBombImgObj[i % 5];
	}

	public ImageObject getSpaceShipImgObj(int i) {
		return spaceShipImgObj[i % 6];
	}

	public ImageObject getLifeImgObj() {
		return lifeImgObj;
	}

	public ImageObject[] getPowerUpImgObj() {
		return powerUpImgObj;
	}

	public ImageObject getPowerDownImgObj() {
		return powerDownImgObj;
	}

	public ImageObject[] getPowerUpBlowImgObj() {
		return powerUpBlowImgObj;
	}

	public ImageObject getShieldImgObj() {
		return shieldImgObj;
	}

	public ImageObject getProjectileGoingDiagonallyLeftImgObj() {
		return projectileGoingDiagonallyLeftImgObj;
	}

	public ImageObject getProjectileGoingDiagonallyRightImgObj() {
		return projectileGoingDiagonallyRightImgObj;
	}

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

	
}
