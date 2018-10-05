package com.rman.de.core;

import java.awt.Font;

import org.lwjgl.input.Keyboard;

import com.rman.engine.Entity;
import com.rman.engine.Game;
import com.rman.engine.Log;
import com.rman.engine.graphics.Color;
import com.rman.engine.graphics.Sound;
import com.rman.engine.graphics.Texture;
import com.rman.engine.gui.Button;
import com.rman.engine.gui.ComponentListener;
import com.rman.engine.gui.TextBox;
import com.rman.engine.gui.TrueTypeFont;
import com.rman.engine.gui.WindowComponent;

public class DefenseEvolution extends Game {
	public static final int INFO_PANE_WIDTH = 80;
	public static final int INFO_PANE_HEIGHT = 80;
	
	// Menu variables
	private static final int MAIN_MENU = 0;
	private static final int INSTRUCTIONS_MENU = 110;
	private static final int CREDITS_MENU = 2;
	private static final int EXIT_MENU = 3;
	private static final int PLAY = 4;
	private static final int PAUSE_MENU = 5;
	private static final int OPTIONS_MENU = 6;

	private Texture infoPaneBackground;
	
	private int currentMenu;

	/* ------------------------------ UI Variables ------------------------------*/
	// Button Format: <menu><name>Button
	
	private Button playButton;
	private Button instructionsButton;
	private Button creditsButton;
	private Button exitButton;
	
	private Button[] instructionArrows;

	private Button[][] playGridButtons;
	private Button exitCloseButton;

	private Button mainMenuButton;

	//private Button playPauseButton;
	private Button playResumeButton;
	private Button playOptionsButton;
	private Button playMainMenuButton;
	private Button playStartWaveButton;
	
	private Button optionsDoneButton;
	
	private Button infoUnitShooterButton;
	private Button infoUnitShooterUpgradeHealthButton;
	private Button infoUnitShooterUpgradeStrengthButton;
	
	private TextBox infoUnitShooterUpgradeHealthMouseOverBox;
	private TextBox infoUnitShooterUpgradeStrengthMouseOverBox;
	
	private Texture interfaceTextures;
	private Texture[] buttonTextures;
	private Texture[] infoPaneTextures;
	private Texture[] mouseOverTextures; 
	
	private TextBox infoUnitShooterMouseOverBox;
	
	private TrueTypeFont sanserif8;
	private TrueTypeFont sanserif12;
	private TrueTypeFont sanserif16;
	private TrueTypeFont sanserif26;
	private TrueTypeFont goudy38;
	private TrueTypeFont goudy96;
	
	private Sound backgroundMusic;
	private Sound buttonClickSound;
	
	/* ------------------------------ Game Variables ------------------------------*/
	/**
	 * Contains the waves in which the number of columns changes. This corresponds to <code>COLUMNS_OUTLINED</code>.
	 */
	public static final int[] COLUMNS_OUTLINED_AT = {5, 10, 15, 20, 25, 30};
	/**
	 * Contains the amount of columns allowed (how much right and left) at each wave specified in <code>COLUMNS_OUTLINED_AT</code>.
	 */
	public static final int[] COLUMNS_OUTLINED = 	{2, 3, 	5, 	6, 	7, 	8};
	
	/**
	 * The amount of gold that the player starts out with.
	 */
	public static final int INIT_GOLD = 500;
	
	/**
	 * The amount of health that the building starts out with.
	 */
	public static final int INIT_BUILDING_HEALTH = 100;
	
	/**
	 * The length (width) of the health bar (in pixels).
	 */
	private static final int HEALTH_BAR_LENGTH = 65;
	/**
	 * The height of the health bar (in pixels).
	 */
	private static final int HEALTH_BAR_HEIGHT = 20;
	
	/**
	 * The current number of allowed columns. See {@link COLUMNS_OUTLINED_AT COLUMNS_OUTLINED_AT} and
	 * {@link COLUMNS_OUTLINED COLUMNS_OUTLINED} for more details.
	 */
	private int allowedColumns;
	
	/**
	 * Indicates whether more columns have been allowed on the current wave, otherwise false.
	 */
	private boolean justSwitchedColumns;
	
	/**
	 * The current amount of gold that the player has.
	 */
	private int gold;
	/**
	 * The current wave that the player is on.
	 */
	private int wave;
	
	/**
	 * The time that the current wave started. If there is no wave going on, this is set to -1.
	 * Otherwise, it is set to the time a wave started using {@link System#nanoTime() System.nanoTime()}.
	 */
	private long waveStartTime;
	/**
	 * The time that the pause menu was most recently entered.
	 */
	private long pauseStartTime;
	/**
	 * A {@link WaveInfo WaveInfo} object that holds information about the current wave.
	 */
	private WaveInfo currentWaveInfo;
	
	/**
	 * The unit ID of the unit that the player wants to buy (which unit the player selected in the buying sections).
	 */
	private int selectedBuyUnit;
	/**
	 * The position of the unit that the player selected on the grid.
	 * The first element is the x-coordinate and the second element is the y-coordinate.
	 * If a unit is not currently selected, both elements are set to -1.
	 */
	private int[] selectedUnit;
	
