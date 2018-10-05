package com.rman.engine.graphics;

/**
 * From LWJGL website.
 */
public class TexturedVertex {
	
	private float[] xyzw = new float[] { 0.0f, 0.0f, 0.0f, 1.0f };
	private float[] rgba = new float[] { 1.0f, 1.0f, 1.0f, 1.0f };
	private float[] uv   = new float[] { 0.0f, 0.0f };
	
	public static final int elementBytes = 4;

	public static final int positionElementCount = 4;
	public static final int colorElementCount = 4;
	public static final int textureElementCount = 2;

	public static final int positionBytesCount = positionElementCount * elementBytes;
	public static final int colorByteCount = colorElementCount * elementBytes;
	public static final int textureByteCount = textureElementCount * elementBytes;

	public static final int positionByteOffset = 0;
	public static final int colorByteOffset = positionByteOffset + positionBytesCount;
	public static final int textureByteOffset = colorByteOffset + colorByteCount;

	public static final int elementCount = positionElementCount + colorElementCount + textureElementCount;
	public static final int stride = positionBytesCount + colorByteCount+ textureByteCount;

	public void setXYZ(float x, float y, float z) {
		this.setXYZW(x, y, z, 1.0f);
	}

	public void setRGB(float r, float g, float b) {
		this.setRGBA(r, g, b, 1.0f);
	}

	public void setUV(float u, float v) {
		this.uv = new float[] { u, v };
	}

	public void setXYZW(float x, float y, float z, float w) {
		this.xyzw = new float[] { x, y, z, w };
	}

	public void setRGBA(float r, float g, float b, float a) {
		this.rgba = new float[] { r, g, b, 1.0f };
	}

	public float[] getElements() {
		float[] out = new float[TexturedVertex.elementCount];
		int i = 0;

		out[i++] = this.xyzw[0];
		out[i++] = this.xyzw[1];
		out[i++] = this.xyzw[2];
		out[i++] = this.xyzw[3];
		
		out[i++] = this.rgba[0];
		out[i++] = this.rgba[1];
		out[i++] = this.rgba[2];
		out[i++] = this.rgba[3];
		
		out[i++] = this.uv[0];
		out[i++] = this.uv[1];

		return out;
	}

	public float[] getXYZW() {
		return new float[] { this.xyzw[0], this.xyzw[1], this.xyzw[2], this.xyzw[3] };
	}

	public float[] getXYZ() {
		return new float[] { this.xyzw[0], this.xyzw[1], this.xyzw[2] };
	}

	public float[] getRGBA() {
		return new float[] { this.rgba[0], this.rgba[1], this.rgba[2], this.rgba[3] };
	}

	public float[] getRGB() {
		return new float[] { this.rgba[0], this.rgba[1], this.rgba[2] };
	}

	public float[] getUV() {
		return new float[] { this.uv[0], this.uv[1] };
	}
}
