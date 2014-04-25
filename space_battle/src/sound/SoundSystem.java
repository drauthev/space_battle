package sound;

import interfaces.SoundSystemForClient;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.*;

// Java Sound can play sound formats with 8- or 16-bit samples with sample rates from 8000Hz to 48,000Hz. Also, it can play either mono or stereo sound. 
public class SoundSystem implements SoundSystemForClient {
	
	// Clips
	private Clip shootClip1;
	private Clip shootClip2;
	private Clip shootClip3;
	private Clip shootClip4;
	private Clip shootClip5;
	
	private Clip enemyExplosionClip1;
	private Clip enemyExplosionClip2;
	private Clip enemyExplosionClip3;
	private Clip enemyExplosionClip4;
	private Clip enemyExplosionClip5;
	
	private Clip spaceShipExplosionClip1;
	private Clip spaceShipExplosionClip2;
	
	private Clip beepClipA1;
	private Clip beepClipA2;
	private Clip beepClipA3;
	
	private Clip beepClipB1;
	private Clip beepClipB2;
	private Clip beepClipB3;
	

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
			
			beepClipA1 = (Clip)AudioSystem.getLine(beepInfoA);
			beepClipA2 = (Clip)AudioSystem.getLine(beepInfoA);
			beepClipA3 = (Clip)AudioSystem.getLine(beepInfoA);
			
			beepClipB1 = (Clip)AudioSystem.getLine(beepInfoB);
			beepClipB2 = (Clip)AudioSystem.getLine(beepInfoB);
			beepClipB3 = (Clip)AudioSystem.getLine(beepInfoB);
	

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
			
			beepClipA1.open(beepStreamA);
			beepClipA2.open(beepStreamA);
			beepClipA3.open(beepStreamA);
			
			beepClipB1.open(beepStreamB);
			beepClipB2.open(beepStreamB);
			beepClipB3.open(beepStreamB);
			
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


	@Override
	public void playSound(String sound) {
		// TODO Auto-generated method stub
		
	}

//    public void Play(String soundName)
//    {
//		if (soundName == "shoot") //isActive, isRunning
//		{
//			if (shootCounter == 1)
//			{
//				clip1.stop();
//				clip1.setFramePosition(0);
//				clip1.start();
//			}
//			...
//		}
//		else if (soundName == "beep2")
//		{
//			clip2.stop();
//			clip2.setFramePosition(0);
//			clip2.start();
//		}
//    }
//}	
//	
//	
}
	
		// shoot
		// enemyExplosion
		// spaceShipExplosion
		// beepA
		// beepB