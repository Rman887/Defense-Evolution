package com.rman.de.core;

import java.util.HashMap;

import org.lwjgl.util.Point;

import com.rman.engine.graphics.Animation;
import com.rman.engine.graphics.SpriteSheet;

public class Enemy extends Unit {
	public static final int ID = 1;
	
	private static final int BASE_HEALTH = 50;
	private static final double COOLDOWN = 1.0;
	private static final int STRENGTH = 5;
	private static final double SPEED = 0.5;
	private static final int GOLD_VALUE = 20;
	private static final int XP_VALUE = 1;
	
	private static int healthBoost = 0;
	private static int strengthBoost = 0;
	private static double speedBoost = 0;
	public static void boostHealth(int amount) {
		healthBoost += amount;
	}
	public static void boostStrength(int amount) {
		strengthBoost += amount;
	}
	public static void boostSpeed(double amount) {
		speedBoost += amount;
	}
	
	public Enemy(Square square, int numRows, int numColumns) {
		super("Enemy", square, numRows, numColumns);
		this.currentDir = DOWN;
	}
	
	public void update(double delta) {
		super.update(delta);
	}
	
	public int getID() {
		return ID;
	}
	
	public int getBaseHealth() {
		return BASE_HEALTH + healthBoost;
	}

	public double getCooldown() {
		return COOLDOWN;
	}

	public int getStrength() {
		return STRENGTH + strengthBoost;
	}
	
	public double getSpeed() {
		return SPEED + speedBoost;
	}
	
	public int getGoldValue() {
		return GOLD_VALUE;
	}
	
	public int getXPValue() {
		return XP_VALUE;
	}
	
	public int[] getXPTable() {
		return null;
	}
	
	public static void loadAnimations(SpriteSheet spriteSheet) {
		HashMap<Integer, Animation> entityAnimations = new HashMap<Integer, Animation>();
		
		// Idle animations
		entityAnimations.put(IDLE + UP, new Animation("Enemy Idle Up", spriteSheet, 60, true, new Point[] {
				new Point(0, 0)
		}));
		entityAnimations.put(IDLE + DOWN, new Animation("Enemy Idle Down", spriteSheet, 60, true, new Point[] {
				new Point(1, 0)
		}));
		entityAnimations.put(IDLE + RIGHT, new Animation("Enemy Idle Right", spriteSheet, 60, true, new Point[] {
				new Point(2, 0)
		}));
		entityAnimations.put(IDLE + LEFT, new Animation("Enemy Idle Left", spriteSheet, 60, true, new Point[] {
				new Point(3, 0)
		}));
		
		// Walking entityAnimations
		entityAnimations.put(MOVING + UP, new Animation("Enemy Walking Up", spriteSheet, 60, true, new Point[] {
				new Point(4, 0), new Point(5, 0), new Point(6, 0), new Point(7, 0)
		}));
		entityAnimations.put(MOVING + DOWN, new Animation("Enemy Walking Down", spriteSheet, 60, true, new Point[] {
				new Point(4, 1), new Point(5, 1), new Point(6, 1), new Point(7, 1)
		}));
		entityAnimations.put(MOVING + RIGHT, new Animation("Enemy Walking Right", spriteSheet, 60, true, new Point[] {
				new Point(4, 2), new Point(5, 2), new Point(6, 2), new Point(7, 2)
		}));
		entityAnimations.put(MOVING + LEFT, new Animation("Enemy Walking Left", spriteSheet, 60, true, new Point[] {
				new Point(4, 3), new Point(5, 3), new Point(6, 3), new Point(7, 3)
		}));
		
		// Attacking entityAnimations
		entityAnimations.put(ATTACKING + UP, new Animation("Enemy Attacking Up", spriteSheet, 60, false, new Point[] {
				new Point(4, 4), new Point(5, 4), new Point(6, 4), new Point(7, 4)
		}));
		entityAnimations.put(ATTACKING + DOWN, new Animation("Enemy Attacking Down", spriteSheet, 60, false, new Point[] {
				new Point(4, 5), new Point(5, 5), new Point(6, 5), new Point(7, 5)
		}));
		entityAnimations.put(ATTACKING + RIGHT, new Animation("Enemy Attacking Right", spriteSheet, 60, false, new Point[] {
				new Point(4, 6), new Point(5, 6), new Point(6, 6), new Point(7, 6)
		}));
		entityAnimations.put(ATTACKING + LEFT, new Animation("Enemy Attacking Left", spriteSheet, 60, false, new Point[] {
				new Point(4, 7), new Point(5, 7), new Point(6, 7), new Point(7, 7)
		}));
		
		animations.add(ID, entityAnimations);
	}
}
