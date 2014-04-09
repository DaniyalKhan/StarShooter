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
import com.starshooter.util.AtlasParser;
import com.starshooter.util.TextureCache;

public class StarShooter extends ApplicationAdapter {
	public static final String DIR_GRAPHICS = "graphics/";
	public static final String DIR_BACKGROUNDS = DIR_GRAPHICS + "backgrounds/";
	
	SpriteBatch batch;
	Texture img;
	BitmapFont font;
	Sprite ship;
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
		damage = new Sprite(TextureCache.obtain().get("playerShip1_damage1"));
		ship = new Sprite(TextureCache.obtain().get("playerShip1_red"));
		background = new TextureRegion(new Texture("graphics/backgrounds/black.png"));
		
		controller = Controllers.getControllers().first();
		
		Controllers.addListener(new ControllerAdapter() {
			@Override
			public boolean axisMoved(Controller controller, int axisIndex, float value) {
				if (axisIndex == 0) { // left & right
//					System.err.println(axisIndex + " : " + value);
					return true;
				} else if (axisIndex == 1) { // up && down
					
				}
				return super.axisMoved(controller, axisIndex, value);
			}

			@Override
			public boolean buttonDown(Controller controller, int buttonIndex) {
				System.out.println(buttonIndex);
				return super.buttonDown(controller, buttonIndex);
			}
			
			
		});
	}

	@Override
	public void render () {
		float cx = (controller.getAxis(0) * 100) / 100f;
		float cy = -(controller.getAxis(1) * 100) / 100f;
		float delta = Gdx.graphics.getDeltaTime();
//		float cy = 0, cx = 0;
		int w = Gdx.graphics.getWidth();
		int h = Gdx.graphics.getHeight();
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		starField.advance(delta);
		starField.render(batch);
//		batch.draw(TextureCache.obtain().get("star1"), 0, 0);
		ship.translate(400 * cx * delta, 400 * cy * delta);
		batch.draw(ship, w/2f - ship.getWidth()/2f + ship.getX(), h/2f - ship.getHeight()/2f + ship.getY());
//		batch.draw(damage, 0, 0);
//		batch.draw(img, 0, 0);
//		font.setScale(1f, 1f);
//		font.setColor(0f, 0f, 0f, 1f);
//		font.draw(batch, "STARFISH SHOOTER", 100, 100);
		batch.end();
	}
}
