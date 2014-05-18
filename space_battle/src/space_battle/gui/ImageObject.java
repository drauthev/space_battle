package space_battle.gui;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * ImageObject class contains the BufferedImage of the image file, and the width and height of the images 
 * @author fimi
 */
public class ImageObject { 
	
	private BufferedImage bufferedImg;
	private int width;
	private int height;
	
	/**
	 * Constructor for ImageObject class
	 * @author fimi
	 * @param fname Picture file location and name
	 */
	public ImageObject(String fname) {
		try {
			bufferedImg = ImageIO.read(ImageIO.class.getResourceAsStream(fname));
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Invalid picture File!");
		}
		
		height = getBufferedImg().getHeight();
		width  = getBufferedImg().getWidth();
	}

	/**
	 * Return the height of the image
	 * @author fimi
	 * @return Height of the image
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * Return the width of the image
	 * @author fimi
	 * @return Width of the image
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Return the BufferedImage of the image
	 * @author fimi
	 * @return Width of the image
	 */
	public BufferedImage getBufferedImg() {
		return bufferedImg;
	}
}



