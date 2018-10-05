package com.rman.de.core;

import java.util.HashMap;

import org.lwjgl.util.Point;

import com.rman.engine.graphics.Animation;
import com.rman.engine.graphics.SpriteSheet;

public class Shooter extends Unit {
	
	public static final int ID = 0;
	
	private static final int BASE_HEALTH = 100;
	private static final double COOLDOWN = 2.0;
	private static final int STRENGTH = 10;
	private static final double SPEED = 1;
	private static final int[] XP_TABLE = new int[] {5, 10, 15, 20, 25, 30, 35, 40};
	
	private static int healthBoost = 0;
	private static int strengthBoost = 0;
	private static double speedBoost = 0;
	private static int goldValue = 100;
	private static String currentName = "Archer";
	
	public Shooter(Square square, int numRows, int numColumns) {
		super("Shooter", square, numRows, numColumns);
	}
	
	public void update(double delta) {
		super.update(delta);
	}
	
	public int getProjectile() {
		return Projectile.ARROW;
	}
	
	public int getID() {
		return ID;
	}
	
	public int getBaseHealth() {
		return getCurrentBaseHealth();
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
		return goldValue;
	}
	
	public int getXPValue() {
		return 0;
	}
	
	public int[] getXPTable() {
		return XP_TABLE;
	}
	
	public static int getCurrentBaseHealth() {
		return BASE_HEALTH + healthBoost;
	}
	public static int getCurrentStrength() {
		return STRENGTH + healthBoost;
	}
	public static double getCurrentSpeed() {
		return SPEED + healthBoost;
	}
	public static int getCurrentGoldValue() {
		return goldValue;
	}
	public static String getCurrentName() {
		return currentName;
	}
	
	public static void boostHealth(int amount) {
		healthBoost += amount;
	}
	public static void boostStrength(int amount) {
		strengthBoost += amount;
	}
	public static void boostSpeed(double amount) {
		speedBoost += amount;
	}
	public static void increaseGoldValue(int amount) {
		goldValue += amount;
	}
	public static void setCurrentName(String newName) {
		currentName = newName;
	}
	
	public static void loadAnimations(SpriteSheet spriteSheet) {
		HashMap<Integer, Animation> entityAnimations = new HashMap<Integer, Animation>();
		
		// Idle animations
		entityAnimations.put(IDLE + UP, new Animation("Shooter Idle Up", spriteSheet, 60, true, new Point[] {
				new Point(0, 0)
		}));
		entityAnimations.put(IDLE + DOWN, new Animation("Shooter Idle Down", spriteSheet, 60, true, new Point[] {
				new Point(1, 0)
		}));
		entityAnimations.put(IDLE + RIGHT, new Animation("Shooter Idle Right", spriteSheet, 60, true, new Point[] {
				new Point(2, 0)
		}));
		entityAnimations.put(IDLE + LEFT, new Animation("Shooter Idle Left", spriteSheet, 60, true, new Point[] {
				new Point(3, 0)
		}));
		
		// Walking entityAnimations
		entityAnimations.put(MOVING + UP, new Animation("Shooter Walking Up", spriteSheet, 60, true, new Point[] {
				new Point(4, 0), new Point(5, 0), new Point(6, 0), new Point(7, 0)
		}));
		entityAnimations.put(MOVING + DOWN, new Animation("Shooter Walking Down", spriteSheet, 60, true, new Point[] {
				new Point(4, 1), new Point(5, 1), new Point(6, 1), new Point(7, 1)
		}));
		entityAnimations.put(MOVING + RIGHT, new Animation("Shooter Walking Right", spriteSheet, 60, true, new Point[] {
				new Point(4, 2), new Point(5, 2), new Point(6, 2), new Point(7, 2)
		}));
		entityAnimations.put(MOVING + LEFT, new Animation("Shooter Walking Left", spriteSheet, 60, true, new Point[] {
				new Point(4, 3), new Point(5, 3), new Point(6, 3), new Point(7, 3)
		}));
		
		// Attacking entityAnimations
		entityAnimations.put(ATTACKING + UP, new Animation("Shooter Attacking Up", spriteSheet, 800, false, new Point[] {
				new Point(4, 4), new Point(5, 4), new Point(6, 4), new Point(7, 4)
		}));
		entityAnimations.put(ATTACKING + DOWN, new Animation("Shooter Attacking Down", spriteSheet, 800, false, new Point[] {
				new Point(4, 5), new Point(5, 5), new Point(6, 5), new Point(7, 5)
		}));
		entityAnimations.put(ATTACKING + RIGHT, new Animation("Shooter Attacking Right", spriteSheet, 800, false, new Point[] {
				new Point(4, 6), new Point(5, 6), new Point(6, 6), new Point(7, 6)
		}));
		entityAnimations.put(ATTACKING + LEFT, new Animation("Shooter Attacking Left", spriteSheet, 800, false, new Point[] {
				new Point(4, 7), new Point(5, 7), new Point(6, 7), new Point(7, 7)
		}));
		
		animations.add(ID, entityAnimations);
	}
}
