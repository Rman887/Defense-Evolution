package com.rman.engine.graphics;

import java.net.URL;

import com.rman.engine.Log;

public class SpriteSheet {
	
	private String name;
	private Texture spriteSheet;
	private int tileWidth;
	private int tileHeight;

	private int numColumns;
	private int numRows;

	private Texture[][] sprites;

	public SpriteSheet(String spriteSheetName, URL textureLocation, int tileWidth, int tileHeight) {
		this(new Texture(spriteSheetName, textureLocation), tileWidth, tileHeight);
	}
	
	public SpriteSheet(Texture spriteSheet, int tileWidth, int tileHeight) {
		this(spriteSheet.toString(), spriteSheet, tileWidth, tileHeight);
	}

	public SpriteSheet(String name, Texture spriteSheet, int tileWidth, int tileHeight) {
		this.name = name;
		this.spriteSheet = spriteSheet;
		this.tileWidth = tileWidth;
		this.tileHeight = tileHeight;
		Log.log("Creating subtextures for: " + spriteSheet);

		this.numColumns = (int) (spriteSheet.getWidth() / tileWidth);
		this.numRows = (int) (spriteSheet.getHeight() / tileHeight);
		this.sprites = new Texture[this.numRows][this.numColumns];
		for (int column = 0; column < spriteSheet.getWidth() / tileWidth; column++) {
			for (int row = 0; row < spriteSheet.getHeight() / tileHeight; row++) {
				sprites[row][column] = new Texture(this.spriteSheet
						+ String.format(" (Row: %s, Column: %s)", row, column), this.spriteSheet,
						column * this.tileWidth, row * this.tileHeight, this.tileWidth,
						this.tileHeight);
			}
		}
	}

	public int getNumColumns() {
		return this.numColumns;
	}

	public int getNumRows() {
		return this.numRows;
	}

	public int getNumSprites() {
		return getNumColumns() * getNumRows();
	}

	public Texture getSprite(int row, int column) {
		return sprites[row][column];
	}

	/**
	 * <pre>public void drawSprite({@link Renderer Renderer} renderer, int row, int column, int x, int y)</pre>
	 * 
	 * <p> Draws a sprite at the given row and column to the specified location. Note that (0, 0) is the top-left
	 * box. </p>
	 * 
	 * @param renderer - The <code>Renderer</code> used to draw the sprite
	 * @param row - The row of the sprite (in this <code>SpriteSheet</code>)
	 * @param column - The column of the sprite (in this <code>SpriteSheet</code>)
	 * @param x - The x coordinate to draw the texture
	 * @param y - The y coordinate to draw the texture
	 */
	public void drawSprite(Renderer renderer, int row, int column, int x, int y) {
		renderer.drawTexture(sprites[row][column], x, y);
	}

	/**
	 * <pre>public void drawSprite({@link Renderer Renderer} renderer, int row, int column, int x, int y, int
	 * width, int height)</pre>
	 * 
	 * <p> Draws a sprite at the given row and column to the specified location and size. Note that (0, 0) is the
	 * top-left box.
	 * 
	 * @param renderer - The <code>Renderer</code> used to draw the sprite
	 * @param row - The row of the sprite (in this <code>SpriteSheet</code>)
	 * @param column - The column of the sprite (in this <code>SpriteSheet</code>)
	 * @param x - The x coordinate to draw the texture at
	 * @param y - The y coordinate to draw the texture at
	 * @param width - The width to draw the texture
	 * @param height - The height to draw the texture
	 */
	public void drawSprite(Renderer renderer, int row, int column, int x, int y, int width,
			int height) {
		renderer.drawTexture(sprites[row][column], x, y, width, height);
	}

	public int getTileWidth() {
		return this.tileWidth;
	}

	public int getTileHeight() {
		return this.tileHeight;
	}

	public Texture asTexture() {
		return this.spriteSheet;
	}

	@Override
	public String toString() {
		return "SpriteSheet '" + this.name + "'";
	}
}
