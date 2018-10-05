package com.rman.de.core;

import java.util.Arrays;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;

public class WaveInfo {
	private Queue<Spawn> spawns;
	private int numPositions;
	
	public WaveInfo(int numPositions) {
		this.spawns = new PriorityQueue<Spawn>();
		this.numPositions = numPositions;
	}
	
	public void addSpawn(double time, Iterable<String> units) {
		Spawn spawn = new Spawn(time, this.numPositions);
		for (String unit : units) {
			spawn.addSpawnPos(unit);
		}
		this.spawns.add(spawn);
	}
	
	public boolean isDone() {
		return this.spawns.size() == 0;
	}
	
	public double nextTime() {
		if (isDone())
			return -1.0;
		return this.spawns.peek().time;
	}
	
	public String[] extractNextSpawn() {
		if (isDone())
			return null;
		return this.spawns.poll().positions;
	}
	
	private static class Spawn implements Comparable<Spawn> {
		double time;
		String[] positions;
		int numFilled;
		Random rand;
		
		public Spawn(double time, int numPositions) {
			this.time = time;
			this.positions = new String[numPositions * 2];
			Arrays.fill(this.positions, "");
			this.numFilled = 0;
			this.rand = new Random();
		}
		
		public void addSpawnPos(String unitType) {
			if (this.numFilled < this.positions.length) {
				int pos = this.rand.nextInt(this.positions.length);
				
				while (this.positions[pos] != "") {
					pos = (pos + 1) % this.positions.length;
				}
				this.positions[pos] = unitType;
				this.numFilled++;
			}
		}
		
		public int compareTo(Spawn other) {
			if (this.time < other.time) {
				return -1;
			} else if (this.time > other.time) {
				return 1;
			}
			return 0;
		}
	}
}
