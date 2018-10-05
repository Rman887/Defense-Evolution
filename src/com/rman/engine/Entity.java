package com.rman.engine;

import com.rman.engine.graphics.Animation;
import com.rman.engine.graphics.Color;
import com.rman.engine.graphics.Renderer;
import com.rman.engine.graphics.Texture;

/**
 * <pre>public abstract class Entity</pre>
 * 
 * <p> This abstract class represents a basic game entity. It stores information such as 
 * location, size, {@link Texture Texture}, {@link Animation Animation}, and name. It also
 * provides a basic {@link #render(Renderer) render} method. </p>
 * 
 * @author Arman
 */
public abstract class Entity {
	
	/**
	 * The name of this entity.
	 */
	protected String name;
	
	/**
	 * This entity's {@link Texture Texture} that will be used to render it. 
	 * If the texture's size does not match this entity's size, then the texture 
	 * will be scaled appropriately.
	 */
	private Texture texture;
	/**
	 * This entity's {@link Animation Animation} that will be used to render it.
	 * If the animation's texture sizes don't match this entity's size, then the
	 * textures will be scaled appropriately.
	 */
	private Animation animation;
	
	/**
	 * The x-coordinate of this entity.
	 */
	protected float x;
	/**
	 * The y-coordinate of this entity.
	 */
	protected float y;
	
	/**
	 * The width (in pixels) of this entity.
	 */
	protected float width;
	/**
	 * The height (in pixels) of this entity.
	 */
	protected float height;
	
	/**
	 * Indicates whether this entity is active. This entity
	 * is only rendered if it's active. Subclasses can use
	 * this information for other purposes too.
	 */
	private volatile boolean isActive = true;
	
	/**
	 * The amount to rotate this entity (in degrees)
	 */
	protected float rot;
	
	public Entity(String name, float x, float y, int width, int height) {
		this(name, null, x, y, width, height);
	}
	
	public Entity(String name, Texture texture, float x, float y) {
		this(name, texture, x, y, (int) texture.getWidth(), (int) texture.getHeight());
	}
	
	public Entity(String name, Texture texture, float x, float y, float width, float height) {
		this.name = name;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.texture = texture;
	}
	
	public Entity(String name, Animation animation, float x, float y) {
		this(name, animation.getSpriteSheet().asTexture(), x, y, animation.getSpriteSheet().getTileWidth(), animation.getSpriteSheet().getTileHeight());
		this.animation = animation;
	}

	public Texture getTexture() {
		return this.texture;
	}
	
	public void setTexture(Texture newTexture) {
		this.texture = newTexture;
	}
	
	public Animation getAnimation() {
		return this.animation;
	}
	
	public void setAnimation(Animation newAnimation) {
		this.animation = newAnimation;
	}

	public float getWidth() {
		return this.width;
	}

	public float getHeight() {
		return this.height;
	}

	public boolean isActive() {
		return this.isActive;
	}

	public void setActive(boolean active) {
		this.isActive = active;
	}

	public void setPosition(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}

	public float getX() {
		return this.x;
	}

	public float getY() {
		return this.y;
	}
	
	public void setRotation(float degrees) {
		this.rot = degrees;
	}
	
	public float getRotation() {
		return this.rot;
	}
	
	public void moveUp(float amount) {
		this.y -= amount;
	}
	
	public void moveDown(float amount) {
		this.y += amount;
	}
	
	public void moveLeft(float amount) {
		this.x -= amount;
	}
	
	public void moveRight(float amount) {
		this.x += amount;
	}

	public abstract void update(double delta);

	public void render(Renderer renderer) {
		if (isActive()) {
			if (this.animation != null) {
				renderer.drawAnimation(this.animation, this.x, this.y, this.width, this.height, this.rot);
			} else if (this.texture != null) {
				renderer.drawTexture(this.texture, this.x, this.y, this.width, this.height, this.rot);
			} else {
				renderer.setColor(Color.WHITE);
				renderer.fillRect(this.x, this.y, this.width, this.height, this.rot);
			}
		}
	}
	
	@Override
	public String toString() {
		return "Entity '" + this.name + "'";
	}
}
