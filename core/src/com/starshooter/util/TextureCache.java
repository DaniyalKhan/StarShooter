package com.starshooter.util;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.ObjectMap;

public class TextureCache {

	private static TextureCache instance;
	private ObjectMap<String, TextureRegion> cache;
	
	private TextureCache() {
		cache = new ObjectMap<String, TextureRegion>();
	}
	
	public static TextureCache obtain() {
		if (instance == null) instance = new TextureCache();
		return instance;
	}
	
	public TextureRegion get(String filename) {
		if (!cache.containsKey(filename)) throw new RuntimeException("Error, cache does not contain texture region: " + filename);
		return cache.get(filename);
	}
	
	public void cache(String filename, TextureRegion region) {
		cache.put(filename, region);
	}

}
