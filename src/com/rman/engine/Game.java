package com.rman.engine;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import com.rman.engine.graphics.Renderer;
import com.rman.engine.gui.Window;

/**
 * <pre>public abstract class Game</pre>
 * 
 * <p> An abstract class for a game. This class provides a {@link Keyboard Keyboard}, {@link Mouse Mouse},
 * {@link Renderer Renderer}, {@link Window Window}, and a {@link #run() main game loop}. Subclasses must implement
 * the {@link #init() init} method, the {@link #update(double) update} method, the {@link #render() render} method,
 * and the {@link #cleanUp() cleanUp} method. </p>
 * 
 * @author Arman
 */
public abstract class Game {

	/**
	 * The window of this game.
	 */
	protected Window window;

	/**
	 * Indicates whether this game is running. This variable is set to true right before entering the main game
	 * loop.
	 */
	protected volatile boolean running = false;
	/**
	 * Indicates whether this game is paused. Subclasses should implement pause functionality, as it is not
	 * implemented in this <code>Game</code> class.
	 */
	protected volatile boolean isPaused = false;

	/**
	 * The renderer of this game. See the {@link Renderer Renderer} class for more details.
	 */
	protected Renderer renderer;

	/**
	 * An {@link Keyboard Keyboard} object that processes keyboard input.
	 * 
	 * @see {@link Keyboard Keyboard}
	 */
	protected Keyboard keyboard;

	/**
	 * A {@link Mouse Mouse} object that processes mouse input.
	 * 
	 * @see {@link Mouse Mouse}
	 */
	protected Mouse mouse;
	
	/**
	 * The current time. This variable is updated on each iteration of the main game loop in {@link #run() run()}.
	 * @see {@link System#nanoTime() System.nanoTime()}
	 */
	protected long currentTime;

	/**
	 * <pre>public Game({@link String String} title, int targetFPS)</pre>
	 * 
	 * <p> Constructs a game with the given title and target FPS. </p>
	 * 
	 * @param title - The title of the game window
	 * @param targetFPS - The FPS that this game will maintain
	 */
	public Game(String title, int targetFPS) {
		this(title, targetFPS, 0, 0);
	}

	/**
	 * <pre>public Game({@link String String} title, int targetFPS, int width, int height)</pre>
	 * 
	 * <p> Constructs a game with the given title, target FPS, and size (width and height). </p>
	 * 
	 * @param title - The title of the game window
	 * @param targetFPS - The FPS that this game will maintain
	 * @param width - The initial width of the game window
	 * @param height - The initial height of the game window
	 */
	public Game(String title, int targetFPS, int width, int height) {
		Log.start();
		Log.log("Initializing...");
		
		if (width <= 0 && height <= 0) {
			this.window = new Window(targetFPS, title);
		} else {
			this.window = new Window(targetFPS, width, height, title);
		}
		
		this.renderer = new Renderer(this.window);
		
		init();
		System.out.println();
		run();
	}

	/**
	 * <pre>public void init()</pre>
	 * 
	 * <p> Initializes the game. In this method, subclasses can initialize game components, game information, etc. <p>
	 */
	public abstract void init();

	/**
	 * <pre>public void update(double delta)</pre>
	 * 
	 * <p> Update the game. In this method, subclasses can update game logic, process input, etc. </p>
	 * 
	 * @param delta - The time since the last update divided by the optimal time
	 */
	public abstract void update(double delta);

	/**
	 * <pre>public void render()</pre>
	 * 
	 * <p> Renders the game. In this method, subclasses can render entities, draw backgrounds, etc. </p>
	 */
	public abstract void render();

	/**
	 * <pre>public void cleanUp()</pre>
	 * 
	 * <p> "Cleans up" the game. Note that this is not the place to have a "Game Over" message. This method is
	 * called after the game is stopped, just before the window is closed. This method is used to do things
	 * like deleting textures. </p>
	 */
	public abstract void cleanUp();

	/**
	 * <pre>private void run()</pre>
	 * 
	 * <p> Runs the game. This method basically executes a main game loop. </p>
	 * 
	 * <p> During the loop, the time since the last update divided by the optimal 
	 * time (delta) is calculated. Then, the {@link #update(double) update()} and
	 * {@link #render() render()} methods are called, and the <code>Window</code>
	 * is updated.
	 * 
	 * <p> If an exception is thrown while the game is running, this method uses 
	 * the {@link Log Log} class to print the error. </p>
	 * 
	 * <p> Once the game stops running, the {@link #cleanUp() cleanUp()} method is 
	 * called, the <code>Renderer</code> and <code>Window</code> objects are destroyed,
	 * and {@link System#exit(int) System.exit(0)} is called. </p>
	 */
	private void run() {
		long lastLoopTime = System.nanoTime();
		final long OPTIMAL_TIME = 1000000000L / this.window.getTargetFPS();

		this.running = true;
		try {
			while (this.running) {
				// Calculate delta
				this.currentTime = System.nanoTime();
				long updateTimeLength = this.currentTime - lastLoopTime;
				lastLoopTime = this.currentTime;
				double delta = updateTimeLength / ((double) OPTIMAL_TIME);
				
				// Update the game
				this.window.clear();
				
				update(delta);
				render();
	
				this.window.updateWindow(this.renderer);
	
				if (this.window.isClosing()) {
					this.running = false;
				}
			}
		} catch (Exception e) {
			Log.logError("", e);
		}
		
		// Clean up and exit
		System.out.println();
		Log.log("Cleaning up...");
		cleanUp();
		this.renderer.destroy();
		this.window.destroy();
		Log.stop();
		System.exit(0);
	}
}
