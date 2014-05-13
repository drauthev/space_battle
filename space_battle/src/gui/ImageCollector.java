package gui;

import java.io.File;


public class ImageCollector {
	
	private static GraphicObject foregroundImg;
	private static GraphicObject backgroundImg;
	private static GraphicObject[] bulletImg = new GraphicObject[3];
	private static GraphicObject[] enemyABlowImg = new GraphicObject[4];
	private static GraphicObject[] enemyBBlowImg = new GraphicObject[4];
	private static GraphicObject[] enemyCBlowImg = new GraphicObject[4];
	private static GraphicObject[] enemyAImg = new GraphicObject[3];
	private static GraphicObject[] enemyBImg = new GraphicObject[3];
	private static GraphicObject[] enemyCImg = new GraphicObject[3];
	private static GraphicObject[] spaceShipBombImg = new GraphicObject[5];
	private static GraphicObject[] spaceShipImg = new GraphicObject[6];
	private static GraphicObject lifeImg;
	private static GraphicObject[] powerUpImg= new GraphicObject[5];
	private static GraphicObject powerDownImg;
	private static GraphicObject[] powerUpBlowImg = new GraphicObject[4];
	private static GraphicObject shieldImg;
	private static GraphicObject projectileGoingDiagonallyLeftImg;
	private static GraphicObject projectileGoingDiagonallyRightImg;
	
	private GraphicObject[] enemyBlowImg = new GraphicObject[4];
	private GraphicObject[] enemyImg = new GraphicObject[3];
	
	public ImageCollector() {
		String projdir = System.getProperty("user.dir");
		backgroundImg	  	  = new GraphicObject(new File(projdir + "/res/sprites/backgroundImg.png"));
		foregroundImg	 	  = new GraphicObject(new File(projdir + "/res/sprites/foregroundImg.png"));
		bulletImg[0] 		  = new GraphicObject(new File(projdir + "/res/sprites/bulletImg1.png"));
		bulletImg[1] 		  = new GraphicObject(new File(projdir + "/res/sprites/bulletImg2.png"));
		bulletImg[2] 		  = new GraphicObject(new File(projdir + "/res/sprites/bulletImg3.png"));
		enemyABlowImg[0]  	  = new GraphicObject(new File(projdir + "/res/sprites/1/enemyBlowImg1.png"));
		enemyABlowImg[1] 	  = new GraphicObject(new File(projdir + "/res/sprites/1/enemyBlowImg2.png"));
		enemyABlowImg[2] 	  = new GraphicObject(new File(projdir + "/res/sprites/1/enemyBlowImg3.png"));
		enemyABlowImg[3] 	  = new GraphicObject(new File(projdir + "/res/sprites/1/enemyBlowImg4.png"));
		enemyAImg[0] 		  = new GraphicObject(new File(projdir + "/res/sprites/1/enemyImg1.png"));
		enemyAImg[1] 		  = new GraphicObject(new File(projdir + "/res/sprites/1/enemyImg2.png"));
		enemyAImg[2] 		  = new GraphicObject(new File(projdir + "/res/sprites/1/enemyImg3.png"));
		enemyBBlowImg[0] 	  = new GraphicObject(new File(projdir + "/res/sprites/2/enemyBlowImg1.png"));
		enemyBBlowImg[1] 	  = new GraphicObject(new File(projdir + "/res/sprites/2/enemyBlowImg2.png"));
		enemyBBlowImg[2] 	  = new GraphicObject(new File(projdir + "/res/sprites/2/enemyBlowImg3.png"));
		enemyBBlowImg[3] 	  = new GraphicObject(new File(projdir + "/res/sprites/2/enemyBlowImg4.png"));
		enemyBImg[0] 		  = new GraphicObject(new File(projdir + "/res/sprites/2/enemyImg1.png"));
		enemyBImg[1] 		  = new GraphicObject(new File(projdir + "/res/sprites/2/enemyImg2.png"));
		enemyBImg[2] 		  = new GraphicObject(new File(projdir + "/res/sprites/2/enemyImg3.png"));
		enemyCBlowImg[0] 	  = new GraphicObject(new File(projdir + "/res/sprites/3/enemyBlowImg1.png"));
		enemyCBlowImg[1] 	  = new GraphicObject(new File(projdir + "/res/sprites/3/enemyBlowImg2.png"));
		enemyCBlowImg[2] 	  = new GraphicObject(new File(projdir + "/res/sprites/3/enemyBlowImg3.png"));
		enemyCBlowImg[3] 	  = new GraphicObject(new File(projdir + "/res/sprites/3/enemyBlowImg4.png"));
		enemyCImg[0] 		  = new GraphicObject(new File(projdir + "/res/sprites/3/enemyImg1.png"));
		enemyCImg[1] 		  = new GraphicObject(new File(projdir + "/res/sprites/3/enemyImg2.png"));
		enemyCImg[2] 		  = new GraphicObject(new File(projdir + "/res/sprites/3/enemyImg3.png"));
		lifeImg			  	  = new GraphicObject(new File(projdir + "/res/sprites/lifeImg.png"));
		spaceShipBombImg[0]   = new GraphicObject(new File(projdir + "/res/sprites/spaceShipBombImg1.png"));
		spaceShipBombImg[1]   = new GraphicObject(new File(projdir + "/res/sprites/spaceShipBombImg2.png"));
		spaceShipBombImg[2]   = new GraphicObject(new File(projdir + "/res/sprites/spaceShipBombImg3.png"));
		spaceShipBombImg[3]   = new GraphicObject(new File(projdir + "/res/sprites/spaceShipBombImg4.png"));
		spaceShipBombImg[4]   = new GraphicObject(new File(projdir + "/res/sprites/spaceShipBombImg5.png"));
		spaceShipImg[0] 	  = new GraphicObject(new File(projdir + "/res/sprites/spaceShipImg1.png"));
		spaceShipImg[1] 	  = new GraphicObject(new File(projdir + "/res/sprites/spaceShipImg2.png"));
		spaceShipImg[2] 	  = new GraphicObject(new File(projdir + "/res/sprites/spaceShipImg3.png"));
		spaceShipImg[3] 	  = new GraphicObject(new File(projdir + "/res/sprites/spaceShipImg4.png"));
		spaceShipImg[4]	      = new GraphicObject(new File(projdir + "/res/sprites/spaceShipImg5.png"));
		spaceShipImg[5]	   	  = new GraphicObject(new File(projdir + "/res/sprites/spaceShipImg6.png"));
		powerUpImg[0]	      = new GraphicObject(new File(projdir + "/res/sprites/powerUpImg1.png"));
		powerUpImg[1]	      = new GraphicObject(new File(projdir + "/res/sprites/powerUpImg2.png"));
		powerUpImg[2]	      = new GraphicObject(new File(projdir + "/res/sprites/powerUpImg3.png"));
		powerUpImg[3]	      = new GraphicObject(new File(projdir + "/res/sprites/powerUpImg4.png"));
		powerUpImg[4]	      = new GraphicObject(new File(projdir + "/res/sprites/powerUpImg5.png"));
		powerDownImg	  	  = new GraphicObject(new File(projdir + "/res/sprites/powerDownImg.png"));
		shieldImg	  	      = new GraphicObject(new File(projdir + "/res/sprites/shieldImg.png"));
		powerUpBlowImg[0]  	  = new GraphicObject(new File(projdir + "/res/sprites/powerUpBlowImg1.png"));
		powerUpBlowImg[1] 	  = new GraphicObject(new File(projdir + "/res/sprites/powerUpBlowImg2.png"));
		powerUpBlowImg[2] 	  = new GraphicObject(new File(projdir + "/res/sprites/powerUpBlowImg3.png"));
		powerUpBlowImg[3] 	  = new GraphicObject(new File(projdir + "/res/sprites/powerUpBlowImg4.png"));
		projectileGoingDiagonallyLeftImg	= new GraphicObject(new File(projdir + "/res/sprites/projectileGoingDiagonallyLeftImg.png"));
		projectileGoingDiagonallyRightImg	= new GraphicObject(new File(projdir + "/res/sprites/projectileGoingDiagonallyRightImg.png"));
		
		enemyImg = enemyAImg;
		enemyBlowImg = enemyABlowImg;
	}

