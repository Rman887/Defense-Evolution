package com.rman.engine.graphics;

import java.awt.Font;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

import com.rman.engine.gui.TrueTypeFont;
import com.rman.engine.gui.Window;

/**
 * <pre>public class Renderer</pre>
 * 
 * <p> This class is used to render objects to the window. </p>
 * 
 * @author Arman
 */
public class Renderer {
	
	/**
	 * The default font that the <code>Renderer</code> will used.
	 */
	public static final TrueTypeFont DEFAULT_FONT = new TrueTypeFont("Default Font", new Font("SansSerif", Font.BOLD, 24), true);
	
	/**
	 * The current color that shapes will be drawn with.
	 */
	private Color currentColor = new Color(255, 255, 255);
	
	private Color filter = Color.WHITE;
	/**
	 * The Vertex Buffer Object (VBO) ID for rectangles.
	 */
	private int rectID;
	/**
	 * The Vertex Buffer Object (VBO) ID for circles.
	 */
	private int circleID;
	
	/**
	 * The current font that is used for rendering text.
	 */
	private TrueTypeFont currentFont = DEFAULT_FONT;

	/**
	 * The {@link Window Window} of the application. This is used to get the current width and height.
	 */
	private Window window;

	/**
	 * <pre>public Renderer()</pre>
	 * 
	 * <p> Initializes this renderer by preparing the Vertex Buffer Objects (VBOs). </p>
	 */
	public Renderer(Window window) {
		this.window = window;
		
		GL11.glLineWidth(1.0f);
		
		FloatBuffer rectVertices = BufferUtils.createFloatBuffer(8);
		rectVertices.put(new float[] {0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f,});
		rectVertices.flip();

		FloatBuffer circleVertices = BufferUtils.createFloatBuffer(724);
		circleVertices.put(new float[] {0.0f, 0.0f});
		for (int i = -1; i < 360; i++) {
			float x = (float) Math.cos(Math.toRadians(i));
			float y = (float) Math.sin(Math.toRadians(i));
			circleVertices.put(new float[] {x, y});
		}
		circleVertices.flip();

		this.rectID = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, this.rectID);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, rectVertices, GL15.GL_STATIC_DRAW);
		GL11.glVertexPointer(2, GL11.GL_FLOAT, 4, 0L);

		this.circleID = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, this.circleID);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, circleVertices, GL15.GL_STATIC_DRAW);
		GL11.glVertexPointer(2, GL11.GL_FLOAT, 0, 0L);

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}

	/**
	 * <pre>public void drawRect(float x, float y, float width, float height)</pre>
	 * 
	 * <p> Draws a rectangle with the top-left vertex at (x, y), and with the specified width and height. Note
	 * that only the edges of the rectangle are drawn; the rectangle is not filled in. </p>
	 * 
	 * @param x - The x coordinate to draw to (of the top-left vertex)
	 * @param y - The y coordinate to draw to (of the top-left vertex)
	 * @param width - The width of the rectangle (in pixels)
	 * @param height - The height of the rectangle (in pixels)
	 */
	public void drawRect(float x, float y, float width, float height) {
		drawRect(x, y, width, height, 0.0f);
	}

	/**
	 * <pre>public void drawRect(float x, float y, float width, float height, float rot)</pre>
	 * 
	 * <p> Draws a rectangle with the top-left vertex at (x, y), and with the specified width and height. The
	 * rectangle is rotated the specified amount in the counter-clockwise direction. Note that only the edges of
	 * the rectangle are drawn; the rectangle is not filled in. </p>
	 * 
	 * @param x - The x coordinate of the top-left vertex
	 * @param y - The y coordinate of the top-left vertex
	 * @param width - The width of the rectangle
	 * @param height - The height of the rectangle
	 * @param rot - The amount (in degrees) that the rectangle should be rotated
	 */
	public void drawRect(float x, float y, float width, float height, float rot) {
		this.currentColor.setAsCurrentOpenGLColor();

		GL11.glPushMatrix();
		GL11.glTranslatef(x, window.getHeight() - y - height, 0.0f);
		GL11.glRotatef(rot, 0.0f, 0.0f, 1.0f);
		GL11.glScalef(width, height, 1.0f);

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, this.rectID);
		GL11.glVertexPointer(2, GL11.GL_FLOAT, 0, 0L);

		GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
		GL11.glDrawArrays(GL11.GL_LINE_STRIP, 0, 5);
		GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL11.glPopMatrix();
	}

	/**
	 * <pre>public void fillRect(float x, float y, float width, float height)</pre>
	 * 
	 * <p> Draws a filled rectangle with the top-left vertex at (x, y), and with the specified width, and height.
	 * This method is identical to the {@link #drawRect(int, int, int, int) drawRect()} method, except the
	 * rectangle is filled in. </p>
	 * 
	 * @param x - The x coordinate to draw to (of the top-left vertex)
	 * @param y - The y coordinate to draw to (of the top-left vertex)
	 * @param width - The width of the rectangle (in pixels)
	 * @param height - The height of the rectangle (in pixels)
	 */
	public void fillRect(float x, float y, float width, float height) {
		fillRect(x, y, width, height, 0.0f);
	}

	/**
	 * <pre>public void fillRect(float x, float y, float width, float height, float rot)</pre>
	 * 
	 * <p> Draws a filled rectangle width the top-left vertex at (x, y), and with the specified width and height.
	 * The rectangle is rotated the specified amount in the counter-clockwise direction. This method is identical
	 * to the {@link #drawRect(int, int, int, int, float) drawRect()} method, except the rectangle is filled in.
	 * </p>
	 * 
	 * @param x - The x coordinate to draw to (of the top-left vertex)
	 * @param y - The y coordinate to draw to (of the top-left vertex)
	 * @param width - The width of the rectangle (in pixels)
	 * @param height - The height of the rectangle (in pixels)
	 * @param rot - The amount (in degrees) that the rectangle should be rotated
	 */
	public void fillRect(float x, float y, float width, float height, float rot) {
		this.currentColor.setAsCurrentOpenGLColor();

		GL11.glPushMatrix();
		GL11.glTranslatef(x, window.getHeight() - y - height, 0.0f);
		GL11.glRotatef(rot, 0.0f, 0.0f, 1.0f);
		GL11.glScalef(width, height, 1.0f);

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, this.rectID);
		GL11.glVertexPointer(2, GL11.GL_FLOAT, 0, 0L);

		GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
		GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, 5);
		GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL11.glPopMatrix();
	}

	/**
	 * <pre>public void drawTexture({@link Texture Texture} texture, float x, float y)</pre>
	 * 
	 * <p> Draws a <code>Texture</code> at the given coordinates. </p>
	 * 
	 * @param texture - The texture to be drawn
	 * @param x - The x coordinate to draw to
	 * @param y - The y coordinate to draw to
	 */
	public void drawTexture(Texture texture, float x, float y) {
		drawTexture(texture, x, y, texture.getWidth(), texture.getHeight());
	}

	/**
	 * <pre>public void drawTexture({@link Texture Texture} texture, float x, float y, float width, float
	 * height)</pre>
	 * 
	 * <p> Draws a <code>Texture</code> at the given coordinates and scaled to the given size. </p>
	 * 
	 * @param texture - The texture to be drawn
	 * @param x - The x coordinate to draw to (of the top-left vertex)
	 * @param y - The y coordinate to draw to (of the top-left vertex)
	 * @param width - The width of the texture (in pixels)
	 * @param height - The height of the texture (in pixels)
	 */
	public void drawTexture(Texture texture, float x, float y, float width, float height) {
		drawTexture(texture, x, y, width, height, 0.0f);
	}

	/**
	 * <pre>public void drawTexture({@link Texture Texture} texture, float x, float y, float width, float height,
	 * float rot)</pre>
	 * 
	 * <p> Draws a <code>Texture</code> at the given coordinates, scaled to the given size, and rotated the given
	 * amount (in degrees). </p>
	 * 
	 * @param texture - The texture to be drawn
	 * @param x - The x coordinate to draw to (of the top-left vertex)
	 * @param y - The y coordinate to draw to (of the top-left vertex)
	 * @param width - The width of the texture (in pixels)
	 * @param height - The height of the texture (in pixels)
	 * @param rot - The amount (in degrees) this texture should be rotated
	 * 
	 * @throws NullPointerException If the texture is null
	 */
	public void drawTexture(Texture texture, float x, float y, float width, float height, float rot) {
		
		if (texture == null) {
			throw new NullPointerException("Teture is null");
		}
		
		if (texture != null) {
			if (this.filter != null) {
				this.filter.setAsCurrentOpenGLColor();
			} else {
				GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
			}
			GL11.glEnable(GL11.GL_TEXTURE_2D);
	
			GL11.glPushMatrix();
			GL11.glTranslatef(x, this.window.getHeight() - y - height, 0.0f);
			GL11.glRotatef(rot, 0.0f, 0.0f, 1.0f);
			GL11.glScalef(width / texture.getWidth(), height / texture.getHeight(), 1.0f);
	
			texture.render();
	
			GL11.glPopMatrix();
	
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			
			if (this.currentColor != null) {
				this.currentColor.setAsCurrentOpenGLColor();
			}
		}
	}

	/**
	 * <pre>public void drawCircle(int centerX, int centerY, int radius)</pre>
	 * 
	 * <p> Draws a circle with the given center and radius. </p>
	 * 
	 * @param centerX - The x-coordinate of the circle's center
	 * @param centerY - The y-coordinate of the circle's center
	 * @param radius - The radius of the circle
	 */
	public void drawCircle(int centerX, int centerY, int radius) {
		this.currentColor.setAsCurrentOpenGLColor();

		GL11.glPushMatrix();
		GL11.glTranslatef(centerX, centerY, 0.0f);
		GL11.glScalef(radius, radius, 1.0f);

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, this.circleID);
		GL11.glVertexPointer(2, GL11.GL_FLOAT, 0, 0L);

		GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
		GL11.glDrawArrays(GL11.GL_LINE_STRIP, 1, 361);
		GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL11.glPopMatrix();
	}

	/**
	 * <pre>public void fillCircle(int centerX, int centerY, int radius)</pre>
	 * 
	 * <p> Draws a filled circle witht he givne center and radius. This is identical to the
	 * {@link #drawCircle(int, int, int) drawCircle()} method, except the circle is filled in. </p>
	 * 
	 * @param centerX - The x-coordinate of the circle's center
	 * @param centerY - The y-coordinate of the circle's center
	 * @param radius - The radius of the circle
	 */
	public void fillCircle(int centerX, int centerY, int radius) {
		this.currentColor.setAsCurrentOpenGLColor();

		GL11.glPushMatrix();
		GL11.glTranslatef(centerX, window.getHeight() - centerY - radius, 0.0f);
		GL11.glScalef(radius, radius, 1.0f);

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, this.circleID);
		GL11.glVertexPointer(2, GL11.GL_FLOAT, 0, 0L);

		GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
		GL11.glDrawArrays(GL11.GL_POLYGON, 0, 362);
		GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL11.glPopMatrix();
	}
	
	/**
	 * <pre>public void drawAnimation({@link Animation Animation} animation, float x, float y)</pre>
	 * 
	 * <p> Draws an <code>Animation</code> at the given location. </p>
	 * 
	 * @param animation - The <code>Animation</code> to draw
	 * @param x - The x-coordinate to draw to
	 * @param y - The y-coordinate to draw to
	 */
	public void drawAnimation(Animation animation, float x, float y) {
		animation.start();
		drawTexture(animation.update(), x, y);
	}
	
	/**
	 * <pre>public void drawAnimation({@link Animation Animation} animation, float x, float y, float width, float height)</pre>
	 * 
	 * <p> Draws an <code>Animation</code> at the given location with its textures scaled to the given size. </p>
	 * 
	 * @param animation - The <code>Animation</code> to draw
	 * @param x - The x-coordinate to draw to
	 * @param y - The y-coordinate to draw to
	 * @param width - The width of the animation's textures (in pixels)
	 * @param height - The height of the animation's textures (in pixels)
	 */
	public void drawAnimation(Animation animation, float x, float y, float width, float height) {
		animation.start();
		drawTexture(animation.update(), x, y, width, height);
	}
	
	/**
	 * <pre>public void drawAnimation({@link Animation Animation} animation, float x, float y, float width, float height, float rot)</pre>
	 * 
	 * <p> Draws an <code>Animation</code> at the given location with its textures scaled to the given size. </p>
	 * 
	 * @param animation - The <code>Animation</code> to draw
	 * @param x - The x-coordinate to draw to
	 * @param y - The y-coordinate to draw to
	 * @param width - The width of the animation's textures (in pixels)
	 * @param height - The height of the animation's textures (in pixels)
	 * @param rot - The amount (in degrees) the textures of this animation should be rotated
	 */
	public void drawAnimation(Animation animation, float x, float y, float width, float height,
			float rot) {
		animation.start();
		drawTexture(animation.update(), x, y, width, height, rot);
	}
	
	/**
	 * <pre>public void drawText({@link String String} text, float x, float y)</pre>
	 * 
	 * <p> Draws the given text at the specified location using this <code>Renderer</code>'s current font. </p>
	 * 
	 * @param text - The text to draw
	 * @param x - The x-coordinate to draw to
	 * @param y - The y-coordinate to draw to
	 */
	public void drawText(String text, float x, float y) {
		this.currentFont.drawText(this, text, this.currentColor, x, y);
	}
	
	/**
	 * <pre>public void drawText({@link String String} text, float x, float y, float width, float height)</pre>
	 * 
	 * <p> Draws the given text at the specified location using this <code>Renderer</code>'s current font. </p>
	 * 
	 * @param text - The text to draw
	 * @param x - The x-coordinate to draw to
	 * @param y - The y-coordinate to draw to
	 * @param width - The maximum width of a line (in pixels)
	 * @param height - The maximum height of a line (in pixels)
	 */
	public void drawText(String text, float x, float y, float width, float height) {
		this.currentFont.drawText(this, text, this.currentColor, x, y, width, height);
	}
	
	/**
	 * <pre>public void setFont({@link TrueTypeFont TrueTypeFont} newFont)</pre>
	 * 
	 * <p> Sets the <code>TrueTypeFont</code> used for rendering text. </p>
	 * 
	 * @param newFont - The new <code>TrueTypeFont</code>
	 */
	public void setFont(TrueTypeFont newFont) {
		this.currentFont = newFont;
	}
	
	/**
	 * <pre>public {@link String String} getCurrentFontName()</pre>
	 * 
	 * <p> Gets the name of this <code>Renderer</code>'s current {@link TrueTypeFont TrueTypeFont}. </p>
	 * 
	 * @return The current font <code>TrueTypeFont</code>
	 */
	public String getCurrentFontName() {
		return this.currentFont.toString();
	}

	/**
	 * <pre>public {@link Color Color} getColor()</pre>
	 * 
	 * <p> Gets the <code>Color</code> that this <code>Renderer</code> uses while rendering. </p>
	 * 
	 * @return The <code>Color</code>
	 */
	public Color getColor() {
		return this.currentColor;
	}

	/**
	 * <pre>public void setColor({@link Color Color} newColor)</pre>
	 * 
	 * <p> Sets the <code>Color</code> this <code>Renderer</code> will use when rendering. </p>
	 * 
	 * @param newColor - The new <code>Color</code> this <code>Renderer</code> will use
	 */
	public void setColor(Color newColor) {
		this.currentColor = newColor;
	}
	
	/**
	 * <pre>public {@link Color Color} getFilter()</pre>
	 * 
	 * <p> Returns the current texture filter. </p>
	 * 
	 * @return The current texture filter
	 */
	public Color getFilter() {
		return this.filter;
	}
	
	/**
	 * <pre>public void setFilter({@link Color Color} color)</pre>
	 * 
	 * <p> Sets the current texture filter to a new <code>Color</code>. </p>
	 * 
	 * @param color - The new <code>Color</code> this <code>Renderer</code>'s texture filter will use
	 */
	public void setFilter(Color color) {
		this.filter = color;
	}
	
	/**
	 * <pre>public void destroy()</pre>
	 * 
	 * <p> Frees any resources belonging to this <code>Renderer</code>. </p>
	 */
	public void destroy() {
		GL15.glDeleteBuffers(this.rectID);
		GL15.glDeleteBuffers(this.circleID);
		DEFAULT_FONT.destroy();
	}
}
