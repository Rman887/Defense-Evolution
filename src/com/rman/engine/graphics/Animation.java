package com.rman.engine.graphics;

import java.util.ArrayList;

import org.lwjgl.util.Point;

/**
 * An animation of a sprite.
 * 
 * @author Arman
 */
public class Animation {

	/**
	 * The name of the animation.
	 */
	private String name;
	/**
	 * The {@link SpriteSheet SpriteSheet} used by this animation.
	 */
	private SpriteSheet spriteSheet;
	/**
	 * An {@link ArrayList ArrayList} of frames used by this animation.
	 */
	private ArrayList<Frame> clip;

	/**
	 * Whether this animation is playing or is stopped.
	 */
	private volatile boolean isPlaying = true;
	/**
	 * If this animation should replay after finishing.
	 */
	private boolean shouldLoop;
	/**
	 * The index of the current frame the animation is on.
	 */
	private int currentFrame;
	/**
	 * The time that this animation was last updated (advanced a frame).
	 */
	private long lastUpdate;

	/**
	 * <pre>public Animation({@link String String} name, {@link Texture Texture} texture, int tileWidth, int
	 * tileHeight, double duration)</pre>
	 * 
	 * <p> Constructs a new <code>Animation</code> with the given texture. A {@link SpriteSheet SpriteSheet} is
	 * automatically made. </p>
	 * 
	 * @param name - The name of this animation
	 * @param texture - The texture to be used (this will be converted into a <code>SpriteSheet</code>
	 * @param tileWidth - The width of each tile in the texture
	 * @param tileHeight - The height of each tile in the texture
	 * @param duration - The initial duration of each frame (in milliseconds)
	 */
	public Animation(String name, Texture texture, int tileWidth, int tileHeight, double duration) {
		this(name, new SpriteSheet(texture, tileWidth, tileHeight), duration);
	}

	/**
	 * <pre>public Animation({@link String String} name, {@link SpriteSheet SpriteSheet} spriteSheet, double
	 * duration)</pre>
	 * 
	 * <p> Constructs a new <code>Animation</code> with the given <code>SpriteSheet</code>. </p>
	 * 
	 * @param name - The name of this animation
	 * @param spriteSheet - The <code>SpriteSheet</code> to be used
	 * @param duration - The duration of each frame (in milliseconds)
	 */
	public Animation(String name, SpriteSheet spriteSheet, double duration) {
		this(name, spriteSheet, duration, true);
	}

	/**
	 * <pre>public Animation({@link String String} name, {@link SpriteSheet SpriteSheet} spriteSheet, double
	 * duration, boolean loop)</pre>
	 * 
	 * <p> Constructs a new <code>Animation</code> with the given <code>SpriteSheet</code>. </p>
	 * 
	 * @param name - The name of this animation
	 * @param spriteSheet - The <code>SpriteSheet</code> to be used
	 * @param duration - The initial duration of each frame (in milliseconds)
	 * @param loop - Whether this animation should replay after finishing
	 */
	public Animation(String name, SpriteSheet spriteSheet, double duration, boolean loop) {
		this(name, spriteSheet, duration, loop, null);
	}

	/**
	 * <pre>public Animation({@link String String} name, {@link SpriteSheet SpriteSheet} spriteSheet, double
	 * duration, boolean loop, {@link Point Point}[] frames)</pre>
	 * 
	 * <p> Constructs a new <code>Animation</code> using the indicated frames of the given
	 * <code>SpriteSheet</code>. </p>
	 * 
	 * @param name - The name of this animation
	 * @param spriteSheet - The <code>SpriteSheet</code> to be used
	 * @param duration - The initial duration of each frame (in milliseconds)
	 * @param loop - Whether this animation should replay after finishing
	 * @param frames - The frames that this animation will play from the <code>SpriteSheet</code>
	 */
	public Animation(String name, SpriteSheet spriteSheet, double duration, boolean loop,
			Point[] frames) {
		this.name = name;
		this.spriteSheet = spriteSheet;
		this.shouldLoop = loop;

		this.currentFrame = 0;
		this.lastUpdate = System.nanoTime();
		this.clip = new ArrayList<Frame>();

		if (frames == null) { // No frames specified, so use all sprites in the spritesheet
			for (int row = 0; row < spriteSheet.getNumRows(); row++) {
				for (int column = 0; column < spriteSheet.getNumColumns(); column++) {
					addFrame(duration, row, column);
				}
			}
		} else {
			for (Point frameIndex : frames) {
				addFrame(duration, frameIndex.getX(), frameIndex.getY());
			}
		}
	}

	/**
	 * <pre>public void addFrame(double duration, int row, int column)</pre>
	 * 
	 * <p> Adds a specified frame from this animation's {@link SpriteSheet SpriteSheet}. </p>
	 * 
	 * @param duration - The duration of the frame (in milliseconds)
	 * @param row - The row of the frame in the <code>SpriteSheet</code>
	 * @param column - The column of the frame in the <code>SpriteSheet</code>
	 */
	public void addFrame(double duration, int row, int column) {
		if (duration <= 0) {
			throw new IllegalArgumentException("Error adding new frame in " + this.name
					+ " (duration must be greater than 0)");
		}
		if (row >= this.spriteSheet.getNumRows()) {
			throw new IllegalArgumentException("Error adding new frame in " + this.name
					+ " (row is too large)");
		}
		if (column >= this.spriteSheet.getNumColumns()) {
			throw new IllegalArgumentException("Error adding new frame in " + this.name
					+ " (column is too large)");
		}

		this.clip.add(new Frame(this.spriteSheet.getSprite(row, column),
				(long) (duration * 1000000L))); // milliseconds -> nanoseconds
	}

