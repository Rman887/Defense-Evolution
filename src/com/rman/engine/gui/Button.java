package com.rman.engine.gui;

import org.lwjgl.input.Mouse;

import com.rman.engine.graphics.Color;
import com.rman.engine.graphics.Renderer;
import com.rman.engine.graphics.Sound;
import com.rman.engine.graphics.Texture;

public class Button extends WindowComponent {
	
	private static final int NORMAL = 10;
	private static final int MOUSE_OVER = 20;
	private static final int MOUSE_DOWN = 30;
	
	private static Sound buttonClickSound;
	
	private int currentState = NORMAL;
	
	private Texture texture;
	private Texture mouseOverTexture;
	private Texture mousePressedTexture;
	
	private TrueTypeFont font;
	private String text;
	private Color textColor;
	private boolean playSound;
	
	public Button(Window window, TrueTypeFont font, Color textColor, String text, float x, float y) {
		this(window, font, textColor, text, x, y, font.getWidth(text), font.getHeight());
	}
	
	public Button(Window window, Color textColor, String text, float x, float y, float width, float height) {
		this(window, null, textColor, text, x, y, width, height);
	}
	
	public Button(Window window, float x, float y, float width, float height) {
		this(window, x, y, width, height, null);
	}
	
	public Button(Window window, float x, float y, Texture texture) {
		this(window, x, y, texture.getWidth(), texture.getHeight(), texture);
	}
	
	public Button(Window window, float x, float y, float width, float height, Texture texture) {
		this(window, "", x, y, width, height, texture);
	}
	
	public Button(Window window, String text, float x, float y, Texture texture) {
		this(window, null, null, text, x, y, texture);
	}
	
	public Button(Window window, String text, float x, float y, float width, float height, Texture texture) {
		this(window, null, null, text, x, y, width, height, texture);
	}
	
	public Button(Window window, String text, float x, float y, float width, float height, Texture texture, Texture mouseOverTexture, Texture mousePressedTexture) {
		this(window, null, null, text, x, y, width, height, texture, mouseOverTexture, mousePressedTexture);
	}
	
	public Button(Window window, Color textColor, String text, float x, float y, Texture texture, Texture mouseOverTexture) {
		this(window, null, textColor, text, x, y, texture, mouseOverTexture);
	}
	
	public Button(Window window, Color textColor, String text, float x, float y, Texture texture, Texture mouseOverTexture, Texture mousePressedTexture) {
		this(window, null, textColor, text, x, y, texture, mouseOverTexture, mousePressedTexture);
	}
	
	public Button(Window window, TrueTypeFont font, Color textColor, String text, float x, float y, float width, float height) {
		this(window, font, textColor, text, x, y, width, height, null);
	}
	
	public Button(Window window, TrueTypeFont font, Color textColor, String text, float x, float y, Texture texture) {
		this(window, font, textColor, text, x, y, texture.getWidth(), texture.getHeight(), texture);
	}

	public Button(Window window, TrueTypeFont font, Color textColor, String text, float x, float y, float width, float height, Texture texture) {
		this(window, font, textColor, text, x, y, width, height, texture, null, null);
	}
	
	public Button(Window window, TrueTypeFont font, Color textColor, String text, float x, float y, Texture texture, Texture mouseOverTexture) {
		this(window, font, textColor, text, x, y, texture.getWidth(), texture.getHeight(), texture, mouseOverTexture, mouseOverTexture);
	}
	
	public Button(Window window, TrueTypeFont font, Color textColor, String text, float x, float y, Texture texture, Texture mouseOverTexture, Texture mousePressedTexture) {
		this(window, font, textColor, text, x, y, texture.getWidth(), texture.getHeight(), texture, mouseOverTexture, mousePressedTexture);
	}
	
	public Button(Window window, TrueTypeFont font, Color textColor, String text, float x, float y, float width, float height, Texture texture, Texture mouseOverTexture) {
		this(window, font, textColor, text, x, y, width, height, texture, mouseOverTexture, mouseOverTexture);
	}

