package com.rman.engine.graphics;

import org.lwjgl.opengl.GL11;

public class Color {
	
	public static final Color WHITE = new Color(255, 255, 255);
	public static final Color RED = new Color(255, 0, 0);
	public static final Color GREEN = new Color(0, 255, 0);
	public static final Color BLUE = new Color(0, 0, 255);
	public static final Color BLACK = new Color(0, 0, 0);
	public static final Color YELLOW = new Color(255, 255, 0);
	public static final Color CYAN = new Color(0, 255, 255);
	public static final Color MAGENTA = new Color(255, 0, 255);
	public static final Color GRAY = new Color(128, 128, 128);
	public static final Color PURPLE = new Color(128, 0, 128);
	public static final Color ORANGE = new Color(255, 128, 0);
	
	public static Color getBrightenedColor(Color c, int amount) {
		Color newColor = new Color(c.red, c.blue, c.green, c.alpha);
		newColor.brighten(amount);
		return newColor;
	}
	
	public static Color getDarkenedColor(Color c, int amount) {
		Color newColor = new Color(c.red, c.blue, c.green, c.alpha);
		newColor.darken(amount);
		return newColor;
	}
	
	public int red;
	public int green;
	public int blue;
	public int alpha;
	
	public Color() {
		this(255, 255, 255);
	}
	
	public Color(int red, int green, int blue) {
		this(red, green, blue, 255);
	}
	
	public Color(int red, int green, int blue, int alpha) {
		this.red = red;
		this.green = green;
		this.blue = blue;
		this.alpha = alpha;
	}
	
	public void brighten(int amount) {
		this.red = Math.max(255, this.red + amount);
		this.blue = Math.max(255, this.blue + amount);
		this.green = Math.max(255, this.green + amount);
	}
	
	public void darken(int amount) {
		this.red = Math.max(0, this.red - amount);
		this.blue = Math.max(0, this.blue - amount);
		this.green = Math.max(0, this.green - amount);
	}

	public float getRed() {
		return (float) (this.red / 255.0f);
	}

	public float getGreen() {
		return (float) (this.green / 255.0f);
	}

	public float getBlue() {
		return (float) (this.blue / 255.0f);
	}

	public float getAlpha() {
		return (float) (this.alpha / 255.0f);
	}
	
	public void setAsCurrentOpenGLColor() {
		GL11.glColor4f(getRed(), getGreen(), getBlue(), getAlpha());
	}
}
