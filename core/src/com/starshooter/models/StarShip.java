package com.starshooter.models;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;

public abstract class StarShip extends Sprite {

	protected static final String BLUE = "blue";
	protected static final String RED = "red";
	protected static final String GREEN = "green";
	
	protected static final String GUN = "gun";
	protected static final int NUMBER_GUNS = 10;
	
	protected static final Pool<Laser> laserPool = new Pool<Laser>() {
		@Override
		protected Laser newObject() {
			return new Laser();
		}
	};
	
	public static void freeLaser(Laser laser) {
		laserPool.free(laser);
	}

	public interface LaserListener {
		void onFire(Laser laser);
	}
	
	private final int health;
	
	private final LaserListener listener;
	
	public StarShip(TextureRegion textureRegion, LaserListener listener) {
		super(textureRegion);
		this.health = getMaxHealth();
		this.listener = listener;
	}
	
	public void fire(float laserSpeed, Vector2 position, Vector2 direction) {
		Laser laser = laserPool.obtain();
		laser.setProperties(position.x, position.y, Laser.BLUE, Laser.TYPE_SHORT, direction.x, direction.y, laserSpeed, Laser.Type.Friendly);
		if (listener != null) listener.onFire(laser);
	}
	
	protected void onFire(Laser laser) {
		if (listener != null) listener.onFire(laser);
	}

	protected abstract int getMaxHealth();
	
}