	/**
	 * The amount of health that the building has.
	 */
	private int buildingHealth;
	
	/**
	 * The {@link Grid Grid} object used for this game.
	 */
	private Grid grid;
	
	public void init() {
		this.grid = new Grid(this, this.window.getWidth() - INFO_PANE_WIDTH, this.window.getHeight() - INFO_PANE_HEIGHT);
		
		this.infoPaneBackground = new Texture("Info Pane Background", this.getClass().getResource("res/infoBackground.png"), 15, 20);
		
		this.buttonClickSound = new Sound(this.getClass().getResource("res/button_click.wav"));
		this.buttonClickSound.setLoop(false);
		Button.setButtonSound(this.buttonClickSound);
		
		this.backgroundMusic = new Sound(this.getClass().getResource("res/The Batman Theme Song.wav"));
		this.backgroundMusic.setLoop(true);
		this.backgroundMusic.play();
		
		loadInterface();
		updateUnitInfo();
		
		this.allowedColumns = COLUMNS_OUTLINED[0];
		
		this.gold = INIT_GOLD;
		this.wave = 0;
		this.waveStartTime = -1L;
		this.selectedBuyUnit = -1;
		this.selectedUnit = new int[] {-1, -1};
		this.buildingHealth = INIT_BUILDING_HEALTH; 
		
		try {
			WaveInfoReader.loadWaveInfoFile(this, this.getClass().getResource("res/waveinfo.de"));
		} catch (Exception e) {
			Log.logError("Error loading wave info:", e);
		}
	}
	
