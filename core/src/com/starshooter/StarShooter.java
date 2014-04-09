package com.starshooter;

import java.io.IOException;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerAdapter;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.starship.models.StarVoyager;
import com.starshooter.util.AtlasParser;
import com.starshooter.util.TextureCache;

public class StarShooter extends ApplicationAdapter {
	public static final String DIR_GRAPHICS = "graphics/";
	public static final String DIR_BACKGROUNDS = DIR_GRAPHICS + "backgrounds/";
	
	SpriteBatch batch;
	Texture img;
	BitmapFont font;
	StarVoyager ship;
	Sprite damage;
	
	TextureRegion background; 
	StarField starField;
	Controller controller;
	
	@Override
	public void create () {
		Gdx.graphics.setDisplayMode(1280, 720, true);
		starField = new StarField(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		batch = new SpriteBatch();
		font = new BitmapFont(Gdx.files.internal("fonts/kenvector.fnt"));
		try {
			AtlasParser.parse(Gdx.files.internal("graphics/sheet.xml"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		background = new TextureRegion(new Texture("graphics/backgrounds/black.png"));

		ship = new StarVoyager();
		
	}

	@Override
	public void render () {
		float delta = Gdx.graphics.getDeltaTime();
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		starField.advance(delta);
		starField.render(batch);
		ship.update(delta);
		ship.draw(batch);
//		batch.draw(damage, 0, 0);
//		batch.draw(img, 0, 0);
//		font.setScale(1f, 1f);
//		font.setColor(0f, 0f, 0f, 1f);
//		font.draw(batch, "STARFISH SHOOTER", 100, 100);
		batch.end();
	}
}
