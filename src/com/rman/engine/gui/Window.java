package com.rman.engine.gui;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Mouse;
import org.lwjgl.openal.AL;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.vector.Matrix4f;

import com.rman.engine.GMath;
import com.rman.engine.Log;
import com.rman.engine.graphics.Renderer;

/**
 * <pre>public class Window</pre>
 * 
 * <p> This class manages the LWJGL <code>Display</code> and it initializes 
 * OpenGL and OpenAL. </p>
 * 
 * @author Arman
 */
public class Window {

	/**
	 * Whether this window is fullscreen.
	 */
	private boolean fullScreen;
	/**
	 * The title of this window.
	 */
	private String title;

	/**
	 * The x coordinate of this window.
	 */
	protected int x;

	/**
	 * The y coordinate of this window.
	 */
	protected int y;

	/**
	 * The width of this window.
	 */
	protected int width;
	/**
	 * The height of this window.
	 */
	protected int height;
	
	private int originX = 0;
	private int originY = 0;

	/**
	 * The target FPS of this window.
	 */
	private int targetFPS;

	/**
	 * Indicates whether this window is resizable.
	 */
	private boolean isResizable;

	/**
	 * Indicates whether this window has VSync enabled.
	 */
	private boolean isVSyncEnabled;

	/**
	 * The first component of the aspect ratio of this window. For example, it could be the "4" in 4:3.
	 */
	private int aspectX;
	/**
	 * The second component of the aspect ratio of this window. For example, it could be the "3" in 4:3.
	 */
	private int aspectY;
	
	private Matrix4f projectionMatrix;
	private Matrix4f viewMatrix;

	/**
	 * <pre>public Window(int fps)</pre>
	 * 
	 * <p> Creates a window with the given FPS. No title is created and the size is default. </p>
	 * 
	 * @param fps - The FPS of this window
	 */
	public Window(int fps) {
		this(fps, (int) (Display.getDesktopDisplayMode().getWidth() * 0.75), (int) (Display
				.getDesktopDisplayMode().getHeight() * 0.75));
	}

	/**
	 * <pre>public Window(int fps, int width, int height)</pre>
	 * 
	 * <p> Creates a window with the given size and FPS. </p>
	 * 
	 * @param fps - The FPS of this window
	 * @param width - The width of this window
	 * @param height - The height of this window
	 */
	public Window(int fps, int width, int height) {
		this(fps, width, height, "");
	}

	/**
	 * <pre>public Window(int fps, {@link String String} title)</pre>
	 * 
	 * <p> Creates a window with the given FPS and title, and default size.<p>
	 * 
	 * @param fps - The FPS of this window
	 * @param title - The title of this window
	 */
	public Window(int fps, String title) {
		this(fps, (int) (Display.getDesktopDisplayMode().getWidth() * 0.75), (int) (Display
				.getDesktopDisplayMode().getHeight() * 0.75), title);
	}

	/**
	 * <pre>public Window(int fps, int x, int y, int width, int height, {@link String String} title)</pre>
	 * 
	 * <p> Creates a window with the given FPS, size, location, and title.
	 * 
	 * @param fps - The FPS of this window
	 * @param x - The x coordinate of this window's location on the desktop
	 * @param y - The y coordinate of this window's location on the desktop
	 * @param width - The width of this window
	 * @param height - The height of this window
	 * @param title - The title of this window
	 */
	public Window(int fps, int x, int y, int width, int height, String title) {
		this(fps, width, height, title);
		setLocation(x, y);
	}

	/**
	 * <pre>public Window(int fps, int width, int height, {@link String String} title)</pre>
	 * 
	 * <p> Creates a window with the given size, FPS, and title. </p>
	 * 
	 * @param fps - The FPS of this window
	 * @param width - The width of this window
	 * @param height - The height of this window
	 * @param title - The title of this window
	 */
	public Window(int fps, int width, int height, String title) {
		this.targetFPS = fps;
		setTitle(title);

		setResizable(true);
		setVSyncEnabled(false);

		try {
			setSize(width, height);
			initWindow();
		} catch (WindowException e) {
			Log.logError("Error creating window", e);
			Log.logError("Destroying window...");
			destroy();
		}
	}

	/**
	 * <pre>public boolean isVSyncEnabled()</pre>
	 * 
	 * <p> Returns whether this window has VSync enabled. </p>
	 * 
	 * @return If this window has VSync enabled or not
	 */
	public boolean isVSyncEnabled() {
		return this.isVSyncEnabled;
	}

	/**
	 * <pre>public void setVSyncEnabled(boolean enableVSync)</pre>
	 * 
	 * <p> Sets whether this window has VSync enabled. </p>
	 * 
	 * @param enableVSync - Whether to enable VSync
	 */
	public void setVSyncEnabled(boolean enableVSync) {
		this.isVSyncEnabled = enableVSync;
		Display.setVSyncEnabled(enableVSync);
	}