	/**
	 * <pre>private void loadInterface()</pre>
	 * 
	 * <p> Loads all resources such as textures, buttons, or fonts. </p>
	 */
	private void loadInterface() {
		this.sanserif8 = new TrueTypeFont("SanSerif (8)", new Font("SanSerif", Font.PLAIN, 8), true);
		this.sanserif12 = new TrueTypeFont("Sanserif (12)", new Font("SanSerif", Font.PLAIN, 12), true);
		this.sanserif16 = new TrueTypeFont("SanSerif (16)", new Font("SansSerif", Font.PLAIN, 16), true);
		this.sanserif26 = new TrueTypeFont("SanSerif (26)", new Font("SansSerif", Font.PLAIN, 26), true);
		this.goudy38 = new TrueTypeFont("Goudy Mediaeval Font (38)", this.getClass().getResource("res/Goudy Mediaeval Regular.ttf"), 38);
		this.goudy96 = new TrueTypeFont("Goudy Mediaeval Font (96)", this.getClass().getResource("res/Goudy Mediaeval Regular.ttf"), 96);
		
		this.interfaceTextures = new Texture("Interface Textures", this.getClass().getResource("res/interface.png"));
		this.buttonTextures = new Texture[] {
				this.interfaceTextures.getSubTexture(0, 0, 200, 50), this.interfaceTextures.getSubTexture(0, 50, 200, 50), this.interfaceTextures.getSubTexture(0, 100, 200, 50),
				this.interfaceTextures.getSubTexture(0, 151, 35, 24), this.interfaceTextures.getSubTexture(36, 151, 34, 24), this.interfaceTextures.getSubTexture(71, 151, 34, 24), 
				this.interfaceTextures.getSubTexture(0, 176, 34, 24), this.interfaceTextures.getSubTexture(35, 176, 34, 24), this.interfaceTextures.getSubTexture(70, 176, 34, 24), 
				this.interfaceTextures.getSubTexture(200, 32, 32, 32), 
				this.interfaceTextures.getSubTexture(0, 200, 32, 32), this.interfaceTextures.getSubTexture(32, 200, 32, 32), this.interfaceTextures.getSubTexture(64, 200, 32, 32),
				this.interfaceTextures.getSubTexture(232, 32, 24, 24), this.interfaceTextures.getSubTexture(232, 56, 24, 24), this.interfaceTextures.getSubTexture(232, 80, 24, 24)
		};
		
		this.mouseOverTextures = new Texture[] {
				this.interfaceTextures.getSubTexture(105, 150, 100, 50)
		};
		
		this.infoPaneTextures = new Texture[] {
			this.interfaceTextures.getSubTexture(200, 64, 32, 32),
			this.interfaceTextures.getSubTexture(200, 96, 32, 32)
		};
		
		int centerX = (int) (this.window.getWidth() - this.buttonTextures[0].getWidth()) / 2;
		
		// Main menu buttons
		this.playButton = new Button(this.window, this.goudy38, Color.BLACK, "Play", centerX, (int) (this.window.getHeight() - this.buttonTextures[0].getHeight()) / 2, this.buttonTextures[0], this.buttonTextures[1], this.buttonTextures[2]);
		this.playButton.addComponentListener(getMenuListener(PLAY));
		this.instructionsButton = new Button(this.window, this.goudy38, Color.BLACK, "Instructions", centerX, this.playButton.getY() + 4 * (int) (this.playButton.getHeight()) / 3, this.buttonTextures[0], this.buttonTextures[1], this.buttonTextures[2]);
		this.instructionsButton.addComponentListener(getMenuListener(INSTRUCTIONS_MENU));
		this.creditsButton = new Button(this.window, this.goudy38, Color.BLACK, "Credits", centerX, this.instructionsButton.getY() + 4 * (int) (this.instructionsButton.getHeight()) / 3, this.buttonTextures[0], this.buttonTextures[1], this.buttonTextures[2]);
		this.creditsButton.addComponentListener(getMenuListener(CREDITS_MENU));
		this.exitButton = new Button(this.window, this.goudy38, Color.BLACK, "Exit", centerX, this.creditsButton.getY() + 4 * (int) (this.creditsButton.getHeight()) / 3, this.buttonTextures[0], this.buttonTextures[1], this.buttonTextures[2]);
		this.exitButton.addComponentListener(getMenuListener(EXIT_MENU));
		
		// Instructions arrows
		this.instructionArrows = new Button[] {
				new Button(this.window, Color.BLACK, "", this.window.getWidth() - (int) (4 * this.buttonTextures[6].getWidth() / 3), this.window.getHeight() - (int) (4 * this.buttonTextures[6].getHeight() / 3), this.buttonTextures[6], this.buttonTextures[7], this.buttonTextures[8]),
				new Button(this.window, Color.BLACK, "", 12, this.window.getHeight() - (int) (4 * this.buttonTextures[6].getHeight() / 3), this.buttonTextures[3], this.buttonTextures[4], this.buttonTextures[5])
		};
		this.instructionArrows[0].addComponentListener(getInstructionArrowListener(1));
		this.instructionArrows[1].addComponentListener(getInstructionArrowListener(-1));
		
		// Close button in exit menu
		this.exitCloseButton = new Button(this.window, this.goudy38, Color.BLACK, "Close", centerX, 534, buttonTextures[0], buttonTextures[1], buttonTextures[2]);
		this.exitCloseButton.addComponentListener(new ComponentListener() { public void onMouseOver(WindowComponent wc) {}; public void onPress(WindowComponent wc) { running = false; }});
		
		// Main menu button (from various menus)
		this.mainMenuButton = new Button(this.window, this.goudy38, Color.BLACK, "Main Menu", centerX, 534, this.buttonTextures[0], this.buttonTextures[1], this.buttonTextures[2]);
		this.mainMenuButton.addComponentListener(getMenuListener(MAIN_MENU));
		
		// Pause menu buttons
		this.playResumeButton = new Button(this.window, this.goudy38, Color.BLACK, "Resume", centerX, 2 * (this.window.getHeight() - 245) / 3, this.buttonTextures[0], this.buttonTextures[1], this.buttonTextures[2]);
		this.playResumeButton.addComponentListener(getPauseMenuListener(PLAY));
		this.playOptionsButton = new Button(this.window, this.goudy38, Color.BLACK, "Options", centerX, this.playResumeButton.getY() + 4 * (int) (this.playButton.getHeight()) / 3, this.buttonTextures[0], this.buttonTextures[1], this.buttonTextures[2]);
		this.playOptionsButton.addComponentListener(getPauseMenuListener(OPTIONS_MENU));
		this.playMainMenuButton = new Button(this.window, this.goudy38, Color.BLACK, "Main Menu", centerX, this.playOptionsButton.getY() + 4 * (int) (this.playOptionsButton.getHeight()) / 3, this.buttonTextures[0], this.buttonTextures[1], this.buttonTextures[2]);
		this.playMainMenuButton.addComponentListener(getPauseMenuListener(MAIN_MENU));
		
		this.playStartWaveButton = new Button(this.window, this.sanserif16, Color.BLACK, "", this.window.getWidth() - (int) this.buttonTextures[10].getWidth(), this.window.getHeight() - (int) this.buttonTextures[10].getHeight() - 5, this.buttonTextures[10], this.buttonTextures[11], this.buttonTextures[12]);
		this.playStartWaveButton.addComponentListener(new ComponentListener() {
			public void onMouseOver(WindowComponent e) {}
			public void onPress(WindowComponent wc) {
				if (buildingHealth == 0) return;
				onWaveStart();
			}
		});
		
		// Options menu buttons
		this.optionsDoneButton = new Button(this.window, this.goudy38, Color.BLACK, "Done", centerX, 534, this.buttonTextures[0], this.buttonTextures[1], this.buttonTextures[2]);
		this.optionsDoneButton.addComponentListener(getMenuListener(PAUSE_MENU));
		
		// Unit Select buttons
		this.infoUnitShooterButton = new Button(this.window, "", this.window.getWidth() - INFO_PANE_WIDTH + 24, this.window.getHeight() - INFO_PANE_HEIGHT - 128, 32, 32, this.grid.shooterSpriteSheet.getSprite(0, 0));
		this.infoUnitShooterButton.addComponentListener(getUnitListener(Shooter.ID));
		
		// Stat Upgrade buttons
		this.infoUnitShooterUpgradeHealthButton = new Button(this.window, "", this.window.getWidth() - INFO_PANE_WIDTH - 170, this.window.getHeight() - INFO_PANE_HEIGHT + (INFO_PANE_HEIGHT - 64) / 2, 64, 64, this.interfaceTextures.getSubTexture(105, 200, 32, 32), this.interfaceTextures.getSubTexture(137, 200, 32, 32), this.interfaceTextures.getSubTexture(169, 200, 32, 32));
		this.infoUnitShooterUpgradeHealthButton.addComponentListener(getUnitUpgradeListener(0));
		this.infoUnitShooterUpgradeStrengthButton = new Button(this.window, "", this.window.getWidth() - INFO_PANE_WIDTH - 95, this.window.getHeight() - INFO_PANE_HEIGHT + (INFO_PANE_HEIGHT - 64) / 2, 64, 64, this.interfaceTextures.getSubTexture(224, 128, 32, 32), this.interfaceTextures.getSubTexture(224, 160, 32, 32), this.interfaceTextures.getSubTexture(224, 192, 32, 32));
		this.infoUnitShooterUpgradeStrengthButton.addComponentListener(getUnitUpgradeListener(1));
		
		// Mouse Over boxes
		this.infoUnitShooterMouseOverBox = new TextBox(this.window, this.mouseOverTextures[0], "Info Unit Shooter Mouse Over", "", this.window.getWidth() - INFO_PANE_WIDTH - 150 - TextBox.DEFAULT_PADDING, this.infoUnitShooterButton.getY(), 150, 100);
		this.infoUnitShooterUpgradeHealthMouseOverBox = new TextBox(this.window, this.mouseOverTextures[0], "Info Unit Shooter Upgrade Health Mouse Over", "Upgrade health by: 50", this.infoUnitShooterUpgradeHealthButton.getX() - 35, this.window.getHeight() - INFO_PANE_HEIGHT - 20, 130, 12);
		this.infoUnitShooterUpgradeStrengthMouseOverBox = new TextBox(this.window, this.mouseOverTextures[0], "Info Unit Shooter Upgrade Strength Mouse Over", "Upgrade strength by: 5", this.infoUnitShooterUpgradeStrengthButton.getX() - 35, this.window.getHeight() - INFO_PANE_HEIGHT - 20, 130, 12);
		
		// Buttons for each square on the grid
		this.playGridButtons = new Button[this.grid.getNumRows()][this.grid.getNumColumns()];
		for (int row = 0; row < this.playGridButtons.length; row++) {
			for (int col = 0; col < this.playGridButtons[0].length; col++) {
				this.playGridButtons[row][col] = new Button(this.window, col * Grid.SQUARE_SIZE, row * Grid.SQUARE_SIZE, Grid.SQUARE_SIZE, Grid.SQUARE_SIZE);
				this.playGridButtons[row][col].setPlaySound(false);
				this.playGridButtons[row][col].addComponentListener(new ComponentListener() {
					public void onMouseOver(WindowComponent e) {}
					public void onPress(WindowComponent wc) {
						onSquareClicked(wc);
					}
				});
			}
		}
	}
	
