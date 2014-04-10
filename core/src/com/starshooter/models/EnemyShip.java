package com.starshooter.models;

import java.util.Random;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.starshooter.util.SpriteUtils;
import com.starshooter.util.TextureCache;

public class EnemyShip extends StarShip {
	
	public static final String BLUE = "Blue";
	public static final String RED = "Red";
	public static final String GREEN = "Green";
	public static final String BLACK = "Black";
	private static final String ENEMY = "enemy";
	
	//note to self: type 1 was weird hit box
	public enum EnemyType {TYPE_1, TYPE_2, TYPE_3, TYPE_4, TYPE_5};
	
	private final EnemyType type;
	
	private static final Random RAND = new Random();
	private static float laserSpeed = 400;

	private static Vector2 tmp = new Vector2();
	
	private final float shootProbability;
	private float fireRate = 3f;
	private float lastFireTime = 0;
	
	private boolean dead;
	
	public EnemyShip(String color, EnemyType type, LaserListener listener, UIListener uiListener) {
		super(TextureCache.obtain().get(ENEMY + color + (type.ordinal() + 1)), listener, uiListener);
		this.type = type;
		this.health = (type.ordinal() + 1) * 2;
		this.shootProbability = (type.ordinal() + 1) * 0.1f + 0.2f;
		this.fireRate -= type.ordinal()/(type.ordinal() + 1f) - Math.abs(RAND.nextGaussian());
	}
	
	@Override
	protected void update(float delta) {
		super.update(delta);
		lastFireTime += delta;
		if (RAND.nextFloat() <= shootProbability && lastFireTime >= fireRate && !dead) {
			lastFireTime = 0;
			float rotationSkew = (float) (getRotation() + (RAND.nextFloat() -0.5) * 15);
			fire(laserSpeed, SpriteUtils.getMid(this), tmp.set(MathUtils.cosDeg(rotationSkew), MathUtils.sinDeg(rotationSkew)), 1, Laser.Type.Foe);
		}
	}

	@Override
	public boolean damage(int damage) {
		dead = super.damage(damage);
		return dead; 
	}

	public int getPoints() {
		return type.ordinal() * 10 + 30;
	}

	@Override
	public int getMaxHealth() {
		if (type == null) return 0;
		else return (type.ordinal() + 1) * 2;
	}

}
