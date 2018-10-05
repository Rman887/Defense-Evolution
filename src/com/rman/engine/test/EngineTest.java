package com.rman.engine.test;

import com.rman.engine.Game;
import com.rman.engine.graphics.Color;
import com.rman.engine.graphics.Texture;

/**
 * Tests rendering operations.
 * 
 * @author Arman
 */
public class EngineTest extends Game {
	
	private Texture texture;

	public EngineTest(String title) {
		super(title, 80, 800, 600);
	}

	@Override
	public void update(double delta) {
		
	}

	@Override
	public void render() {
		this.renderer.setColor(Color.WHITE);
		this.renderer.fillRect(0, 0, 100, 100);
	}

	@Override
	public void cleanUp() {
		this.texture.delete();
	}

	public static void main(String[] args) {
		new EngineTest("Engine Test");
	}

	@Override
	public void init() {
		this.texture = new Texture("Test Texture", this.getClass().getResource("res/guang.png"));
	}
}
