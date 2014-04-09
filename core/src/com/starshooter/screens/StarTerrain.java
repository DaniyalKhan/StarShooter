package com.starshooter.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.starshooter.models.Laser;
import com.starshooter.models.StarShip.LaserListener;
import com.starshooter.models.StarVoyager;
import com.starshooter.util.FontUtils;
import com.starshooter.util.TextureCache;

public class StarTerrain implements Screen, LaserListener {

	private int score = 0;
	
	final float width, height;
	
	//UI STUFF
	Sprite xIcon;
	
	private final StarField starField;
	private final StarVoyager ship;
	private final SpriteBatch batch;
	private final Array<Laser> friendlies = new Array<Laser>(false, 64);
	private final Array<Laser> foes = new Array<Laser>(false, 32);
	
	public StarTerrain(SpriteBatch batch) {
		this.width = Gdx.graphics.getWidth();
		this.height = Gdx.graphics.getHeight();
		this.batch = batch;
		this.starField = new StarField(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		this.ship = new StarVoyager(this);
		this.xIcon = new Sprite(TextureCache.obtain().get("numeralX"));
	}

	@Override
	public void onFire(Laser laser) {
		if (laser.isFriendly()) friendlies.add(laser);
		else foes.add(laser);
	}
	
	@Override
	public void render(float delta) {
		batch.begin();
		
		starField.advance(delta);
		ship.update(delta);
		for (Laser laser: friendlies) laser.update(delta);
		for (Laser laser: foes) laser.update(delta);
		
		starField.render(batch);
		for (Laser laser: friendlies) laser.draw(batch);
		for (Laser laser: foes) laser.draw(batch);
		ship.draw(batch);
		renderUI(batch);
		batch.end();
	}

	public void renderUI(SpriteBatch batch) {
		//lives
		int lives = ship.numLives;
		Sprite lifeIcon = ship.lifeIcon;
		lifeIcon.setPosition(50, height - 53);
		xIcon.setPosition(100, height - 50);
		lifeIcon.draw(batch);
		xIcon.draw(batch);
		FontUtils.draw(batch, "" + lives, 135, height - 33);
		//cannon power
		FontUtils.draw(batch, "AMMO:", width - 270, height - 33);
		FontUtils.drawShadedFont(batch, ship.cannonPower/100f, ship.cannonPower + "%", width - 145, height - 33);
		//score
		FontUtils.draw(batch, "Score: " + score, width - 270, height - 83);
	}
	
	@Override
	public void resize(int width, int height) {
		
	}

	@Override
	public void show() {
		
	}

	@Override
	public void hide() {
		
	}

	@Override
	public void pause() {
		
	}

	@Override
	public void resume() {
		
	}

	@Override
	public void dispose() {
		ship.dispose();
	}

}
