package com.rman.de.core;

import com.rman.engine.Entity;
import com.rman.engine.graphics.Texture;

public class Projectile extends Entity {
	
	public static final int NUM_KINDS = 1;
	
	public static final int ARROW = 0;
	
	private static Texture[] textures;
	
	public static void loadTextures(Texture texture) {
		textures = new Texture[NUM_KINDS];
		
		textures[ARROW] = texture.getSubTexture(14, 5, 4, 21);
	}
	
	private Unit shooter;
	private Unit target;
	private boolean hasHit = false;
	
	private int direction;
	private int type;
	
	private int speed = 30;
	
	public Projectile(int type, Unit shooter, Unit target, int dir) {
		this(type, shooter.getX(), shooter.getY(), shooter, target, dir);
	}
	
	public Projectile(int type, float x, float y, Unit shooter, Unit target, int dir) {
		super("Entity 'Projectile'", textures[type], x, y);
		this.shooter = shooter;
		this.target = target;
		this.direction = dir;
		this.type = type;
	}
	
	public Unit getShooter() {
		return this.shooter;
	}
	
	public Unit getTarget() {
		return this.target;
	}

	public void update(double delta) {
		if (this.direction == Unit.UP) {
			this.y -= this.speed * delta;
			if (this.y <= this.target.getY() + this.target.getHeight()) {
				this.hasHit = true;
			}
		} else if (this.direction == Unit.DOWN) {
			this.y += this.speed * delta;
			if (this.y + this.height >= this.target.getY()) {
				this.hasHit = true;
			}
		} else if (this.direction == Unit.LEFT) {
			this.x -= this.speed * delta;
			if (this.x <= this.target.getX() + this.target.getWidth()) {
				this.hasHit = true;
			}
		} else if (this.direction == Unit.RIGHT) {
			this.x += this.speed * delta;
			if (this.x + this.width > this.target.getX()) {
				this.hasHit = true;
			}
		}
		
		if (this.hasHit || this.target.isDead()) {
			setActive(false);
			this.shooter.attack(this.target);
		}
	}
	
	public String toString() {
		return super.toString() + " (Type: " + type + ", Dir: " + this.direction + ", HasHit: " + this.hasHit + ", Shooter: " + this.shooter + ", Target: " + this.target + ")";
	}
}