	private ComponentListener getMenuListener(final int menu) {
		return new ComponentListener() {
			public void onMouseOver(WindowComponent wc) {}
			public void onPress(WindowComponent wc) {
				currentMenu = menu;
			}
		};
	}
	
	
	private ComponentListener getPauseMenuListener(final int menu) {
		return new ComponentListener() {
			public void onMouseOver(WindowComponent wc) {}
			public void onPress(WindowComponent wc) {
				currentMenu = menu;
				waveStartTime += System.nanoTime() - pauseStartTime;
			}
		};
	}
	
	private ComponentListener getInstructionArrowListener(final int dir) {
		return new ComponentListener() {
			public void onMouseOver(WindowComponent wc) {}
			public void onPress(WindowComponent wc) {
				currentMenu += dir;
			}
		};
	}
	
	private ComponentListener getUnitListener(final int id) {
		return new ComponentListener() {
			public void onMouseOver(WindowComponent wc) {
				displayMouseOverUnitInfo(id, true);
			}
			public void onPress(WindowComponent wc) {
				if (buildingHealth == 0) return;
				
				if (selectedBuyUnit == id) {
					selectedBuyUnit = -1;
				} else {
					selectedBuyUnit = id;
				}
			}
		};
	}
	
	private ComponentListener getUnitUpgradeListener(final int skill) {
		return new ComponentListener() {
			public void onMouseOver(WindowComponent wc) {
				displayMouseOverUnitUpgradeInfo(skill, true);
			}
			public void onPress(WindowComponent wc) {
				if (buildingHealth == 0) return;
				
				Entity e = grid.getEntityAt(selectedUnit[0], selectedUnit[1]);
				if (e instanceof Shooter) {
					Shooter s = (Shooter) e;
					if (skill == 0) {
						s.upgradeBaseHealth(50);
						s.refillHealth();
					} else if (skill == 1) {
						s.upgradeStrength(5);
					}
					s.finishLevelUp();
					grid.setEntity(s, selectedUnit[0], selectedUnit[1]);
				}
			}
		};
	}
	
