package space_battle.gui;
import java.awt.Color;
import java.awt.Font;

/**
 * FontType class stores the specification of a font.
 * Used to set the font parameters before a text is drawn to the screen.
 * @author fimi
 */
public class FontType {
	
		private Font font;
		private Color color;
		
		public FontType(String fontString, int size, Color color) {
			this.color = color;
			this.font = new Font(fontString, Font.BOLD, size);
		}

		/**
		 * Returns the Font field of the font, defined in the current FontType class
		 * @return the Font field of the font, defined in the current FontType class
		 * @author fimi
		 */
		public Font getFont() {
			return font;
		}

		/**
		 * Returns the color of the font, defined in the current FontType class
		 * @return the color of the font, defined in the current FontType class
		 * @author fimi
		 */
		public Color getColor() {
			return color;
		}	

}
