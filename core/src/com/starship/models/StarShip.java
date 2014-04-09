package com.starship.models;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public abstract class StarShip extends Sprite {

	protected static final String BLUE = "blue";
	protected static final String RED = "red";
	protected static final String GREEN = "green";
	
	protected static final String GUN = "gun";
	protected static final int NUMBER_GUNS = 10;
	
	private final int health;
	
	public StarShip() {
		this(null);
	}

	public StarShip(TextureRegion textureRegion) {
		super(textureRegion);
		health = getMaxHealth();
	}

	protected abstract int getMaxHealth();
	
}
