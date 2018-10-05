package com.rman.engine.graphics;

import java.net.URL;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL10;
import org.lwjgl.util.WaveData;

public class Sound {

	private IntBuffer buffer = BufferUtils.createIntBuffer(1);
	private IntBuffer source = BufferUtils.createIntBuffer(1);

	private FloatBuffer sourcePos = BufferUtils.createFloatBuffer(3);
	private FloatBuffer sourceVel = BufferUtils.createFloatBuffer(3);

	private FloatBuffer listenerPos = BufferUtils.createFloatBuffer(3);
	private FloatBuffer listenerVel = BufferUtils.createFloatBuffer(3);
	private FloatBuffer listenerOri = BufferUtils.createFloatBuffer(6);

	public Sound(URL path) {
		this(path, 0.0f, 0.0f);
	}

	public Sound(URL path, float sourceX, float sourceY) {
		AL10.alGenBuffers(this.buffer);

		this.sourcePos.put(new float[] {sourceX, sourceY, 0.0f});
		this.sourcePos.flip();
		this.sourceVel.put(new float[] {0.0f, 0.0f, 0.1f});
		this.sourceVel.flip();

		this.listenerPos.put(new float[] {0.0f, 0.0f, 0.0f});
		this.listenerPos.flip();
		this.listenerVel.put(new float[] {0.0f, 0.0f, 0.0f});
		this.listenerVel.flip();
		this.listenerOri.put(new float[] {0.0f, 0.0f, -1.0f, 0.0f, 1.0f, 0.0f});
		this.listenerOri.flip();

		WaveData waveFile = WaveData.create(path);

		AL10.alBufferData(this.buffer.get(0), waveFile.format, waveFile.data, waveFile.samplerate);
		waveFile.dispose();

		AL10.alGenSources(this.source);

		AL10.alSourcei(this.source.get(0), AL10.AL_BUFFER, this.buffer.get(0));
		AL10.alSourcef(this.source.get(0), AL10.AL_PITCH, 1.0f);
		AL10.alSourcef(this.source.get(0), AL10.AL_GAIN, 1.0f);
		AL10.alSource(this.source.get(0), AL10.AL_POSITION, this.sourcePos);
		AL10.alSource(this.source.get(0), AL10.AL_VELOCITY, this.sourceVel);

		AL10.alListener(AL10.AL_POSITION, this.listenerPos);
		AL10.alListener(AL10.AL_VELOCITY, this.listenerVel);
		AL10.alListener(AL10.AL_ORIENTATION, this.listenerOri);
		
		stop();
	}
	
	public void setLoop(boolean loop) {
		AL10.alSourcei(this.source.get(0), AL10.AL_LOOPING, (loop ? AL10.AL_TRUE : AL10.AL_FALSE));
	}
	
	public void play() {
		AL10.alSourcePlay(this.source.get(0));
	}

	public void pause() {
		AL10.alSourcePause(this.source.get(0));
	}

	public void stop() {
		AL10.alSourceStop(this.source.get(0));
	}

	public void destroy() {
		AL10.alDeleteBuffers(this.buffer);
		AL10.alDeleteSources(this.source);
	}

}
