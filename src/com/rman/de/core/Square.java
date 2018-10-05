package com.rman.de.core;

import com.rman.engine.Entity;

public class Square {
	public Entity entity;
	
	public int x;
	public int y;
	public int width;
	public int height;
	
	public int row;
	public int column;
	
	public Square(Entity entity, int x, int y, int width, int height, int row, int column) {
		this.entity = entity;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.row = row;
		this.column = column;
	}
}
