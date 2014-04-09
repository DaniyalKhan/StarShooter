package com.starshooter.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.starshooter.models.Laser;
import com.starshooter.models.StarShip.LaserListener;
import com.starshooter.models.StarVoyager;

public class StarTerrain implements Screen, LaserListener {

	private final StarField starField;
	private final StarVoyager ship;
	private final SpriteBatch batch;
	private final Array<Laser> friendlies = new Array<Laser>(false, 64);
	private final Array<Laser> foes = new Array<Laser>(false, 32);
	
	public StarTerrain(SpriteBatch batch) {
		this.batch = batch;
		this.starField = new StarField(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		this.ship = new StarVoyager(this);
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
		batch.end();
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
		
	}

}