	/**
	 * <pre>public boolean isResizable()</pre>
	 * 
	 * <p> Returns whether this window is resizable. </p>
	 * 
	 * @return If this window is resizable or not
	 */
	public boolean isResizable() {
		return this.isResizable;
	}

	/**
	 * <pre>public void setResizable(boolean enableResize)</pre>
	 * 
	 * <p> Sets whether this window is resizable. <p>
	 * 
	 * @param Whether to make this window resizable or not
	 */
	public void setResizable(boolean enableResize) {
		this.isResizable = enableResize;
		Display.setResizable(this.isResizable);
	}

	/**
	 * <pre>public boolean isFullScreen()</pre>
	 * 
	 * <p> Returns whether this window is fullscreen. </p>
	 * 
	 * @return If this window is fullscreen or not
	 */
	public boolean isFullScreen() {
		return this.fullScreen;
	}

	/**
	 * <pre>public void setFullScreen(boolean enableFullScreen) throws {@link WindowException WindowException}
	 * </pre>
	 * 
	 * <p> Sets whether this window is fullscreen. </p>
	 * 
	 * @param enableFullScreen - Whether to enable fullscreen or not
	 * 
	 * @throws WindowException
	 */
	public void setFullScreen(boolean enableFullScreen) throws WindowException {
		try {
			Display.setFullscreen(enableFullScreen);
			this.fullScreen = enableFullScreen;
		} catch (LWJGLException le) {
			throw new WindowException("Unable to initialize fullscreen", le);
		}
	}

	/**
	 * <pre>public int getTargetFPS()</pre>
	 * 
	 * <p> Gets the target FPS of this window. </p>
	 * 
	 * @return The FPS
	 */
	public int getTargetFPS() {
		return this.targetFPS;
	}

	/**
	 * <pre>public {@link String String} getTitle()</pre>
	 * 
	 * <p> Gets the title of this window. </p>
	 * 
	 * @return The title
	 */
	public String getTitle() {
		return this.title;
	}

	/**
	 * <pre>public void setTitle({@link String String} newTitle)</pre>
	 * 
	 * <p> Sets the title of this window to the new title. </p>
	 * 
	 * @param newTitle - The new title
	 */
	public void setTitle(String newTitle) {
		Display.setTitle(newTitle);
		this.title = newTitle;
	}

	/**
	 * <pre>public int getX()</pre>
	 * 
	 * <p> Returns the x coordinate of this window. </p>
	 * 
	 * @return The x coordinate on the desktop
	 */
	public int getX() {
		return this.x;
	}

	/**
	 * <pre>public int getY()</pre>
	 * 
	 * <p> Returns the y coordinate of this window. </p>
	 * 
	 * @return The y coordinate on the desktop
	 */
	public int getY() {
		return this.y;
	}

	/**
	 * <pre>public void setX(int newX)</pre>
	 * 
	 * <p> Sets the window's x coordinate on the desktop. </p>
	 * 
	 * @param newX - The new x coordinate
	 */
	public void setX(int newX) {
		setLocation(newX, this.y);
	}

	/**
	 * <pre>public void setY(int newY)</pre>
	 * 
	 * <p> Sets the window's y coordinate on the desktop. </p>
	 * 
	 * @param newY - The new y coordinate
	 */
	public void setY(int newY) {
		setLocation(this.x, newY);
	}

	/**
	 * <pre>public void setLocation(int x, int y)</pre>
	 * 
	 * <p> Sets this window's location on the desktop. </p>
	 * 
	 * @param x - The x coordinate of the new location
	 * @param y - The y coordinate of the new location
	 * 
	 * @see org.lwjgl.opengl.Display#setLocation(int, int)
	 */
	public void setLocation(int newX, int newY) {
		this.x = newX;
		this.y = newY;
		Display.setLocation(this.x, this.y);
	}

	/**
	 * <pre>public int getWidth()</pre>
	 * 
	 * <p> Gets the current width of this window. </p>
	 * 
	 * @return The current width
	 * 
	 * @see org.lwjgl.opengl.Display#getWidth()
	 */
	public int getWidth() {
		return this.width;
	}

	/**
	 * <pre>public void setWidth(int newWidth)</pre>
	 * 
	 * <p> Sets the current width of this window to the given amount. </p>
	 * 
	 * @param newWidth - The new width
	 */
	public void setWidth(int newWidth) {
		try {
			setSize(newWidth, this.height);
		} catch (WindowException e) {
			Log.logError("Error resizing window", e);
		}
	}

	/**
	 * <pre>public int getHeight()</pre>
	 * 
	 * <p> Gets the current height of this window. </p>
	 * 
	 * @return The current height
	 */
	public int getHeight() {
		return this.height;
	}