	public GraphicObject getForegroundImg() {
		return foregroundImg;
	}

	public GraphicObject getBackgroundImg() {
		return backgroundImg;
	}

	public GraphicObject[] getBulletImg() {
		return bulletImg;
	}

	public GraphicObject[] getEnemyBlowImg() {
		return enemyBlowImg;
	}

	public GraphicObject[] getEnemyImg() {
		return enemyImg;
	}

	public GraphicObject[] getSpaceShipBombImg() {
		return spaceShipBombImg;
	}

	public GraphicObject[] getSpaceShipImg() {
		return spaceShipImg;
	}

	public GraphicObject getLifeImg() {
		return lifeImg;
	}

	public GraphicObject[] getPowerUpImg() {
		return powerUpImg;
	}

	public GraphicObject getPowerDownImg() {
		return powerDownImg;
	}

	public GraphicObject[] getPowerUpBlowImg() {
		return powerUpBlowImg;
	}

	public GraphicObject getShieldImg() {
		return shieldImg;
	}

	public GraphicObject getProjectileGoingDiagonallyLeftImg() {
		return projectileGoingDiagonallyLeftImg;
	}

	public GraphicObject getProjectileGoingDiagonallyRightImg() {
		return projectileGoingDiagonallyRightImg;
	}

	public void setEnemy(int i) {
		if (i == 1)
		{
			enemyImg = enemyAImg;
			enemyBlowImg = enemyABlowImg;
		}
		else if (i == 2)
		{
			enemyImg = enemyBImg;
			enemyBlowImg = enemyBBlowImg;
		}
		else if (i == 3)
		{
			enemyImg = enemyCImg;
			enemyBlowImg = enemyCBlowImg;
		}
	}

	
}
