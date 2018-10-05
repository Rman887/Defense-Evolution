package com.rman.engine.graphics;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GLContext;

import com.rman.engine.Log;

/**
 * An OpenGL texture.
 * 
 * @author Arman
 */
public class Texture {
	/**
	 * The bits per pixel of this texture.
	 */
	private static final int BPP = 4;

	/**
	 * The name of this texture.
	 */
	private String name;

	/**
	 * The OpenGL ID of this texture.
	 */
	private int id;

	/**
	 * The width of this texture.
	 */
	private float width;
	/**
	 * The height of this texture.
	 */
	private float height;

	/**
	 * The ID of this texture's vertex buffer object
	 */
	private int vboID;
	
	private int numRows;
	private int numColumns;

	/**
	 * <pre>public Texture({@link String String} name, {@link URL URL} path)</pre>
	 * 
	 * <p> Constructs a new OpenGL texture from the given location. </p>
	 * 
	 * @param name - The name of the texture (ex. Brick, Grass, Wood)
	 * @param path - The path of the texture (where it is located)
	 */
	public Texture(String name, URL path) {
		this.name = name;
		this.id = GL11.glGenTextures();
		this.numRows = 1; this.numColumns = 1;
		Log.log("Creating texture: " + this);

		bind();

		GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);

		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		if (GLContext.getCapabilities().OpenGL12) {
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
		} else {
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP);
		}

		ByteBuffer data = null;
		try {
			BufferedImage test = ImageIO.read(path);
			data = loadImage(test);
		} catch (Exception e) {
			Log.logError("Error creating texture: " + this.name, e);
		}
		
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, (int) this.width, (int) this.height, 0,
				GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, data);

		FloatBuffer coordinates = BufferUtils.createFloatBuffer(16);
		coordinates.put(new float[] {0.0f, 0.0f, 0.0f, 1.0f, this.width, 0.0f, 1.0f, 1.0f,
				this.width, this.height, 1.0f, 0.0f, 0.0f, this.height, 0.0f, 0.0f});
		coordinates.flip();

		this.vboID = GL15.glGenBuffers();

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, this.vboID);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, coordinates, GL15.GL_STATIC_DRAW);
		GL11.glVertexPointer(2, GL11.GL_FLOAT, 16, 0L);
		GL11.glTexCoordPointer(2, GL11.GL_FLOAT, 16, 8L);

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

		bind();
	}
	
	public Texture(String name, BufferedImage image) {
		this.name = name;
		this.id = GL11.glGenTextures();
		this.numRows = 1; this.numColumns = 1;
		Log.log("Creating texture: " + this);

		bind();

		GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);

		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		if (GLContext.getCapabilities().OpenGL12) {
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
		} else {
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP);
		}

		ByteBuffer data = null;
		try {
			data = loadImage(image);
		} catch (Exception e) {
			Log.logError("Error creating texture: " + this.name, e);
		}
		
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, (int) this.width, (int) this.height, 0,
				GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, data);

		FloatBuffer coordinates = BufferUtils.createFloatBuffer(16);
		coordinates.put(new float[] {0.0f, 0.0f, 0.0f, 1.0f, this.width, 0.0f, 1.0f, 1.0f,
				this.width, this.height, 1.0f, 0.0f, 0.0f, this.height, 0.0f, 0.0f});
		coordinates.flip();

		this.vboID = GL15.glGenBuffers();

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, this.vboID);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, coordinates, GL15.GL_STATIC_DRAW);
		GL11.glVertexPointer(2, GL11.GL_FLOAT, 16, 0L);
		GL11.glTexCoordPointer(2, GL11.GL_FLOAT, 16, 8L);

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

		bind();
	}
	
	public Texture(String name, ByteBuffer data, float width, float height) {
		this.name = name;
		this.width = width;
		this.height = height;
		this.id = GL11.glGenTextures();
		this.numRows = 1; this.numColumns = 1;
		Log.log("Creating texture: " + this);

		bind();

		GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);

		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		if (GLContext.getCapabilities().OpenGL12) {
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
		} else {
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP);
		}
		
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, (int) this.width, (int) this.height, 0,
				GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, data);

		FloatBuffer coordinates = BufferUtils.createFloatBuffer(16);
		coordinates.put(new float[] {0.0f, 0.0f, 0.0f, 1.0f, this.width, 0.0f, 1.0f, 1.0f,
				this.width, this.height, 1.0f, 0.0f, 0.0f, this.height, 0.0f, 0.0f});
		coordinates.flip();

		this.vboID = GL15.glGenBuffers();

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, this.vboID);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, coordinates, GL15.GL_STATIC_DRAW);
		GL11.glVertexPointer(2, GL11.GL_FLOAT, 16, 0L);
		GL11.glTexCoordPointer(2, GL11.GL_FLOAT, 16, 8L);

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

		bind();
	}
	
	/**
	 * <pre>public Texture({@link String String} name, {@link Texture Texture} texture, float subX, float subY, float subWidth, float subHeight)</pre>
	 * 
	 * <p> Constructs a sub-texture of the given <code>Texture</code>. Note that the x and y coordinates are relative to
	 * the parent texture. </p>
	 * 
	 * @param name - The name of the sub-texture
	 * @param texture - The texture to create sub-texture of (parent texture)
	 * @param subX - The x coordinate of the sub-texture (relative to the texture)
	 * @param subY - The y coordinate of the sub-texture (relative to the texture)
	 * @param subWidth - The width of the sub-texture
	 * @param subHeight - The height of the sub-texture
	 */
	public Texture(String name, Texture texture, float subX, float subY, float subWidth,
			float subHeight) {
		this.name = name;
		this.id = texture.getTextureID();
		this.width = subWidth;
		this.height = subHeight;
		this.numRows = 1; this.numColumns = 1;

		FloatBuffer coordinates = BufferUtils.createFloatBuffer(16);
		coordinates.put(new float[] {0.0f, 0.0f, subX / texture.getWidth(),
				(subY + this.height) / texture.getHeight(), this.width, 0.0f,
				(subX + this.width) / texture.getWidth(),
				(subY + this.height) / texture.getHeight(), this.width, this.height,
				(subX + this.width) / texture.getWidth(), subY / texture.getHeight(), 0.0f,
				this.height, subX / texture.getWidth(), subY / texture.getHeight()});
		coordinates.flip();

		this.vboID = GL15.glGenBuffers();

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, this.vboID);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, coordinates, GL15.GL_STATIC_DRAW);
		GL11.glVertexPointer(2, GL11.GL_FLOAT, 16, 0L);
		GL11.glTexCoordPointer(2, GL11.GL_FLOAT, 16, 8L);

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}
	
	/**
	 * <pre>public Texture({@link String String} name, {@link URL URL} path, int numRows, int numColumns)</pre>
	 * 
	 * <p> Creates a texture by stacking the given texture into the given number of rows and columns. </p>
	 * 
	 * @param name - The name of the stacked texture
	 * @param texture - The path to the texture to stack (parent texture)
	 * @param numRows - The number of rows to stack
	 * @param numColumns - The number of columns to stack
	 */
	public Texture(String name, URL path, int numRows, int numColumns) {
		this.name = name;
		this.id = GL11.glGenTextures();
		this.numRows = 1; this.numColumns = 1;
		Log.log("Creating texture: " + this);

		ByteBuffer data = null;
		try {
			BufferedImage originalImage = ImageIO.read(path);
			BufferedImage stackedImage = new BufferedImage(originalImage.getWidth() * numColumns, originalImage.getHeight() * numRows, BufferedImage.TYPE_INT_ARGB);
			
			Graphics g = stackedImage.getGraphics();
			for (int row = 0; row < numRows; row++) {
				for (int col = 0; col < numColumns; col++) {
					g.drawImage(originalImage, col * originalImage.getWidth(), row * originalImage.getHeight(), null);
				}
			}
			data = loadImage(stackedImage);
			g.dispose();
		} catch (Exception e) {
			Log.logError("Error creating stacked texture: " + name, e);
		}
		
		setUpTexture(data);
	}
	
	private void setUpTexture(ByteBuffer data) {
		bind();
		
		GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);

		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		if (GLContext.getCapabilities().OpenGL12) {
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
		} else {
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP);
		}
		
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, (int) this.width, (int) this.height, 0,
				GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, data);

		FloatBuffer coordinates = BufferUtils.createFloatBuffer(16);
		coordinates.put(new float[] {0.0f, 0.0f, 0.0f, 1.0f, this.width, 0.0f, 1.0f, 1.0f,
				this.width, this.height, 1.0f, 0.0f, 0.0f, this.height, 0.0f, 0.0f});
		coordinates.flip();

		this.vboID = GL15.glGenBuffers();

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, this.vboID);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, coordinates, GL15.GL_STATIC_DRAW);
		GL11.glVertexPointer(2, GL11.GL_FLOAT, 16, 0L);
		GL11.glTexCoordPointer(2, GL11.GL_FLOAT, 16, 8L);

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

		bind();
	}

	/**
	 * <pre>public void bind()</pre>
	 * 
	 * <p> Binds the texture. If the texture is already bound, then the texture is unbound. </p>
	 */
	public void bind() {
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.id);
	}

	/**
	 * <pre>public {@link ByteBuffer ByteBuffer} loadImage({@link BufferedImage BufferedImage} image) throws {@link java.io.IOException
	 * IOException}</pre>
	 * 
	 * <p> Loads this texture's contents from a <code>BufferedImage</code>. </p>
	 * 
	 * @param image - The <code>BufferedImage</code> to load data from
	 * 
	 * @return A <code>ByteBuffer</code> that contains this texture's data
	 * 
	 * @throws IOException
	 */
	public ByteBuffer loadImage(BufferedImage image) throws IOException {
	    this.width = image.getWidth();
	    this.height = image.getHeight();
		int[] pixels = new int[image.getWidth() * image.getHeight()];
		image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());

		ByteBuffer buffer = BufferUtils.createByteBuffer(image.getWidth() * image.getHeight() * BPP);
	        
		for(int y = 0; y < image.getHeight(); y++){
		    for(int x = 0; x < image.getWidth(); x++){
		        int pixel = pixels[y * image.getWidth() + x];
		        buffer.put((byte) ((pixel >> 16) & 0xFF));
		        buffer.put((byte) ((pixel >> 8) & 0xFF));
		        buffer.put((byte) (pixel & 0xFF));
		        buffer.put((byte) ((pixel >> 24) & 0xFF));
		    }
		}
		
		buffer.flip();
		return buffer;
	}

	/**
	 * <pre>public void delete()</pre>
	 * 
	 * <p> Deletes this texture's IDs. </p>
	 */
	public void delete() {
		Log.log("Deleting texture: " + this);
		GL15.glDeleteBuffers(this.vboID);
		GL11.glDeleteTextures(this.id);
	}

	/**
	 * <pre>public float getWidth()</pre>
	 * 
	 * <p> Gets the current width of this texture. </p>
	 * 
	 * @return The width of this texture
	 */
	public float getWidth() {
		return this.width;
	}

	/**
	 * <pre>public void setWidth(float newWidth)</pre>
	 * 
	 * <p> Sets the width of this texture to the given amount. </p>
	 * 
	 * @param newWidth - The new width of this texture
	 */
	public void setWidth(float newWidth) {
		this.width = newWidth;
	}

	/**
	 * <pre>public float getHeight()</pre>
	 * 
	 * <p> Gets the current height of this texture </p>
	 * 
	 * @return The height of this texture
	 */
	public float getHeight() {
		return this.height;
	}

	/**
	 * <pre>public void setHeight(float newHeight)</pre>
	 * 
	 * <p> Sets the height of this texture to the given amount. </p>
	 * 
	 * @param newHeight - The new height of this texture
	 */
	public void setHeight(float newHeight) {
		this.height = newHeight;
	}
	
	/**
	 * <pre>public int getTextureID()</pre>
	 * 
	 * <p> Gets the OpenGL texture ID of this texture. </p>
	 * 
	 * @return The texture ID
	 */
	public int getTextureID() {
		return this.id;
	}
	
	/**
	 * <pre>public int getVBOID()</pre>
	 * 
	 * <p> Gets the vertex buffer object (VBO) ID of this texture. </p>
	 * 
	 * @return The VBO id
	 */
	public int getVBOID() {
		return this.vboID;
	}
	
	public Texture getSubTexture(int x, int y, int width, int height) {
		return new Texture(this.name, this, (float) x, (float) y, (float) width, (float) height);
	}

	/**
	 * <pre>public void render()</pre>
	 * 
	 * <p> Renders this texture. </p>
	 */
	public void render() {
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, this.vboID);
		GL11.glVertexPointer(2, GL11.GL_FLOAT, 16, 0L);
		GL11.glTexCoordPointer(2, GL11.GL_FLOAT, 16, 8L);

		GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
		GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);

		bind();

		GL11.glDrawArrays(GL11.GL_QUADS, 0, 8 * this.numRows * this.numColumns);

		bind();

		GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
		GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}

	@Override
	public String toString() {
		return this.name + " (id = " + this.id + ")";
	}
}