	/**
	 * <pre>public void setHeight(int newHeight)</pre>
	 * 
	 * <p> Sets the current height of this window to the given amount. </p>
	 * 
	 * @param newHeight - The new height
	 */
	public void setHeight(int newHeight) {
		try {
			setSize(this.height, newHeight);
		} catch (WindowException e) {
			Log.logError("Error resizing window", e);
		}
	}

	/**
	 * <pre>public void setSize(int newWidth, int newHeight) throws {@link WindowException WindowException}</pre>
	 * 
	 * <p> Sets the dimensions of this window to the new width and height. </p>
	 * 
	 * @param newWidth - The new width of this window
	 * @param newHeight - The new height of this window
	 * 
	 * @throws WindowException If the new width or height is less than 1, or if the new size couldn't be set
	 * 
	 * @see org.lwjgl.opengl.Display#setDisplayMode(DisplayMode)
	 */
	public void setSize(int newWidth, int newHeight) throws WindowException {
		if (newWidth < 1) {
			throw new WindowException("Width cannot be less than one.");
		}
		if (newHeight < 1) {
			throw new WindowException("Height cannot be less than one.");
		}

		this.width = newWidth;
		this.height = newHeight;

		try {
			Display.setDisplayMode(new DisplayMode(newWidth, newHeight));
		} catch (LWJGLException le) {
			throw new WindowException("Unable to resize window.", le);
		}
	}
	
	public Matrix4f getProjectionMatrix() {
		return this.projectionMatrix;
	}
	
	public Matrix4f getViewMatrix() {
		return this.viewMatrix;
	}
	
	/**
	 * <pre>public int getMouseX()</pre>
	 * 
	 * <p> Gets the x-coordinate of the mouse's position. Note that the origin is the top-left corner. </p>
	 * 
	 * @return The x-coordinate of the mouse pointer
	 */
	public int getMouseX() {
		return (int) (((float) (Mouse.getX() - this.originX) / (Display.getWidth() - 2 * this.originX)) * this.width);
	}
	
	/**
	 * <pre>public int getMouseY()</pre>
	 * 
	 * <p> Gets the y-coordinate of the mouse's position. Note that the origin is the top-left corner. </p>
	 * 
	 * @return The y-coordinate of the mouse pointer
	 */
	public int getMouseY() {
		return (int) (((float) (Display.getHeight() - Mouse.getY() - this.originY - 1) / (Display.getHeight() - 2 * this.originY)) * this.height);
	}

	/**
	 * <pre>private boolean initWindow() throws {@link WindowException WindowException}</pre>
	 * 
	 * <p> Initializes this window. </p>
	 * 
	 * @return Whether this window was successfully initialized
	 * 
	 * @throws WindowException If the window was not initialized correctly
	 */
	private boolean initWindow() throws WindowException {
		try {
			Display.create();
			this.aspectX = this.width / GMath.gcd(this.width, this.height);
			this.aspectY = this.height / GMath.gcd(this.width, this.height);
			initOpenGL();
			AL.create();
		} catch (LWJGLException le) {
			throw new WindowException("Unable to create window", le);
		}

		return true;
	}

	/**
	 * <pre>private void initOpenGL()</pre>
	 * 
	 * <p> Initializes anything to do with OpenGL. </p>
	 */
	private void initOpenGL() {
		GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		GL11.glViewport(0, 0, this.width, this.height);
		
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GLU.gluOrtho2D(0.0f, this.width, 0.0f, this.height);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	}
	
	/**
	 * <pre>public void clear()</pre>
	 * 
	 * <p> Clears the contents of this window </p>
	 */
	public void clear() {
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
	}

	/**
	 * <pre>public void updateWindow()</pre>
	 * 
	 * <p> Updates this window. </p>
	 */
	public void updateWindow(Renderer renderer) {
		if (Display.isCloseRequested()) {
			destroy();
		} else {
			if (Display.wasResized()) {
				int newHeight = Display.getHeight() - (Display.getHeight() % this.aspectY);
				int newWidth = newHeight / this.aspectY * this.aspectX;

				int displayHeight = Display.getHeight();
				while (newWidth > Display.getWidth()) {
					displayHeight--;
					newHeight = displayHeight - (displayHeight % this.aspectY);
					newWidth = newHeight / this.aspectY * this.aspectX;
				}
				this.originX = (Display.getWidth() - newWidth) / 2;
				this.originY = (Display.getHeight() - newHeight) / 2;
				GL11.glViewport(this.originX, this.originY, newWidth, newHeight);
			}
			
			Display.update();
			Display.sync(this.targetFPS);
		}
	}

	/**
	 * <pre>public boolean isClosing()</pre>
	 * 
	 * <p> Returns whether this window is closing.
	 * 
	 * @return If this window is closing
	 */
	public boolean isClosing() {
		return Display.isCloseRequested();
	}

	/**
	 * <pre>public void destroy()</pre>
	 * 
	 * <p> Destroys this window. </p>
	 */
	public void destroy() {
		Display.destroy();
		AL.destroy();
	}
}