	/**
	 * <pre>public {@link Texture Texture} update()</pre>
	 * 
	 * <p> Updates the animation and returns the <code>Texture</code> of the animation's current frame. This method
	 * should not be called; use {@link Renderer#drawAnimation(Animation, float, float, float, float, float)
	 * Renderer.drawAnimation()} instead. </p>
	 * 
	 * @return This animation's current frame
	 */
	public Texture update() {
		if (!this.isPlaying || this.clip.size() == 0) {
			return null;
		}
		
		if (this.clip.size() == 1) {
			return this.clip.get(0).texture;
		}

		long timeNow = System.nanoTime();
		while ((timeNow - this.lastUpdate) >= this.clip.get(this.currentFrame).duration) {
			this.lastUpdate = timeNow;
			if (!this.shouldLoop && this.currentFrame == this.clip.size() - 1) {
				this.isPlaying = false;
				break;
			} else {
				this.currentFrame = (this.currentFrame + 1) % this.clip.size();
			}
		}

		return this.clip.get(this.currentFrame).texture;
	}

	/**
	 * <pre>public void clear()</pre>
	 * 
	 * <p> Clears this animation's clip. </p>
	 */
	public void clear() {
		this.clip.clear();
	}

	/**
	 * <pre>public boolean doesLoop()</pre>
	 * 
	 * <p> Gets whether this animation replays after finishing. <p>
	 * 
	 * @return Whether this animation loops
	 */
	public boolean doesLoop() {
		return this.shouldLoop;
	}

	/**
	 * <pre>public void setLoop(boolean shouldLoop)</pre>
	 * 
	 * <p> Sets whether this animation should replay after finishing. </p>
	 * 
	 * @param shouldLoop - Whether this animation should loop
	 */
	public void setLoop(boolean shouldLoop) {
		this.shouldLoop = shouldLoop;
	}

	/**
	 * <pre>public void start()</pre>
	 * 
	 * <p> Starts this animation if it's stopped. </p>
	 */
	public void start() {
		this.isPlaying = true;
	}

	/**
	 * <pre>public void stop()</pre>
	 * 
	 * <p> Stops this animation if it's playing. </p>
	 */
	public void stop() {
		this.isPlaying = false;
	}
	
	/**
	 * <pre>public void reset()</pre>
	 * 
	 * <p> Resets this animation (sets the current frame to 0). </p>
	 */
	public void reset() {
		this.currentFrame = 0;
	}

	/**
	 * <pre>public boolean isPlaying()</pre>
	 * 
	 * <p> Gets whether this animation is playing. </p>
	 * 
	 * @return Whether this animation is playing
	 */
	public boolean isPlaying() {
		return this.isPlaying;
	}
	
	public Texture getCurrentTexture() {
		return this.clip.get(currentFrame).texture;
	}

	/**
	 * <pre>public {@link SpriteSheet SpriteSheet} getSpriteSheet()</pre>
	 * 
	 * <p> Gets the <code>SpriteSheet</code> used by this animation. </p>
	 * 
	 * @return The <code>SpriteSheet</code>
	 */
	public SpriteSheet getSpriteSheet() {
		return this.spriteSheet;
	}

	/**
	 * <pre>public void setSpriteSheet({@link SpriteSheet SpriteSheet} newSpriteSheet)</pre>
	 * 
	 * <p> Sets the <code>SpriteSheet</code> used by this animation to the given one. </p>
	 * 
	 * @param newSpriteSheet - The new <code>SpriteSheet</code> to be used
	 */
	public void setSpriteSheet(SpriteSheet newSpriteSheet) {
		this.spriteSheet = newSpriteSheet;
	}

	/**
	 * <pre>public double getDuration(int index)</pre>
	 * 
	 * <p> Gets the duration of the frame at the given index. </p>
	 * 
	 * @param index - The index of the frame in this animation's clip
	 * 
	 * @return The duration of the frame in milliseconds
	 */
	public double getDuration(int index) {
		return (double) this.clip.get(index).duration / 1000000.0;
	}

	/**
	 * <pre>public void setDuration(int index, double duration)</pre>
	 * 
	 * <p> Sets the duration of the frame at the given index. </p>
	 * 
	 * @param index - The index of the frame
	 * @param duration - The new duration of the frame in milliseconds
	 */
	public void setDuration(int index, double duration) {
		this.clip.get(index).duration = (long) (duration * 1000000L);
	}
	
	@Override
	public String toString() {
		return "Animation: " + this.name;
	}

	/**
	 * Represents a frame in an <code>Animation</code>. The frame holds a {@link Texture Texture} and a duration
	 * (in nanoseconds).
	 * 
	 * @author Arman
	 */
	public static class Frame {
		/**
		 * The texture of this frame.
		 */
		Texture texture;
		/**
		 * This frame's duration in nanoseconds.
		 */
		long duration;
		
		/**
		 * <pre>public Frame({@link Texture Texture} texture, long duration)</pre>
		 * 
		 * <p> Constructs a new frame. </p>
		 * 
		 * @param texture - The texture of the frame
		 * @param duration - The duration of the frame in nanoseconds
		 */
		public Frame(Texture texture, long duration) {
			this.texture = texture;
			this.duration = duration;
		}
		
		@Override
		public String toString() {
			return "Texture: " + this.texture + ", duration: " + this.duration / 1000000L;
		}
	}
}