	public Button(Window window, TrueTypeFont font, Color textColor, String text, float x, float y, float width, float height, Texture texture, Texture mouseOverTexture,
			Texture mousePressedTexture) {
		super(window, text, x, y, width, height);
		if (font == null) {
			this.font = Renderer.DEFAULT_FONT;
		} else {
			this.font = font;
		}
		this.text = text;
		this.textColor = textColor;
		this.texture = texture;
		this.mouseOverTexture = mouseOverTexture;
		this.mousePressedTexture = mousePressedTexture;
		this.playSound = true;
	}

	public boolean isMouseOver() {
		return 		this.window.getMouseX() > this.x 
				&& 	this.window.getMouseX() < (this.x + this.width) 
				&& 	this.window.getMouseY() > this.y 
				&& 	this.window.getMouseY() < this.y + this.height;
	}
	
	@Override
	public void update() {
		if (isMouseOver() && this.currentState != MOUSE_DOWN && !Mouse.isButtonDown(0)) {
			this.currentState = MOUSE_OVER;
			if (this.cl != null) {
				this.cl.onMouseOver(this);
			}
		}
		
		if (this.currentState == MOUSE_OVER) {
			if (Mouse.isButtonDown(0)) {
				this.currentState = MOUSE_DOWN;
				if (this.playSound && buttonClickSound != null) {
					buttonClickSound.play();
				}
			} else if (!isMouseOver()) {
				this.currentState = NORMAL;
			}
		}
		
		if (this.currentState == MOUSE_DOWN) {
			if (!isMouseOver()) {
				this.currentState = NORMAL;
			} else if (!Mouse.isButtonDown(0)) {
				this.currentState = NORMAL;
				if (this.cl != null) {
					this.cl.onPress(this);
				}
			}
		}
	}
	
	public void setPlaySound(boolean playSound) {
		this.playSound = playSound;
	}
	
	public void setColor(Color newColor) {
		this.textColor = newColor;
	}
	
	public void setText(String newText) {
		this.text = newText;
	}
	
	public void setTexture(Texture texture) {
		this.texture = texture;
	}
	
	public void setTexture(Texture texture, Texture mouseOverTexture, Texture mousePressedTexture) {
		this.texture = texture;
		this.mouseOverTexture = mouseOverTexture;
		this.mousePressedTexture = mousePressedTexture;
	}
	
	public void render(Renderer renderer) {
		if (this.currentState == MOUSE_DOWN && this.mousePressedTexture != null) {
			renderer.drawTexture(this.mousePressedTexture, this.x, this.y, this.width, this.height);
		} else if (this.currentState == MOUSE_OVER && this.mouseOverTexture != null) {
			renderer.drawTexture(this.mouseOverTexture, this.x, this.y, this.width, this.height);
		} else if (this.texture != null) {
			renderer.drawTexture(this.texture, this.x, this.y, this.width, this.height);
		}
		
		if (this.textColor != null) {
			if (this.currentState == MOUSE_DOWN && this.mousePressedTexture == null) {
				renderer.setColor(Color.getDarkenedColor(this.textColor, 96));
			} else {
				renderer.setColor(this.textColor);
			}
		}
		if (this.font != null) {
			renderer.setFont(this.font);
			renderer.drawText(this.text, this.x + (this.width - this.font.getWidth(this.text)) / 2, this.y + (this.height - this.font.getHeight()) / 2);
		}
		update();
	}
	
	public void renderNoUpdate(Renderer renderer) {
		if (this.texture != null) {
			renderer.drawTexture(this.texture, this.x, this.y, this.width, this.height);
		} else {
			renderer.setColor(new Color(156, 156, 156));
			renderer.fillRect(this.x, this.y, this.width, this.height);
			renderer.setColor(new Color(106, 106, 106));
			renderer.drawRect(this.x, this.y, this.width, this.height);
		}
		
		renderer.setColor(this.textColor);
		renderer.drawText(this.text, this.x + (this.width - this.font.getWidth(this.text)) / 2, this.y + (this.height - this.font.getHeight()) / 2);
	}
	
	public static void setButtonSound(Sound buttonSound) {
		buttonClickSound = buttonSound;
	}
}
