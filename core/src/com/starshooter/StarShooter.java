package com.starshooter;

import java.io.IOException;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.starshooter.screens.StarTerrain;
import com.starshooter.util.AtlasParser;

public class StarShooter extends Game {
	
	public static final String DIR_GRAPHICS = "graphics/";
	public static final String DIR_AUDIO = "audio/";
	public static final String DIR_BACKGROUNDS = DIR_GRAPHICS + "backgrounds/";
	
	SpriteBatch batch;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		try {
			AtlasParser.parse(Gdx.files.internal("graphics/sheet.xml"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		setScreen(new StarTerrain(batch));
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		super.render();
	}
}
