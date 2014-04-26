package sound;

import interfaces.SoundSystemForClient;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.sound.sampled.*;

import enums.SoundType;

// Java Sound can play sound formats with 8- or 16-bit samples with sample rates from 8000Hz to 48,000Hz. Also, it can play either mono or stereo sound. 
public class SoundSystem implements SoundSystemForClient {

	// Clips
	private Clip shootClip1;
	private Clip shootClip2;
	private Clip shootClip3;
	private Clip shootClip4;
	private Clip shootClip5;

	private int shootCounter;

	// Clips
	private Clip powerUpClip1;
	private Clip powerUpClip2;
	private Clip powerUpClip3;

	private int powerUPCounter;

	private Clip powerDownClip1;
	private Clip powerDownClip2;
	private Clip powerDownClip3;

	private int powerDownCounter;

	private Clip enemyExplosionClip1;
	private Clip enemyExplosionClip2;
	private Clip enemyExplosionClip3;
	private Clip enemyExplosionClip4;
	private Clip enemyExplosionClip5;

	private int enemyExplosionCounter;

	private Clip spaceShipExplosionClip1;
	private Clip spaceShipExplosionClip2;

	private int spaceShipExplosionCounter;

	private Clip beepAClip1;
	private Clip beepAClip2;
	private Clip beepAClip3;

	private int beepACounter;

	private Clip beepBClip1;
	private Clip beepBClip2;
	private Clip beepBClip3;

	private int beepBCounter;