	private void displayMouseOverUnitUpgradeInfo(int skill, boolean shouldRender) {
		this.renderer.setColor(Color.BLACK);
		this.renderer.setFont(this.sanserif12);
		if (skill == 0) {
			this.infoUnitShooterUpgradeHealthMouseOverBox.setActive(shouldRender);
			this.infoUnitShooterUpgradeHealthMouseOverBox.render(this.renderer);
		} else if (skill == 1) {
			this.infoUnitShooterUpgradeStrengthMouseOverBox.setActive(shouldRender);
			this.infoUnitShooterUpgradeStrengthMouseOverBox.render(this.renderer);
		}
	}
	
	private void displayMouseOverUnitInfo(int id, boolean shouldRender) {
		this.renderer.setColor(Color.BLACK);
		this.renderer.setFont(this.sanserif12);
		if (id == Shooter.ID) {
			this.infoUnitShooterMouseOverBox.setActive(shouldRender);
			this.infoUnitShooterMouseOverBox.render(this.renderer);
		}
	}
	
	private void onWaveStart() {
		if (this.buildingHealth == 0) return;
		
		if (this.grid.getEnemyCount() == 0) {
			// Update wave info
			this.waveStartTime = System.nanoTime();
			this.currentWaveInfo = WaveInfoReader.getWaveInfo(++this.wave);
			
			// Update the number of columns outlined
			for (int i = 0; i < COLUMNS_OUTLINED_AT.length; i++) {
				if (this.wave == COLUMNS_OUTLINED_AT[i]) {
					Enemy.boostHealth((i + 1) * 20);
					Enemy.boostStrength((i + 1) * 5);
				}
			}
			
			updateUnitInfo();
			
			this.allowedColumns = getAllowedColumns(this.wave);
			this.justSwitchedColumns = false;
		}
	}
	
	private void onWaveEnd() {
		if (this.grid.getEnemyCount() == 0) {
			this.allowedColumns = getAllowedColumns(this.wave + 1);
			if (getAllowedColumns(this.wave) != this.allowedColumns) {
				this.justSwitchedColumns = true;
			}
			this.waveStartTime = -1L;
		}
	}
	
	public int getAllowedColumns(int wave) {
		for (int i = 0; i < COLUMNS_OUTLINED_AT.length; i++) {
			if (wave < COLUMNS_OUTLINED_AT[i]) {
				return COLUMNS_OUTLINED[i];
			}
		}
		return this.grid.getNumColumns() / 2;
	}
	
	private void updateUnitInfo() {
		this.infoUnitShooterMouseOverBox.setText(String.format(
				  "%s:\n"
				+ "Fires arrows at enemies.\n \n"
				+ "Health: %d\n"
				+ "Strength: %d\n"
				+ "Gold Cost: %d"
				, Shooter.getCurrentName(), Shooter.getCurrentBaseHealth(), Shooter.getCurrentStrength(), Shooter.getCurrentGoldValue()
		));
	}
	
	private void onSquareClicked(WindowComponent wc) {
		if (this.buildingHealth == 0) return;
		
		int row = (int) wc.getY() / Grid.SQUARE_SIZE;
		int column = (int) wc.getX() / Grid.SQUARE_SIZE;
		if (row > 0 && this.selectedBuyUnit != -1 && this.grid.getEntityAt(row, column) == null && Math.abs(convertFromColumn(column)) <= this.allowedColumns) {
			this.grid.addUnit(this.selectedBuyUnit, row, column);
			this.selectedBuyUnit = -1;
			this.justSwitchedColumns = false;
			this.selectedUnit[0] = -1; this.selectedUnit[1] = -1;
		} else if (this.grid.getEntityAt(row, column) != null) {
			if (this.selectedUnit[0] == row && this.selectedUnit[1] == column) {
				this.selectedUnit[0] = -1; this.selectedUnit[1] = -1;
			} else {
				this.selectedUnit[0] = row; this.selectedUnit[1] = column;
			}
		} else {
			this.selectedUnit[0] = -1; this.selectedUnit[1] = -1;
		}
	}
	
	public void update(double delta) {
		if (this.currentMenu == PLAY) {
			this.grid.update(delta);
			
			// If a wave is going on, then spawn units
			if (this.currentWaveInfo != null) {
				if (this.currentWaveInfo.isDone()) {
					onWaveEnd();
				} else if (this.waveStartTime != -1L && this.currentTime - this.waveStartTime >= this.currentWaveInfo.nextTime() * 1000000000L) {
					String[] positions = this.currentWaveInfo.extractNextSpawn();
					for (int pos = 0; pos < positions.length; pos++) {
						String sPos = positions[pos];
						if (sPos.equalsIgnoreCase("e")) {
							this.grid.addUnit(Enemy.ID, 0, (this.grid.getNumColumns() - positions.length) / 2 + pos);
						}
					}
				}
			}
			
			// If the building's health is less than or equal to 0, set it to 0 to indicate game over
			if (this.buildingHealth <= 0) {
				this.buildingHealth = 0;
			}
		}
	}
	
