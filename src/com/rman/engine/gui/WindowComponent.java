package com.rman.engine.gui;

import com.rman.engine.graphics.Renderer;

public abstract class WindowComponent {
	
	protected Window window;
	
	protected String name;
	
	protected float x;
	protected float y;
	
	protected float width;
	protected float height;
	
	protected ComponentListener cl;
	
	public WindowComponent(Window window, float x, float y, float width, float height) {
		this(window, String.format("WindowComponent(%d, %d, %d, %d)", x, y, width, height), x, y, width, height);
	}
	
	public WindowComponent(Window window, String name, float x, float y, float width, float height) {
		this.window = window;
		this.name = name;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	public void addComponentListener(ComponentListener wl) {
		this.cl = wl;
	}
	
	public void removeComponentListener() {
		this.cl = null;
	}
	
	public void setName(String newName) {
		this.name = newName;
	}
	
	public float getX() {
		return this.x;
	}
	
	public void setX(int newX) {
		this.x = newX;
	}
	
	public float getY() {
		return this.y;
	}
	
	public void setY(int newY) {
		this.y = newY;
	}
	
	public void setLocation(int newX, int newY) {
		setX(newX);
		setY(newY);
	}
	
	public float getWidth() {
		return this.width;
	}
	
	public void setWidth(int newWidth) {
		this.width = newWidth;
	}
	
	public float getHeight() {
		return this.height;
	}
	
	public void setHeight(int newHeight) {
		this.height = newHeight;
	}
	
	public void setDimensions(int newWidth, int newHeight) {
		setWidth(newWidth);
		setHeight(newHeight);
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof WindowComponent) {
			WindowComponent windowComponent = (WindowComponent) o;
			return this.x == windowComponent.getX() && this.y == windowComponent.getY()
					&& this.width == windowComponent.getWidth() && this.height == windowComponent.getHeight();
		}
		return false;
	}
	
	@Override
	public String toString() {
		return this.name;
	}
	
	public abstract void update();
	public abstract void render(Renderer renderer);
}
