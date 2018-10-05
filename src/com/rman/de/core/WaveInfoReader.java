package com.rman.de.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

public class WaveInfoReader {
	
	private static List<WaveInfo> waves;
	
	public static void loadWaveInfoFile(DefenseEvolution de, URL path) throws IOException, URISyntaxException {
		BufferedReader in = new BufferedReader(new InputStreamReader(path.openStream()));
		
		waves = new ArrayList<WaveInfo>();
		int index = -1;
		String line = "";
		while ((line = in.readLine()) != null) {
			if (line.length() <= 1) {
				index++;
				waves.add(new WaveInfo(de.getAllowedColumns(index + 1)));
			} else if (!line.startsWith("//")) {
				StringTokenizer st = new StringTokenizer(line);
				double time = Double.parseDouble(st.nextToken());
				List<String> units = new LinkedList<String>();
				while (st.hasMoreTokens()) {
					String token = st.nextToken();
					int amount = Integer.parseInt(token.substring(0, 1));
					String unitType = token.substring(1);
					for (int count = 0; count < amount; count++) {
						units.add(unitType);
					}
				}
				waves.get(index).addSpawn(time, units);
			}
		}
		
		in.close();
	}
	
	public static WaveInfo getWaveInfo(int wave) {
		if (wave <= waves.size()) {
			return waves.get(wave - 1);
		}
		return null;
	}
}