	// Constructor
	public SoundSystem()
	{
		String projdir = System.getProperty("user.dir");

		// FILE
		File shootFileA 			= new File(projdir + "/res/sounds/shootWavA.wav");
		File shootFileB 			= new File(projdir + "/res/sounds/shootWavB.wav");
		File shootFileC 			= new File(projdir + "/res/sounds/shootWavC.wav");
		File enemyExplosionFileA 	= new File(projdir + "/res/sounds/enemyExplosionWavA.wav");
		File enemyExplosionFileB 	= new File(projdir + "/res/sounds/enemyExplosionWavB.wav");
		File enemyExplosionFileC 	= new File(projdir + "/res/sounds/enemyExplosionWavC.wav");
		File spaceShipExplosionFile = new File(projdir + "/res/sounds/spaceShipExplosionWavC.wav");
		File powerUpFile 			= new File(projdir + "/res/sounds/powerUpWav.wav");
		File powerDownFile 			= new File(projdir + "/res/sounds/powerDownWav.wav");
		File beepAFile 		     	= new File(projdir + "/res/sounds/beepWavA.wav");
		File beepBFile 		     	= new File(projdir + "/res/sounds/beepWavB.wav");

		try {
			// AudioInpuStream
			AudioInputStream shootStream1 				= AudioSystem.getAudioInputStream(shootFileA);
			AudioInputStream shootStream2 				= AudioSystem.getAudioInputStream(shootFileB);
			AudioInputStream shootStream3 				= AudioSystem.getAudioInputStream(shootFileA);
			AudioInputStream shootStream4 				= AudioSystem.getAudioInputStream(shootFileB);
			AudioInputStream shootStream5 				= AudioSystem.getAudioInputStream(shootFileC);
			AudioInputStream enemyExplosionStream1 		= AudioSystem.getAudioInputStream(enemyExplosionFileA);
			AudioInputStream enemyExplosionStream2 		= AudioSystem.getAudioInputStream(enemyExplosionFileB);
			AudioInputStream enemyExplosionStream3 		= AudioSystem.getAudioInputStream(enemyExplosionFileA);
			AudioInputStream enemyExplosionStream4 		= AudioSystem.getAudioInputStream(enemyExplosionFileC);
			AudioInputStream enemyExplosionStream5 		= AudioSystem.getAudioInputStream(enemyExplosionFileB);
			
			AudioInputStream spaceShipExplosionStream1 	= AudioSystem.getAudioInputStream(spaceShipExplosionFile);
			AudioInputStream spaceShipExplosionStream2 	= AudioSystem.getAudioInputStream(spaceShipExplosionFile);
			AudioInputStream beepAStream3 				= AudioSystem.getAudioInputStream(beepAFile);
			AudioInputStream beepAStream2 				= AudioSystem.getAudioInputStream(beepAFile);
			AudioInputStream beepAStream1 				= AudioSystem.getAudioInputStream(beepAFile);
			AudioInputStream beepBStream1 				= AudioSystem.getAudioInputStream(beepBFile);
			AudioInputStream beepBStream2 				= AudioSystem.getAudioInputStream(beepBFile);
			AudioInputStream beepBStream3 				= AudioSystem.getAudioInputStream(beepBFile);
			AudioInputStream powerUpStream1  			= AudioSystem.getAudioInputStream(powerUpFile);
			AudioInputStream powerUpStream2  			= AudioSystem.getAudioInputStream(powerUpFile);
			AudioInputStream powerUpStream3  			= AudioSystem.getAudioInputStream(powerUpFile);
			AudioInputStream powerDownStream1  			= AudioSystem.getAudioInputStream(powerDownFile);
			AudioInputStream powerDownStream2  			= AudioSystem.getAudioInputStream(powerDownFile);
			AudioInputStream powerDownStream3  			= AudioSystem.getAudioInputStream(powerDownFile);

			//AudioFomat
			AudioFormat shootFormatA 				= shootStream1.getFormat(); 			
			AudioFormat shootFormatB 				= shootStream2.getFormat(); 
			AudioFormat shootFormatC 				= shootStream5.getFormat(); 
			AudioFormat enemyExplosionFormatA 		= enemyExplosionStream1.getFormat(); 	
			AudioFormat enemyExplosionFormatB 		= enemyExplosionStream2.getFormat(); 
			AudioFormat enemyExplosionFormatC 		= enemyExplosionStream4.getFormat(); 
			AudioFormat spaceShipExplosionFormat 	= spaceShipExplosionStream1.getFormat(); 
			AudioFormat beepAFormat 				= beepAStream1.getFormat(); 
			AudioFormat beepBFormat 				= beepBStream1.getFormat(); 
			AudioFormat powerUpFormat 				= powerUpStream1.getFormat(); 
			AudioFormat powerDownFormat 			= powerDownStream1.getFormat(); 

			// DataLine
			DataLine.Info shootInfoA 				= new DataLine.Info(Clip.class,shootFormatA);			 
			DataLine.Info shootInfoB 				= new DataLine.Info(Clip.class,shootFormatB); 			 
			DataLine.Info shootInfoC 				= new DataLine.Info(Clip.class,shootFormatC);			 
			DataLine.Info enemyExplosionInfoA 		= new DataLine.Info(Clip.class,enemyExplosionFormatA);
			DataLine.Info enemyExplosionInfoB 		= new DataLine.Info(Clip.class,enemyExplosionFormatB);	 
			DataLine.Info enemyExplosionInfoC 		= new DataLine.Info(Clip.class,enemyExplosionFormatC);	 
			DataLine.Info spaceShipExplosionInfo 	= new DataLine.Info(Clip.class,spaceShipExplosionFormat); 
			DataLine.Info beepAInfo 				= new DataLine.Info(Clip.class,beepAFormat);
			DataLine.Info beepBInfo 				= new DataLine.Info(Clip.class,beepBFormat);		 
			DataLine.Info powerUpInfo 				= new DataLine.Info(Clip.class,powerUpFormat);
			DataLine.Info powerDownInfo 				= new DataLine.Info(Clip.class,powerDownFormat);
			 



			// Clip
			shootClip1 = (Clip)AudioSystem.getLine(shootInfoA);
			shootClip2 = (Clip)AudioSystem.getLine(shootInfoB);
			shootClip3 = (Clip)AudioSystem.getLine(shootInfoA);
			shootClip4 = (Clip)AudioSystem.getLine(shootInfoB);
			shootClip5 = (Clip)AudioSystem.getLine(shootInfoC);

			enemyExplosionClip1 = (Clip)AudioSystem.getLine(enemyExplosionInfoA);
			enemyExplosionClip2 = (Clip)AudioSystem.getLine(enemyExplosionInfoB);
			enemyExplosionClip3 = (Clip)AudioSystem.getLine(enemyExplosionInfoA);
			enemyExplosionClip4 = (Clip)AudioSystem.getLine(enemyExplosionInfoC);
			enemyExplosionClip5 = (Clip)AudioSystem.getLine(enemyExplosionInfoB);

			spaceShipExplosionClip1 = (Clip)AudioSystem.getLine(spaceShipExplosionInfo);
			spaceShipExplosionClip2 = (Clip)AudioSystem.getLine(spaceShipExplosionInfo);

			beepAClip1 = (Clip)AudioSystem.getLine(beepAInfo);
			beepAClip2 = (Clip)AudioSystem.getLine(beepAInfo);
			beepAClip3 = (Clip)AudioSystem.getLine(beepAInfo);

			beepBClip1 = (Clip)AudioSystem.getLine(beepBInfo);
			beepBClip2 = (Clip)AudioSystem.getLine(beepBInfo);
			beepBClip3 = (Clip)AudioSystem.getLine(beepBInfo);

			// Clips
			powerUpClip1 	= (Clip)AudioSystem.getLine(powerUpInfo);
			powerUpClip2 	= (Clip)AudioSystem.getLine(powerUpInfo);
			powerUpClip3 	= (Clip)AudioSystem.getLine(powerUpInfo);
			powerDownClip1 	= (Clip)AudioSystem.getLine(powerDownInfo);
			powerDownClip2 	= (Clip)AudioSystem.getLine(powerDownInfo);
			powerDownClip3 	= (Clip)AudioSystem.getLine(powerDownInfo);


			// Open Clip
			shootClip1.open(shootStream1);
			shootClip2.open(shootStream2);
			shootClip3.open(shootStream3);
			shootClip4.open(shootStream4);
			shootClip5.open(shootStream5);


			enemyExplosionClip1.open(enemyExplosionStream1);
			enemyExplosionClip2.open(enemyExplosionStream2);
			enemyExplosionClip3.open(enemyExplosionStream3);
			enemyExplosionClip4.open(enemyExplosionStream4);
			enemyExplosionClip5.open(enemyExplosionStream5);

			spaceShipExplosionClip1.open(spaceShipExplosionStream1);
			spaceShipExplosionClip2.open(spaceShipExplosionStream2);

			beepAClip1.open(beepAStream1);
			beepAClip2.open(beepAStream2);
			beepAClip3.open(beepAStream3);

			beepBClip1.open(beepBStream1);
			beepBClip2.open(beepBStream2);
			beepBClip3.open(beepBStream3);

			powerUpClip1.open(powerUpStream1);
			powerUpClip2.open(powerUpStream2);
			powerUpClip3.open(powerUpStream3); 

			powerDownClip1.open(powerDownStream1);
			powerDownClip2.open(powerDownStream2);
			powerDownClip3.open(powerDownStream3); 
			
			shootCounter = 1;
			powerUPCounter = 1;
			powerDownCounter = 1;
			enemyExplosionCounter = 1;
			spaceShipExplosionCounter = 1;
			beepACounter = 1;
			beepBCounter = 1;

		} catch (UnsupportedAudioFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (LineUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void playSound(SoundType soundType)
	{
		if (soundType == SoundType.shoot)
		{
			if (shootCounter == 1)
			{
				shootClip1.stop();
				shootClip1.setFramePosition(0);
				shootClip1.start();
			}
			else if (shootCounter == 2)
			{
				shootClip2.stop();
				shootClip2.setFramePosition(0);
				shootClip2.start();
			}
			else if (shootCounter == 3)
			{
				shootClip3.stop();
				shootClip3.setFramePosition(0);
				shootClip3.start();
			}
			else if (shootCounter == 4)
			{
				shootClip4.stop();
				shootClip4.setFramePosition(0);
				shootClip4.start();
			}
			else if (shootCounter == 5)
			{
				shootClip5.stop();
				shootClip5.setFramePosition(0);
				shootClip5.start();
			}

			if (shootCounter != 5) shootCounter++;
			else shootCounter = 1;
		}

		else if (soundType == SoundType.powerUp)
		{
			if (powerUPCounter == 1)
			{
				powerUpClip1.stop();
				powerUpClip1.setFramePosition(0);
				powerUpClip1.start();
			}
			else if (powerUPCounter == 2)
			{
				powerUpClip2.stop();
				powerUpClip2.setFramePosition(0);
				powerUpClip2.start();
			}
			else if (powerUPCounter == 3)
			{
				powerUpClip3.stop();
				powerUpClip3.setFramePosition(0);
				powerUpClip3.start();
			}

			if (powerUPCounter != 3) powerUPCounter = powerUPCounter + 1;
			else powerUPCounter = 1;
		}	
		else if (soundType == SoundType.powerDown)
		{
			if (powerDownCounter == 1)
			{
				powerDownClip1.stop();
				powerDownClip1.setFramePosition(0);
				powerDownClip1.start();
			}
			else if (powerDownCounter == 2)
			{
				powerDownClip2.stop();
				powerDownClip2.setFramePosition(0);
				powerDownClip2.start();
			}
			else if (powerDownCounter == 3)
			{
				powerDownClip3.stop();
				powerDownClip3.setFramePosition(0);
				powerDownClip3.start();
			}

			if (powerDownCounter != 3) powerDownCounter = powerDownCounter + 1;
			else powerDownCounter = 1;
		}	
		else if (soundType == SoundType.enemyExplosion)
		{
			if (enemyExplosionCounter == 1)
			{
				enemyExplosionClip1.stop();
				enemyExplosionClip1.setFramePosition(0);
				enemyExplosionClip1.start();
			}
			else if (enemyExplosionCounter == 2)
			{
				enemyExplosionClip2.stop();
				enemyExplosionClip2.setFramePosition(0);
				enemyExplosionClip2.start();
			}
			else if (enemyExplosionCounter == 3)
			{
				enemyExplosionClip3.stop();
				enemyExplosionClip3.setFramePosition(0);
				enemyExplosionClip3.start();
			}
			else if (enemyExplosionCounter == 4)
			{
				enemyExplosionClip4.stop();
				enemyExplosionClip4.setFramePosition(0);
				enemyExplosionClip4.start();
			}
			else if (enemyExplosionCounter == 5)
			{
				enemyExplosionClip5.stop();
				enemyExplosionClip5.setFramePosition(0);
				enemyExplosionClip5.start();
			}
			if (enemyExplosionCounter != 5) enemyExplosionCounter = enemyExplosionCounter + 1;
			else enemyExplosionCounter = 1;
		}	
		else if (soundType == SoundType.spaceShiftExplosion)
		{
			if (spaceShipExplosionCounter == 1)
			{
				spaceShipExplosionClip1.stop();
				spaceShipExplosionClip1.setFramePosition(0);
				spaceShipExplosionClip1.start();
			}
			else if (spaceShipExplosionCounter == 2)
			{
				spaceShipExplosionClip2.stop();
				spaceShipExplosionClip2.setFramePosition(0);
				spaceShipExplosionClip2.start();
			}
			if (spaceShipExplosionCounter != 2) spaceShipExplosionCounter = spaceShipExplosionCounter + 1;
			else spaceShipExplosionCounter = 1;
		}	
		else if (soundType == SoundType.beepA)
		{
			if (beepACounter == 1)
			{
				beepAClip1.stop();
				beepAClip1.setFramePosition(0);
				beepAClip1.start();
			}
			else if (beepACounter == 2)
			{
				beepAClip2.stop();
				beepAClip2.setFramePosition(0);
				beepAClip2.start();
			}
			else if (beepACounter == 3)
			{
				beepAClip3.stop();
				beepAClip3.setFramePosition(0);
				beepAClip3.start();
			}

			if (beepACounter != 3) beepACounter = beepACounter + 1;
			else beepACounter = 1;
		}	
		else if (soundType == SoundType.beepB)
		{
			if (beepBCounter == 1)
			{
				beepBClip1.stop();
				beepBClip1.setFramePosition(0);
				beepBClip1.start();
			}
			else if (beepBCounter == 2)
			{
				beepBClip2.stop();
				beepBClip2.setFramePosition(0);
				beepBClip2.start();
			}
			else if (beepBCounter == 3)
			{
				beepBClip3.stop();
				beepBClip3.setFramePosition(0);
				beepBClip3.start();
			}

			if (beepBCounter != 3) beepBCounter = beepBCounter + 1;
			else beepBCounter = 1;
		}	
	}

	public void playSound(String soundType)
	{
		if (soundType == "shoot")
		{
			if (shootCounter == 1)
			{
				shootClip1.stop();
				shootClip1.setFramePosition(0);
				shootClip1.start();
			}
			else if (shootCounter == 2)
			{
				shootClip2.stop();
				shootClip2.setFramePosition(0);
				shootClip2.start();
			}
			else if (shootCounter == 3)
			{
				shootClip3.stop();
				shootClip3.setFramePosition(0);
				shootClip3.start();
			}
			else if (shootCounter == 4)
			{
				shootClip4.stop();
				shootClip4.setFramePosition(0);
				shootClip4.start();
			}
			else if (shootCounter == 5)
			{
				shootClip5.stop();
				shootClip5.setFramePosition(0);
				shootClip5.start();
			}

			if (shootCounter != 5) shootCounter++;
			else shootCounter = 0;
		}

		else if (soundType == "powerUp")
		{
			if (powerUPCounter == 1)
			{
				powerUpClip1.stop();
				powerUpClip1.setFramePosition(0);
				powerUpClip1.start();
			}
			else if (powerUPCounter == 2)
			{
				powerUpClip2.stop();
				powerUpClip2.setFramePosition(0);
				powerUpClip2.start();
			}
			else if (powerUPCounter == 3)
			{
				powerUpClip3.stop();
				powerUpClip3.setFramePosition(0);
				powerUpClip3.start();
			}

			if (powerUPCounter != 3) powerUPCounter = powerUPCounter + 1;
			else powerUPCounter = 1;
		}	
		else if (soundType == "powerDown")
		{
			if (powerDownCounter == 1)
			{
				powerDownClip1.stop();
				powerDownClip1.setFramePosition(0);
				powerDownClip1.start();
			}
			else if (powerDownCounter == 2)
			{
				powerDownClip2.stop();
				powerDownClip2.setFramePosition(0);
				powerDownClip2.start();
			}
			else if (powerDownCounter == 3)
			{
				powerDownClip3.stop();
				powerDownClip3.setFramePosition(0);
				powerDownClip3.start();
			}

			if (powerDownCounter != 3) powerDownCounter = powerDownCounter + 1;
			else powerDownCounter = 1;
		}	
		else if (soundType == "enemyExplosion")
		{
			if (enemyExplosionCounter == 1)
			{
				enemyExplosionClip1.stop();
				enemyExplosionClip1.setFramePosition(0);
				enemyExplosionClip1.start();
			}
			else if (enemyExplosionCounter == 2)
			{
				enemyExplosionClip2.stop();
				enemyExplosionClip2.setFramePosition(0);
				enemyExplosionClip2.start();
			}
			else if (enemyExplosionCounter == 3)
			{
				enemyExplosionClip3.stop();
				enemyExplosionClip3.setFramePosition(0);
				enemyExplosionClip3.start();
			}
			else if (enemyExplosionCounter == 4)
			{
				enemyExplosionClip4.stop();
				enemyExplosionClip4.setFramePosition(0);
				enemyExplosionClip4.start();
			}
			else if (enemyExplosionCounter == 5)
			{
				enemyExplosionClip5.stop();
				enemyExplosionClip5.setFramePosition(0);
				enemyExplosionClip5.start();
			}
			if (enemyExplosionCounter != 5) enemyExplosionCounter = enemyExplosionCounter + 1;
			else enemyExplosionCounter = 1;
		}	
		else if (soundType == "spaceShiftExplosion")
		{
			if (spaceShipExplosionCounter == 1)
			{
				spaceShipExplosionClip1.stop();
				spaceShipExplosionClip1.setFramePosition(0);
				spaceShipExplosionClip1.start();
			}
			else if (spaceShipExplosionCounter == 2)
			{
				spaceShipExplosionClip2.stop();
				spaceShipExplosionClip2.setFramePosition(0);
				spaceShipExplosionClip2.start();
			}
			if (spaceShipExplosionCounter != 2) spaceShipExplosionCounter = spaceShipExplosionCounter + 1;
			else spaceShipExplosionCounter = 1;
		}	
		else if (soundType == "beepA")
		{
			if (beepACounter == 1)
			{
				beepAClip1.stop();
				beepAClip1.setFramePosition(0);
				beepAClip1.start();
			}
			else if (beepACounter == 2)
			{
				beepAClip2.stop();
				beepAClip2.setFramePosition(0);
				beepAClip2.start();
			}
			else if (beepACounter == 3)
			{
				beepAClip3.stop();
				beepAClip3.setFramePosition(0);
				beepAClip3.start();
			}

			if (beepACounter != 3) beepACounter = beepACounter + 1;
			else beepACounter = 1;
		}	
		else if (soundType == "beepB")
		{
			if (beepBCounter == 1)
			{
				beepBClip1.stop();
				beepBClip1.setFramePosition(0);
				beepBClip1.start();
			}
			else if (beepBCounter == 2)
			{
				beepBClip2.stop();
				beepBClip2.setFramePosition(0);
				beepBClip2.start();
			}
			else if (beepBCounter == 3)
			{
				beepBClip3.stop();
				beepBClip3.setFramePosition(0);
				beepBClip3.start();
			}

			if (beepBCounter != 3) beepBCounter = beepBCounter + 1;
			else beepBCounter = 1;
		}	
	}
}
