package com.rman.engine.gui;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.StringTokenizer;

import com.rman.engine.Log;
import com.rman.engine.graphics.Color;
import com.rman.engine.graphics.Renderer;
import com.rman.engine.graphics.Texture;

/**
 * <pre>public class TrueTypeFont</pre>
 * 
 * <p> This class is used to import TrueType fonts and render them. </p>
 * 
 * <p> Note: This class contains code used in the Slick library's TrueTypeFont class. </p>
 * 
 * @author Arman
 */
public class TrueTypeFont {

	public static final int TAB_AMOUNT = 8;

	private String name;
	private boolean antiAlias;

	private Font font;

	private int fontSize = 0;

	private int fontHeight = 0;

	private Texture fontTexture;
	private CharLoc[] charLocs;
	private FontMetrics fontMetrics;

	private static class CharLoc {
		public int x, y, width, height;

		public CharLoc(int x, int y, int width, int height) {
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
		}
	}

	public TrueTypeFont(String name, Font font, boolean antiAlias) {
		this.name = name;
		this.antiAlias = antiAlias;
		this.charLocs = new CharLoc[256];
		this.font = font;
		this.fontSize = this.font.getSize() + 3;

		loadCharacters();
	}
	
	public TrueTypeFont(String name, URL location, int size) {
		this(name, location, size, true);
	}

	public TrueTypeFont(String name, URL location, int size, boolean antiAlias) {
		this.name = name;
		this.antiAlias = antiAlias;
		this.charLocs = new CharLoc[256];
		try {
			this.font = Font.createFont(Font.TRUETYPE_FONT, location.openStream());
			this.font = this.font.deriveFont((float) size);
			this.fontSize = this.font.getSize() + 3;
		} catch (Exception e) {
			Log.logError("Error loading font: " + this.name, e);
		}

		loadCharacters();
	}

	private void loadCharacters() {
		try {
			int textureSize = (int) Math.pow(2,
					Math.ceil(Math.log10(16 * this.fontSize) / Math.log10(2)));

			BufferedImage chars = new BufferedImage(textureSize, textureSize,
					BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = (Graphics2D) chars.getGraphics();

			g.setColor(new java.awt.Color(0, 0, 0, 1));
			g.fillRect(0, 0, textureSize, textureSize);

			int rowHeight = 0;
			int positionX = 0;
			int positionY = 0;

			for (int i = 0; i < 256; i++) {
				char ch = (char) i;
				BufferedImage fontImage = getCharImage(ch);

				if (positionX + fontImage.getWidth() >= textureSize) {
					positionX = 0;
					positionY += rowHeight;
					rowHeight = 0;
				}

				CharLoc charLoc = new CharLoc(positionX + 1, positionY, fontImage.getWidth() - 2,
						fontImage.getHeight());

				if (charLoc.height > this.fontHeight) {
					this.fontHeight = charLoc.height;
				}

				if (charLoc.height > rowHeight) {
					rowHeight = charLoc.height;
				}

				if (positionY > textureSize) {
					textureSize = (int) Math.pow(2,
							Math.ceil(Math.log10(positionY) / Math.log10(2)));
				}

				g.drawImage(fontImage, positionX, positionY, null);

				positionX += charLoc.width + 2;
				this.charLocs[i] = charLoc;
				fontImage = null;
			}
			this.fontTexture = new Texture(this.name, chars);
			g.dispose();
			chars = null;
		} catch (Exception e) {
			Log.logError("Error creating font: " + this.name, e);
		}
	}

	private BufferedImage getCharImage(char ch) {
		BufferedImage tempfontImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = (Graphics2D) tempfontImage.getGraphics();
		if (this.antiAlias) {
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		}
		g.setFont(this.font);
		this.fontMetrics = g.getFontMetrics();
		int charwidth = this.fontMetrics.charWidth(ch);

		if (charwidth <= 0) {
			charwidth = 1;
		}
		int charheight = fontMetrics.getHeight();
		if (charheight <= 0) {
			charheight = fontSize;
		}

		BufferedImage fontImage;
		fontImage = new BufferedImage(charwidth + 2, charheight, BufferedImage.TYPE_INT_ARGB);
		Graphics2D gt = (Graphics2D) fontImage.getGraphics();
		if (this.antiAlias) {
			gt.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		}
		gt.setFont(this.font);

		gt.setColor(java.awt.Color.WHITE);
		gt.drawString(String.valueOf(ch), 1, fontMetrics.getAscent());

		return fontImage;
	}
	
	public int drawChar(Renderer renderer, char c, Color color, float x, float y) {
		CharLoc charLoc = this.charLocs[c];

		if (charLoc != null) {
			renderer.drawTexture(this.fontTexture.getSubTexture(charLoc.x, charLoc.y,
					charLoc.width, charLoc.height), x, y);
			return charLoc.width;
		}
		return 0;
	}

	public void drawText(Renderer renderer, String text, Color color, float x, float y) {
		renderer.setFilter(color);
		
		int totalWidth = 0;
		int totalHeight = 0;
		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			
			if (c == '\n') {
				totalHeight += this.fontHeight;
				totalWidth = 0;
			} else if (c == '\t') {
				totalWidth += (this.charLocs[' '].width) * TAB_AMOUNT;
			} else {
				totalWidth += drawChar(renderer, text.charAt(i), color, x + totalWidth, y + totalHeight);
			}
		}

		renderer.setFilter(Color.WHITE);
	}
	
	public void drawText(Renderer renderer, String text, Color color, float x, float y, float width, float height) {
		renderer.setFilter(color);
		
		int totalWidth = 0;
		int totalHeight = 0;
		
		StringTokenizer stNewline = new StringTokenizer(text, "\n");
		
		while (stNewline.hasMoreTokens()) {
			StringTokenizer st = new StringTokenizer(stNewline.nextToken(), " ");
		
			while (st.hasMoreTokens()) {
				String word = st.nextToken();
				int wordWidth = 0;
				for (char c : word.toCharArray()) {
					wordWidth += this.charLocs[c].width;
				}
				
				if (totalHeight >= height) {
					break;
				} else if (totalWidth + wordWidth > width) {
					totalHeight += this.fontHeight;
					totalWidth = 0;
					if (totalHeight >= height - this.fontHeight) {
						break;
					}
				}
				
				for (char c : word.toCharArray()) {
					if (c == '\t') {
						totalWidth += (this.charLocs[' '].width) * TAB_AMOUNT;
					} else {
						totalWidth += drawChar(renderer, c, color, x + totalWidth, y + totalHeight);
					}
				}
				
				if (st.hasMoreTokens()) {
					totalWidth += drawChar(renderer, ' ', color, x + totalWidth, y + totalHeight);
				}
			}
			
			totalHeight += this.fontHeight;
			totalWidth = 0;
		}

		renderer.setFilter(Color.WHITE);
	}

	public int getWidth(String text) {
		int totalWidth = 0;
		for (int i = 0; i < text.length(); i++) {
			CharLoc charLoc = this.charLocs[text.charAt(i)];

			if (charLoc != null) {
				totalWidth += charLoc.width - 1;
			}
		}
		return totalWidth;
	}

	public int getHeight() {
		return this.fontHeight;
	}

	public int getSize() {
		return this.fontSize;
	}

	public void setSize(int newSize) {
		this.font = this.font.deriveFont((float) newSize);
		this.fontSize = this.font.getSize() + 3;
		loadCharacters();
	}

	public void destroy() {
		this.fontTexture.delete();
	}

	@Override
	public String toString() {
		return this.name;
	}
}