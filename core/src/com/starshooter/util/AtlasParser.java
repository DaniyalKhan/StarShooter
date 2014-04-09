package com.starshooter.util;

import java.io.IOException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.XmlReader;
import com.starshooter.StarShooter;

public class AtlasParser {

	public static void parse(FileHandle file) throws IOException {
		TextureCache cache = TextureCache.obtain();
		XmlReader reader = new XmlReader();
		XmlReader.Element root = reader.parse(file);
		Texture texture = new Texture(StarShooter.DIR_GRAPHICS +  Gdx.files.internal(root.getAttribute("imagePath")));
		texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		for (int i = 0; i < root.getChildCount(); i++) {
			XmlReader.Element subTexture = root.getChild(i);
			cache.cache(removeExtension(subTexture.get("name")), new TextureRegion(texture, subTexture.getInt("x"), subTexture.getInt("y"), 
					subTexture.getInt("width"), subTexture.getInt("height")));
		}
	}
	
	private static String removeExtension(String s) {
		int dotIndex = s.lastIndexOf('.');
		return s.substring(0, dotIndex);
	}
	
}