	private int convertFromColumn(int col) {
		return col - this.grid.getNumColumns() / 2 + (col >= this.grid.getNumColumns() / 2 ? 1 : 0);
	}
	
	public int getGold() {
		return this.gold;
	}
	
	public void addGold(int amount) {
		this.gold += amount;
	}
	
	public void removeGold(int amount) {
		this.gold -= amount;
	}
	
	public int getBuildingHealth() {
		return this.buildingHealth;
	}
	
	public void healBuilding(int amount) {
		this.buildingHealth += amount;
	}
	
	public void damageBuilding(int amount) {
		this.buildingHealth -= amount;
	}
	
	public void render() {
		this.renderer.drawTexture(this.infoPaneBackground, 0, 0);
		
		if (this.currentMenu == MAIN_MENU) {
			renderMainMenu();
		} else if (this.currentMenu == EXIT_MENU) {
			renderExitMenu();
		} else if (this.currentMenu == CREDITS_MENU) {
			renderCreditsMenu();
		} else if (this.currentMenu >= INSTRUCTIONS_MENU) {
			renderInstructionsMenu();
		} else if (this.currentMenu == PLAY || this.currentMenu == PAUSE_MENU) {
			if (this.currentMenu == PAUSE_MENU) {
				renderPauseMenu();
			} else {
				if (Keyboard.isKeyDown(Keyboard.KEY_P) || Keyboard.isKeyDown(Keyboard.KEY_PAUSE)) {
					this.currentMenu = PAUSE_MENU;
					this.pauseStartTime = System.nanoTime();
				}
				renderGame();
				if (this.buildingHealth == 0) {
					renderGameOver();
				}
			}
		} else if (this.currentMenu == OPTIONS_MENU) {
			renderOptionsMenu();
		}
	}
	
	private void renderGame() {
		this.grid.render(this.renderer, (this.selectedBuyUnit != -1 || this.justSwitchedColumns) ? this.allowedColumns : 0);
		for (Button[] bs : this.playGridButtons) {
			for (Button b : bs) {
				b.render(this.renderer);
			}
		}
		renderInfoPane();
	}
	
