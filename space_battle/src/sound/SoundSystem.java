package sound;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.*;

import enums.SoundType;

// Java Sound can play sound formats with 8- or 16-bit samples with sample rates from 8000Hz to 48,000Hz. Also, it can play either mono or stereo sound. 
public class SoundSystem {

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
		// FILE
		File shootFileA 			= new File("shootWavA.wav");
		File shootFileB 			= new File("shootWavB.wav");
		File shootFileC 			= new File("shootWavC.wav");
		File enemyExplosionFileA 	= new File("enemyExplosionWavA.wav");
		File enemyExplosionFileB 	= new File("enemyExplosionWavB.wav");
		File enemyExplosionFileC 	= new File("enemyExplosionWavC.wav");
		File spaceShipExplosionFile = new File("spaceShipExplosionWavC.wav");
		File beepFileA 				= new File("beepWavA.wav");
		File beepFileB 				= new File("beepWavB.wav");
		File powerUpFile 			= new File("powerUpWav.wav");
		File beepAFile 			= new File("beepAWav.wav");

		try {
			// AudioInpuStream
			AudioInputStream shootStreamA 				= AudioSystem.getAudioInputStream(shootFileA);
			AudioInputStream shootStreamB 				= AudioSystem.getAudioInputStream(shootFileB);
			AudioInputStream shootStreamC 				= AudioSystem.getAudioInputStream(shootFileC);
			AudioInputStream enemyExplosionStreamA 		= AudioSystem.getAudioInputStream(enemyExplosionFileA);
			AudioInputStream enemyExplosionStreamB 		= AudioSystem.getAudioInputStream(enemyExplosionFileB);
			AudioInputStream enemyExplosionStreamC 		= AudioSystem.getAudioInputStream(enemyExplosionFileC);
			AudioInputStream spaceShipExplosionStream 	= AudioSystem.getAudioInputStream(spaceShipExplosionFile);
			AudioInputStream beepStreamA 				= AudioSystem.getAudioInputStream(beepFileA);
			AudioInputStream beepStreamB 				= AudioSystem.getAudioInputStream(beepFileB);
			AudioInputStream powerUpStream  			= AudioSystem.getAudioInputStream(powerUpFile);
			AudioInputStream beepAStream 			= AudioSystem.getAudioInputStream(beepAFile);

			//AudioFomat
			AudioFormat shootFormatA 				= shootStreamA.getFormat(); 			
			AudioFormat shootFormatB 				= shootStreamB.getFormat(); 
			AudioFormat shootFormatC 				= shootStreamC.getFormat(); 
			AudioFormat enemyExplosionFormatA 		= enemyExplosionStreamA.getFormat(); 	
			AudioFormat enemyExplosionFormatB 		= enemyExplosionStreamB.getFormat(); 
			AudioFormat enemyExplosionFormatC 		= enemyExplosionStreamC.getFormat(); 
			AudioFormat spaceShipExplosionFormat 	= spaceShipExplosionStream.getFormat(); 
			AudioFormat beepFormatA 				= beepStreamA.getFormat(); 
			AudioFormat beepFormatB 				= beepStreamB.getFormat(); 
			AudioFormat powerUpFormat 				= powerUpStream.getFormat(); 
			AudioFormat beepAFormat				= beepAStream.getFormat(); 

			// DataLine
			DataLine.Info shootInfoA 				= new DataLine.Info(Clip.class,shootFormatA);			 
			DataLine.Info shootInfoB 				= new DataLine.Info(Clip.class,shootFormatB); 			 
			DataLine.Info shootInfoC 				= new DataLine.Info(Clip.class,shootFormatC);			 
			DataLine.Info enemyExplosionInfoA 		= new DataLine.Info(Clip.class,enemyExplosionFormatA);
			DataLine.Info enemyExplosionInfoB 		= new DataLine.Info(Clip.class,enemyExplosionFormatB);	 
			DataLine.Info enemyExplosionInfoC 		= new DataLine.Info(Clip.class,enemyExplosionFormatC);	 
			DataLine.Info spaceShipExplosionInfo 	= new DataLine.Info(Clip.class,spaceShipExplosionFormat); 
			DataLine.Info beepInfoA 				= new DataLine.Info(Clip.class,beepFormatA);
			DataLine.Info beepInfoB 				= new DataLine.Info(Clip.class,beepFormatB);		 
			DataLine.Info powerUpInfo 				= new DataLine.Info(Clip.class,powerUpFormat);
			DataLine.Info beepAInfo				= new DataLine.Info(Clip.class,beepAFormat);		 



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

			beepAClip1 = (Clip)AudioSystem.getLine(beepInfoA);
			beepAClip2 = (Clip)AudioSystem.getLine(beepInfoA);
			beepAClip3 = (Clip)AudioSystem.getLine(beepInfoA);

			beepBClip1 = (Clip)AudioSystem.getLine(beepInfoB);
			beepBClip2 = (Clip)AudioSystem.getLine(beepInfoB);
			beepBClip3 = (Clip)AudioSystem.getLine(beepInfoB);

			// Clips
			powerUpClip1 	= (Clip)AudioSystem.getLine(powerUpInfo);
			powerUpClip2 	= (Clip)AudioSystem.getLine(powerUpInfo);
			powerUpClip3 	= (Clip)AudioSystem.getLine(powerUpInfo);
			beepAClip1 	= (Clip)AudioSystem.getLine(beepAInfo);
			beepAClip2 	= (Clip)AudioSystem.getLine(beepAInfo);
			beepAClip3 	= (Clip)AudioSystem.getLine(beepAInfo);

			// Open Clip
			shootClip1.open(shootStreamA);
			shootClip2.open(shootStreamB);
			shootClip3.open(shootStreamA);
			shootClip4.open(shootStreamB);
			shootClip5.open(shootStreamC);


			enemyExplosionClip1.open(enemyExplosionStreamA);
			enemyExplosionClip2.open(enemyExplosionStreamB);
			enemyExplosionClip3.open(enemyExplosionStreamA);
			enemyExplosionClip4.open(enemyExplosionStreamC);
			enemyExplosionClip5.open(enemyExplosionStreamB);

			spaceShipExplosionClip1.open(spaceShipExplosionStream);
			spaceShipExplosionClip2.open(spaceShipExplosionStream);

			beepBClip1.open(beepStreamA);
			beepBClip2.open(beepStreamA);
			beepBClip3.open(beepStreamA);

			beepBClip1.open(beepStreamB);
			beepBClip2.open(beepStreamB);
			beepBClip3.open(beepStreamB);

			powerUpClip1.open(powerUpStream);
			powerUpClip2.open(powerUpStream);
			powerUpClip3.open(powerUpStream); 

			beepAClip1.open(beepAStream);
			beepAClip2.open(beepAStream);
			beepAClip3.open(beepAStream);

			shootCounter = 1;

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

	public void playSound(SoundType soundName)
	{
		if (soundName == SoundType.shoot)
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

		else if (soundName == SoundType.powerUp)
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
			else if (powerUPCounter == 23)
			{
				powerUpClip3.stop();
				powerUpClip3.setFramePosition(0);
				powerUpClip3.start();
			}

			if (powerUPCounter != 2) powerUPCounter = powerUPCounter + 1;
			else powerUPCounter = 1;
		}	
		else if (soundName == SoundType.powerDown)
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

			if (powerDownCounter != 2) powerDownCounter = powerDownCounter + 1;
			else powerDownCounter = 1;
		}	
		else if (soundName == SoundType.enemyExplosion)
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
		else if (soundName == SoundType.spaceShiftExplosion)
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
		else if (soundName == SoundType.beepA)
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

			if (beepACounter != 2) beepACounter = beepACounter + 1;
			else beepACounter = 1;
		}	
		else if (soundName == SoundType.beepB)
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

			if (beepBCounter != 2) beepBCounter = beepBCounter + 1;
			else beepBCounter = 1;
		}	
	}
}
