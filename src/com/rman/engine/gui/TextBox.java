package com.rman.engine.gui;

import com.rman.engine.graphics.Renderer;
import com.rman.engine.graphics.Texture;

public class TextBox extends WindowComponent {
	
	public static final float DEFAULT_PADDING = 8.0f;
	
	private Texture backgroundTexture;
	private String text;
	private boolean active;
	private float padding;
	
	public TextBox(Window window, Texture backgroundTexture, String name, String text, float x, float y, float width, float height) {
		this(window, backgroundTexture, name, text, x, y, width, height, DEFAULT_PADDING);
	}
	
	public TextBox(Window window, Texture backgroundTexture, String name, String text, float x, float y, float width, float height, float padding) {
		super(window, name, x, y, width, height);
		this.text = text;
		this.active = false;
		this.padding = padding;
		this.backgroundTexture = backgroundTexture;
	}
	
	public boolean isActive() {
		return this.active;
	}
	
	public void setActive(boolean isActive) {
		this.active = isActive;
	}
	
	public String getText() {
		return this.text;
	}
	
	public void setText(String text) {
		this.text = text;
	}

	public void update() {
		
	}

	public void render(Renderer renderer) {
		if (isActive()) {
			if (this.backgroundTexture != null) {
				renderer.drawTexture(this.backgroundTexture, this.x - this.padding, this.y - this.padding, this.width + 2 * this.padding, this.height + 2 * this.padding);
			}
			renderer.drawText(this.text, this.x, this.y, this.width, this.height);
		}
	}
}