	private void renderInfoPane() {
		this.renderer.setColor(Color.BLACK);
		this.renderer.setFont(this.sanserif26);
		this.renderer.drawText("Units", this.window.getWidth() - INFO_PANE_WIDTH + this.sanserif26.getWidth("Units") / 4 - 2, this.infoUnitShooterButton.getY() - 40);
		this.infoUnitShooterButton.render(this.renderer);
		
		if (this.selectedBuyUnit != -1) {
			if (this.selectedBuyUnit == Shooter.ID) {
				// Draw the selection box over the infoUnitShooter button
				this.renderer.drawTexture(this.buttonTextures[9], this.infoUnitShooterButton.getX(), this.infoUnitShooterButton.getY(), this.infoUnitShooterButton.getWidth(), this.infoUnitShooterButton.getHeight());
			}
		} else {
			this.renderer.setColor(new Color(90, 90, 90));
			this.renderer.drawRect(this.infoUnitShooterButton.getX(), this.infoUnitShooterButton.getY(), this.infoUnitShooterButton.getWidth(), this.infoUnitShooterButton.getHeight());
		}
		
		if (this.grid.getEntityAt(this.selectedUnit[0], this.selectedUnit[1]) != null) {
			Entity e = this.grid.getEntityAt(this.selectedUnit[0], this.selectedUnit[1]);
			
			if (e instanceof Shooter) {
				Shooter shooter = (Shooter) e;
				
				// Draw the selection box over the selected shooter
				this.renderer.drawTexture(this.buttonTextures[9], shooter.getX(), shooter.getY(), shooter.getWidth(), shooter.getHeight());
				
				// Draw the shooter picture on the bottom
				this.renderer.drawTexture(shooter.getCurrentTexture(), 4, this.window.getHeight() - INFO_PANE_HEIGHT + 4, INFO_PANE_HEIGHT - 8, INFO_PANE_HEIGHT - 8);
				
				// Draw the shooter's name
				this.renderer.setFont(this.sanserif16);
				this.renderer.setColor(Color.BLACK);
				this.renderer.drawText(Shooter.getCurrentName(), INFO_PANE_WIDTH + 8, this.window.getHeight() - INFO_PANE_HEIGHT + 8);
				
				// Draw the shooter's position
				this.renderer.drawText(String.format("Position: (%d, %d)", shooter.getGridPos()[0] + 1, shooter.getGridPos()[1] + 1), INFO_PANE_WIDTH + 8, this.window.getHeight() - INFO_PANE_HEIGHT + 38);
				
				// Draw the shooter's health
				this.renderer.drawTexture(this.infoPaneTextures[0], INFO_PANE_WIDTH + 168, this.window.getHeight() - INFO_PANE_HEIGHT + 10, HEALTH_BAR_HEIGHT + 1, HEALTH_BAR_HEIGHT + 1);
				int currentHealthLength = (shooter.getHealth() > 0) ? (HEALTH_BAR_LENGTH * shooter.getHealth() / shooter.getUpgradedBaseHealth()) : 0;
				String healthText = String.format("%d/%d", shooter.getHealth(), shooter.getUpgradedBaseHealth());
				
				this.renderer.setColor(Color.GREEN);
				this.renderer.fillRect(INFO_PANE_WIDTH + 193, this.window.getHeight() - INFO_PANE_HEIGHT + 10, currentHealthLength, HEALTH_BAR_HEIGHT);
				this.renderer.setColor(Color.RED);
				this.renderer.fillRect(INFO_PANE_WIDTH + 193 + currentHealthLength, this.window.getHeight() - INFO_PANE_HEIGHT + 10, HEALTH_BAR_LENGTH - currentHealthLength, HEALTH_BAR_HEIGHT);
				
				this.renderer.setFont(this.sanserif12);
				this.renderer.setColor(Color.BLACK);
				this.renderer.drawText(healthText, INFO_PANE_WIDTH + 193 + (HEALTH_BAR_LENGTH - this.sanserif12.getWidth(healthText)) / 2 - 3, this.window.getHeight() - INFO_PANE_HEIGHT + 10 + (HEALTH_BAR_HEIGHT - this.sanserif12.getHeight()) / 2);
				
				// Draw the shooter's strength
				this.renderer.drawTexture(this.infoPaneTextures[1], INFO_PANE_WIDTH + 168, this.window.getHeight() - INFO_PANE_HEIGHT + 42, HEALTH_BAR_HEIGHT + 1, HEALTH_BAR_HEIGHT + 1);
				this.renderer.setFont(this.sanserif16);
				this.renderer.drawText(shooter.getUpgradedStrength() + "", INFO_PANE_WIDTH + 193, this.window.getHeight() - INFO_PANE_HEIGHT + 42);
			
				// Draw the shooter's current xp
				this.renderer.setFont(this.sanserif16);
				this.renderer.drawText("Level: " + shooter.getLevel(), INFO_PANE_WIDTH + 290, this.window.getHeight() - INFO_PANE_HEIGHT + 10);
				this.renderer.drawText("XP: " + shooter.getCurrentXP(), INFO_PANE_WIDTH + 290, this.window.getHeight() - INFO_PANE_HEIGHT + 42);
				
				if (shooter.hasLeveledUp()) {
					this.infoUnitShooterUpgradeHealthButton.render(this.renderer);
					this.infoUnitShooterUpgradeStrengthButton.render(this.renderer);
				}
			}
		} else {
			this.renderer.setFont(this.sanserif16);
			this.renderer.setColor(Color.BLACK);
			
			// Draw the current era
			if (this.wave < 10) {
				this.renderer.drawText("Ancient Era", 4, this.window.getHeight() - INFO_PANE_HEIGHT + 4);
			} else if (this.wave < 20) {
				this.renderer.drawText("Classical Era", 4, this.window.getHeight() - INFO_PANE_HEIGHT + 4);
			} else if (this.wave < 30) {
				this.renderer.drawText("Renaissance\n        Era", 4, this.window.getHeight() - INFO_PANE_HEIGHT + 4);
			} else if (this.wave < 40) {
				this.renderer.drawText("Industrial Era", 4, this.window.getHeight() - INFO_PANE_HEIGHT + 4);
			} else if (this.wave < 50) {
				this.renderer.drawText("Modern Era", 4, this.window.getHeight() - INFO_PANE_HEIGHT + 4);
			} else {
				this.renderer.drawText("Future era", 4, this.window.getHeight() - INFO_PANE_HEIGHT + 4);
			}
			
			this.renderer.drawText("Building Health: " + this.buildingHealth, (this.grid.getNumColumns() * Grid.SQUARE_SIZE - this.sanserif16.getWidth("Building Health: " + this.buildingHealth)) / 2, this.window.getHeight() - INFO_PANE_HEIGHT + 4);
		}
		
		// Draw gold and wave information
		this.renderer.setColor(Color.BLACK);
		this.renderer.setFont(this.sanserif16);
		String goldText = "Gold: " + (this.gold > 10000000 ? (this.gold / 1000000) : (this.gold > 100000 ? (this.gold / 1000) + "K" : this.gold));
		this.renderer.drawText(goldText, this.window.getWidth() - INFO_PANE_WIDTH + 4, 4);
		this.renderer.drawText("Wave: " + this.wave, this.window.getWidth() - INFO_PANE_WIDTH + 4, 24);
		
		// Draw the wave button. If a wave is still going on, does not update the button
		if (this.grid.getEnemyCount() > 0) {
			this.playStartWaveButton.renderNoUpdate(this.renderer);
		} else {
			this.playStartWaveButton.render(this.renderer);
		}
	}
	
	private void renderGameOver() {
		this.renderer.setFont(this.goudy96);
		this.renderer.setColor(Color.BLACK);
		this.renderer.drawText("Game Over", (this.window.getWidth() - this.goudy96.getWidth("Game Over")) / 2, (this.window.getHeight() - this.goudy96.getHeight()) / 2);
		this.playMainMenuButton.render(this.renderer);
	}

