package sound;

import interfaces.SoundSystemForClient;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class SoundSystem implements SoundSystemForClient {

	private int shootNumber = 5;
	private int powerUpNumber = 3;
	private int powerDownNumber = 3;
	private int enemyExplosionNumber = 5;
	private int spaceShipExplosionNumber = 2;
	private int beepANumber = 3;
	private int beepBNumber = 3;
	
	// Clips
	private Clip[] shootClip = new Clip[shootNumber];  
	private Clip[] powerUpClip = new Clip[powerUpNumber];   	
	private Clip[] powerDownClip = new Clip[powerDownNumber]; 	
	private Clip[] enemyExplosionClip = new Clip[enemyExplosionNumber]; 	
	private Clip[] spaceShipExplosionClip = new Clip[spaceShipExplosionNumber]; 
	private Clip[] beepAClip = new Clip[beepANumber]; 			
	private Clip[] beepBClip = new Clip[beepANumber]; 			

	String projdir;
	
	// Constructor
	public SoundSystem()
	{
		projdir = System.getProperty("user.dir");
		
		shootClip[0] = generateClip("shootWavA.wav");
		shootClip[1] = generateClip("shootWavB.wav");
		shootClip[2] = generateClip("shootWavA.wav");
		shootClip[3] = generateClip("shootWavC.wav");
		shootClip[4] = generateClip("shootWavB.wav");
		
		enemyExplosionClip[0] = generateClip("enemyExplosionWavA.wav");
		enemyExplosionClip[1] = generateClip("enemyExplosionWavB.wav");
		enemyExplosionClip[2] = generateClip("enemyExplosionWavA.wav");
		enemyExplosionClip[3] = generateClip("enemyExplosionWavC.wav");
		enemyExplosionClip[4] = generateClip("enemyExplosionWavB.wav");
		
		for (int i = 0; i < spaceShipExplosionClip.length; i++) {
			spaceShipExplosionClip[i] = generateClip("spaceShipExplosionWavC.wav");
		}	
		
		for (int i = 0; i < beepAClip.length; i++) {
			beepAClip[i] = generateClip("beepWavA.wav");
		}
		for (int i = 0; i < beepBClip.length; i++) {
			beepBClip[i] = generateClip("beepWavB.wav");
		}
		
		for (int i = 0; i < powerUpClip.length; i++) {
			powerUpClip[i] = generateClip("powerUpWav.wav");
		}
		
		for (int i = 0; i < powerDownClip.length; i++) {
			powerDownClip[i] = generateClip("powerDownWav.wav");
		}
	}
		
	private Clip generateClip(String string) {
		
		File localFile 			= new File(projdir + "/res/sounds/" + string);
		
		AudioInputStream localStream = null;
		try {
			localStream = AudioSystem.getAudioInputStream(localFile);
		} catch (UnsupportedAudioFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Clip localClip = null;
		
		AudioFormat localFormat = localStream.getFormat(); 
		DataLine.Info localInfo = new DataLine.Info(Clip.class,localFormat);
		try {
			localClip = (Clip)AudioSystem.getLine(localInfo);
		} catch (LineUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			
			localClip.open(localStream);
		} catch (LineUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		return localClip;
	}
	
	public void playSound(SoundType soundType)
	{
		Clip localClip[] = new Clip[5]; 
		
		if (soundType == SoundType.shoot) localClip = shootClip;
		else if (soundType == SoundType.powerUp)  localClip = powerUpClip;
		else if (soundType == SoundType.powerDown) localClip = powerDownClip;
		else if (soundType == SoundType.enemyExplosion) localClip = enemyExplosionClip;
		else if (soundType == SoundType.spaceShipExplosion) localClip = spaceShipExplosionClip;
		else if (soundType == SoundType.beepA) localClip = beepAClip;
		else if (soundType == SoundType.beepB) localClip = beepBClip;
			
		int localNumber = 0;
		if (soundType == SoundType.shoot) localNumber = shootNumber;
		else if (soundType == SoundType.powerUp)  localNumber = powerUpNumber;
		else if (soundType == SoundType.powerDown) localNumber = powerDownNumber;
		else if (soundType == SoundType.enemyExplosion) localNumber = enemyExplosionNumber;
		else if (soundType == SoundType.spaceShipExplosion) localNumber = spaceShipExplosionNumber;
		else if (soundType == SoundType.beepA) localNumber = beepANumber;
		else if (soundType == SoundType.beepB) localNumber = beepBNumber;

		
		int random = (int )(Math.random() * localNumber + 1);
		int cntr = 0;
		
		while (cntr != (localNumber-1) && localClip[(random + cntr) % localNumber].isRunning())
			cntr++;

		localClip[(random + cntr) % localNumber].stop();
		localClip[(random + cntr) % localNumber].setFramePosition(0);
		localClip[(random + cntr) % localNumber].start();
	}


	public void playSound(String soundType)
	{
		if      (soundType == "shoot") playSound(SoundType.shoot);
		else if (soundType == "powerUp") playSound(SoundType.powerUp);
		else if (soundType == "powerDown") playSound(SoundType.powerDown);
		else if (soundType == "enemyExplosion") playSound(SoundType.enemyExplosion);
		else if (soundType == "spaceShipExplosion") playSound(SoundType.spaceShipExplosion);
		else if (soundType == "beepA") playSound(SoundType.beepA);
		else if (soundType == "beepB") playSound(SoundType.beepB);
	}
}
