package gui;
import java.awt.Color;
import java.awt.Font;


public class FontType {
	
		private Font font;
		private Color color;
		
		public FontType(String fontString, int size, Color color) {
			this.color = color;
			this.font = new Font(fontString, Font.BOLD, size);
		}

		public Font getFont() {
			return font;
		}

		public Color getColor() {
			return color;
		}	

}
