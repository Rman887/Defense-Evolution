package com.rman.de.core;

import java.util.Iterator;
import java.util.LinkedList;

import org.lwjgl.util.Point;

import com.rman.engine.Entity;
import com.rman.engine.graphics.Animation;
import com.rman.engine.graphics.Color;
import com.rman.engine.graphics.Renderer;
import com.rman.engine.graphics.SpriteSheet;
import com.rman.engine.graphics.Texture;

/**
 * <pre>public class Grid</pre>
 * 
 * <p> This class manages the different {@link Square Square} objects in the game, as well as the different
 * {@link Entity Entity} objects that they contain. </p>
 * 
 * @author Arman
 */
public class Grid {
	
	/**
	 * The size (width and height) of each square in this grid (in pixels).
	 */
	public static final int SQUARE_SIZE = 40;
	
	/**
	 * The {@link Texture Texture} for this grid's background.
	 */
	public Texture background;
	
	/**
	 * The shooter entity's {@link SpriteSheet SpriteSheet} that contains all of its animations.
	 */
	public SpriteSheet shooterSpriteSheet;
	/**
	 * The enemy entity's {@link SpriteSheet SpriteSheet} that contains all of its animations.
	 */
	public SpriteSheet enemySpriteSheet;
	/**
	 * A {@link Texture Texture} object that contains the textures for all projectiles.
	 */
	public Texture projectilesTexture;
	
	/**
	 * A {@link SpriteSheet SpriteSheet} of various effects.
	 */
	public SpriteSheet effectsSpriteSheet;
	
	/**
	 * The {@link Square Square} objects that make up this grid.
	 */
	private Square[][] squares;
	
	/**
	 * A reference to the main {@link DefenseEvolution DefenseEvolution} game.
	 */
	private DefenseEvolution de;
	
	/**
	 * A list of projectiles that are currently active on this grid.
	 */
	private LinkedList<Projectile> projectiles;
	
	/**
	 * The number of enemy units currently on this grid.
	 */
	private int enemyCount;
	
	private Animation upgradeEffect;
	
	/**
	 * <pre>public Grid({@link DefenseEvolution DefenseEvolution} de, int gridWidth, int gridHeight)</pre>
	 * 
	 * <p> Constructs a <code>Grid</code> object by initializing an array of {@link Square Square} objects
	 * and loading the textures for the grid's background and all game units. </p>
	 * 
	 * @param de - A reference to the main game
	 * @param gridWidth - The width of the grid (in pixels)
	 * @param gridHeight - The height of the grid (in pixels)
	 */
	public Grid(DefenseEvolution de, int gridWidth, int gridHeight) {
		this.de = de;
		initTextures();
		
		// Initialize the squares
		int rowSize = gridWidth / SQUARE_SIZE;
		int columnSize = gridHeight / SQUARE_SIZE;
		this.squares = new Square[columnSize][rowSize];
		for (int row = 0; row < columnSize; row++) {
			for (int column = 0; column < rowSize; column++) {
				this.squares[row][column] = new Square(null, column * SQUARE_SIZE, row * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE, row, column);
			}
		}
		
		this.projectiles = new LinkedList<Projectile>();
		
		Point[] upgradeEffectPoints = new Point[16];
		for (int i = 0; i < 8; i++) {
			upgradeEffectPoints[i] = new Point(0, i);
			upgradeEffectPoints[8 + i] = new Point(1, i);
		}
		this.upgradeEffect = new Animation("Unit Upgrade Effect", this.effectsSpriteSheet, 15.0, true, upgradeEffectPoints);
	}
	
