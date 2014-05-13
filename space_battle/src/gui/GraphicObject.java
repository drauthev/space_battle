package gui;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class GraphicObject { 
	
	private BufferedImage bufferedImg;
	private int width;
	private int height;
	
	public GraphicObject(File imgFile) {
		try {
			bufferedImg = ImageIO.read(imgFile);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Invalid picture File!");
		}
		
		height = getBufferedImg().getHeight();
		width  = getBufferedImg().getWidth();
	}

	public int getHeight() {
		return height;
	}

	public int getWidth() {
		return width;
	}

	public BufferedImage getBufferedImg() {
		return bufferedImg;
	}
}



