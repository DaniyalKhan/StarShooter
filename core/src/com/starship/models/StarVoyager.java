package com.starship.models;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.starfish.controllers.PlayStation3;
import com.starfish.controllers.PlayStation3.PS3ButtonCallback;
import com.starshooter.util.TextureCache;

public class StarVoyager extends StarShip {
	
	private static final String PLAYER_SHIP = "playerShip";
	private static final String SHIP_1 = "1_";
	private static final String SHIP_2 = "2_";
	private static final String SHIP_3 = "3_";

	private static final String ORANGE = "orange";
	
	private boolean frontCannon = true;
	private boolean leftCannon = false;
	private boolean rightCannon = false;
	private boolean rearCannon = false;
	
	private final PlayStation3 controller;
	
	public StarVoyager() {
		super(TextureCache.obtain().get(PLAYER_SHIP + SHIP_3 + ORANGE));
		controller = new PlayStation3(new PS3ButtonCallback() {
			@Override
			public void onTriangle() {frontCannon = !frontCannon;}
			@Override
			public void onStart() {}
			@Override
			public void onSquare() {leftCannon = !leftCannon;}
			@Override
			public void onCross() {rearCannon = !rearCannon;}
			@Override
			public void onCircle() {rightCannon = !rightCannon;}
		});
		setX(Gdx.graphics.getWidth()/2f - getWidth()/2f);
		setY(Gdx.graphics.getHeight()/2f - getHeight()/2f);
	}

	@Override
	protected int getMaxHealth() {
		return 10;
	}

	public void update(float delta) {
		float cx = controller.pollLeftAxisX();
		float cy = controller.pollLeftAxisY();
		translate(600 * cx * delta, 400 * cy * delta);
	}
	
	@Override
	public void draw(Batch batch) {
		float[] vertices = getVertices();
		drawCannons(batch);
		super.draw(batch);
	}
	
	public void drawCannons(Batch batch) {
		TextureRegion gun = TextureCache.obtain().get(GUN + "06");
		if (frontCannon) drawGun(batch, gun, getX() + getWidth()/2f,  getY() + getHeight(), 270);
		if (leftCannon) drawGun(batch, gun, getX(),  getY() + getHeight()/3f, 0);
		if (rightCannon) drawGun(batch, gun, getX() + getWidth(),  getY() + getHeight()/3f, 180);
		if (rearCannon) drawGun(batch, gun, getX() + getWidth()/2f, getY(), 90);
	}
	
	private void drawGun(Batch batch, TextureRegion gun, float x, float y, float rotation) {
		//FIXME: weird hack??
		float width = gun.getRegionHeight();
		float height = gun.getRegionWidth();
		float originX = width/2f;
		float originY = height/2f;
		batch.draw(gun, x - originX, y - originY, originX, originY, width, height, 1f, 1f, rotation, true);
	}
	
}
