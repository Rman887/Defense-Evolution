package com.rman.de.core;

import java.util.ArrayList;
import java.util.HashMap;

import com.rman.engine.Entity;
import com.rman.engine.graphics.Animation;
import com.rman.engine.graphics.Texture;

public abstract class Unit extends Entity {
	
	public static final int NUM_ENTITIES = 2;
	
	public static final int IDLE = 10;
	public static final int MOVING = 20;
	public static final int ATTACKING = 30;
	
	public static final int UP = 1;
	public static final int DOWN = 2;
	public static final int RIGHT = 3;
	public static final int LEFT = 4;
	
	protected static ArrayList<HashMap<Integer, Animation>> animations;
	
	static {
		animations = new ArrayList<HashMap<Integer, Animation>>(NUM_ENTITIES);
	}
	
	protected int currentState = IDLE;
	protected int currentDir = UP;
	
	protected int row;
	protected int column;
	protected int gridNumRows;
	protected int gridNumColumns;
	
	protected boolean moving;
	protected boolean canAttack = true;
	private long attackTime;
	
	protected boolean isDead = false;
	
	protected int id;
	
	protected int health;
	protected int baseHealth;
	protected int strength;
	protected int xp;
	protected int level;
	private boolean hasLeveledUp;

	public Unit(String name, Square square, int numRows, int numColumns) {
		super(name, square.x, square.y, square.width, square.height);
		this.row = square.row;
		this.column = square.column;
		this.gridNumRows = numRows;
		this.gridNumColumns = numColumns;
		
		this.id = getID();
		this.health = getBaseHealth();
		this.baseHealth = getBaseHealth();
		this.strength = getStrength();
		this.xp = 0;
		this.level = 1;
	}
	
	public Texture getCurrentTexture() {
		return animations.get(getID()).get(IDLE + UP).getCurrentTexture();
	}
	
	public void setDirection(int direction) {
		this.currentDir = direction;
	}
	
	public void setState(int state) {
		this.currentState = state;
	}
	
	public int[] getGridPos() {
		return new int[] {this.row, this.column};
	}
	
	public void setGridPosition(int row, int column) {
		this.row = row;
		this.column = column;
	}
	
	public void moveUp(float step) {
		if (this.row - step >= 0 && !this.moving) {
			this.row -= step;
			this.moving = true;
		}
	}
	
	public void moveDown(float step) {
		if (this.row < this.gridNumRows - step && !this.moving) {
			this.row += step;
			this.moving = true;
		}
	}
	
	public void moveRight(float step) {
		if (this.column < this.gridNumRows - step && !this.moving) {
			this.column += step;
			this.moving = true;
		}
	}
	
	public void moveLeft(float step) {
		if (this.column >= step && !this.moving) {
			this.column -= step;
			this.moving = true;
		}
	}
	
	public boolean isDead() {
		return this.isDead;
	}
	
	public void attack(Unit target) {
		target.damage(this.strength);
		if (target.getHealth() <= 0) {
			addXP(target.getXPValue());
		}
	}
	
	public boolean canAttack() {
		return this.canAttack;
	}
	
	public void hasJustAttacked() {
		this.attackTime = System.nanoTime();
		this.currentState = ATTACKING;
		this.canAttack = false;
	}
	
	public boolean isMoving() {
		return this.moving;
	}
	
	public int getHealth() {
		return this.health;
	}
	
	public void refillHealth() {
		this.health = getUpgradedBaseHealth();
	}
	
	public int getUpgradedBaseHealth() {
		return this.baseHealth;
	}
	
	public int getUpgradedStrength() {
		return this.strength;
	}
	
	public void upgradeBaseHealth(int amount) {
		this.baseHealth += amount;
	}
	
	public void upgradeStrength(int amount) {
		this.strength += amount;
	}
	
	public void addXP(int amount) {
		this.xp += amount;
		if (getXPTable() != null && this.level <= getXPTable().length && this.xp >= getXPTable()[this.level - 1]) {
			this.level++;
			this.hasLeveledUp = true;
		}
	}
	
	public boolean hasLeveledUp() {
		return this.hasLeveledUp;
	}
	
	public void finishLevelUp() {
		this.hasLeveledUp = false;
	}
	
	public int getCurrentXP() {
		return this.xp;
	}
	
	public int getLevel() {
		return this.level;
	}
	
	public abstract int getID();
	
	public abstract int getBaseHealth();
	public abstract double getCooldown();
	public abstract int getStrength();
	public abstract double getSpeed();
	
	public abstract int getGoldValue();
	public abstract int getXPValue();
	public abstract int[] getXPTable();
	
	public void damage(int amount) {
		this.health -= amount;
	}
	
	public void update(double delta) {
		if (!this.canAttack && System.nanoTime() - this.attackTime > getCooldown() * 1000000000L) {
			this.canAttack = true;
			this.currentState = IDLE;
		}
		
		if (this.health <= 0) {
			this.isDead = true;
			setActive(false);
		}
		
		if (this.moving) {
			if (this.x == this.column * Grid.SQUARE_SIZE && this.y == this.row * Grid.SQUARE_SIZE) {
				this.moving = false;
			} else {
				this.currentState = MOVING;
				if (this.x < this.column * Grid.SQUARE_SIZE) {
					if (this.currentDir == LEFT) {
						this.x = this.column * Grid.SQUARE_SIZE;
					}
					this.x += getSpeed();
				} else if (this.x > this.column * Grid.SQUARE_SIZE) {
					if (this.currentDir == RIGHT) {
						this.x = this.column * Grid.SQUARE_SIZE;
					}
					this.x -= getSpeed();
				}
				
				if (this.y < this.row * Grid.SQUARE_SIZE) {
					if (this.currentDir == UP) {
						this.y = this.row * Grid.SQUARE_SIZE;
					}
					this.y += getSpeed();
				} else if (this.y > this.row * Grid.SQUARE_SIZE) {
					if (this.currentDir == DOWN) {
						this.y = this.row * Grid.SQUARE_SIZE;
					}
					this.y -= getSpeed();
				}
			}
		} else {
			this.currentState = IDLE;
		}
		
		setAnimation(animations.get(getID()).get(this.currentState + this.currentDir));
	}
}
