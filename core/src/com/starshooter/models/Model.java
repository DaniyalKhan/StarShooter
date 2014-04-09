package com.starshooter.models;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Model {

	public float x, y;
	private float xOrigin, yOrigin, rotation;
	private TextureRegion region;
	
	public Model(float x, float y, TextureRegion region) {
		this.x = x;
		this.y = y;
		this.region = region;
	}

}