	/**
	 * <pre>private void initTextures()</pre>
	 * 
	 * <p> Initializes the textures for the background and all game units. </p>
	 */
	private void initTextures() {
		this.background = new Texture("Grid Background", this.getClass().getResource("res/background.png"));
		
		Texture shooterTexture = new Texture("Shooter Texture", this.getClass().getResource("res/shooter.png"));
		this.shooterSpriteSheet = new SpriteSheet("Shooter SpriteSheet", shooterTexture, 32, 32);
		Shooter.loadAnimations(this.shooterSpriteSheet);
		
		Texture enemyTexture = new Texture("Enemy Texture", this.getClass().getResource("res/enemy.png"));
		this.enemySpriteSheet = new SpriteSheet("Enemy SpriteSheet", enemyTexture, 32, 32);
		Enemy.loadAnimations(this.enemySpriteSheet);
		
		this.projectilesTexture = new Texture("Projectiles Texture", this.getClass().getResource("res/projectiles.png"));
		Projectile.loadTextures(this.projectilesTexture);
		
		Texture effectsTexture = new Texture("Effects Texture", this.getClass().getResource("res/effects.png"));
		this.effectsSpriteSheet = new SpriteSheet("Effects SpriteSheet", effectsTexture, 32, 32);
	}
	
	/**
	 * <pre>public void addEntity({@link Entity Entity} entity, int row, int column)</pre>
	 * 
	 * <p> Adds an <code>Entity</code> to the specified square if there isn't one there already. </p>
	 * 
	 * @param entity - The <code>Entity</code> to add
	 * @param row - The square's row in the grid
	 * @param column - The square's column in the grid
	 */
	public void addEntity(Entity entity, int row, int column) {
		if (this.squares[row][column].entity == null) {
			setEntity(entity, row, column);
		}
	}
	
	/**
	 * <pre>public void setEntity(Entity entity, int row, int column)</pre>
	 * 
	 * <p> Sets the specified square's <code>Entity</code> to the new one. </p>
	 * 
	 * @param entity - The <code>Entity</code> to set
	 * @param row - The square's row in the grid
	 * @param column - The square's column in the grid
	 */
	public void setEntity(Entity entity, int row, int column) {
		this.squares[row][column].entity = entity;
	}
	
	/**
	 * <pre>public void removeEntity(int row, int column)</pre>
	 * 
	 * <p> Removes the entity at the specified square. </p>
	 * 
	 * @param row - The square's row in the grid
	 * @param column - The square's column in the grid
	 */
	public void removeEntity(int row, int column) {
		setEntity(null, row, column);
	}
	
	public void addUnit(int unitID, int row, int column) {
		Unit u = null;
		if (unitID == Shooter.ID) {
			u = new Shooter(this.squares[row][column], getNumRows(), getNumColumns());
		} else if (unitID == Enemy.ID) {
			u = new Enemy(this.squares[row][column], getNumRows(), getNumColumns());
		}
		
		int goldValue = u.getGoldValue();
		
		// If the player can afford this unit, then add it to the game and remove the proper amount of gold
		if (goldValue <= this.de.getGold()) {
			addEntity(u, row, column);
			if (!(u instanceof Enemy)) {
				this.de.removeGold(goldValue);
			}
		}
	}
	
	public void moveEntity(int fromRow, int fromColumn, int toRow, int toColumn) {
		this.squares[toRow][toColumn].entity = this.squares[fromRow][fromColumn].entity;
		this.squares[fromRow][fromColumn].entity = null;
	}
	
	public Entity getEntityAt(int row, int column) {
		if (row < 0 || row > this.squares.length || column < 0 || column > this.squares[0].length) {
			return null;
		}
		return this.squares[row][column].entity;
	}
	
	public int getNumRows() {
		return this.squares.length;
	}
	
	public int getNumColumns() {
		return this.squares[0].length;
	}
	
	public int getEnemyCount() {
		return this.enemyCount;
	}
	