	private void renderMainMenu() {
		this.renderer.setFont(this.goudy96);
		this.renderer.setColor(new Color(225, 225, 225));
		this.renderer.drawText("Defense Evolution", 102, 15);
		
		this.playButton.render(this.renderer);
		this.instructionsButton.render(this.renderer);
		this.creditsButton.render(this.renderer);
		this.exitButton.render(this.renderer);
	}
	
	private void renderExitMenu() {
		this.renderer.setFont(this.goudy96);
		this.renderer.setColor(new Color(225, 225, 225));
		this.renderer.drawText("   Thanks for\n     playing \nDefense Evolution!", 102, 70);
		
		this.exitCloseButton.render(this.renderer);
	}
	
	private void renderCreditsMenu() {
		this.renderer.setFont(this.goudy96);
		this.renderer.setColor(new Color(225, 225, 225));
		this.renderer.drawText("Credits", 282, 15);
		
		this.renderer.setFont(this.goudy38);
		this.renderer.setColor(new Color(30, 30, 30));
		this.renderer.drawText("Creator: Arman Siddique", 242, 145);
		
		this.mainMenuButton.render(this.renderer);
	}
	
	private void renderPauseMenu() {
		renderGame();
		
		this.renderer.setFont(this.goudy96);
		this.renderer.setColor(new Color(225, 225, 225));
		this.renderer.drawText("Paused", 280, 15);
		
		this.renderer.setColor(Color.WHITE);
		this.renderer.drawRect(275, 200, 250, 250);
		this.renderer.setColor(Color.BLACK);
		this.renderer.fillRect(276, 201, 248, 248);
		
		this.playResumeButton.render(this.renderer);
		this.playOptionsButton.render(this.renderer);
		this.playMainMenuButton.render(this.renderer);
	}
	
	private void renderInstructionsMenu() {
		this.renderer.setFont(this.goudy96);
		this.renderer.setColor(new Color(225, 225, 225));
		this.renderer.drawText("Instructions", 204, 15);
		
		this.mainMenuButton.render(this.renderer);
		
		this.renderer.setFont(this.sanserif26);
		this.renderer.setColor(new Color(30, 30, 30));
		
		if (this.currentMenu == INSTRUCTIONS_MENU) {
			this.renderer.drawText(""
					+ "\tThe objective of the game is to defend your building for as long as possible "
					+ "against waves of enemies.  When your building's health reaches zero, you lose. "
					+ "The waves of enemies start off easy, but get progressively harder.  After completing "
					+ "a certain amount of waves of enemies, you progress to the next era of history, "
					+ "which will unlock new units, upgrades, and spaces."
					, 15, 170, this.window.getWidth() - 30, this.window.getHeight());
			this.instructionArrows[0].render(this.renderer);
		} else if (this.currentMenu == INSTRUCTIONS_MENU + 1) {
			this.renderer.drawText(""
					+ "\tTo aid you in protecting your building, you can buy units using gold (earned "
					+ "through defeating enemies). You can also upgrade units with gold, which enhances "
					+ "the unit or gives the unit new abilities. As mentioned before, more units and "
					+ "upgrades are available in each era.\n"
					+ "\n\tTo buy a unit, click the unit's icon under the game.  Then, click one of the "
					+ "highlighted squares to place that unit there."
					, 15, 170, this.window.getWidth() - 30, this.window.getHeight());
			this.instructionArrows[0].render(this.renderer);
			this.instructionArrows[1].render(this.renderer);
		} else if (this.currentMenu == INSTRUCTIONS_MENU + 2) {
			this.renderer.drawText(""
					+ "\tAfter buying a unit, you can place it anywhere besides on top of your building or "
					+ "another unit. As units can't move, you must place your units strategically. "
					+ "Each unit has a specific amount of health that will go down when it's attacked. "
					+ "When it becomes zero, the unit is defeated. Each unit also has a specific strength "
					+ "value that indicates how much damage it does to enemies. Health and strength can "
					+ "be increased with upgrades."
					, 15, 170, this.window.getWidth() - 30, this.window.getHeight());
			this.instructionArrows[1].render(this.renderer);
		}
	}
	
	private void renderOptionsMenu() {
		this.renderer.setFont(this.goudy96);
		this.renderer.setColor(new Color(225, 225, 225));
		this.renderer.drawText("Options", 267, 15);
		
		this.optionsDoneButton.render(this.renderer);
	}

	public void cleanUp() {
		this.sanserif8.destroy();
		this.sanserif12.destroy();
		this.sanserif16.destroy();
		this.sanserif26.destroy();
		this.goudy38.destroy();
		this.goudy96.destroy();
		
		this.backgroundMusic.destroy();
		this.buttonClickSound.destroy();
		
		this.grid.destroy();
		this.interfaceTextures.delete();
		this.infoPaneBackground.delete();
	}

	public DefenseEvolution() {
		super("Defense Evolution", 60, 800, 600);
		this.window.setResizable(false);
	}

	public static void main(String[] args) {
		new DefenseEvolution();
	}
}