	public void update(double delta) {
		this.enemyCount = 0;
		
		// Update entities
		for (int i = 0; i < this.squares.length; i++) {
			for (int j = 0; j < this.squares[i].length; j++) {
				Entity e = this.squares[i][j].entity;
				if (e != null) {
					if (!e.isActive()) {
						this.squares[i][j].entity = null;
					} else {
						e.update(delta);
					}
				}
			}
		}
		
		// Check unit positions
		for (int i = 0; i < this.squares.length; i++) {
			for (int j = 0; j < this.squares[i].length; j++) {
				Entity e = this.squares[i][j].entity;
				if (e != null && e instanceof Unit) {
					Unit u = (Unit) e;
					int[] unitPos = u.getGridPos();
					if (i != unitPos[0] || j != unitPos[1]) {
						moveEntity(i, j, unitPos[0], unitPos[1]);
					}
					
					if (u instanceof Enemy && i + 1 < getNumRows() && this.squares[i + 1][j].entity == null) {
						u.moveDown(1);
					}
				}
			}
		}
		
		// Post-movement update
		for (int i = 0; i < this.squares.length; i++) {
			for (int j = 0; j < this.squares[i].length; j++) {
				Square s = this.squares[i][j];
				Entity e = s.entity;
				if (e != null) {
					if (e instanceof Shooter) {
						Shooter shooter = (Shooter) e;
						
						// Make the shooter shoot at enemies
						if (shooter.canAttack()) {
							int proj = shooter.getProjectile();
							for (int k = 1; k <= i; k++) {
								if (this.squares[i - k][j].entity instanceof Enemy) {
									this.projectiles.add(new Projectile(proj, shooter.getX() + shooter.getWidth() / 2 - 2, shooter.getY(), shooter, (Enemy) this.squares[i - k][j].entity, Unit.UP));
									shooter.hasJustAttacked();
									break;
								}
							}
						}
					} else if (e instanceof Enemy) {
						Enemy enemy = (Enemy) e;
						
						if (!enemy.isMoving()) {
							if (i == this.squares.length - 1) {
								// Make the enemy disappear and damage building if it's in the last row
								this.de.damageBuilding(enemy.getStrength());
								this.squares[i][j].entity = null;
							} else if (this.squares[i + 1][j].entity instanceof Shooter && enemy.canAttack()) { 
								// Make the enemy attack a shooter if it's in front of the enemy
								enemy.attack((Shooter) this.squares[i + 1][j].entity);
								enemy.hasJustAttacked();
							}
						}
						this.enemyCount++;
					}
				}
			}
		}
		
		// Update the projectiles, and if they're not active, remove them
		Iterator<Projectile> projIter = this.projectiles.iterator();
		while (projIter.hasNext()) {
			Projectile proj = projIter.next();
			if (!proj.isActive()) {
				if (proj.getTarget().isDead()) {
					this.de.addGold(proj.getTarget().getGoldValue());
				}
				projIter.remove();
			} else {
				proj.update(delta);
			}
		}
	}
	
	/**
	 * <pre>public void render({@link Renderer Renderer} renderer, int numOutlined)</pre>
	 * 
	 * <p> Renders the entities on this grid. </p>
	 * 
	 * @param renderer - The <code>Renderer</code> to draw with
	 * @param numOutlined - The number of columns outlined (from the origin)
	 */
	public void render(Renderer renderer, int numOutlined) {
		renderer.drawTexture(this.background, 0, 0, SQUARE_SIZE * this.squares[0].length, SQUARE_SIZE * this.squares.length);
		Texture currentUpgradeTexture = this.upgradeEffect.update();
		
		int y = 0;
		for (Square[] row : squares) {
			int x = 0;
			int col = -(this.squares[0].length / 2);
			for (Square square : row) {
				if (col == 0) col++;
				if (y > 0 && Math.abs(col) <= numOutlined) {
					renderer.setColor(new Color(245, 245, 245));
					renderer.drawRect(x, y, SQUARE_SIZE, SQUARE_SIZE);
				}
				if (square.entity != null) {
					square.entity.render(renderer);
					if (square.entity instanceof Unit && ((Unit) square.entity).hasLeveledUp()) {
						renderer.drawTexture(currentUpgradeTexture, x, y, SQUARE_SIZE, SQUARE_SIZE);
					}
				}
				x += SQUARE_SIZE;
				col++;
			}
			y += SQUARE_SIZE;
		}
		
		for (Projectile proj : projectiles) {
			proj.render(renderer);
		}
	}
	
	public void destroy() {
		this.background.delete();
		this.shooterSpriteSheet.asTexture().delete();
		this.enemySpriteSheet.asTexture().delete();
		this.projectilesTexture.delete();
		this.effectsSpriteSheet.asTexture().delete();
	}
}
